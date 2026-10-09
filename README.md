# UniReg (Native Android)

UniReg is a mobile app that helps university students register for courses and find timetable clashes **before** they confirm their registration.

- Module: IT3060 Human Computer Interaction, Milestone 03 (Mobile App Implementation and Final Evaluation)
- Group: WD_42 (WD_42_TimeTable_Clash_Detection)
- Package: `com.example.uniregnative`

## Group members

| Student ID | Name |
|---|---|
| IT23606692 | Sarangan |
| IT22144294 | H.K.D.W.M. Harith Senura Divarathna |

## What the app does

- Sign up and log in with a Student ID, name, email and password.
- Add a profile photo (kept separately for each account).
- Search and select courses from a live course list.
- See a weekly timetable preview that flags classes that overlap.
- Clash Warning and Clash Details screens, with alternative times for a clashing course.
- Confirm the registration, and get a notification.
- Profile screen that lists the registered courses and lets the student change the name, photo and registered courses.
- Log out and log in again; the registered courses and photo are still there.

Not built in this version: comparing whole timetables (Compare Options), calendar and filter screens, and password reset by email.

## Tech stack

- Kotlin and Jetpack Compose (Material 3)
- Supabase (PostgreSQL with the REST API) for the shared course data and for new accounts
- Android Studio, Gradle (Kotlin DSL)

## How to run it

### Option 1: install the APK

1. Download `app-debug.apk` from this repository (see the `apk` folder or the Releases page).
2. Copy it to an Android phone and tap it to open.
3. If the phone blocks it, allow **Install from this source** and tap **Install** again.
4. Open **UniReg**. Use a phone with internet access, because the course list comes from Supabase.

### Option 2: build from source

1. Install the latest stable **Android Studio** and open it.
2. Choose **Open** and select the project folder.
3. Wait for the Gradle sync to finish.
4. Connect an Android phone with **USB debugging** turned on, or start an emulator.
5. Click **Run** (green triangle).

To build the APK from the command line:

```
./gradlew assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

The minimum Android version is set in `app/build.gradle.kts` (`minSdk`). A phone running an older Android version than that cannot install the app.

## Back end (Supabase)

The app uses two tables:

- `courses`: code, title, instructor, credits, capacity, enrolled, slots
- `accounts`: name, email, time created (new sign-ups are sent here)

The Supabase project address and the public `anon` key are in `SupabaseAccountRepository.kt`. This key is the public one, and access is controlled by Row Level Security on the tables. The app needs internet access to read courses and create accounts.

## Project structure

| File | What it does |
|---|---|
| `MainActivity.kt` | Navigation between screens, notifications and the registration logic |
| `LoginScreen.kt`, `SignupScreen.kt` | Log in and sign up screens |
| `CourseCatalogScreen.kt` | Course list, search and selection |
| `ProfileScreen.kt` | Profile, photo and registered courses |
| `AccountStore.kt` | Saves each account, its photo and its courses on the phone |
| `SupabaseAccountRepository.kt` | Sends new accounts to Supabase and reads course data |
| `AuthBackground.kt` | Shared background used by the login screens |
| `ui/theme/` | Colours, type and theme |

## Testing

- Functional testing: 28 test cases were written and run by hand on a real Android phone. The results and screenshots are in the project report.
- Usability testing: participants completed 7 tasks and answered a feedback form (task completion, ease, and the System Usability Scale). The results are in section 9 of the report.

## Known limitations

- Passwords are saved on the phone as plain text. This is only acceptable for a prototype. A later version should use Supabase Auth and store password hashes.
- On the Sign up screen the on-screen keyboard can cover the Password fields (planned fix: make the form scroll).
- On phones set to dark mode, typed text can look white on a white field (planned fix: force the light theme).
- Registrations are saved on the phone, not on the server, so a student cannot use more than one phone.
- Compare Options and password reset are not built.

## Documents

The Milestone 01, 02 and 03 reports are in the submission on CourseWeb.
