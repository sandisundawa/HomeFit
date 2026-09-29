# HomeFit product baseline

HomeFit is a beginner-first, no-equipment home workout companion. Its promise is: **get fit at home in about 15 minutes a day**.

## V1 decisions recovered from planning

- Primary audience: beginners and people pursuing weight loss.
- A fixed, personalized 28-day program; three to five exact workout days per week.
- The canonical plan uses four sessions: full body, lower body, upper body + core, and low-impact conditioning.
- Repetition-based prescriptions are the default; holds, cardio, warm-ups, and cooldowns use timers.
- Missed workouts stay missed. Later sessions do not move automatically.
- No equipment. Low-impact substitution is resolved when a plan is generated.
- Healthy habits support the workout experience rather than compete with it.
- Optional weight tracking uses integer grams in persistence.
- Exercise media will use compact animation assets; V1 ships placeholders until licensed assets are selected.

## Technical contract

The app uses shared Compose Multiplatform UI for Android and iOS. State follows unidirectional MVI: UI emits intents, a store reduces them into immutable state, and Compose observes `StateFlow`.

Room3 is the offline source of truth. Seed JSON is readable and versioned, while Room is normalized. Published workout and program templates are immutable. A generated program snapshots resolved exercise prescriptions; starting a workout creates a second execution snapshot. This prevents content updates or preference changes from silently modifying active plans.

The current UI state is owned by a shared AndroidX `HomeFitViewModel`. Screens emit `AppIntent` values, the reducer produces immutable `AppState`, and Compose observes its `StateFlow`. Android scopes this ViewModel through `ViewModelProvider`; iOS and desktop create it at their platform host boundary.

Room schema and seed contracts are established beside the UI. The next persistence increment wires seeding, repository implementations, database builders, `SavedStateHandle`, and process-death restoration into the existing contracts without changing the screen flow.
