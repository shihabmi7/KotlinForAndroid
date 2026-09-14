# Cross-Platform Mobile Engineer Interview Guide
### (Swift/SwiftUI · Kotlin/Jetpack Compose · Kotlin Multiplatform)

Tailored to a senior (5+ yrs) native mobile role covering iOS + Android with shared business logic via KMP. Organized by the core requirements: language/UI mastery, concurrency, architecture, API integration, testing, CI/CD, troubleshooting, and collaboration.

## Contents
1. [Role Context: Why KMP + Native UI](#1-role-context-why-kmp--native-ui)
2. [Kotlin Multiplatform (KMP)](#2-kotlin-multiplatform-kmp)
3. [Native UI: SwiftUI vs. Jetpack Compose](#3-native-ui-swiftui-vs-jetpack-compose)
4. [Structured Concurrency & Async Programming](#4-structured-concurrency--async-programming)
5. [App Lifecycle Management](#5-app-lifecycle-management)
6. [Architecture: MVVM, Unidirectional Data Flow, Modularization](#6-architecture-mvvm-unidirectional-data-flow-modularization)
7. [API Integration: REST, Auth, Analytics, Notifications, Remote Config](#7-api-integration-rest-auth-analytics-notifications-remote-config)
8. [Testing: Unit, Integration, Snapshot, UI](#8-testing-unit-integration-snapshot-ui)
9. [CI/CD & Git/GitHub Workflows](#9-cicd--gitgithub-workflows)
10. [Troubleshooting Production Issues](#10-troubleshooting-production-issues)
11. [Working with Figma](#11-working-with-figma)
12. [Behavioral & Collaboration Questions](#12-behavioral--collaboration-questions)
13. [Sample Coding Exercises](#13-sample-coding-exercises)

---

## 1. Role Context: Why KMP + Native UI

### Q: What's the current (2026) industry-standard approach to sharing code between iOS and Android?
The dominant pattern is **"shared core, native UI"**: business logic, networking, persistence, and state management live in shared Kotlin modules (KMP), while the UI stays fully native — **SwiftUI on iOS**, **Jetpack Compose on Android**. This is exactly the pattern implied by a JD that lists both "Swift/SwiftUI" and "Kotlin/Jetpack Compose" *and* KMP for shared logic — it's not asking you to render one shared UI on both platforms.

- KMP itself has been stable since late 2023 and is officially backed by both JetBrains and Google.
- **Compose Multiplatform** (fully shared UI, including iOS) reached stable status for iOS in 2025 and has kept maturing — some teams do adopt it, but it's a separate decision from "shared core + native UI," and most job descriptions phrased like this one are asking for the native-UI-per-platform approach.
- Direct Kotlin→Swift export (no Objective-C bridging step) is newer and still maturing — worth mentioning if asked about KMP/iOS interop pain points.

---

## 2. Kotlin Multiplatform (KMP)

### Q: What does a typical KMP module structure look like?

```
shared/
├── commonMain/     # shared Kotlin code (business logic, models, repositories)
├── androidMain/    # Android-specific implementations (expect/actual)
├── iosMain/        # iOS-specific implementations (expect/actual)
└── commonTest/     # shared unit tests
```

### Q: Explain `expect`/`actual`.
`expect` declares an API in shared code without an implementation; each platform provides the concrete `actual` implementation. This is how KMP handles platform-specific needs (e.g., secure storage, platform HTTP engines) while keeping the calling code shared.

```kotlin
// commonMain
expect class PlatformInfo() {
    val deviceName: String
}

// androidMain
actual class PlatformInfo actual constructor() {
    actual val deviceName: String = Build.MODEL
}

// iosMain
actual class PlatformInfo actual constructor() {
    actual val deviceName: String = UIDevice.currentDevice.name
}
```

### Q: What do you typically put in shared KMP code vs. keep platform-native?

| Shared (KMP) | Platform-native |
|---|---|
| Networking layer (Ktor client) | UI (SwiftUI / Compose) |
| Data models, serialization | Push notification registration/handling |
| Repository / use-case layer | Deep platform APIs (camera, Bluetooth, biometrics) |
| Local caching logic (SQLDelight) | App lifecycle wiring |
| Validation, business rules | Platform-specific animations/gestures |

### Q: What are common KMP pain points you should be ready to discuss?
- Swift interop still occasionally needs Objective-C bridging for advanced Kotlin features (sealed classes with generics, coroutine flows require wrapper types on the Swift side).
- Build times/tooling can be heavier than a single-platform project (multiple targets to compile).
- Debugging shared code from Xcode is less mature than from Android Studio.
- Coroutines/`Flow` need adaptation for Swift consumption (e.g., via `SKIE` or manual callback wrapping) since Swift doesn't natively understand Kotlin `Flow`.

---

## 3. Native UI: SwiftUI vs. Jetpack Compose

Both are **declarative, state-driven UI toolkits** — the concepts map closely, which is exactly why interviewers expect you to speak to both if you're on a KMP team.

| Concept | SwiftUI | Jetpack Compose |
|---|---|---|
| UI unit | `View` (struct, protocol conformance) | `@Composable` function |
| Local state | `@State` | `remember { mutableStateOf(...) }` |
| Shared/observed state | `@ObservedObject` / `@StateObject` / `@Published` | `StateFlow` collected via `collectAsState()` |
| Dependency injection into view | `@EnvironmentObject` | `CompositionLocal` |
| List rendering | `List` / `ForEach` | `LazyColumn` / `LazyRow` |
| Navigation | `NavigationStack` | Jetpack `Navigation` (Compose) |

```swift
// SwiftUI
struct CounterView: View {
    @State private var count = 0
    var body: some View {
        VStack {
            Text("Count: \(count)")
            Button("Increment") { count += 1 }
        }
    }
}
```

```kotlin
// Jetpack Compose
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    Column {
        Text("Count: $count")
        Button(onClick = { count++ }) { Text("Increment") }
    }
}
```

### Q: How does each framework decide when to redraw?
SwiftUI diffs the `View` struct's identity/values each time state changes and redraws only the affected subviews. Compose re-runs (recomposes) only the composables that actually read the changed `State`. Conceptually identical — both are optimizing away full-tree re-renders.

---

## 4. Structured Concurrency & Async Programming

### Q: Compare Swift's structured concurrency to Kotlin Coroutines.

| Concept | Swift | Kotlin |
|---|---|---|
| Suspending function | `async func` | `suspend fun` |
| Awaiting a result | `await` | nothing extra needed inside another `suspend fun`; `.await()` for `Deferred` |
| Concurrency scope | `Task { }` / `TaskGroup` | `CoroutineScope` / `viewModelScope` |
| Cancellation | Cooperative, checked via `Task.checkCancellation()` | Cooperative, checked via `isActive` / `ensureActive()` |
| Actor isolation (thread safety) | `actor` type | Coroutine `Mutex` / confined dispatcher |

```swift
// Swift structured concurrency
func loadUser(id: String) async throws -> User {
    async let profile = api.fetchProfile(id)
    async let settings = api.fetchSettings(id)
    return try await User(profile: profile, settings: settings)
}
```

```kotlin
// Kotlin structured concurrency
suspend fun loadUser(id: String): User = coroutineScope {
    val profile = async { api.fetchProfile(id) }
    val settings = async { api.fetchSettings(id) }
    User(profile.await(), settings.await())
}
```

**Why both matter here:** in a KMP+native-UI shop, shared repository code is written once in Kotlin coroutines, but the iOS UI layer consumes it through Swift's `async/await` — so you need to reason about cancellation and threading on both sides of that boundary.

---

## 5. App Lifecycle Management

| Stage | iOS (UIKit/SwiftUI `Scene`) | Android (Activity/Compose) |
|---|---|---|
| Launch | `application(_:didFinishLaunchingWithOptions:)` | `Application.onCreate()` / `Activity.onCreate()` |
| Foreground → active | `sceneDidBecomeActive` | `onResume()` |
| Backgrounding | `sceneWillResignActive` / `didEnterBackground` | `onPause()` / `onStop()` |
| Termination | `applicationWillTerminate` (not guaranteed) | `onDestroy()` (not guaranteed; process death is common) |
| State restoration | `NSUserActivity` / scene state restoration | `onSaveInstanceState()` / `SavedStateHandle` |

**Key interview point:** Android's process death model is more aggressive than iOS's — you need `ViewModel` + `SavedStateHandle` (or KMP-shared state persisted to disk) to survive it, whereas iOS backgrounding is comparatively more forgiving but still requires handling suspension correctly (e.g., finishing in-flight tasks with `BGTaskScheduler` for iOS background work vs. `WorkManager` on Android).

---

## 6. Architecture: MVVM, Unidirectional Data Flow, Modularization

### Q: How does MVVM map onto SwiftUI and Compose, given they're both already reactive?
Both frameworks are inherently MV-ish (View binds directly to observable state), but teams still add an explicit **ViewModel** layer to keep business logic out of the View and make it testable without instantiating UI.

```kotlin
// Kotlin ViewModel exposing UDF-style state
class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Load -> loadProfile(event.userId)
            is ProfileEvent.Retry -> loadProfile(_state.value.userId)
        }
    }
}
```

### Q: What is Unidirectional Data Flow (UDF), and why do interviewers ask about it specifically?
UDF (the pattern behind MVI/Redux-style architectures) means: **state flows down, events flow up** — the View renders a single immutable `UiState`, and all user actions become `Event`/`Intent` objects sent back to the ViewModel, which is the *only* thing allowed to produce a new state. This eliminates the classic bug class where multiple mutable properties drift out of sync, and it's straightforward to unit test since a `(state, event) -> newState` reducer function is pure.

```kotlin
data class ProfileUiState(
    val userId: String = "",
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val error: String? = null
)

sealed interface ProfileEvent {
    data class Load(val userId: String) : ProfileEvent
    object Retry : ProfileEvent
}
```

### Q: What does "modular architecture" mean in a mobile context, and why does it matter at scale?
Splitting the app into independent Gradle/Swift Package modules by feature or layer (`:core:network`, `:core:database`, `:feature:profile`, `:feature:settings`) instead of one monolithic module/target.

**Benefits interviewers look for:**
- Faster incremental builds (only rebuild what changed).
- Enforced boundaries — a feature module can't reach into another feature's internals.
- Enables parallel team ownership and cleaner code review scope.
- Easier to test a feature in isolation.

---

## 7. API Integration: REST, Auth, Analytics, Notifications, Remote Config

### Q: How would you structure a shared networking layer in KMP?
Ktor's multiplatform HTTP client is the standard choice, since it runs on both `iosMain` and `androidMain` targets from shared code.

```kotlin
// commonMain
class UserApi(private val client: HttpClient) {
    suspend fun getUser(id: String): User =
        client.get("https://api.example.com/users/$id").body()
}
```

### Q: How do you handle authentication (token refresh) cleanly across both platforms?
Centralize the auth/token-refresh logic in the shared Ktor client via an `Auth` plugin or a custom interceptor, so both platforms get consistent behavior (attaching bearer tokens, retrying on 401 after refresh) without duplicating logic in Swift and Kotlin separately.

### Q: Where do analytics, push notifications, and remote config typically live — shared or native?
- **Analytics events** — the *triggering* logic can live in shared code (an `AnalyticsLogger` interface with `expect/actual` platform implementations wrapping Firebase/Segment/etc. native SDKs).
- **Push notifications** — registration and OS-level handling (APNs / FCM) must stay native, since they depend on platform frameworks; the shared layer can own what happens *after* a notification payload is parsed.
- **Remote config** — the fetch/caching logic can be shared; the native layer just reads flags to decide UI behavior.

---

## 8. Testing: Unit, Integration, Snapshot, UI

| Test type | iOS tool | Android tool | Purpose |
|---|---|---|---|
| Unit | XCTest | JUnit + MockK/Mockito | Test a single function/class in isolation |
| Integration | XCTest (with real/fake collaborators) | JUnit + fakes/in-memory Room | Test multiple layers together (e.g. ViewModel + repository) |
| Snapshot | `swift-snapshot-testing` | Paparazzi / Compose screenshot testing | Catch unintended visual regressions by comparing rendered output to a saved reference |
| UI (end-to-end) | XCUITest | Espresso / Compose UI Test | Simulate real user interaction through the full UI |

```kotlin
// Compose UI test
@Test
fun clickingIncrementUpdatesCount() {
    composeTestRule.setContent { CounterScreen() }
    composeTestRule.onNodeWithText("Increment").performClick()
    composeTestRule.onNodeWithText("Count: 1").assertIsDisplayed()
}
```

```swift
// XCTest for a ViewModel
func testLoadProfileSetsState() async throws {
    let viewModel = ProfileViewModel(repo: FakeProfileRepository())
    await viewModel.load(userId: "1")
    XCTAssertEqual(viewModel.state.profile?.id, "1")
}
```

**Interview tip:** since shared KMP business logic can be unit-tested once in `commonTest` and run on both platforms, be ready to explain why that's valuable (single source of truth for logic correctness) versus duplicating the same test in XCTest and JUnit.

---

## 9. CI/CD & Git/GitHub Workflows

### Q: What does a typical mobile CI/CD pipeline look like?
1. **On pull request:** lint, unit tests, build both targets, run static analysis (SwiftLint/ktlint/detekt).
2. **On merge to main:** run full test suite (including UI/snapshot tests), build release artifacts.
3. **On release branch/tag:** sign and upload to TestFlight (iOS) / Play Console internal track (Android), often via **Fastlane**.
4. **Post-deploy:** crash/analytics monitoring dashboards checked for regressions.

Typical tools: **GitHub Actions** (or Bitrise/CircleCI), **Fastlane** for signing/upload, **Gradle** build cache for Android, **xcodebuild**/`xcresult` parsing for iOS.

### Q: What does a healthy PR/code review workflow look like on a team like this?
- Small, focused PRs tied to a GitHub Issue.
- CI must pass (build + tests + lint) before review.
- At least one approving review; comments addressed or explicitly discussed, not silently dismissed.
- Squash or rebase merge strategy agreed on as a team convention to keep history readable.

---

## 10. Troubleshooting Production Issues

### Q: A crash-free rate metric suddenly drops after a release. Walk through your process.
1. **Triage:** check the crash reporting dashboard (Crashlytics / Sentry) for the top new crash signature, its device/OS distribution, and whether it correlates with the release version.
2. **Reproduce:** try to reproduce locally, or use the crash's stack trace plus breadcrumbs/logs to narrow it down.
3. **Platform tools:** Xcode **Instruments** (Time Profiler, Allocations, Leaks) for iOS; Android Studio **Profiler** (CPU, Memory, Energy) for Android — use these for performance/memory regressions specifically.
4. **Mitigate:** if severe, consider a remote-config kill switch for the offending feature, or an expedited hotfix release.
5. **Prevent recurrence:** add a regression test, and note the gap in test/monitoring coverage that let it through.

### Q: How do you investigate a memory leak on each platform?
- **iOS:** Instruments' **Leaks** and **Allocations** tools, watching for retain cycles (commonly `self` captured strongly in a closure — fix with `[weak self]`).
- **Android:** **LeakCanary** for automatic detection during development, plus the Android Studio Memory Profiler to inspect the heap for objects that should have been garbage collected (commonly a static reference or an unregistered listener holding an Activity/Fragment).

---

## 11. Working with Figma

You don't need to *design* — you need to **read specs accurately and flag ambiguity early**.

- Use Figma's **Inspect** panel to pull exact spacing, colors, typography tokens, and exported assets rather than eyeballing them.
- Check for a connected **design system/library** (shared components) so your native components map 1:1 to Figma components instead of being rebuilt ad hoc per screen.
- Flag inconsistencies (e.g., a spacing value that doesn't match the design system) to the designer rather than silently "fixing" it in code.
- For animations/interactions, ask whether a **prototype flow** exists in Figma before guessing at the intended motion.

---

## 12. Behavioral & Collaboration Questions

- *"Tell me about a time you disagreed with a technical decision on your team."* — focus on how you raised it (data/reasoning, not just opinion) and how it was resolved.
- *"Describe a production incident you handled."* — structure as situation → diagnosis → fix → prevention, echoing the troubleshooting flow in [section 10](#10-troubleshooting-production-issues).
- *"How do you mentor a junior engineer?"* — concrete example: pairing, reviewing PRs with explanatory comments, not just approvals.
- *"How do you decide what goes in shared KMP code vs. platform-native?"* — tie back to [section 2](#2-kotlin-multiplatform-kmp)'s table; show you think about trade-offs, not "share everything."

---

## 13. Sample Coding Exercises

### Shared repository with platform-specific caching (KMP pattern)

```kotlin
// commonMain
interface UserRepository {
    suspend fun getUser(id: String): User
}

class DefaultUserRepository(
    private val api: UserApi,
    private val cache: UserCache // expect/actual-backed local storage
) : UserRepository {
    override suspend fun getUser(id: String): User =
        cache.get(id) ?: api.getUser(id).also { cache.put(id, it) }
}
```

### Unidirectional data flow reducer (interview whiteboard favorite)

```kotlin
fun reduce(state: ProfileUiState, event: ProfileEvent): ProfileUiState = when (event) {
    is ProfileEvent.Load -> state.copy(isLoading = true, userId = event.userId)
    is ProfileEvent.Retry -> state.copy(isLoading = true, error = null)
}
```

**Follow-up they may ask:** "How would you test this without a ViewModel or any Android/iOS dependency?" — Answer: it's a pure function, so you just assert `reduce(initialState, event) == expectedState` in a plain unit test, no mocking required. That testability is the entire point of UDF.
