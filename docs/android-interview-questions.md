# Android Developer Interview Guide

A structured Q&A reference covering Android fundamentals through advanced/SDE-III topics, with Kotlin code examples, comparison tables, and notes on what's deprecated vs. current (updated for 2026).

## Contents
1. [Android Fundamentals](#1-android-fundamentals)
2. [App Components](#2-app-components)
3. [UI Basics](#3-ui-basics)
4. [Activity & Fragment Lifecycle](#4-activity--fragment-lifecycle)
5. [Intents](#5-intents)
6. [RecyclerView](#6-recyclerview)
7. [Kotlin & Coroutines](#7-kotlin--coroutines)
8. [Jetpack & Architecture Components](#8-jetpack--architecture-components)
9. [Architecture Patterns: MVC vs MVP vs MVVM](#9-architecture-patterns-mvc-vs-mvp-vs-mvvm)
10. [Jetpack Compose](#10-jetpack-compose)
11. [Persistence](#11-persistence)
12. [Dependency Injection](#12-dependency-injection)
13. [Networking](#13-networking)
14. [Performance, Memory & ANR](#14-performance-memory--anr)
15. [Testing](#15-testing)
16. [Advanced Topics](#16-advanced-topics)
17. [App Startup: Cold, Warm, Hot](#17-app-startup-cold-warm-hot)
18. [Deprecated APIs → Modern Replacements](#18-deprecated-apis--modern-replacements)
19. [Coding Exercises](#19-coding-exercises)
20. [Tricky Questions](#20-tricky-questions)
21. [Quick Tips](#21-quick-tips)

---

## 1. Android Fundamentals

### Q: What is Android, and what's the latest version?
Android is Google's open-source, Linux-based OS for mobile and embedded devices (phones, tablets, wearables, TVs, cars). As of late 2026, the newest stable release is **Android 17 (API level 37)**, with **Android 16 (API level 36)** as the current Google Play minimum target requirement for new app submissions (enforced from August 31, 2026).

- Linux kernel-based, maintained by Google, powers the large majority of the world's smartphones.
- Ships regularly (roughly yearly major releases plus quarterly feature drops).
- **Interview tip:** don't memorize the exact version number — say "the latest stable release" and mention you'd check `developer.android.com` for the current figure, since it changes yearly.

### Q: What is the Dalvik Virtual Machine, and how does it differ from ART?
**Dalvik** was Android's original runtime, used before Android 5.0 (Lollipop). It executed `.dex` (Dalvik Executable) bytecode, a format built for low memory and CPU overhead on mobile hardware.

**ART (Android Runtime)** replaced Dalvik from Android 5.0 onward and is what every modern Android device uses today.

| Aspect | Dalvik (legacy) | ART (current) |
|---|---|---|
| Compilation | JIT — compiled at runtime, on every launch | Hybrid AOT + JIT — precompiled at install/idle time, with runtime profiling |
| App launch speed | Slower (recompiles each run) | Faster (already largely native) |
| Battery/CPU | Higher runtime overhead | Lower runtime overhead |
| Garbage collection | Less efficient, longer pauses | Improved, shorter pauses |
| Status | Deprecated, removed from modern Android | Standard runtime since Android 5.0 |

### Q: How does an Android app actually run on a device?
Source code (Kotlin/Java) compiles to `.dex` bytecode, gets bundled with resources and a manifest into an **APK** (or an **AAB** — Android App Bundle — for Play Store distribution, which Google Play then splits into device-specific APKs). ART executes the `.dex`/OAT-compiled code at runtime, and the system manages the app's process and lifecycle alongside it.

---

## 2. App Components

Every Android app is built from four component types, declared in `AndroidManifest.xml`:

- **Activity** — a single UI screen; the primary entry point for user interaction.
- **Service** — runs in the background with no UI (music playback, sync, long uploads). Types: Foreground, Background, Bound (for inter-process communication).
- **BroadcastReceiver** — listens for system-wide or app-level events (battery low, boot completed, connectivity change) and reacts without needing a UI.
- **ContentProvider** — manages and exposes structured data (a database, files, etc.) through a standard, permission-controlled interface so **other apps** can read/write it safely.

```xml
<application>
    <activity android:name=".MainActivity" />
    <service android:name=".SyncService" />
    <receiver android:name=".BatteryReceiver" />
    <provider
        android:name=".NotesProvider"
        android:authorities="com.example.notes.provider" />
</application>
```

### Q: When would you actually build a ContentProvider?
Only when an **external app** needs controlled, permissioned access to your data (e.g., a contacts app, a file-sharing app). If you're just sharing data within your own app, a repository/singleton backed by Room is simpler and is the modern default.

```kotlin
class NotesProvider : ContentProvider() {
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = db.query("notes", projection, selection, selectionArgs, null, null, sortOrder)
    // insert(), update(), delete(), getType() also required
}
```

### 2.1 Foreground Service — deep dive

**What it is:** a `Service` that performs work the user is actively aware of, and that Android will not kill as readily as a normal background process — in exchange, it **must** show a persistent notification while it runs, so the user always knows something is happening.

**Why apps need it:** since Android 8 (Oreo, API 26), the OS aggressively restricts what plain background services can do to save battery — they get stopped shortly after the app leaves the foreground. If your work needs to keep running *and* is something the user cares about seeing in progress, a foreground Service is the sanctioned way to keep it alive.

**Typical real-world uses:**
- Music/audio or video playback continuing after the user leaves the app.
- Live GPS tracking during a workout, run, or navigation session.
- An active, large file upload/download the user is watching progress on.
- Ongoing screen recording or a active VoIP/call session.

**Since Android 14 (API 34):** you must also declare a specific `foregroundServiceType` (e.g., `mediaPlayback`, `location`, `dataSync`) both in the manifest and when starting the service — Android now enforces that the declared type matches what the service is actually allowed to do, and picking the wrong type can cause a `SecurityException` at runtime.

```kotlin
class TrackingService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking your run")
            .setContentText("Recording your route…")
            .setSmallIcon(R.drawable.ic_run)
            .build()

        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
```

```xml
<service
    android:name=".TrackingService"
    android:foregroundServiceType="location" />

<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
```

**Likely follow-up:** "What happens if you don't call `startForeground()` quickly enough?" — the system throws `ForegroundServiceDidNotStartInTimeException` (Android 14+ enforces this strictly); you must call `startForeground()` within a few seconds of the service starting, or the OS kills it.

### 2.2 AlarmManager — building an alarm clock app

> **TL;DR:** `AlarmManager` schedules the exact wake-up moment; a `BroadcastReceiver` catches it and starts a foreground service to play sound and show a full-screen UI; the user dismisses it to stop the service. Five pieces, one flow.

**Why not WorkManager for this?** As covered in [section 8.2](#82-workmanager--guaranteed-background-work), WorkManager is deliberately imprecise (minimum 15-minute intervals, OS decides exact timing) — it's built for deferrable, battery-friendly work. An alarm clock needs to fire at an *exact* second, so `AlarmManager` is the correct tool instead.

**The end-to-end flow:**

```
AlarmManager (exact time scheduled)
        │
        ▼
AlarmReceiver (BroadcastReceiver fires at exact time)
        │
        ▼
Foreground Service (plays sound, shows notification)
        │
        ▼
Full-screen UI (pops up, even over the lock screen)
        │
        ▼
User taps Dismiss → service stops, alarm is off
```

**Step 1 — Scheduling the exact alarm:**

```kotlin
val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
val intent = Intent(context, AlarmReceiver::class.java).apply {
    putExtra("alarm_id", alarmId)
}
val pendingIntent = PendingIntent.getBroadcast(
    context, alarmId, intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

// setAlarmClock — the most reliable option for a genuine alarm-clock app:
// exempt from Doze entirely, and shows the alarm icon in the status bar
alarmManager.setAlarmClock(
    AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent),
    pendingIntent
)
```

| Method | Exact? | Survives Doze? | Typical use |
|---|---|---|---|
| `set()` / `setAndAllowWhileIdle()` | No / approximate | Partially | Non-critical reminders |
| `setExactAndAllowWhileIdle()` | Yes | Yes | Time-sensitive but not user-facing as a literal clock (e.g. a timed sync) |
| `setAlarmClock()` | Yes | Yes — fully exempt | **A real alarm-clock app** — this is the one you want here |

**Permission note (Android 12+):** exact alarms require the `SCHEDULE_EXACT_ALARM` permission in the manifest. On Android 13+, this is a special permission the user may need to grant manually in system settings unless your app is categorized as an alarm-clock/calendar app — check with `alarmManager.canScheduleExactAlarms()` before scheduling, and guide the user to the settings screen if it returns `false`.

**Step 2 — The receiver that catches the exact-time broadcast:**

```kotlin
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarm_id", -1)
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarm_id", alarmId)
        }
        // Must use startForegroundService() — the app may not be in the foreground
        // when this fires, so a plain startService() call could be rejected by the OS
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
```

**Step 3 — The foreground service that plays sound and posts the full-screen notification:**

```kotlin
class AlarmService : Service() {
    private lateinit var ringtone: Ringtone

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Wake up!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // ← this is what pops the UI over the lock screen
            .build()

        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM))
        ringtone.play()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
```

**Step 4 & 5 — The full-screen UI, and dismissing:**

```kotlin
class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Show over the lock screen and wake the device
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            AlarmScreen(onDismiss = {
                stopService(Intent(this, AlarmService::class.java)) // stops sound + removes notification
                finish()
            })
        }
    }
}
```

**Likely follow-up:** "Does `setAlarmClock()` drain the battery more than WorkManager?" — a little, since it's specifically exempt from the battery-saving deferrals WorkManager relies on — but that's the intentional trade-off: a real alarm-clock app's whole value proposition depends on firing at the exact second, so Android deliberately gives this specific use case an escape hatch from Doze.

---

## 3. UI Basics

### Q: Why is XML traditionally used for Android UI, and is it still the standard?
XML separates layout/design from app logic, is declarative, and supports different screen configurations through qualifiers (`layout-land`, `layout-sw600dp`, etc.). It's still fully supported, but **Jetpack Compose (Kotlin-based, declarative UI) is now Google's recommended approach for new projects** — see [section 10](#10-jetpack-compose).

### Q: What is a `View` vs. a `ViewGroup`?

| | View | ViewGroup |
|---|---|---|
| Definition | Basic UI building block | Container that holds Views/ViewGroups |
| Role | Displays content, handles input | Arranges and manages child views |
| Can nest children? | No | Yes |
| Examples | `TextView`, `Button`, `ImageView` | `LinearLayout`, `ConstraintLayout`, `FrameLayout` |

### Q: What is a `Toast`?
A short-lived, non-interactive popup message shown for brief feedback (e.g., "Saved successfully"). It requires no user action and dismisses itself automatically.

```kotlin
Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
```

---

## 4. Activity & Fragment Lifecycle

### Q: Walk through the Activity lifecycle.

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // load layout, one-time setup
    }
    override fun onStart() { super.onStart() }     // becoming visible
    override fun onResume() { super.onResume() }   // interactive, foreground
    override fun onPause() { super.onPause() }     // losing focus (e.g. dialog shown over it)
    override fun onStop() { super.onStop() }       // fully hidden
    override fun onRestart() { super.onRestart() } // returning from Stopped, before onStart()
    override fun onDestroy() { super.onDestroy() } // final cleanup
}
```

**`onPause()` vs `onStop()`:** `onPause()` fires when the activity loses focus but may still be partly visible. `onStop()` fires once it's completely hidden.

**Why `setContentView()` in `onCreate()`?** Because `onCreate()` runs once, when the activity is first constructed — it's the correct place to inflate and attach the layout before the user can interact with anything.

### Q: Walk through the Fragment lifecycle.

| Callback | Purpose |
|---|---|
| `onAttach()` | Fragment attached to its host Activity |
| `onCreate()` | Initialize non-view state |
| `onCreateView()` | Inflate and return the Fragment's view |
| `onViewCreated()` | View hierarchy is ready — safe to find views, set listeners, observe ViewModel data |
| `onStart()` / `onResume()` | Becoming visible / interactive |
| `onPause()` / `onStop()` | Losing focus / no longer visible |
| `onDestroyView()` | Clean up view references (avoid leaks) |
| `onDestroy()` / `onDetach()` | Final Fragment cleanup / detach from Activity |

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
        // handle click — view hierarchy is guaranteed ready here
    }
}
```

### Q: Fragment vs. Activity?

| | Activity | Fragment |
|---|---|---|
| Represents | A full screen | A reusable piece of a screen |
| Can exist alone? | Yes | No — needs a host Activity |
| Reusability | Low | High (reused across screens/layouts) |
| Lifecycle | Independent | Tied to host Activity's lifecycle |

---

## 5. Intents

### Q: Intent vs. Intent Filter?

An **Intent** is a message object requesting an action (start an Activity/Service, deliver a broadcast). An **Intent Filter** (declared in the manifest) declares which intents a component is willing to handle.

| | Intent | Intent Filter |
|---|---|---|
| Purpose | Requests an action | Declares what a component can receive |
| Declared in | Code | `AndroidManifest.xml` |
| Example | Opening another screen | Letting your app open a specific URL scheme |

### Q: Implicit vs. Explicit Intent — what's the difference, and when do you use each?

| | Explicit Intent | Implicit Intent |
|---|---|---|
| Target | Names the exact component (class) to start | Declares an action/data; the system finds a matching component |
| Used for | Navigating within **your own app** | Delegating to **another app** (yours or a third party) to handle something |
| Resolution | Direct — no ambiguity | Android matches it against installed apps' Intent Filters, and may show a chooser if multiple match |
| Risk | None specific to resolution | Can fail with `ActivityNotFoundException` if no app can handle it — should be handled defensively |

```kotlin
// Explicit intent — you know exactly which component should handle this
val intent = Intent(this, DetailActivity::class.java).putExtra("id", itemId)
startActivity(intent)

// Implicit intent — let the system/user pick a capable app
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "Check this out!")
}

// Defensive check before launching an implicit intent
if (shareIntent.resolveActivity(packageManager) != null) {
    startActivity(Intent.createChooser(shareIntent, "Share via"))
} else {
    // no app available to handle this — show a fallback message
}
```

**Likely follow-up:** "Why does `resolveActivity()` sometimes return null even when an app is installed that could handle it?" — since Android 11 (API 30), apps must declare a `<queries>` element in their manifest listing the packages/intents they need visibility into; without it, `PackageManager` queries like `resolveActivity()` won't see otherwise-matching apps, as a privacy protection against apps silently enumerating everything else installed on the device.

---

## 6. RecyclerView

### Q: What is RecyclerView and how does it stay efficient with large lists?
It displays large, scrollable datasets by **recycling** off-screen item views instead of creating new ones, built around three pieces: `Adapter`, `ViewHolder`, `LayoutManager`.

```kotlin
class ItemsAdapter : ListAdapter<Item, ItemsAdapter.ItemViewHolder>(DiffCallback()) {
    class ItemViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.title.text = getItem(position).name
    }
    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
        override fun areContentsTheSame(old: Item, new: Item) = old == new
    }
}
```

### Q: How would you improve RecyclerView scrolling performance?
- Use `DiffUtil` / `ListAdapter` and targeted calls (`notifyItemInserted()`, etc.) instead of `notifyDataSetChanged()`.
- Set fixed `ImageView` dimensions to avoid layout recalculation; load images with Coil/Glide.
- Call `setHasFixedSize(true)` when the RecyclerView's own size won't change.
- Avoid heavy work (DB calls, calculations) inside `onBindViewHolder()`.
- Avoid nesting a RecyclerView inside another — use `ConcatAdapter` instead.

### Q: How is RecyclerView actually more efficient than the older ListView?

Both are technically capable of recycling off-screen views — the real difference is that **RecyclerView enforces the efficient pattern by design, while ListView only recycles efficiently if the developer does it right manually.**

| | `ListView` | `RecyclerView` |
|---|---|---|
| View recycling | Recycling is *possible* via `convertView` in `getView()`, but it's **optional** — easy to forget, and a huge number of real ListView implementations call `findViewById()` on every single `getView()` call because the ViewHolder pattern wasn't enforced | The **`ViewHolder` pattern is mandatory**, built into the framework itself — `onCreateViewHolder()`/`onBindViewHolder()` structurally forces you to find views once and reuse them, not per bind |
| Layout direction | Vertical list only | Pluggable via `LayoutManager` — `LinearLayoutManager` (vertical or horizontal), `GridLayoutManager`, `StaggeredGridLayoutManager`, or a custom one |
| Item animations | Manual — no built-in support | Built-in `ItemAnimator`, plus automatic add/remove/move animations when paired with `DiffUtil` |
| Partial/targeted updates | Effectively just `notifyDataSetChanged()` — re-binds every visible row regardless of what actually changed | `DiffUtil`/`ListAdapter` compute the minimal diff and fire targeted calls (`notifyItemInserted()`, etc.), so only the rows that actually changed get rebound |
| Decoupling | Adapter and layout logic are more tangled together | Adapter, `LayoutManager`, and `ItemAnimator` are separate, swappable pieces |

**The one-sentence version for an interview:** ListView *can* be made efficient by manually implementing the ViewHolder pattern yourself inside `getView()`, but nothing forces you to — RecyclerView bakes that requirement into its architecture, and adds pluggable layouts, built-in animations, and `DiffUtil`-driven partial updates on top, which is why it replaced ListView as the standard.

---

## 7. Kotlin & Coroutines

### 7.1 What are Kotlin Coroutines?
A lightweight concurrency framework for asynchronous work (network calls, DB access, file I/O) that avoids blocking the main thread. Coroutines **suspend** instead of **block** — when a coroutine hits a suspension point (e.g., waiting on a network response), it frees up the underlying thread to do other work instead of parking it. This is why you can run thousands of coroutines on a handful of real threads, whereas raw Java threads are comparatively heavy (each one reserves its own stack memory and OS scheduling overhead) and can't scale the same way.

```kotlin
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = withContext(Dispatchers.IO) { repository.getUser(id) }
                _uiState.value = UiState.Success(user)
            } catch (e: IOException) {
                _uiState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }
}
```

---

### 7.2 CoroutineScope — where a coroutine's lifetime lives

A `CoroutineScope` defines the **lifetime boundary** for the coroutines launched inside it — this is the backbone of **structured concurrency**: a parent scope being cancelled automatically cancels every coroutine launched within it, so you can't accidentally leak a running coroutine that's outlived the screen that started it.

| Scope | Tied to | Auto-cancelled when | Typical use |
|---|---|---|---|
| **`viewModelScope`** | The `ViewModel` | `onCleared()` fires (ViewModel destroyed) | Any coroutine work started from a ViewModel |
| **`lifecycleScope`** | The Activity/Fragment `Lifecycle` | The lifecycle reaches `DESTROYED` | One-off work directly tied to a screen (rare if you're using a ViewModel properly) |
| **`repeatOnLifecycle(STARTED)`** (used inside `lifecycleScope`) | The lifecycle being at least `STARTED` | Lifecycle drops below `STARTED` (and restarts the block when it comes back) | Safely collecting a `Flow`/`StateFlow` in the UI layer without leaking or missing emissions |
| **`GlobalScope`** | The **application process** — nothing shorter | Only when the process dies | Rare — genuinely app-wide, fire-and-forget work with no natural owner |
| **Custom scope** (`CoroutineScope(SupervisorJob() + Dispatchers.Default)`) | Whatever object you attach it to (a repository, a shared/KMP class) | Only when you manually call `.cancel()` | Non-Android modules, repositories, or shared KMP code with no `ViewModel`/`Lifecycle` available |

```kotlin
// Safe Flow collection in the UI layer — the standard modern pattern
class ProfileFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }
}
```

### Q: What is `onCleared()`, and how does it relate to `viewModelScope`?

`onCleared()` is called by the framework when a `ViewModel` is being **permanently** destroyed and will never be used again — not on a configuration change (that's the entire point of `ViewModel` surviving those), but when the owning screen is actually finishing for good, or its back stack entry is popped off entirely in Navigation.

```kotlin
class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {
    private val connection = repo.openLiveConnection()

    override fun onCleared() {
        super.onCleared()
        connection.close() // cleanup — this ViewModel is gone for good
    }
}
```

`viewModelScope` is built on top of this: internally, it's a `CoroutineScope` that the ViewModel KTX library automatically cancels the instant `onCleared()` fires — so every coroutine you launched with `viewModelScope.launch { ... }` is cancelled for free at that same moment, with no manual `.cancel()` call needed.

**Key distinction for interviews:** rotate the screen → ViewModel survives, `onCleared()` does **not** fire. Actually leave the screen (back press, finish, nav pop) → `onCleared()` fires, ViewModel and its `viewModelScope` are both gone.

**Likely follow-up:** "Why is `GlobalScope` generally discouraged?" — because it defeats structured concurrency entirely: a coroutine launched in `GlobalScope` has no parent to cancel it, so it keeps running even after the screen/feature that started it is long gone, and you're fully responsible for its cancellation and error handling yourself. It's rarely the right default.

---

### 7.3 Dispatchers — which thread pool a coroutine runs on

A `Dispatcher` decides which thread (or thread pool) a coroutine actually executes on. Coroutines don't hard-bind to one dispatcher for their whole lifetime — `withContext()` lets you hop between them mid-coroutine.

| Dispatcher | Backed by | Use for | Notes |
|---|---|---|---|
| **`Dispatchers.Main`** | The single Android UI thread | Updating views, anything touching the UI | Only one thread — never do blocking work here |
| **`Dispatchers.IO`** | A large, elastic thread pool (64+ threads, grows as needed) | Network calls, disk/file access, database queries | Optimized for many threads mostly *waiting*, not computing |
| **`Dispatchers.Default`** | A thread pool sized to the number of CPU cores | CPU-intensive work: sorting, JSON/image parsing, complex calculations | Optimized for actual computation, not for blocking I/O |
| **`Dispatchers.Unconfined`** | No dedicated pool — starts in the caller's thread, resumes on whatever thread the previous suspension used | Rare; some testing/internal scenarios | Not confined to any single thread — generally avoid using it directly in app code |

```kotlin
suspend fun loadAndProcess(): Result {
    val raw = withContext(Dispatchers.IO) { api.fetchRawData() }      // network — IO
    val processed = withContext(Dispatchers.Default) { parse(raw) }   // CPU-bound — Default
    return processed // caller resumes on whatever dispatcher it was already on (e.g. Main)
}
```

**Likely follow-up:** "What happens if you run a network call on `Dispatchers.Default` instead of `IO`?" — it still works, but it's wasteful: `Default`'s pool is deliberately small (CPU-core-sized) since it's meant for computation, so blocking those few threads waiting on network I/O can starve other CPU-bound work. `IO`'s much larger pool exists specifically because I/O threads spend most of their time waiting, not computing.

---

### 7.4 `launch` vs. `async`, and exception propagation

- **`launch`** — fire-and-forget; returns a `Job`, no result needed (UI updates, logging, side effects).
- **`async`** — returns a `Deferred<T>` you `await()` for a result (parallel data fetches, DB queries).

**Exception handling differs between them:**
- In `launch`, an uncaught exception propagates immediately up to its parent scope (and, by default, cancels sibling coroutines under a plain `Job`).
- In `async`, an exception is **held inside the `Deferred`** and only thrown when you call `.await()` — if you never call `await()`, the exception can silently disappear.

### ❌ Wrong — `async` is launched but never awaited

```kotlin
fun loadUserWrong(id: String) {
    viewModelScope.launch {
        async { api.getUser(id) }
        // If getUser() throws here, the exception is trapped inside the
        // Deferred and just disappears — no crash, no log, nothing.
    }
}
```

### ✅ Right — always `await()` the result, inside a `try/catch`

```kotlin
fun loadUserRight(id: String) {
    viewModelScope.launch {
        try {
            val user = async { api.getUser(id) }.await()  // exception surfaces HERE
            _uiState.value = UiState.Success(user)
        } catch (e: IOException) {
            _uiState.value = UiState.Error(e.message ?: "Failed to load user")
        }
    }
}
```

**Simplest fix of all:** if you don't actually need a return value, just use `launch` instead of `async { }` in the first place — then the "forgot to `await()`" trap can't happen, since `launch` throws immediately instead of hiding the error in a `Deferred` nobody's holding onto.

```kotlin
// Job (default): one child failing cancels the whole scope, including siblings
viewModelScope.launch {
    launch { riskyCallA() }  // if this throws...
    launch { riskyCallB() }  // ...this gets cancelled too
}

// SupervisorJob: children fail independently — one failure doesn't cancel the others
val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
scope.launch { riskyCallA() }  // if this throws, only this one fails
scope.launch { riskyCallB() }  // this keeps running regardless
```

**`CoroutineExceptionHandler`** — a global catch-all for uncaught exceptions from `launch` (not `async`, since `async` defers the exception to `await()` instead):

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    Log.e("Coroutine", "Uncaught: ${exception.message}")
}
viewModelScope.launch(handler) { riskyCall() }
```

---

### 7.5 Cancellation is cooperative

**Simple way to think about it:** calling `job.cancel()` isn't like force-killing a process — it's more like **sending a polite request**: "please stop when you get a chance." The coroutine only actually stops if its code checks for that request and honors it.

| | Behaves like... |
|---|---|
| Cancelling a **Thread** (Java-style `interrupt()`) | Roughly forced — the thread gets interrupted whether it's checking or not |
| Cancelling a **Coroutine** (`job.cancel()`) | A request only — the coroutine must check for it itself, or it just keeps running |

**Where does that check happen automatically?**
- Any `suspend` function that actually suspends — `delay()`, a Retrofit call, a Room query — checks for cancellation for you, for free, every time it suspends.
- A **plain, tight loop doing pure computation** (no `delay()`, no network/DB call inside it) never suspends, so it never gets a chance to notice the cancellation — it will happily run to completion even after `.cancel()` was called.

### ❌ Wrong — a heavy loop that ignores cancellation

```kotlin
val job = viewModelScope.launch {
    repeat(1_000_000) { i ->
        heavyComputation(i) // pure CPU work, no suspension point — never checks for cancellation
    }
}
job.cancel() // requested... but the loop above has no idea, and keeps running to completion anyway
```

### ✅ Right — manually check for cancellation inside the loop

```kotlin
val job = viewModelScope.launch {
    repeat(1_000_000) { i ->
        ensureActive() // manually checks: "has cancellation been requested? if so, stop now"
        heavyComputation(i)
    }
}
job.cancel() // this time, the very next ensureActive() check actually stops the loop
```

### Q: What is `ensureActive()` actually doing internally?

```kotlin
// Roughly what ensureActive() does under the hood
fun ensureActive() {
    if (!isActive) {                                     // check the Job's own status flag
        throw CancellationException("Job was cancelled")  // if cancelled, throw right here
    }
    // if still active: do nothing, just return and let your code continue
}
```

Two moving parts:
1. **`isActive`** — a boolean the `Job` maintains internally, flipped to `false` the moment `.cancel()` is called (or a parent coroutine fails/cancels).
2. **`throw CancellationException(...)`** — if that flag is `false`, it throws immediately, unwinding the coroutine right at that line (running any `finally` blocks on the way out, like any other exception).

**Why throw instead of returning a boolean?** So you don't have to write `if (!isActive) return` by hand after every check — `ensureActive()` does that unwinding for you, in one line.

**Note on `CancellationException`:** it's treated specially — the "normal, expected" way a coroutine stops, not a crash. It won't trigger your `CoroutineExceptionHandler`. If you ever catch it yourself, convention is to rethrow it rather than swallow it, or you'll break cancellation for the rest of that coroutine tree.

**One-line takeaway:** if your coroutine only calls `suspend` functions (network/DB/`delay`), cancellation "just works." The moment you write a tight, non-suspending loop doing real computation, you're responsible for checking `ensureActive()` (or `isActive`) yourself — otherwise `.cancel()` silently does nothing until the loop finishes on its own.

---

### 7.6 Flow: Cold vs. Hot

This distinction comes up constantly in interviews because it explains *why* `Flow`, `StateFlow`, and `SharedFlow` behave so differently.

- **Cold** — the producer code doesn't run at all until something collects it, and it runs **fresh, independently, from the start** for every single collector. Plain `flow { ... }` builders are cold.
- **Hot** — the producer runs (or the value already exists) independently of whether anyone is collecting, and multiple collectors share the **same** ongoing stream of emissions rather than each triggering their own run. `StateFlow` and `SharedFlow` are hot.

```kotlin
// ── 1. Flow (COLD) — two collectors get two INDEPENDENT runs ──────────────
val coldFlow = flow {
    println("Starting fresh production run")
    emit(1)
    emit(2)
}

coldFlow.collect { println("Collector A got $it") }
// prints: "Starting fresh production run", "Collector A got 1", "Collector A got 2"

coldFlow.collect { println("Collector B got $it") }
// prints: "Starting fresh production run" AGAIN, "Collector B got 1", "Collector B got 2"
// → the producer block ran TWICE, once per collector, from scratch each time
```

```kotlin
// ── 2. StateFlow (HOT) — two collectors SHARE one stream, no history replay ──
val stateFlow = MutableStateFlow(0)

launch { stateFlow.collect { println("Collector A sees $it") } }
launch { stateFlow.collect { println("Collector B sees $it") } }

stateFlow.value = 1 // ONE emission — both A and B receive it
stateFlow.value = 2 // ONE emission — both A and B receive it

// Output (A/B order may interleave, but both always see the same values):
// Collector A sees 1   Collector B sees 1   Collector A sees 2   Collector B sees 2
//
// A collector that joins LATE only sees the current value onward — e.g. if a
// third collector started right after stateFlow.value = 2, it would see 2,
// never the earlier 0 or 1. No history is kept, only the latest value.
```

```kotlin
// ── 3. SharedFlow (HOT) — also SHARED, but with configurable replay ─────────
val sharedFlow = MutableSharedFlow<Int>(replay = 1) // keeps the last 1 value for late joiners

launch { sharedFlow.collect { println("Collector A sees $it") } }
sharedFlow.emit(1) // A sees 1

launch { sharedFlow.collect { println("Collector B sees $it") } } // joins LATE
// Because replay = 1, B immediately receives the last emitted value too:
// Collector B sees 1   ← B did NOT miss this, thanks to replay = 1

sharedFlow.emit(2) // now BOTH A and B receive 2 — same shared stream, same as StateFlow's behavior
```

**The clear picture, all three side by side:**

| | `Flow` (cold) | `StateFlow` (hot) | `SharedFlow` (hot) |
|---|---|---|---|
| Two collectors → how many producer runs? | **Two** — fully independent, each from scratch | **One** — both watch the same single stream | **One** — both watch the same single stream |
| What does a *late* collector receive? | N/A — it just starts its own fresh run | Only the current value onward — no history | Configurable via `replay` — can receive the last N buffered values, or none if `replay = 0` |
| Mental model | "Everyone gets their own private screening" | "Everyone's watching the same live broadcast, tuning in from now" | "Same live broadcast, but latecomers can rewind up to `replay` moments" |

**Other differences worth knowing:**

| | `Flow` (cold) | `StateFlow` (hot) | `SharedFlow` (hot) |
|---|---|---|---|
| Always has a current value? | No | Yes — requires an initial value | No — configurable replay (0 or more) |
| Conflates rapid updates? | N/A | Yes — only the latest value matters to a new/slow collector | Configurable (`replay`, `extraBufferCapacity`) |
| Typical use | One-shot or per-collector async sequences (a single network call, a paginated query) | Observable UI state (there's always a "current" state) | One-off events (snackbar messages, navigation events) that shouldn't replay indefinitely |

**Likely follow-up:** "Why is `StateFlow` a bad fit for one-time events like 'show a snackbar'?" — because it's conflated and always holds a *current* value: a late collector (e.g., after a config change) immediately receives whatever the last value was, which would re-trigger an event that already happened once. `SharedFlow` (with `replay = 0`) is the better fit for genuinely one-shot events.

---

### 7.7 Common Flow Operators

| Operator | What it does |
|---|---|
| `map` | Transforms each emitted value |
| `filter` | Drops values that don't match a predicate |
| `debounce(ms)` | Waits for a pause in emissions before passing the latest one through (e.g. search-as-you-type) |
| `distinctUntilChanged()` | Skips consecutive duplicate values |
| `flatMapLatest` | Switches to a new inner Flow whenever the source emits, cancelling the previous inner Flow's collection |
| `combine` | Merges the *latest* values from multiple Flows whenever any one of them emits |
| `zip` | Pairs up emissions from two Flows by matching index/order |
| `catch` | Intercepts upstream exceptions without crashing the collector |
| `retry` / `retryWhen` | Re-subscribes to the upstream Flow on failure, optionally with a condition/backoff |

```kotlin
val searchResults: Flow<List<Result>> = queryFlow
    .debounce(300)
    .distinctUntilChanged()
    .flatMapLatest { query -> repo.search(query).catch { emit(emptyList()) } }
```

---

### 7.8 Serial vs. Parallel API Calls

**Serial (sequential)** — each `suspend` call is awaited before the next one starts. Total time ≈ the **sum** of every call's duration.

```kotlin
suspend fun loadProfileScreen(userId: String): ProfileScreenData {
    val profile = api.getProfile(userId)   // waits ~300ms
    val posts = api.getPosts(userId)       // then waits ~400ms
    val friends = api.getFriends(userId)   // then waits ~200ms
    return ProfileScreenData(profile, posts, friends) // total ≈ 900ms
}
```

**Parallel** — fire all the calls at once with `async`, then `await()` them together. Total time ≈ the **slowest** individual call, not the sum.

```kotlin
suspend fun loadProfileScreen(userId: String): ProfileScreenData = coroutineScope {
    val profileDeferred = async { api.getProfile(userId) }   // ~300ms
    val postsDeferred = async { api.getPosts(userId) }       // ~400ms
    val friendsDeferred = async { api.getFriends(userId) }   // ~200ms
    ProfileScreenData(
        profile = profileDeferred.await(),
        posts = postsDeferred.await(),
        friends = friendsDeferred.await()
    ) // total ≈ 400ms — bounded by the slowest call, not the sum
}
```

**`coroutineScope` vs. `supervisorScope` here matters:** with `coroutineScope`, if *any* of the three calls throws, the whole block fails and the others are cancelled. With `supervisorScope`, you can let one call fail independently (e.g., catch it per-`async` and fall back to a default) while still getting results from the others — useful when a screen can render partially even if one data source fails.

```kotlin
suspend fun loadProfileScreenResilient(userId: String): ProfileScreenData = supervisorScope {
    val profile = async { api.getProfile(userId) }
    val posts = async { runCatching { api.getPosts(userId) }.getOrDefault(emptyList()) }
    val friends = async { runCatching { api.getFriends(userId) }.getOrDefault(emptyList()) }
    ProfileScreenData(profile.await(), posts.await(), friends.await())
}
```

---

### 7.9 Switching / Changing the Data Source Dynamically

Two distinct things interviewers might mean by this — worth being ready for either:

**A) Switching which upstream source is collected when an input changes** (e.g., the user changes a filter, toggles "local only" vs. "remote", or a search query changes). Use `flatMapLatest`, which automatically cancels the previous inner Flow's collection the moment a new one starts — so you never end up processing a stale source.

```kotlin
enum class SourceMode { LOCAL, REMOTE }
val sourceModeFlow = MutableStateFlow(SourceMode.LOCAL)

val itemsFlow: Flow<List<Item>> = sourceModeFlow.flatMapLatest { mode ->
    when (mode) {
        SourceMode.LOCAL -> localDb.observeItems()      // switch to Room's Flow
        SourceMode.REMOTE -> remoteApi.observeItems()    // switch to a network-polling Flow
    }
}
// Changing sourceModeFlow.value automatically cancels the old source's collection
// and starts collecting the new one — no manual unsubscribe/resubscribe needed.
```

**B) The "Single Source of Truth" pattern** — the standard modern Android approach for combining a local cache with a remote source, so the UI never talks to the network directly:

```kotlin
class NoteRepository(private val dao: NoteDao, private val api: NoteApi) {

    // The UI only ever observes Room — Room IS the single source of truth
    fun observeNotes(): Flow<List<Note>> = dao.observeNotes()

    // Refreshing means: fetch remote, write into Room, and Room's Flow re-emits automatically
    suspend fun refresh() {
        val remoteNotes = api.getNotes()
        dao.insertAll(remoteNotes)
    }
}
```
This avoids ever having two competing in-memory copies of the same data (one from network, one from cache) get out of sync — the UI layer only needs to know about Room's `Flow`, and refreshing is just "write to the database, let the existing Flow do the rest."

**Likely follow-up:** "What's the risk of just swapping data sources with a plain `flatMapConcat` instead of `flatMapLatest`?" — `flatMapConcat` processes inner flows one at a time, in order, without cancelling earlier ones — so a slow, now-irrelevant old source could still emit and finish *after* a newer one has already started, delivering stale data late. `flatMapLatest` is specifically designed to avoid that by cancelling outdated work.

---

### 7.10 Testing Coroutines & Flow

Covered in full in [section 15 (Testing)](#15-testing) — the short version: use `runTest` to run suspending test bodies on a virtual-time test dispatcher (so `delay()` doesn't actually slow down your tests), and a rule like `MainDispatcherRule` to swap `Dispatchers.Main` for a test dispatcher during unit tests. For `Flow`, the **Turbine** library (`flow.test { awaitItem(); awaitComplete() }`) is the standard way to assert on emissions without manually collecting into a list.

---

### 7.11 Other Kotlin Language Features

### Q: What is an inline function, and why use one?
A function whose body is copied directly into the call site at compile time, avoiding function-call/lambda-object overhead — commonly used with higher-order functions.

```kotlin
inline fun greet(name: String) {
    println("Hello, $name")
}
```

### Q: What do `@JvmStatic`, `@JvmOverloads`, and `@JvmField` do?
They smooth Kotlin/Java interop:
- **`@JvmStatic`** — exposes a companion-object method as a true static method in Java.
- **`@JvmOverloads`** — generates overloaded Java methods for a Kotlin function's default parameters.
- **`@JvmField`** — exposes a Kotlin property as a plain public Java field, skipping getter/setter generation.

### Q: What is Android KTX?
A set of Kotlin extension functions over the Android/Jetpack APIs that cut boilerplate (e.g., `sharedPrefs.edit { putString(...) }` instead of manual `Editor` handling). Comes bundled with most modern `androidx` libraries.

---

## 8. Jetpack & Architecture Components

### Q: What is Android Jetpack?
A set of Google-provided libraries, tools, and guidance (launched 2018) to build robust apps faster: reduces boilerplate, standardizes architecture, and provides backward compatibility through the `androidx.*` package (which replaced the old Support Library).

**Categories:**
- **Foundation** — AppCompat, Android KTX, Multidex, Test
- **Architecture** — Room, WorkManager, Lifecycle, ViewModel, Paging, Navigation
- **Behavior** — WorkManager/DownloadManager, Permissions, Sharing, Slices
- **UI** — Fragment, Animation & Transition, Layout, Compose

### Q: Name the core Architecture Components and what each does.

| Component | Purpose |
|---|---|
| **Room** | Type-safe abstraction over SQLite |
| **WorkManager** | Reliable, deferrable background work (survives app close/reboot) |
| **Lifecycle** | Makes components aware of Activity/Fragment lifecycle state |
| **ViewModel** | Holds UI state; survives configuration changes |
| **LiveData** | Observable, lifecycle-aware data holder that updates the UI automatically |
| **Navigation** | Manages in-app navigation and the back stack |
| **Paging** | Loads large datasets in chunks |
| **Data Binding** | Binds XML views directly to data sources |

### 8.1 Room — local database

Room sits on top of SQLite, giving you compile-time-checked queries and direct integration with `LiveData`/`Flow` so the UI updates automatically when the underlying data changes.

```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeNotes(): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note)
}

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
```
**Likely follow-up:** "What happens if you change the schema?" — you must bump the `version` and either provide a `Migration` or, in dev builds only, allow destructive migration (`fallbackToDestructiveMigration()`).

### 8.2 WorkManager — guaranteed background work

Used for deferrable work that must run even if the app is closed or the device restarts (e.g., uploading logs, syncing data) — unlike a plain coroutine or thread, which dies with the process.

```kotlin
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
    .setConstraints(
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    )
    .build()

WorkManager.getInstance(context).enqueue(syncRequest)
```
**Likely follow-up:** "WorkManager vs. a foreground Service?" — WorkManager is for deferrable, guaranteed work where exact timing doesn't matter; a foreground Service is for work the user needs to see is actively happening right now (e.g., music playback, an active GPS-tracked run). See [section 2.1](#21-foreground-service-deep-dive) for the full comparison.

### Q: Give three real-world examples of what an app would use WorkManager for.

1. **Periodic cloud backup/sync** — e.g., a photo app backing up new images to the cloud only when the device is charging and on Wi-Fi (`Constraints` + `PeriodicWorkRequest`), without needing the app open.
2. **Uploading crash/analytics logs after the fact** — batching and sending diagnostic data once network is available, even if the user already closed the app right after the event occurred.
3. **Post-processing deferred content** — e.g., compressing a video the user just recorded and uploading it in the background, with automatic retry (`Result.retry()`) if the upload fails mid-transfer.

Common thread across all three: the work **must eventually complete**, doesn't need to happen the instant it's requested, and should survive the app process being killed — exactly what WorkManager is built for (it delegates to `JobScheduler`/`AlarmManager`/a background thread depending on API level and constraints).

### 8.3 Lifecycle — lifecycle-aware components

Lets a class observe an Activity/Fragment's lifecycle state directly, instead of manually overriding `onStart()`/`onStop()` everywhere — this is the mechanism `LiveData` and `viewModelScope` are built on.

```kotlin
class LocationTracker(lifecycle: Lifecycle) : DefaultLifecycleObserver {
    init { lifecycle.addObserver(this) }

    override fun onStart(owner: LifecycleOwner) { startTracking() }
    override fun onStop(owner: LifecycleOwner) { stopTracking() } // auto-unregisters, avoids leaks
}
```
**Likely follow-up:** "Why does this prevent memory leaks?" — because the observer's cleanup is tied to the lifecycle event automatically; you can't forget to call `stopTracking()` since the framework calls `onStop()` for you.

### 8.4 ViewModel — UI state across configuration changes

Survives configuration changes (e.g., screen rotation) because it's scoped to the Activity/Fragment's `ViewModelStore`, not the Activity instance itself, so rotating the screen doesn't destroy and re-fetch its data.

```kotlin
class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(profile = repo.getProfile(userId))
        }
    }
}
```
**Likely follow-up:** "Does ViewModel survive process death?" — No. Configuration changes only. For process death (system kills the app in the background), you need `SavedStateHandle` to persist small bits of UI state.

### 8.5 LiveData — observable, lifecycle-aware data

Automatically stops notifying observers that are in a destroyed/stopped state, which avoids both memory leaks and crashes from updating a dead UI.

### Q: How exactly does LiveData decide which observers get notified, and when?

- **"Active" has a precise definition:** a `LifecycleOwner` (Activity/Fragment) counts as active only while its state is `STARTED` or `RESUMED`. An observer paired with a lifecycle only receives updates while its owner is in one of those two states — not while merely created or already stopped.
- **`observe(lifecycleOwner, observer)`** — the normal, recommended way to subscribe. Because it's tied to a `LifecycleOwner`, Android automatically removes the observer the moment that lifecycle reaches `DESTROYED`, so an Activity/Fragment can observe without ever manually unsubscribing or worrying about leaking a reference to itself.
- **`observeForever(observer)`** — subscribes with no lifecycle attached at all, so it's treated as permanently active and keeps receiving updates regardless of any screen's state. Because nothing removes it automatically, **you're responsible for calling `removeObserver()` yourself** (e.g., when a singleton/service you're using it from is torn down) — skipping this is a real leak source.
- **`onActive()` / `onInactive()`** — lifecycle callbacks on the `LiveData` itself (not the observer), fired when the count of active observers transitions between 0 and 1. This lets a `LiveData` implementation start expensive work (a sensor listener, a location callback) only while someone is actually watching, and tear it down again once the last active observer disappears — rather than running constantly regardless of whether the UI is even visible.

**Likely follow-up:** "Where does LiveData fit — only inside a ViewModel?" — its most common home is exposing a `ViewModel`'s fields to the UI, but it's a general-purpose observable holder: it can also be used to pass data between otherwise-decoupled parts of an app (e.g., two modules that shouldn't reference each other directly) without either side needing to know who's on the other end.

### Q: `MutableLiveData` vs. `LiveData` — what's the difference?
They're the same underlying holder — `MutableLiveData` is the read-write subclass, `LiveData` is its read-only base type. The standard pattern is to expose the **mutable** version privately (so only the ViewModel can change it) and the **immutable** `LiveData` publicly (so the UI can only observe, never set a value directly). This is the same encapsulation pattern used for `MutableStateFlow`/`StateFlow`.

```kotlin
class ProfileViewModel : ViewModel() {
    private val _profile = MutableLiveData<Profile>()   // ViewModel-internal, read-write
    val profile: LiveData<Profile> = _profile            // exposed to UI, read-only

    fun load(id: String) { _profile.value = repository.getProfile(id) }
}

// In the Fragment/Activity
viewModel.profile.observe(viewLifecycleOwner) { profile ->
    binding.nameText.text = profile.name
}
```
**Likely follow-up:** "Why not just expose `MutableLiveData` directly?" — because then any class holding a reference to it (including the View layer) could call `.setValue()`/`.postValue()` and mutate state from outside the ViewModel, breaking the single-source-of-truth principle that MVVM/UDF relies on.

### Q: `setValue()` vs. `postValue()` on `MutableLiveData` — what's the difference?

| | `setValue()` | `postValue()` |
|---|---|---|
| Which thread can call it | **Main/UI thread only** — throws `IllegalStateException` if called off the main thread | **Any thread**, including background threads |
| When the value actually updates | Immediately, synchronously | Asynchronously — schedules the update to run on the main thread shortly after |
| Multiple rapid calls | Each call updates observers in order | If called several times before the main thread processes the post, **only the last value wins** — the earlier ones are overwritten, not queued |

```kotlin
// From the main thread — fine
liveData.setValue(newProfile)

// From a background thread (e.g. inside a Room callback or a repository coroutine on Dispatchers.IO)
liveData.postValue(newProfile)   // setValue() here would crash
```

**Likely follow-up:** "What happens if you call `postValue()` three times in quick succession from a background thread before the main thread catches up?" — only the **last** of the three values is delivered to observers; the first two are silently dropped, since `postValue()` isn't a queue — it just overwrites whatever pending value hasn't been dispatched yet. This is a common gotcha when someone expects every intermediate value (e.g., incremental progress updates) to arrive.

### Q: LiveData vs. StateFlow — when should you use which?

| | `LiveData` | `StateFlow` |
|---|---|---|
| Origin | Android Jetpack-specific | Plain Kotlin coroutines (`kotlinx.coroutines`) |
| Works outside Android? | No | Yes — usable in shared/KMP modules, plain Kotlin, backend code |
| Lifecycle awareness | Built in — only notifies active (started+) observers automatically | Not automatic — needs `repeatOnLifecycle(STARTED)` or `collectAsStateWithLifecycle()` in Compose to avoid updating a stopped UI |
| Always has a value | Yes, once set | Yes — requires an initial value at creation |
| Operators (map, combine, debounce, etc.) | Limited | Full Kotlin Flow operator set |
| Best fit | XML-based Views, legacy/existing LiveData codebases | Compose-based UI, coroutine-heavy repositories, any shared/multiplatform logic |

**Rule of thumb for an interview answer:** if you're starting a new screen today, especially one using Compose or sharing logic across platforms, reach for `StateFlow`. Reach for `LiveData` mainly when you're already in an XML/View-based codebase that uses it consistently, or maintaining existing code — not because it's "wrong," just because `StateFlow` is now the more flexible default for new work.

### 8.6 Navigation — screen navigation and back stack

Manages fragment/destination transactions, the back stack, and argument passing through a single `NavGraph`, instead of manual `FragmentTransaction` calls scattered through the app.

```kotlin
// navigate with type-safe arguments (Safe Args)
findNavController().navigate(
    ProfileFragmentDirections.actionProfileToSettings(userId = "42")
)

// In Compose
NavHost(navController, startDestination = "profile") {
    composable("profile") { ProfileScreen(onSettingsClick = { navController.navigate("settings") }) }
    composable("settings") { SettingsScreen() }
}
```
**Likely follow-up:** "How does it handle deep links?" — destinations can declare a `<deepLink>` in the nav graph, and the Navigation component matches an incoming URI to the right destination and builds the correct back stack automatically.

### 8.7 Paging — loading large datasets in chunks

Loads data incrementally (page by page) instead of all at once, reducing memory use and initial load time — pairs directly with RecyclerView/`LazyColumn`.

```kotlin
class NotesPagingSource(private val api: NotesApi) : PagingSource<Int, Note>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
        val page = params.key ?: 0
        return try {
            val notes = api.getNotes(page, params.loadSize)
            LoadResult.Page(
                data = notes, prevKey = if (page == 0) null else page - 1,
                nextKey = if (notes.isEmpty()) null else page + 1
            )
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Note>) = null
}

val notesFlow: Flow<PagingData<Note>> =
    Pager(PagingConfig(pageSize = 20)) { NotesPagingSource(api) }.flow
```
**Likely follow-up:** "How does it avoid reloading everything on rotation?" — combine with `.cachedIn(viewModelScope)` so the paged data survives configuration changes without re-fetching from page 0.

### 8.8 Data Binding — binding UI directly to data

Lets XML layouts reference data objects and even simple expressions directly, cutting down `findViewById()`/manual view-update boilerplate.

```xml
<layout>
    <data>
        <variable name="viewModel" type="com.example.ProfileViewModel" />
    </data>
    <TextView
        android:text="@{viewModel.profile.name}"
        android:visibility="@{viewModel.isLoading ? View.GONE : View.VISIBLE}" />
</layout>
```
### Q: Data Binding vs. View Binding — what's the difference, and which one is deprecated?

| | View Binding | Data Binding |
|---|---|---|
| What it does | Generates a type-safe reference to each view in a layout (replaces `findViewById()`) | Everything View Binding does, **plus** lets layouts bind directly to data/expressions (`@{viewModel.name}`), two-way binding, and custom Binding Adapters |
| Setup cost | Just enable in Gradle, no `<layout>` tag needed | Requires wrapping the layout in a `<layout>` tag |
| Build/compile overhead | Low | Higher (annotation processing for expressions) |
| Current status | **Not deprecated** — actively maintained, still shipping regular releases in 2026, and remains the recommended lightweight option for XML-based screens | **Not formally deprecated as a library either** (still receives releases), but Google has been visibly de-emphasizing it — for example, its official "Data Binding" codelab is now marked deprecated in favor of Jetpack Compose. Treat it as legacy-leaning: fine to maintain in existing code, not the default choice to start new XML screens with. |

**Bottom line for an interview:** don't say "Data Binding is deprecated" as a flat fact — the *library* still ships updates. What's actually happening is Google steering new development toward **Compose** (which needs neither), with View Binding as the lighter-weight option for teams still building XML screens, and Data Binding treated as the option you're less likely to reach for on a new project.

---

## 9. Architecture Patterns: MVC vs MVP vs MVVM

| | MVC | MVP | MVVM |
|---|---|---|---|
| Roles | Model, View, Controller | Model, View, Presenter | Model, View, ViewModel |
| Coupling | Tight (View↔Controller) | Loose | Very loose |
| Testability | Weak | Good | Excellent |
| Data binding | No | No | Yes (with Compose/Data Binding) |
| Android usage today | Rare | Occasional | **Most common in modern apps** |

**Why MVVM wins in Android today:** it separates UI (View) from business/data logic (Model) via an observable ViewModel layer, integrates cleanly with `LiveData`/`StateFlow`, `ViewModel`, and Compose, survives configuration changes, and is the easiest of the three to unit test since the ViewModel has no Android framework dependency.

```kotlin
class ProductViewModel(private val repo: ProductRepository) : ViewModel() {
    val products: StateFlow<List<Product>> =
        repo.observeProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
```

---

## 10. Jetpack Compose

### Q: What is Jetpack Compose, and why does Google recommend it now?
Compose is Google's modern, declarative, Kotlin-native UI toolkit — the current recommended default for new Android UI, replacing the traditional XML + `findViewById()`/View Binding approach for most greenfield work. Benefits: less boilerplate, idiomatic Kotlin, faster iteration with live previews, and UI expressed as a function of state.

```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Count: $count", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { count++ }) { Text("Increment") }
    }
}
```

**What triggers recomposition?** A change to any `State<T>` read within a composable's scope causes Compose to re-run that composable (and affected children) automatically.

**Note:** XML layouts aren't deprecated and remain fully supported (huge amounts of production code still use them), but Compose is where Google's investment and new APIs are focused, so most interviewers expect familiarity with both.

---

## 11. Persistence

### Q: Room — entity, DAO, database.

```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)
}

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
```

Room gives compile-time SQL verification, works with `Flow`/`LiveData` for automatic UI updates, and removes most boilerplate versus raw SQLite.

*(For LiveData vs. StateFlow and Data Binding vs. View Binding, see the [Jetpack & Architecture Components](#8-jetpack--architecture-components) deep dives in section 8 — they cover both in full with comparison tables.)*

### Q: SharedPreferences vs. DataStore?
`SharedPreferences` is the classic key-value store; **Jetpack DataStore** (Preferences or Proto DataStore) is the modern, coroutine/Flow-based replacement, offering type safety and avoiding SharedPreferences' synchronous-read pitfalls on the main thread.

---

## 12. Dependency Injection

### Q: Dagger vs. Hilt — which should you use in 2026?
**Dagger** is a compile-time DI framework that generates the dependency graph at build time (fast, no reflection). **Hilt** is Google's Android-specific layer on top of Dagger and is now the **recommended default for Android apps** — it standardizes Dagger setup, integrates with Android component lifecycles (`@AndroidEntryPoint`), and removes most manual Dagger boilerplate. Plain Dagger is still valid for non-Android/Kotlin Multiplatform modules.

| Project size | Recommended approach |
|---|---|
| Small | Manual DI / Service Locator |
| Medium | Service Locator or Hilt |
| Large | **Hilt** |

```kotlin
@HiltAndroidApp
class MyApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()
```

---

## 13. Networking

### Q: Retrofit vs. Volley — which is current?
**Retrofit** (Square) is the de facto standard today: a type-safe HTTP client that converts REST responses directly into Kotlin objects (via Moshi/Gson/kotlinx.serialization) and works naturally with coroutines/Flow. **Volley** (Google) still works and offers built-in request queuing/caching/image loading, but it's seen far less in new projects — most teams reach for Retrofit + OkHttp, since it fits coroutine-based, MVVM architectures more cleanly.

```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User
}

class UserRepository(private val api: ApiService) {
    suspend fun getUser(id: String): User = api.getUser(id)
}
```

---

## 14. Performance, Memory & ANR

### Q: How does garbage collection work in Android?
ART automatically identifies and reclaims objects no longer referenced by the app, running in the background to free memory and prevent leaks — this is largely automatic, but excessive short-lived object creation (e.g., inside `onBindViewHolder()` or tight loops) increases GC pressure and can cause jank.

### Q: What's a common cause of memory leaks, and how do you avoid one?
Holding a long-lived reference to an Activity/Fragment/View — e.g., in a static field, singleton, or un-cleared listener — prevents garbage collection even after the screen is destroyed.

```kotlin
// Leak-prone: static reference outlives the Activity
class LocationTracker {
    companion object { var listener: LocationListener? = null }
}

// Better: use applicationContext, clear references in onDestroy()/onDestroyView()
class LocationTracker(context: Context) {
    private val appContext = context.applicationContext
}
```
Use **LeakCanary** during development to catch leaks automatically.

### Q: What is ANR, and how do you prevent it?
"Application Not Responding" — triggered when the main thread is blocked too long (roughly 5 seconds for input events). Prevention: move I/O, network calls, and heavy computation off the main thread (coroutines/`WorkManager`), keep layouts shallow, and profile with Android Studio's Profiler.

### Q: How do you reduce APK/AAB size?
- Enable **R8** (ProGuard's successor) for code shrinking/obfuscation.
- Turn on resource shrinking; use WebP for images.
- Ship an **Android App Bundle (AAB)** so Google Play generates device-optimized APKs, instead of one universal APK.
- Remove unused libraries/dependencies.

---

## 15. Testing

### Q: Unit-test a ViewModel that uses coroutines.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadUser emits Success state`() = runTest {
        val fakeRepo = FakeUserRepository(User("1", "Jane"))
        val viewModel = UserViewModel(fakeRepo)
        viewModel.loadUser("1")
        assertEquals(UiState.Success(User("1", "Jane")), viewModel.uiState.value)
    }
}
```

**JUnit** remains the standard unit-testing framework (`@Test`, `@Before`, `@After`), paired with MockK/Mockito for mocking and Turbine for testing `Flow` emissions.

---

## 16. Advanced Topics

### Q: What is AIDL?
Android Interface Definition Language — defines an interface for **inter-process communication (IPC)** between apps/processes using a client-server model; Android auto-generates the boilerplate. Mostly relevant for bound services communicating across app boundaries.

### Q: What is the Android NDK, and when would you use it?
The Native Development Kit lets you write performance-critical code in C/C++. Useful for: squeezing out extra performance (games, physics/DSP), reusing existing C/C++ libraries, accessing low-level hardware, or avoiding GC pauses via manual memory management.

### Q: What is Multidex, and when is it needed?
Android's method-count limit per `.dex` file is 65,536. Apps with many dependencies can exceed this; Multidex splits the app across multiple `.dex` files to work around it (`multiDexEnabled true` in Gradle). Less of an issue today since **D8/R8 and modern minSdkVersions handle multidexing automatically** in most projects — it rarely needs manual configuration anymore.

### Q: What does Gradle actually do in an Android build?
Compiles Kotlin/Java/XML, resolves and manages dependencies (via `build.gradle`/`build.gradle.kts`), runs tests, and packages everything into a `.dex`-based APK or AAB. The **Android Gradle Plugin (AGP)** is the Gradle plugin specifically responsible for the Android build steps, and is updated alongside each Android Studio/platform release.

---

## 17. App Startup: Cold, Warm, Hot

| Type | When it happens |
|---|---|
| **Cold start** | App launched fresh — process wasn't in memory (after reboot or being killed) |
| **Warm start** | Process still in memory but Activity needs to be recreated (partially backgrounded) |
| **Hot start** | App already running in the foreground/recent stack — just brought back to front |

Cold starts are the slowest (full process + Application + Activity creation) and are the main target of startup-performance work (e.g., the Jetpack **App Startup** library, baseline profiles, avoiding heavy work in `Application.onCreate()`).

---

## 18. Deprecated APIs → Modern Replacements

Interviewers at the SDE-II/III level often probe whether you know *why* something changed, not just the old API. Quick reference:

| Deprecated / Legacy | Replaced by | Why |
|---|---|---|
| `AsyncTask` (removed API 30) | Kotlin Coroutines / `WorkManager` | Leak-prone, awkward cancellation, no structured concurrency |
| Support Library (`android.support.*`) | **AndroidX** (`androidx.*`) | Unified versioning, better backward compatibility |
| `Loader` / `LoaderManager` | `ViewModel` + `LiveData`/`Flow` | Simpler, lifecycle-aware, less boilerplate |
| `startActivityForResult()` | **Activity Result APIs** (`registerForActivityResult`) | Type-safe, decoupled from request codes |
| Manual `findViewById()` everywhere | **View Binding** (or Compose, which removes the need entirely) | Compile-time null/type safety |
| XML layouts + imperative UI updates (for new projects) | **Jetpack Compose** | Declarative, less boilerplate, Kotlin-native |
| Raw `SharedPreferences` for structured/critical data | **Jetpack DataStore** | Coroutine/Flow-based, avoids main-thread I/O issues |
| Eclipse + ADT plugin | **Android Studio** | Official Google IDE since 2014, JetBrains/IntelliJ-based |
| `AsyncTaskLoader`, raw `Volley` for most new apps | **Retrofit + OkHttp + Coroutines** | Cleaner API layer, better coroutine/Flow integration |
| Plain Dagger setup for Android apps | **Hilt** | Removes most Dagger Android boilerplate |
| `ProGuard` | **R8** | Faster, combines shrinking + obfuscation + optimization in one tool |

Note: none of these old APIs necessarily crash your app — most still compile and run — but citing the modern replacement (and *why*) is what signals current knowledge in an interview.

---

## 19. Coding Exercises

### Debounced search with Flow

```kotlin
class SearchViewModel(private val repo: SearchRepository) : ViewModel() {
    private val queryFlow = MutableStateFlow("")

    val results: StateFlow<List<Result>> = queryFlow
        .debounce(300)
        .filter { it.length >= 2 }
        .distinctUntilChanged()
        .flatMapLatest { query -> repo.search(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChanged(query: String) { queryFlow.value = query }
}
```
**Why `flatMapLatest`?** It cancels the previous in-flight search when a new query arrives, avoiding a race where a slower, stale response overwrites a newer one.

### Pagination (conceptual)
Loads data in chunks instead of all at once — reduces memory footprint, network load, and initial latency, and pairs naturally with RecyclerView. In modern apps this is implemented with the Jetpack **Paging 3** library (`PagingSource`/`PagingData` + `Flow`) rather than manual offset tracking.

---

## 20. Tricky Questions

These go a level beyond standard Q&A — multi-requirement coding problems where the trick is usually in the coroutine/exception-handling details, not the business logic itself.

### Q: Run two API calls in parallel. If a call takes more than 2 seconds, return `null` for it instead of failing. If a call throws an exception, propagate it immediately.

```kotlin
suspend fun <A, B> runInParallelWithTimeout(
    timeoutMillis: Long = 2000,
    callA: suspend () -> A,
    callB: suspend () -> B
): Pair<A?, B?> = coroutineScope {
    val deferredA = async { withTimeoutOrNull(timeoutMillis) { callA() } }
    val deferredB = async { withTimeoutOrNull(timeoutMillis) { callB() } }
    deferredA.await() to deferredB.await()
}
```

**Why this works:**
- **Timeout → `null`, not a failure:** `withTimeoutOrNull(2000) { ... }` catches *only* its own timeout — if the call hasn't finished in time, it cancels that coroutine and returns `null`, nothing else.
- **A real exception → propagates immediately:** an actual thrown exception is a different type entirely, so `withTimeoutOrNull` doesn't catch it — it escapes upward. Because everything runs inside `coroutineScope { }`, structured concurrency cancels the sibling call and rethrows immediately, satisfying "propagate immediately."

**Likely follow-up: "What if you want to *handle* the exception inside this method instead of propagating it — using `supervisorScope`?"**

Switching to `supervisorScope` on its own only changes one thing: a failure in `callA` no longer automatically cancels `callB`. It does **not**, by itself, stop the exception from being thrown when you call `.await()` — you still need to catch it somewhere. To actually contain each call's outcome inside the method, combine `supervisorScope` with a `try/catch` (or `runCatching`) around each individual call, typically returning a small result type instead of a raw value:

```kotlin
sealed class CallResult<out T> {
    data class Success<T>(val value: T) : CallResult<T>()
    data class Failure(val error: Throwable) : CallResult<Nothing>()
    object TimedOut : CallResult<Nothing>()
}

suspend fun <A, B> runInParallelHandlingErrors(
    timeoutMillis: Long = 2000,
    callA: suspend () -> A,
    callB: suspend () -> B
): Pair<CallResult<A>, CallResult<B>> = supervisorScope {
    val deferredA = async {
        try {
            withTimeoutOrNull(timeoutMillis) { callA() }
                ?.let { CallResult.Success(it) } ?: CallResult.TimedOut
        } catch (e: Exception) {
            CallResult.Failure(e)
        }
    }
    val deferredB = async {
        try {
            withTimeoutOrNull(timeoutMillis) { callB() }
                ?.let { CallResult.Success(it) } ?: CallResult.TimedOut
        } catch (e: Exception) {
            CallResult.Failure(e)
        }
    }
    deferredA.await() to deferredB.await()
}
```

**What actually changed, and why both pieces are needed:**

| | Original (`coroutineScope`) | Handled version (`supervisorScope` + `try/catch`) |
|---|---|---|
| One call fails | Cancels the other call, rethrows immediately | Other call keeps running independently |
| Caller gets | A thrown exception (via normal `try/catch` around the function call) | Always a `Pair` of results — inspect each side for `Success`/`Failure`/`TimedOut` |
| Best for | "Fail fast" — you need both results or neither | Partial success is acceptable (e.g. render half a screen even if one data source failed) |

**The trap to call out explicitly:** `supervisorScope` alone does **not** swallow anything — it only stops one child's failure from cancelling its siblings. If you don't also wrap each call in `try/catch` (or `runCatching`), calling `.await()` on the failed `Deferred` still throws right there. Handling the exception "inside the method" requires the `try/catch`; `supervisorScope` just makes sure that failure doesn't take the other call down with it.

---

## 21. Quick Tips

- Explain trade-offs, not just definitions — e.g., "when would you *not* reach for Compose or Hilt?"
- Know `viewModelScope` vs. `lifecycleScope` vs. avoiding `GlobalScope`.
- Be ready to whiteboard a small feature end-to-end: UI → ViewModel → Repository → data source.
- If asked about a deprecated API, name what replaced it and why — that's usually the real question.
- Practice at least one testing framework (JUnit + MockK/Mockito + Turbine for `Flow`).
