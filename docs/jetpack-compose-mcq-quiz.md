# Jetpack Compose — 60-Question MCQ Quiz

Multiple-choice practice questions covering Jetpack Compose fundamentals through advanced/performance topics. Each question has one correct answer and a short explanation.

## Contents
1. [Fundamentals & Composables](#1-fundamentals--composables) (Q1–10)
2. [State & Recomposition](#2-state--recomposition) (Q11–22)
3. [Side Effects](#3-side-effects) (Q23–32)
4. [Layouts & Modifiers](#4-layouts--modifiers) (Q33–42)
5. [Lists, Lazy Layouts & Performance](#5-lists-lazy-layouts--performance) (Q43–50)
6. [Navigation, Theming & Animation](#6-navigation-theming--animation) (Q51–56)
7. [Advanced Topics & Testing](#7-advanced-topics--testing) (Q57–60)

---

## 1. Fundamentals & Composables

#### Q1. What annotation marks a function as a Compose UI-building function?
- **A.** `@Compose`
- **B.** `@Composable`
- **C.** `@UIBuilder`
- **D.** `@ComposeFun`

**Answer: B** — `@Composable` tells the Compose compiler this function participates in composition and can call other composables.

#### Q2. What is "composition" in Jetpack Compose?
- **A.** The XML layout inflation process
- **B.** The tree of UI elements produced by executing composable functions, describing the current UI
- **C.** A build-time code generation step only
- **D.** The process of merging Gradle modules

**Answer: B** — Composition is the in-memory tree/description of the UI resulting from executing composables; Compose then renders (and re-renders) this to the screen.

#### Q3. What is "recomposition"?
- **A.** Restarting the entire Activity
- **B.** Re-executing composable functions (or parts of them) to update the UI tree when relevant state changes
- **C.** Recompiling the app
- **D.** Re-running `onCreate()`

**Answer: B** — Recomposition selectively re-invokes composable functions whose inputs/state changed, updating just the affected parts of the UI tree.

#### Q4. Why must composable functions be idempotent / free of side effects during composition?
- **A.** Because Kotlin doesn't allow side effects in functions
- **B.** Because Compose can call a composable function multiple times, skip it, or run it out of order (e.g., during recomposition, skipping, or preview), so side effects would run unpredictably
- **C.** Because it improves compile time only
- **D.** It's just a style convention with no functional impact

**Answer: B** — Compose's runtime can re-invoke, skip, or reorder composable calls, so any side effect (like network calls or mutable global writes) directly in the function body can run more/less often than expected — hence side effects belong in effect handlers.

#### Q5. What does it mean that Compose composition is "declarative"?
- **A.** You imperatively mutate Views one at a time
- **B.** You describe what the UI should look like for a given state, and Compose figures out how to update the actual UI to match
- **C.** You must declare all XML files upfront
- **D.** All composables must be declared as top-level functions

**Answer: B** — Rather than manually mutating a View tree, you describe the desired UI as a function of state, and the framework reconciles the difference.

#### Q6. What is the purpose of the trailing lambda parameter typically named `content: @Composable () -> Unit` in container composables like `Box`?
- **A.** It's required boilerplate with no functional purpose
- **B.** It's a "slot" allowing the caller to inject arbitrary child composables into the container, enabling composition over configuration
- **C.** It only accepts `Text` composables
- **D.** It replaces the need for `Modifier`

**Answer: B** — Slot APIs let container composables (Box, Scaffold, Card, etc.) accept flexible children, a core Compose design pattern.

#### Q7. What is a `Preview` (`@Preview` annotation) used for?
- **A.** Running actual production code on a device
- **B.** Rendering a composable in Android Studio's design view without deploying to a device/emulator
- **C.** Compiling the app faster
- **D.** Testing coroutines

**Answer: B** — `@Preview` lets you visually inspect a composable's output directly in the IDE, speeding up UI iteration.

#### Q8. Can a composable function have a return value other than `Unit`?
- **A.** No, composables must always return `Unit`
- **B.** Yes — some composables (like `remember`, or ones producing state/values) legitimately return values, though most UI-emitting composables return `Unit`
- **C.** Only if annotated `@Preview`
- **D.** Only inside `MainActivity`

**Answer: B** — While most UI composables return `Unit` (they emit UI as a side effect of composition), value-producing composable functions (e.g. `remember { ... }`) do return values.

#### Q9. What is `setContent { }` used for in an Activity?
- **A.** Setting an XML layout resource
- **B.** Defining the Compose UI hierarchy as the Activity's content, replacing `setContentView`
- **C.** Starting a coroutine
- **D.** Registering a `BroadcastReceiver`

**Answer: B** — `setContent` is the entry point that hosts a Compose UI tree inside a `ComponentActivity`, replacing the traditional XML `setContentView`.

#### Q10. What determines whether Compose can "skip" recomposing a composable function during a recomposition pass?
- **A.** Whether the function has a `@Preview` annotation
- **B.** Whether all of its parameters are considered "stable" and unchanged since the last composition
- **C.** Whether it's declared as `private`
- **D.** Whether it returns `Unit`

**Answer: B** — Compose's smart recomposition skips a composable if its inputs are stable and equal to their previous values, avoiding unnecessary work.

---

## 2. State & Recomposition

#### Q11. What does `mutableStateOf(0)` create?
- **A.** An immutable constant
- **B.** An observable state holder (`MutableState<Int>`) whose reads during composition are tracked, triggering recomposition on writes
- **C.** A `Flow<Int>`
- **D.** A regular Kotlin `var`

**Answer: B** — `mutableStateOf` creates Compose's core observable state primitive; the Snapshot system tracks who reads it, so writes trigger targeted recomposition.

#### Q12. Why do you typically wrap `mutableStateOf` in `remember`?
- **A.** `remember` improves performance by caching to disk
- **B.** Without `remember`, a new state instance would be created on every recomposition, losing the value each time
- **C.** `remember` is required syntactically by the compiler
- **D.** `mutableStateOf` doesn't compile without `remember`

**Answer: B** — `remember` retains the state object across recompositions of the same composition, so the value survives instead of resetting each time the function re-runs.

#### Q13. What are the three ways to destructure `by remember { mutableStateOf(0) }` usage requiring an import?
- **A.** No import is needed for `by` delegation with `MutableState`
- **B.** `import androidx.compose.runtime.getValue` (and `setValue` for `var`), enabling Kotlin property delegate syntax
- **C.** `import kotlin.properties.Delegates`
- **D.** `import androidx.compose.ui.State`

**Answer: B** — Using `by` with `MutableState` requires `getValue`/`setValue` extension operators from `androidx.compose.runtime`, imported explicitly (Android Studio usually auto-imports them).

#### Q14. What is "state hoisting" in Compose?
- **A.** Moving state into a database
- **B.** Moving state up to a caller/parent composable, making the child composable stateless and controlled via parameters and callbacks (e.g., `value` + `onValueChange`)
- **C.** Caching state in `SharedPreferences`
- **D.** Declaring state as a top-level global variable

**Answer: B** — State hoisting is the pattern of lifting state ownership out of a composable, making it more reusable, testable, and controllable by the caller.

#### Q15. What problem does `rememberSaveable` solve that plain `remember` doesn't?
- **A.** It makes recomposition faster
- **B.** It persists state across configuration changes and process death by saving into the instance-state `Bundle` (via `Saver`)
- **C.** It removes the need for `mutableStateOf`
- **D.** It replaces ViewModel entirely

**Answer: B** — `rememberSaveable` integrates with the saved-instance-state mechanism, so simple state survives rotation or process death, unlike `remember` alone.

#### Q16. For a custom data class to be saved with `rememberSaveable`, what must you typically provide?
- **A.** Nothing — all classes work automatically
- **B.** A `Saver` (or make the class `@Parcelize`/implement `Parcelable`, or use a mapSaver/listSaver) so it can be serialized into the Bundle
- **C.** A `Flow` wrapper
- **D.** It must extend `ViewModel`

**Answer: B** — Non-primitive/non-Parcelable types need a custom `Saver` (or `mapSaver`/`listSaver`) to tell Compose how to save/restore them.

#### Q17. What causes unnecessary recomposition when a lambda captures state incorrectly?
- **A.** Lambdas never cause recomposition issues
- **B.** Reading a `State` value directly in a parent's scope and passing the raw value down (rather than deferring the read) can widen the recomposition scope to the parent unnecessarily
- **C.** Using `remember` too often
- **D.** Using `Modifier.fillMaxSize()`

**Answer: B** — Reading state too high up in the composable tree ties recomposition to that broader scope; deferring reads (e.g. via lambdas or `derivedStateOf`) narrows the scope that needs to recompose.

#### Q18. What does "stability" mean for a type in Compose's compiler analysis?
- **A.** Whether the class is marked `final`
- **B.** Whether Compose can guarantee that if the type's public properties are unchanged (by `equals`), the composable using it can be skipped — immutable/stable types enable skipping optimizations
- **C.** Whether the type implements `Serializable`
- **D.** Whether it's a `data class`

**Answer: B** — Stable types (all `val`, immutable, or explicitly annotated `@Stable`/`@Immutable`) let the compiler safely skip recomposition when equal, improving performance.

#### Q19. Why are unstable types like a `List<T>` interface (vs. `ImmutableList`) sometimes a Compose performance issue?
- **A.** `List` is always faster
- **B.** The Compose compiler can't statically prove a plain `List` interface is immutable (implementers could be mutable), so it's treated as unstable, disabling skip optimizations for composables that take it as a parameter
- **C.** Lists cannot be used as composable parameters
- **D.** It causes compile errors

**Answer: B** — Because `List` is an interface with potentially-mutable implementations, Compose conservatively marks it unstable; using `kotlinx.collections.immutable`'s `ImmutableList` (or `@Immutable` wrapper classes) can restore stability guarantees.

#### Q20. What does `derivedStateOf` optimize for?
- **A.** Persisting state to disk
- **B.** Avoiding recomposition when a computed value doesn't actually change, even though its underlying state inputs change frequently
- **C.** Making state mutable from multiple threads
- **D.** Replacing `LaunchedEffect`

**Answer: B** — It memoizes a derived computation and only signals "changed" (triggering recomposition of readers) when the computed *result* differs, not every time an input changes.

#### Q21. What's a typical mistake when using `derivedStateOf`?
- **A.** Using it for values that are cheap and rarely change (it's harmless there, just slightly wasteful)
- **B.** Using it for a computation that itself changes every single time its inputs change (e.g., `sum = a + b` where both always change together) — providing no benefit and adding overhead
- **C.** Wrapping it in `remember`
- **D.** Reading it inside a composable

**Answer: B** — `derivedStateOf` only pays off when the computed result changes less often than the inputs; if the output always changes whenever an input does, it adds indirection with no recomposition savings.

#### Q22. What is the Snapshot system in Compose's state model?
- **A.** A screenshot-taking utility for testing
- **B.** The underlying mechanism (from Compose runtime) that tracks reads/writes of `State` objects to know what to recompose, supporting isolated, consistent views of state
- **C.** A caching layer for images
- **D.** A database migration tool

**Answer: B** — Snapshots provide transactional, isolated state observation — Compose uses them to detect exactly which state reads occurred where, powering fine-grained recomposition.

---

## 3. Side Effects

#### Q23. What is `LaunchedEffect(key1) { ... }` used for?
- **A.** Rendering a composable conditionally
- **B.** Running a suspend-function coroutine tied to the composition, launched when it first enters composition and re-launched (cancelling the previous one) when `key1` changes
- **C.** Declaring a `remember` block
- **D.** Handling click events

**Answer: B** — `LaunchedEffect` is the standard way to trigger coroutine-based side effects (e.g., API calls, animations) scoped to a composable's lifecycle, keyed for restart control.

#### Q24. What happens to a `LaunchedEffect` coroutine when the composable leaves composition?
- **A.** It keeps running forever in the background
- **B.** It is automatically cancelled
- **C.** It throws an unhandled exception
- **D.** It pauses and resumes later automatically

**Answer: B** — `LaunchedEffect`'s coroutine is tied to the composition lifecycle and is cancelled when the composable is removed from the tree.

#### Q25. What does `DisposableEffect(key) { onDispose { ... } }` provide that `LaunchedEffect` doesn't?
- **A.** Coroutine support
- **B.** A structured way to register a non-coroutine side effect (e.g., a listener/callback registration) with explicit cleanup logic that runs when the key changes or the composable leaves composition
- **C.** State persistence across process death
- **D.** Animation interpolation

**Answer: B** — `DisposableEffect` is for effects requiring explicit setup/teardown (like registering/unregistering a broadcast receiver or listener), with `onDispose` mandatory as the cleanup hook.

#### Q26. What must every `DisposableEffect` block end with?
- **A.** `return@DisposableEffect`
- **B.** A call to `onDispose { ... }`, which is required and enforced by the compiler
- **C.** `launch { }`
- **D.** Nothing special

**Answer: B** — `DisposableEffect` requires an `onDispose` clause as its last statement to define cleanup behavior — omitting it is a compile error.

#### Q27. What is `SideEffect { }` used for?
- **A.** Launching coroutines
- **B.** Publishing Compose state to non-Compose code (e.g., an external object) after every successful recomposition
- **C.** Cancelling the composition
- **D.** Declaring stable types

**Answer: B** — `SideEffect` runs its block after every recomposition completes, useful for syncing Compose state out to non-Compose systems (e.g., analytics, legacy Views).

#### Q28. What does `rememberCoroutineScope()` provide?
- **A.** A scope that automatically launches on composition
- **B.** A `CoroutineScope` tied to the composable's lifecycle that you can use to launch coroutines from callbacks (e.g., `onClick`), rather than from composition directly
- **C.** A replacement for `viewModelScope`
- **D.** A blocking scope like `runBlocking`

**Answer: B** — Since you can't call `LaunchedEffect` from a click handler, `rememberCoroutineScope` gives you a scope (cancelled when the composable leaves composition) to manually `launch` coroutines from event callbacks.

#### Q29. What problem does `rememberUpdatedState` solve?
- **A.** It memoizes expensive calculations
- **B.** It lets a long-lived effect/callback (e.g., inside `LaunchedEffect` that shouldn't restart) always reference the latest value of a changing parameter without needing to add it as a restart key
- **C.** It replaces `remember` entirely
- **D.** It persists state to disk

**Answer: B** — `rememberUpdatedState` wraps a value so effects that capture it via closures see the latest version without forcing the effect to restart when that value changes.

#### Q30. What does `produceState` do?
- **A.** Converts a Compose `State` into a `Flow`
- **B.** Converts non-Compose asynchronous data sources (e.g., a callback API or suspend function) into Compose `State`, running as a coroutine effect internally
- **C.** Declares a `ViewModel`
- **D.** Triggers animations

**Answer: B** — `produceState` bridges external async data producers into Compose's `State` system, so results can be read reactively by composables.

#### Q31. What's a key difference between `LaunchedEffect` and `SideEffect`?
- **A.** They are functionally identical
- **B.** `LaunchedEffect` launches a coroutine tied to keys, running once/when keys change; `SideEffect` runs synchronously (not a coroutine) after every successful recomposition, with no keys
- **C.** `SideEffect` is used for animations only
- **D.** `LaunchedEffect` runs after every recomposition like `SideEffect`

**Answer: B** — `LaunchedEffect` is coroutine-based and keyed for controlled (re)execution; `SideEffect` fires on every recomposition without keys and runs synchronously, not as a suspend block.

#### Q32. If you pass `Unit` (or `true`) as the key to `LaunchedEffect`, what's the effect?
- **A.** It restarts on every recomposition
- **B.** It runs exactly once when the composable enters composition and is not restarted on recomposition (since the key never changes) — cancelled only when the composable leaves composition
- **C.** It never runs at all
- **D.** It causes a compile error

**Answer: B** — A constant key means the effect's restart condition never triggers again, effectively giving "run once, cancel on dispose" semantics.

---

## 4. Layouts & Modifiers

#### Q33. What is `Modifier` in Compose?
- **A.** A base class all composables must extend
- **B.** An ordered, immutable chain of elements used to decorate or configure a composable's layout, drawing, and behavior (e.g., size, padding, click handling)
- **C.** A ViewModel utility
- **D.** A theme configuration object

**Answer: B** — Modifiers are chained (`Modifier.padding(8.dp).clickable { }`), each wrapping/decorating the composable, and order matters since they apply sequentially.

#### Q34. Why does the order of chained modifiers matter?
```kotlin
Modifier.padding(16.dp).size(50.dp)
// vs.
Modifier.size(50.dp).padding(16.dp)
```
- **A.** It never matters, Compose reorders them automatically
- **B.** Each modifier wraps the result of the previous one, so e.g. padding-then-size vs. size-then-padding produce different final layout dimensions
- **C.** Only the last modifier in the chain has any effect
- **D.** Modifiers must always be applied in alphabetical order

**Answer: B** — Because each modifier constrains/wraps its predecessor, `padding` before `size` shrinks the content area before sizing, while `size` before `padding` fixes the box size and then adds padding around/inside it — producing different results.

#### Q35. What do `Row` and `Column` do in Compose?
- **A.** They are database-related concepts, not UI
- **B.** They arrange child composables horizontally (`Row`) or vertically (`Column`), analogous to `LinearLayout`
- **C.** They're used only for animations
- **D.** `Row` and `Column` are interchangeable

**Answer: B** — `Row` and `Column` are basic linear-layout composables for arranging children in a horizontal or vertical sequence.

#### Q36. What layout composable would you use to stack composables on top of each other (z-axis)?
- **A.** `Row`
- **B.** `Box`
- **C.** `Column`
- **D.** `Spacer`

**Answer: B** — `Box` stacks its children, layering them by declaration order (later children draw on top), commonly used with `Modifier.align(...)`.

#### Q37. What does `Modifier.weight(1f)` do inside a `Row` or `Column`?
- **A.** Sets the visual opacity
- **B.** Distributes remaining available space among children proportionally to their weight values (only valid inside `Row`/`Column` scopes)
- **C.** Sets a fixed pixel size
- **D.** Controls animation speed

**Answer: B** — `weight` is a scope-specific modifier that tells the parent `Row`/`Column` how to divide leftover space among weighted children.

#### Q38. What is `ConstraintLayout` in Compose used for?
- **A.** Replacing `remember`
- **B.** Positioning composables relative to each other and the parent using constraints, useful for complex layouts that are awkward to express with nested `Row`/`Column`
- **C.** Managing coroutine constraints
- **D.** State management

**Answer: B** — `ConstraintLayout` (a separate artifact) lets you define relative positioning constraints, similar to the View-based `ConstraintLayout`, helping flatten deeply nested layout hierarchies.

#### Q39. What does `Modifier.fillMaxSize()` do?
- **A.** Sets the composable's size to `wrap_content`
- **B.** Makes the composable expand to fill all available space given by its parent's constraints
- **C.** Fills the composable with a solid color
- **D.** Only affects text composables

**Answer: B** — `fillMaxSize()` (and its `fillMaxWidth`/`fillMaxHeight` variants) expands the element to occupy the maximum size allowed by incoming layout constraints.

#### Q40. In Compose's layout system, what is the general measurement pass model?
- **A.** Bottom-up only — children measure themselves independent of parents
- **B.** Single-pass, top-down constraints combined with children measuring themselves and reporting size back up ("measure then place")
- **C.** Compose re-measures the entire screen on every frame regardless of change
- **D.** XML-based, using `wrap_content`/`match_parent` exclusively

**Answer: B** — Parents pass down `Constraints`; children measure themselves within those constraints and report their size, and the parent then places children — generally a single measure/layout pass per node (with exceptions like intrinsics).

#### Q41. What's the purpose of a custom `Layout` composable (the low-level layout primitive)?
- **A.** To define custom themes
- **B.** To implement fully custom measurement and placement logic when built-in layouts (`Row`, `Column`, `Box`) aren't sufficient
- **C.** To manage ViewModel state
- **D.** To declare navigation graphs

**Answer: B** — `Layout` is the foundational composable that `Row`/`Column`/`Box` are themselves built on, exposed for writing custom layout algorithms.

#### Q42. What does `Modifier.clickable { }` provide out of the box (compared to manually detecting taps with `pointerInput`)?
- **A.** Nothing beyond a raw tap detector
- **B.** Built-in ripple/indication feedback, accessibility semantics (click action), and handling of enabled/disabled state
- **C.** Only works on `Text` composables
- **D.** Requires manually implementing accessibility

**Answer: B** — `clickable` bundles standard interaction semantics (ripple, focus, accessibility click action, enabled state) that you'd otherwise need to implement manually with lower-level pointer input handling.

---

## 5. Lists, Lazy Layouts & Performance

#### Q43. What is the main advantage of `LazyColumn` over a `Column` wrapped in `verticalScroll`?
- **A.** `LazyColumn` looks visually different
- **B.** `LazyColumn` only composes and lays out the items currently visible (plus a small buffer), rather than all items upfront, which is far more efficient for long/large lists
- **C.** `Column` cannot scroll at all
- **D.** There's no functional difference

**Answer: B** — Lazy layouts virtualize their content, composing/measuring only visible items — critical for performance with large or unbounded lists.

#### Q44. In `LazyColumn`'s `items()` block, what's the purpose of specifying a `key`?
```kotlin
items(list, key = { it.id }) { item -> ... }
```
- **A.** To sort the list
- **B.** To give Compose a stable identity for each item across list mutations (insert/remove/reorder), preserving item state and improving recomposition/animation correctness
- **C.** It's purely cosmetic
- **D.** It's required for the code to compile

**Answer: B** — Without a stable `key`, Compose defaults to positional identity, which can cause state to "jump" between items or unnecessary recomposition when the list is reordered/filtered; a key based on a stable ID fixes this.

#### Q45. What does `rememberLazyListState()` provide?
- **A.** A way to declare list item layout
- **B.** A state object exposing scroll position info (e.g., `firstVisibleItemIndex`) and providing programmatic scroll control, that survives recomposition
- **C.** A network cache for list data
- **D.** A ViewModel replacement

**Answer: B** — `LazyListState` tracks scroll position/offset and lets you call `scrollToItem`/`animateScrollToItem` programmatically.

#### Q46. Why is capturing large/unstable objects (e.g., a whole ViewModel) directly inside a lazy list item's lambda a common performance pitfall?
- **A.** It's not actually a problem
- **B.** It can make the per-item composable's inputs appear "unstable," disabling skip-recomposition optimizations for that item, causing wider/more frequent recomposition than necessary
- **C.** It causes a compile error
- **D.** It only affects `LazyRow`, not `LazyColumn`

**Answer: B** — Passing broad, unstable objects into item composables (instead of narrow, stable data + lambdas) reduces Compose's ability to skip unaffected items during recomposition.

#### Q47. What is the purpose of `contentType` in `LazyColumn`'s `items()`?
- **A.** To set the item's font
- **B.** To hint to Compose when a list has heterogeneous item layouts, improving item composition reuse (recycling) across different visual types
- **C.** To specify network content type headers
- **D.** It's identical to `key`

**Answer: B** — `contentType` helps Compose's internal reuse pool group similarly-shaped items together, improving recycling efficiency for lists mixing different item layouts.

#### Q48. What tool would you use to diagnose excessive/unexpected recomposition during development?
- **A.** Logcat filtering for `ERROR` only
- **B.** The Layout Inspector's recomposition counts, or the Compose Compiler's stability/metrics reports, or manually adding `Modifier.composed` debug logging
- **C.** `adb shell dumpsys meminfo`
- **D.** There's no tooling for this; it must be inferred by reading code

**Answer: B** — Android Studio's Layout Inspector shows recomposition/skip counts per composable, and the Compose compiler can emit stability reports — both are standard tools for diagnosing recomposition issues.

#### Q49. Why might extracting a lambda like `onClick = { viewModel.onItemClick(item) }` as a remembered/stable reference matter for list performance?
- **A.** It never matters — lambdas are always free
- **B.** A newly-allocated lambda on every recomposition can be seen as "changed" by equality checks, potentially preventing the composable receiving it from being skipped; wrapping with `remember` (or using stable references) can avoid unnecessary allocations/instability
- **C.** Lambdas cannot be passed to composables
- **D.** It only matters for coroutines, not click handlers

**Answer: B** — While Compose can often optimize simple lambdas at the compiler level, in more complex cases unstable/newly-allocated lambdas can still contribute to unnecessary recomposition, so being deliberate about stable references matters in hot paths like large lists.

#### Q50. What's the effect of using `Modifier.animateItemPlacement()` (or `animateItem()` in newer Compose) inside a lazy list item?
- **A.** It disables list scrolling
- **B.** It animates an item's position change (e.g., when items are reordered, inserted, or removed) smoothly rather than snapping instantly
- **C.** It only works with `LazyRow`
- **D.** It replaces the need for a `key`

**Answer: B** — This modifier enables automatic placement animations for lazy list items when their position changes due to list mutations, provided items have a stable `key`.

---

## 6. Navigation, Theming & Animation

#### Q51. What is `NavHost` used for in Compose Navigation?
- **A.** Hosting a network server
- **B.** Defining a container that displays composable destinations and manages back stack navigation via a `NavController`
- **C.** Managing ViewModel scopes exclusively
- **D.** Declaring themes

**Answer: B** — `NavHost` ties a `NavController` to a graph of composable destinations, swapping the displayed composable as navigation occurs.

#### Q52. What does `MaterialTheme` provide to composables in its subtree?
- **A.** Only color values
- **B.** A structured set of theming values — colors, typography, and shapes — accessible via `MaterialTheme.colorScheme`, `MaterialTheme.typography`, etc., propagated implicitly via `CompositionLocal`
- **C.** Network theming configuration
- **D.** ViewModel scoping

**Answer: B** — `MaterialTheme` bundles color scheme, typography, and shape tokens and exposes them through `CompositionLocal`s so descendant composables can read consistent styling without explicit parameter passing.

#### Q53. What is `CompositionLocal` used for?
- **A.** Local variable declaration inside a function
- **B.** Implicitly passing data down the composition tree without threading it through every function's parameters explicitly (e.g., theme, LocalContext)
- **C.** Persisting data across app restarts
- **D.** Managing coroutine dispatchers only

**Answer: B** — `CompositionLocal` (e.g., `LocalContext`, `LocalDensity`) provides an implicit way to make ambient data available to descendants, useful for cross-cutting concerns like theming.

#### Q54. What does `animateContentSize()` modifier do?
- **A.** Changes text font size
- **B.** Automatically animates size changes of a composable when its content changes (e.g., expanding/collapsing text), without manually specifying start/end values
- **C.** Speeds up recomposition
- **D.** Only works with images

**Answer: B** — It's a convenience modifier that animates layout size transitions smoothly whenever the composable's measured size changes.

#### Q55. What's the difference between `animate*AsState` (e.g., `animateFloatAsState`) and low-level `Animatable`?
- **A.** They are identical APIs
- **B.** `animate*AsState` is a simpler, declarative API that animates a single value to a target automatically on change; `Animatable` gives lower-level, imperative control (e.g., manual `animateTo`, interruption handling, custom sequencing) usually driven from a coroutine
- **C.** `Animatable` cannot be interrupted
- **D.** `animate*AsState` requires a coroutine scope explicitly

**Answer: B — `animate*AsState` is best for simple "animate to this target whenever it changes" cases, while `Animatable` supports more advanced imperative animation sequencing.**

#### Q56. What composable/modifier combo is commonly used for enter/exit animations when a composable is conditionally shown/hidden?
- **A.** `remember` alone
- **B.** `AnimatedVisibility` (with configurable `enter`/`exit` transitions)
- **C.** `LaunchedEffect` alone
- **D.** `Box` with no modifiers

**Answer: B** — `AnimatedVisibility` handles animated appearance/disappearance of content, with built-in fade/slide/expand transition specs.

---

## 7. Advanced Topics & Testing

#### Q57. What does `CompositionLocalProvider` do?
- **A.** Declares a new `@Composable` function
- **B.** Provides (overrides) a value for a `CompositionLocal` within a specific subtree of the composition
- **C.** Registers a ViewModel provider
- **D.** Starts a coroutine

**Answer: B** — `CompositionLocalProvider(LocalFoo provides value) { ... }` scopes an overridden value to its content block, restoring the previous value outside that subtree.

#### Q58. In Compose UI testing, what does `composeTestRule.onNodeWithText("Save").performClick()` do?
- **A.** It's pseudocode; Compose doesn't support UI testing
- **B.** Finds a composable node matching the text "Save" in the test's semantics tree and simulates a click on it
- **C.** Sets the text content to "Save"
- **D.** Verifies the app compiles

**Answer: B** — Compose's testing APIs (`ComposeTestRule`, `onNode...` finders, and actions like `performClick`) let you query and interact with composables via their semantics tree in instrumented/unit tests.

#### Q59. What role does the "semantics tree" play in Compose?
- **A.** It's an internal compiler artifact with no runtime relevance
- **B.** It exposes UI structure/meaning for accessibility services and testing frameworks, separate from (but derived from) the visual composition tree
- **C.** It replaces the need for `Modifier`
- **D.** It's only used for animations

**Answer: B** — The semantics tree describes UI elements' roles, text, and actions for accessibility (TalkBack) and is also what Compose's testing APIs query against.

#### Q60. What's a key architectural benefit of Compose's unidirectional data flow (state flows down, events flow up) pattern, commonly paired with a ViewModel exposing `StateFlow`/`State`?
- **A.** It requires more boilerplate with no real benefit
- **B.** It creates a single source of truth for UI state, making UI updates predictable, easier to test, and avoiding scattered, hard-to-track mutable state across composables
- **C.** It eliminates the need for any state at all
- **D.** It only works with `LiveData`, not `StateFlow`

**Answer: B** — Unidirectional data flow (state down, events up) keeps state ownership centralized (e.g., in a ViewModel), making the UI a predictable function of state and simplifying testing/debugging compared to ad hoc mutable state scattered across composables.

---

*60/60 questions. Companion file: [kotlin-coroutines-mcq-quiz.md](kotlin-coroutines-mcq-quiz.md)*
