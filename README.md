# HomeFit

A greenfield Compose Multiplatform home-workout app based on the recovered product plan. Android and iOS share the same Compose UI and MVI state model.

## Included now

- Complete onboarding and personalized 28-day schedule generation
- Today dashboard, workout library, progress, habits, and profile
- All 30 planned screen experiences, represented by 29 routes plus the player's get-ready/exercise/rest states
- AndroidX `ViewModel`-owned MVI state with immutable state, intents, and reducer transitions
- Forty-exercise beginner/no-equipment master catalog
- Four progressive weekly workout families and a four-week program
- Room3 schema for profile, plan snapshots, session snapshots, weight, habits, and seed metadata
- Versioned JSON seed bundle
- Shared domain/reducer tests
- Android app, desktop preview target, and iOS framework/Swift entry point

## Run

```bash
./gradlew :desktopApp:run
./gradlew :appAndroid:assembleDebug
./gradlew :shared:allTests
```

For iOS, install Xcode and XcodeGen, run `xcodegen` inside `iosApp`, then open the generated project. The Gradle task produces the shared Compose framework.

See [docs/PRODUCT.md](docs/PRODUCT.md) for the recovered product and architecture baseline.
