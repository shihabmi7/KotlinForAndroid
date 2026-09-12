# notes_compose

A Jetpack Compose rewrite of the Notes screen (`NoteComposeActivity`), built
on the same `Note` / `NoteDao` / `NoteRepository` / `NoteViewModel` as the
legacy MVVM screen — both live off the shared `:notes_data` module, so they
read and write the same `note_table`.

## Why this module is pinned to old Compose/Kotlin versions

This is not a stylistic choice — it's forced by the host app's toolchain,
and it's the reason this module uses **Material 2** (`androidx.compose.material`)
instead of **Material 3**.

The root project's actual Kotlin compiler is **1.6.10**
(`build.gradle` → `classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10'`),
even though nothing in this module chose that on purpose. Two things pin it there:

1. **The Compose compiler is version-locked to an exact Kotlin release.**
   Each Compose compiler build only works with one specific Kotlin version
   (not a range) — mismatches aren't just a warning, they fail with either
   a hard compile error or (worse) a silent-looking `NoSuchMethodError` deep
   in Compose's IR lowering, because the compiler plugin calls internal
   Kotlin compiler APIs whose signatures change between patch releases.
   Compose compiler `1.1.1` (`composeOptions.kotlinCompilerExtensionVersion`
   in `build.gradle`) is the one built for Kotlin `1.6.10`. There is no
   Compose compiler release that targets `1.6.21`, and the
   `suppressKotlinVersionCompatibilityCheck` escape hatch does **not**
   help here — it silences the version-string warning but the real IR API
   mismatch still crashes the compiler.

2. **Hilt's kapt processor is version-sensitive too, in the other direction.**
   The project's `hilt-android` / `hilt-compiler` are pinned at `2.38.1`
   throughout (see `app/build.gradle`, `notes_data/build.gradle`, this
   module). That version of `hilt-compiler` reads Kotlin metadata with an
   embedded `kotlinx-metadata-jvm` that can't parse metadata emitted by
   Kotlin 1.7.x — bumping the project to Kotlin 1.7.21 (to unlock newer
   Compose/Material 3) breaks every `@Module`/`@HiltViewModel` in the app
   with `[Hilt] Unsupported metadata version`.

So the two constraints point at the same, narrow spot: Kotlin 1.6.10 is the
newest version that keeps both Compose and Hilt's kapt processor working
without touching the rest of the app. That's what's in the root
`build.gradle` today, and why this module's `compose_version` is `1.1.1`
and its `androidx.compose.material3` dependency doesn't exist — Material 3
1.0.x needs a newer Compose runtime than 1.1.1 provides.

## What it would take to move to Material 3 / current Compose

Both constraints have to be lifted together, project-wide, not just in this
module:

1. Bump `hilt-android-gradle-plugin` / `hilt-android` / `hilt-compiler`
   everywhere (`app`, `notes_data`, `notes_compose`) to a version whose kapt
   processor understands Kotlin 1.7+/1.8+ metadata (2.44+ is a reasonable
   target).
2. Bump the root `kotlin-gradle-plugin` classpath to match.
3. Recompile the whole app and fix whatever the newer Kotlin/Hilt surface
   breaks elsewhere (the rest of `:app` has 100+ legacy files that have
   never been built against anything past 1.6.x).
4. Only then raise `composeOptions.kotlinCompilerExtensionVersion` and swap
   this module's `androidx.compose.material:material` dependency for
   `androidx.compose.material3:material3`.

Until that project-wide bump happens, treat the versions in this module's
`build.gradle` as load-bearing, not stale — don't "helpfully" update just
`notes_compose`'s Compose version without redoing the same analysis above.
