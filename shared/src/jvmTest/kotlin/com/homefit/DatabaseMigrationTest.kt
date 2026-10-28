package com.homefit

import androidx.room3.Room
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.homefit.core.database.*
import com.homefit.core.model.FitnessGoal
import com.homefit.feature.app.*
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.*
import kotlin.test.*

class DatabaseMigrationTest {
    private inline fun <T> AppDatabase.use(block: (AppDatabase) -> T): T =
        try { block(this) } finally { close() }

    private fun schema(version: Int) = Json.parseToJsonElement(
        File("schemas/com.homefit.core.database.AppDatabase/$version.json").readText(),
    ).jsonObject.getValue("database").jsonObject

    private fun JsonObject.text(key: String) = getValue(key).jsonPrimitive.content
    private val oldTables get() = schema(1).getValue("entities").jsonArray.map { it.jsonObject.text("tableName") }

    // Fixtures come from the checked-in schema, never the current entity definitions.
    private fun historicalDatabase(file: File, version: Int = 1) {
        val schema = schema(version)
        BundledSQLiteDriver().open(file.path).use { db ->
            db.execSQL("PRAGMA foreign_keys=ON")
            for (element in schema.getValue("entities").jsonArray) {
                val entity = element.jsonObject
                val table = entity.text("tableName")
                db.execSQL(entity.text("createSql").replace("\${TABLE_NAME}", table))
                entity["indices"]?.jsonArray?.forEach {
                    db.execSQL(it.jsonObject.text("createSql").replace("\${TABLE_NAME}", table))
                }
                val fields = entity.getValue("fields").jsonArray.map { it.jsonObject }
                val values = fields.map { field ->
                    val name = field.text("columnName")
                    when {
                        name == "json" -> "'{\"route\":\"HOME\"}'"
                        name == "status" -> "'IN_PROGRESS'"
                        name == "remainingSeconds" -> "17"
                        name == "scheduledEpochDay" || name == "startedEpochDay" -> "20000"
                        name == "weightGrams" -> "72000"
                        field["notNull"]?.jsonPrimitive?.boolean != true -> "NULL"
                        field.text("affinity") == "INTEGER" -> "1"
                        else -> "'fixture'"
                    }
                }
                db.execSQL("INSERT INTO `$table` (${fields.joinToString { "`${it.text("columnName")}`" }}) VALUES (${values.joinToString()})")
            }
            db.execSQL("INSERT INTO workout_sessions SELECT 'completed', userId, userProgramDayId, sourceWorkoutId, 'COMPLETED', phase, currentExerciseIndex, 0, startedAtEpochMs, 900001, lastUpdatedAtEpochMs FROM workout_sessions")
            db.execSQL("UPDATE user_program_days SET status='MISSED'")
            schema.getValue("setupQueries").jsonArray.forEach { db.execSQL(it.jsonPrimitive.content) }
            db.execSQL("PRAGMA user_version=$version")
        }
    }

    private fun SQLiteConnection.rows(sql: String): List<List<String?>> = prepare(sql).use { statement ->
        buildList {
            while (statement.step()) add((0 until statement.getColumnCount()).map {
                if (statement.isNull(it)) null else statement.getText(it)
            })
        }
    }

    private fun snapshot(file: File) = BundledSQLiteDriver().open(file.path).use { db ->
        oldTables.associateWith { db.rows("SELECT * FROM `$it` ORDER BY rowid") }
    }

    private fun structure(file: File) = BundledSQLiteDriver().open(file.path).use { db ->
        oldTables.associateWith { table ->
            listOf(db.rows("PRAGMA table_info(`$table`)"), db.rows("PRAGMA foreign_key_list(`$table`)"),
                db.rows("SELECT name, sql FROM sqlite_master WHERE type='index' AND tbl_name='$table' ORDER BY name"))
        }
    }

    private fun open(file: File, migrations: Boolean = true): AppDatabase {
        val builder = Room.databaseBuilder<AppDatabase>(file.path)
        return if (migrations) builder.withHomeFitDefaults().build()
        else builder.setDriver(BundledSQLiteDriver()).build()
    }

    private fun fixture(block: suspend (File) -> Unit) = runBlocking {
        val directory = Files.createTempDirectory("homefit-migration-test").toFile()
        try { block(File(directory, "synthetic.db")) }
        finally { directory.deleteRecursively() }
    }

    @Test fun `v1 upgrade preserves every table and opens through current DAOs`() = fixture { file ->
        historicalDatabase(file)
        val before = snapshot(file)
        val beforeStructure = structure(file)
        open(file).use { db ->
            assertNull(db.appStateSnapshotDao().get(), "Do not fabricate a snapshot from normalized data")
            assertEquals("fixture", db.exerciseDao().getById("fixture")?.name)
            assertEquals(1, db.workoutDao().observeAll().first().size)
            assertEquals(1, db.workoutDao().getExercises("fixture").size)
            assertTrue(db.profileDao().observe("fixture").first()!!.reminderEnabled)
            assertEquals(1, db.profileDao().getDays("fixture").single().dayOfWeekIso)
            assertEquals("MISSED", db.programDao().observeDays("fixture").first().single().status)
            assertEquals(20000L, db.programDao().observeDays("fixture").first().single().scheduledEpochDay)
            assertEquals(1, db.programDao().getDayExercises("fixture").size)
            assertEquals(17, db.sessionDao().observeActive("fixture").first()!!.remainingSeconds)
            assertEquals(1, db.sessionDao().observeExercises("fixture").first().size)
            assertEquals(72000, db.trackingDao().observeWeight("fixture").first().single().weightGrams)
            assertTrue(db.trackingDao().observeHabits("fixture", 1).first().single().isCompleted)
            assertEquals(1, db.seedMetadataDao().get()!!.contentVersion)
            db.appStateSnapshotDao().upsert(AppStateSnapshotEntity(json = "{\"route\":\"HOME\"}"))
        }
        assertEquals(before, snapshot(file))
        assertEquals(beforeStructure, structure(file))
        BundledSQLiteDriver().open(file.path).use { db ->
            assertEquals(listOf(listOf("2")), db.rows("PRAGMA user_version"))
            assertEquals(emptyList(), db.rows("PRAGMA foreign_key_check"))
            assertEquals(listOf(listOf("ok")), db.rows("PRAGMA integrity_check"))
            assertEquals(listOf(listOf(schema(2).text("identityHash"))), db.rows("SELECT identity_hash FROM room_master_table WHERE id=42"))
            db.execSQL("PRAGMA foreign_keys=ON")
            assertFails { db.execSQL("INSERT INTO user_workout_days VALUES ('missing-parent', 2)") }
            assertFails { db.execSQL("INSERT INTO workouts SELECT 'duplicate', slug, version, name, category, difficulty, estimatedDurationSeconds, isActive FROM workouts") }
            assertFails { db.execSQL("INSERT INTO app_state_snapshot VALUES (2, NULL)") }
        }
        open(file).use { assertEquals("{\"route\":\"HOME\"}", it.appStateSnapshotDao().get()!!.json) }
    }

    @Test fun `missing migration fails without deleting v1 data`() = fixture { file ->
        historicalDatabase(file)
        val before = snapshot(file)
        open(file, migrations = false).use { db ->
            val error = assertFailsWith<IllegalStateException> { db.exerciseDao().getById("fixture") }
            assertTrue(error.message.orEmpty().contains("migration", ignoreCase = true))
        }
        assertEquals(before, snapshot(file))
        BundledSQLiteDriver().open(file.path).use { assertEquals(listOf(listOf("1")), it.rows("PRAGMA user_version")) }
    }

    @Test fun `existing v2 snapshot survives reopening`() = fixture { file ->
        historicalDatabase(file, version = 2)
        val before = snapshot(file)
        open(file).use { assertEquals("{\"route\":\"HOME\"}", it.appStateSnapshotDao().get()!!.json) }
        assertEquals(before, snapshot(file))
    }

    @Test fun `fresh install creates current database`() = fixture { file ->
        open(file).use { db ->
            assertNull(db.appStateSnapshotDao().get())
            db.appStateSnapshotDao().upsert(AppStateSnapshotEntity(json = "{}"))
            assertEquals("{}", db.appStateSnapshotDao().get()!!.json)
        }
    }

    @Test fun `legacy continuity gap - migrated records are absent from startup state`() = fixture { file ->
        historicalDatabase(file)
        BundledSQLiteDriver().open(file.path).use { db ->
            // Schema-valid synthetic values; historical enum encodings are not established.
            db.execSQL("UPDATE user_profiles SET id='local_user', goal='BUILD_STRENGTH', fitnessLevel='INTERMEDIATE', lowImpact=0, reminderEnabled=0")
            db.execSQL("UPDATE user_workout_days SET userId='local_user'")
            db.execSQL("INSERT INTO user_workout_days VALUES ('local_user', 3), ('local_user', 5)")
            db.execSQL("UPDATE user_programs SET userId='local_user'")
            db.execSQL("UPDATE workout_sessions SET userId='local_user'")
        }
        val before = snapshot(file)
        open(file).use { db ->
            assertEquals("BUILD_STRENGTH", db.profileDao().observe().first()!!.goal)
            assertEquals(listOf(1, 3, 5), db.profileDao().getDays().map { it.dayOfWeekIso })
            assertEquals("MISSED", db.programDao().observeDays("fixture").first().single().status)
            val repository = RoomAppStateRepository(db)
            assertNull(repository.load(), "The repository ignores normalized records without a snapshot")
            withTimeout(10_000) {
                withContext(Dispatchers.Main) {
                    val model = HomeFitViewModel(repository)
                    try {
                        model.restorationStatus.first { it == RestorationStatus.READY }
                        assertEquals(AppState(), model.state.value)
                        assertNotEquals(FitnessGoal.BUILD_STRENGTH, model.state.value.profile.goal)
                        assertTrue(model.state.value.program.isEmpty())
                        assertTrue(model.state.value.workoutHistory.isEmpty())
                        assertNull(model.state.value.player)
                        model.dispatch(AppIntent.FinishSplash)
                        assertEquals(AppRoute.WELCOME, model.state.value.route)
                    } finally { model.viewModelScope.cancel() }
                }
            }
        }
        assertEquals(before, snapshot(file), "Missing UI continuity does not mean normalized rows were deleted")
    }
}
