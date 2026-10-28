package com.homefit.core.database

import androidx.room3.*
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "app_state_snapshot")
data class AppStateSnapshotEntity(
    @PrimaryKey val id: Int = 1,
    val json: String,
)

@Entity(tableName = "exercises", indices = [Index(value = ["slug"], unique = true)])
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val category: String,
    val exerciseType: String,
    val defaultReps: Int?,
    val defaultDurationSeconds: Int?,
    val impactLevel: String,
    val requiresFloor: Boolean,
    val requiresWall: Boolean,
    val requiresJumping: Boolean,
    val instructions: String,
    val primaryCue: String?,
    val isActive: Boolean = true,
)

@Entity(tableName = "workouts", indices = [Index(value = ["slug", "version"], unique = true)])
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val version: Int,
    val name: String,
    val category: String,
    val difficulty: String,
    val estimatedDurationSeconds: Int,
    val isActive: Boolean = true,
)

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(entity = WorkoutEntity::class, parentColumns = ["id"], childColumns = ["workoutId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"], onDelete = ForeignKey.RESTRICT),
    ],
    indices = [Index("workoutId"), Index("exerciseId"), Index(value = ["workoutId", "orderIndex"], unique = true)],
)
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val exerciseId: String,
    val section: String,
    val orderIndex: Int,
    val reps: Int?,
    val durationSeconds: Int?,
    val restAfterSeconds: Int,
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = LOCAL_USER_ID,
    val goal: String,
    val fitnessLevel: String,
    val lowImpact: Boolean,
    val reminderEnabled: Boolean,
    val onboardingCompleted: Boolean,
    val updatedAtEpochMs: Long,
)

@Entity(
    tableName = "user_workout_days",
    primaryKeys = ["userId", "dayOfWeekIso"],
    foreignKeys = [ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")],
)
data class UserWorkoutDayEntity(val userId: String, val dayOfWeekIso: Int)

@Entity(tableName = "user_programs", indices = [Index("userId")])
data class UserProgramEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val templateProgramId: String,
    val startedEpochDay: Long,
    val durationDays: Int,
    val status: String,
)

@Entity(tableName = "user_program_days", indices = [Index("userProgramId"), Index("scheduledEpochDay"), Index(value = ["userProgramId", "dayNumber"], unique = true)])
data class UserProgramDayEntity(
    @PrimaryKey val id: String,
    val userProgramId: String,
    val dayNumber: Int,
    val scheduledEpochDay: Long,
    val dayType: String,
    val templateWorkoutId: String?,
    val status: String?,
)

@Entity(tableName = "user_program_day_exercises", indices = [Index("userProgramDayId"), Index("exerciseId"), Index(value = ["userProgramDayId", "orderIndex"], unique = true)])
data class UserProgramDayExerciseEntity(
    @PrimaryKey val id: String,
    val userProgramDayId: String,
    val sourceWorkoutExerciseId: String?,
    val exerciseId: String,
    val section: String,
    val orderIndex: Int,
    val reps: Int?,
    val durationSeconds: Int?,
    val restAfterSeconds: Int,
)

@Entity(tableName = "workout_sessions", indices = [Index("userId"), Index("userProgramDayId"), Index("status")])
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userProgramDayId: String?,
    val sourceWorkoutId: String?,
    val status: String,
    val phase: String,
    val currentExerciseIndex: Int,
    val remainingSeconds: Int,
    val startedAtEpochMs: Long,
    val completedAtEpochMs: Long?,
    val lastUpdatedAtEpochMs: Long,
)

@Entity(tableName = "workout_session_exercises", indices = [Index("sessionId"), Index("exerciseId"), Index(value = ["sessionId", "orderIndex"], unique = true)])
data class WorkoutSessionExerciseEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val sourceProgramDayExerciseId: String?,
    val exerciseId: String,
    val section: String,
    val orderIndex: Int,
    val plannedReps: Int?,
    val plannedDurationSeconds: Int?,
    val plannedRestSeconds: Int,
    val actualReps: Int?,
    val actualDurationSeconds: Int?,
    val status: String,
)

@Entity(tableName = "weight_entries", indices = [Index(value = ["userId", "epochDay"], unique = true)])
data class WeightEntryEntity(@PrimaryKey val id: String, val userId: String, val epochDay: Long, val weightGrams: Int)

@Entity(tableName = "habit_entries", primaryKeys = ["userId", "epochDay", "habitType"])
data class HabitEntryEntity(val userId: String, val epochDay: Long, val habitType: String, val isCompleted: Boolean)

@Entity(tableName = "seed_metadata")
data class SeedMetadataEntity(@PrimaryKey val id: Int = 1, val schemaVersion: Int, val contentVersion: Int, val seededAtEpochMs: Long)

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE isActive = 1 ORDER BY name") fun observeAll(): Flow<List<ExerciseEntity>>
    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1") suspend fun getById(id: String): ExerciseEntity?
    @Upsert suspend fun upsertAll(values: List<ExerciseEntity>)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts WHERE isActive = 1 ORDER BY name") fun observeAll(): Flow<List<WorkoutEntity>>
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex") suspend fun getExercises(workoutId: String): List<WorkoutExerciseEntity>
    @Upsert suspend fun upsertWorkouts(values: List<WorkoutEntity>)
    @Upsert suspend fun upsertExercises(values: List<WorkoutExerciseEntity>)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1") fun observe(id: String = LOCAL_USER_ID): Flow<UserProfileEntity?>
    @Query("SELECT * FROM user_workout_days WHERE userId = :id ORDER BY dayOfWeekIso") suspend fun getDays(id: String = LOCAL_USER_ID): List<UserWorkoutDayEntity>
    @Upsert suspend fun upsert(value: UserProfileEntity)
    @Upsert suspend fun upsertDays(values: List<UserWorkoutDayEntity>)
}

@Dao
interface ProgramDao {
    @Query("SELECT * FROM user_program_days WHERE userProgramId = :programId ORDER BY dayNumber") fun observeDays(programId: String): Flow<List<UserProgramDayEntity>>
    @Query("SELECT * FROM user_program_day_exercises WHERE userProgramDayId = :dayId ORDER BY orderIndex") suspend fun getDayExercises(dayId: String): List<UserProgramDayExerciseEntity>
    @Upsert suspend fun upsertProgram(value: UserProgramEntity)
    @Upsert suspend fun upsertDays(values: List<UserProgramDayEntity>)
    @Upsert suspend fun upsertDayExercises(values: List<UserProgramDayExerciseEntity>)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND status = 'IN_PROGRESS' ORDER BY startedAtEpochMs DESC LIMIT 1") fun observeActive(userId: String = LOCAL_USER_ID): Flow<WorkoutSessionEntity?>
    @Query("SELECT * FROM workout_session_exercises WHERE sessionId = :sessionId ORDER BY orderIndex") fun observeExercises(sessionId: String): Flow<List<WorkoutSessionExerciseEntity>>
    @Upsert suspend fun upsert(value: WorkoutSessionEntity)
    @Upsert suspend fun upsertExercises(values: List<WorkoutSessionExerciseEntity>)
}

@Dao
interface TrackingDao {
    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY epochDay") fun observeWeight(userId: String = LOCAL_USER_ID): Flow<List<WeightEntryEntity>>
    @Query("SELECT * FROM habit_entries WHERE userId = :userId AND epochDay = :epochDay") fun observeHabits(userId: String = LOCAL_USER_ID, epochDay: Long): Flow<List<HabitEntryEntity>>
    @Upsert suspend fun upsertWeight(value: WeightEntryEntity)
    @Upsert suspend fun upsertHabit(value: HabitEntryEntity)
}

@Dao
interface SeedMetadataDao {
    @Query("SELECT * FROM seed_metadata WHERE id = 1") suspend fun get(): SeedMetadataEntity?
    @Upsert suspend fun upsert(value: SeedMetadataEntity)
}

@Dao
interface AppStateSnapshotDao {
    @Query("SELECT * FROM app_state_snapshot WHERE id = 1 LIMIT 1") suspend fun get(): AppStateSnapshotEntity?
    @Upsert suspend fun upsert(value: AppStateSnapshotEntity)
}

@Database(
    entities = [ExerciseEntity::class, WorkoutEntity::class, WorkoutExerciseEntity::class, UserProfileEntity::class, UserWorkoutDayEntity::class, UserProgramEntity::class, UserProgramDayEntity::class, UserProgramDayExerciseEntity::class, WorkoutSessionEntity::class, WorkoutSessionExerciseEntity::class, WeightEntryEntity::class, HabitEntryEntity::class, SeedMetadataEntity::class, AppStateSnapshotEntity::class],
    version = 2,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun profileDao(): ProfileDao
    abstract fun programDao(): ProgramDao
    abstract fun sessionDao(): SessionDao
    abstract fun trackingDao(): TrackingDao
    abstract fun seedMetadataDao(): SeedMetadataDao
    abstract fun appStateSnapshotDao(): AppStateSnapshotDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun RoomDatabase.Builder<AppDatabase>.withHomeFitDefaults(): RoomDatabase.Builder<AppDatabase> =
    setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.Default).addMigrations(MIGRATION_1_2)

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_state_snapshot` (`id` INTEGER NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}

const val LOCAL_USER_ID = "local_user"
