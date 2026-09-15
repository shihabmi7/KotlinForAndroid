# Kotlin for Android — Interview Cheatsheet

A single running example (a small **Task Tracker** feature) threads through this whole document, so you can see how each Kotlin feature actually gets used together in a real Android screen — not just in isolated snippets.

## Contents
1. [Why Kotlin Over Java?](#1-why-kotlin-over-java)
2. [How Kotlin Compiles to Bytecode](#2-how-kotlin-compiles-to-bytecode)
3. [From Bytecode to a Running Android App](#3-from-bytecode-to-a-running-android-app)
4. [Null Safety](#4-null-safety)
5. [Variables, Types & Type Inference](#5-variables-types--type-inference)
6. [Functions: Defaults, Named Args, Extensions](#6-functions-defaults-named-args-extensions)
7. [Classes & Objects](#7-classes--objects)
8. [Sealed Classes & `when` — Modeling State](#8-sealed-classes--when--modeling-state)
9. [Interfaces & Smart Casts](#9-interfaces--smart-casts)
10. [Higher-Order Functions & Lambdas](#10-higher-order-functions--lambdas)
11. [Scope Functions: `let`, `run`, `with`, `apply`, `also`](#11-scope-functions-let-run-with-apply-also)
12. [Collections & Functional Operators](#12-collections--functional-operators)
13. [Delegated Properties](#13-delegated-properties)
14. [Kotlin ↔ Java Interop](#14-kotlin--java-interop)
15. [The Running Example, Part 1: Data & Domain Layer](#15-the-running-example-part-1-data--domain-layer)
16. [The Running Example, Part 2: ViewModel & UI Layer](#16-the-running-example-part-2-viewmodel--ui-layer)
17. [Quick Reference: Kotlin vs. Java Syntax](#17-quick-reference-kotlin-vs-java-syntax)
18. [Rapid-Fire Q&A](#18-rapid-fire-qa)

---

## 1. Why Kotlin Over Java?

> **TL;DR:** Null safety and conciseness are the strongest, most defensible reasons. Coroutines, interop, and KMP explain why the Java→Kotlin migration was actually *feasible* for existing codebases.

### Q: Why did Google make Kotlin the preferred language for Android (announced at I/O 2019), and why should you use it over Java today?

| Reason | What it actually buys you |
|---|---|
| **Null safety built into the type system** | The compiler forces you to handle nullability explicitly, eliminating most `NullPointerException`s at compile time instead of runtime — historically Android's single most common crash cause. |
| **Conciseness** | Data classes, type inference, default/named arguments, and lambdas cut boilerplate dramatically — a Java POJO with `equals()`/`hashCode()`/`toString()` collapses into one line. |
| **Coroutines** | First-class, lightweight structured concurrency built into the language — no callback hell, no heavyweight thread management (Java has no equivalent built in; you'd reach for `CompletableFuture`, `RxJava`, or raw threads/executors). |
| **100% Java interop** | You can call Java from Kotlin and Kotlin from Java freely in the same project — this is *why* the migration from Java was realistic in the first place; teams migrated incrementally, file by file. |
| **Extension functions** | Add functionality to existing classes (including Android/Java framework classes you don't own) without inheritance or utility classes full of static methods. |
| **Smart casts & `when` expressions** | The compiler tracks type checks for you and eliminates redundant casting; `when` is a more powerful, exhaustive `switch`. |
| **Modern language features** | Sealed classes for representing restricted state hierarchies, data classes, destructuring, coroutines, and Flow map naturally onto MVVM/UDF architecture — see [section 8](#8-sealed-classes--when--modeling-state). |
| **Multiplatform (KMP)** | Kotlin isn't JVM-only — it also compiles to native (iOS) and JavaScript targets, letting teams share business logic across Android and iOS. Java has no equivalent path. |

**Interview framing tip:** don't just say "Kotlin is more modern" — anchor your answer on **null safety** and **conciseness** first (the two things that directly reduce bugs and code volume), then mention coroutines and interop as the reasons the migration was actually *feasible* for large existing Java codebases.

---

## 2. How Kotlin Compiles to Bytecode

> **TL;DR:** Kotlin compiles **directly** to JVM bytecode via its own compiler — never through Java source. It just happens to target the *same* bytecode format `javac` produces, which is why Kotlin/Java interop works seamlessly in one project.

### Q: What actually happens when the Kotlin compiler runs?

```
Kotlin source (.kt)  ──┐
                        ├──► Kotlin Compiler (kotlinc, K2 frontend) ──► JVM bytecode (.class)
Java source (.java)  ──┘                    ▲
                                             │
                              javac compiles Java source directly to the same .class format
```

- Both languages produce **the same JVM bytecode format** — this is the entire reason Kotlin/Java interop works seamlessly in one project.
- From the JVM's perspective, a `.class` file from Kotlin and one from Java are indistinguishable.

### Q: So does Kotlin compile "into Java" first?

**No** — this is a common misconception.

- Kotlin compiles **directly** to JVM bytecode via its own compiler (`kotlinc`, using the K2 frontend as of Kotlin 2.0+ — K2 is the only supported frontend from Kotlin 2.4 onward).
- It never generates intermediate Java source code.
- It just happens to target the *same bytecode format* that `javac` also produces, which is what makes the two languages interoperate.

### Q: What's different about Kotlin's compiler versus Java's, in practice?

| | `javac` (Java) | `kotlinc` (Kotlin, K2 frontend) |
|---|---|---|
| Output | `.class` (JVM bytecode) | `.class` (JVM bytecode) — same format |
| Null checks | None at compile time | Enforces nullable/non-null distinctions at compile time |
| Extra compile-time smarts | Minimal | Smart casts, type inference, inline function expansion, default-argument overload generation |
| Other compile targets | JVM only | Also targets Kotlin/Native (iOS, native binaries) and Kotlin/JS — via entirely separate backends sharing the same frontend/IR |
| Interop annotations | N/A | `@JvmStatic`, `@JvmOverloads`, `@JvmField` exist specifically to shape the generated bytecode for cleaner Java consumption |

**Likely follow-up:** "If they produce the same bytecode, why can Kotlin do things Java can't, like default arguments?" — because the Kotlin compiler does more work *before* bytecode generation: for a function with default parameters, it generates multiple overloaded bytecode signatures itself (or a synthetic method with a bitmask parameter), so what looks like one concise Kotlin function actually gets desugared into ordinary JVM-compatible method signatures under the hood.

---

## 3. From Bytecode to a Running Android App

> **TL;DR:** ART doesn't run standard JVM bytecode directly — D8/R8 convert the `.class` files from *both* Kotlin and Java into Android's `.dex` format first, and that's what actually ships and runs on-device.

### Q: Once you have `.class` bytecode, how does it actually become a running Android app?

```
.class files (from both Kotlin AND Java) ──► D8/R8 compiler ──► .dex bytecode ──► packaged into APK/AAB ──► ART executes on-device
```

| Tool | Role |
|---|---|
| **D8** | Google's dexer; converts JVM `.class` bytecode into Android's `.dex` (Dalvik Executable) format. |
| **R8** | Does the same job as D8, plus code shrinking, obfuscation, and optimization in one pass (has replaced the older ProGuard + separate dexing pipeline). |
| **ART** | Executes the final `.dex`/OAT-compiled code on the device (see the Dalvik/ART comparison in the companion Android interview doc for more detail). |

**Likely follow-up:** "Does it matter that this step comes from Kotlin vs. Java source?" — no. By the time D8/R8 sees the `.class` files, the language they originated from is irrelevant — this stage treats Kotlin- and Java-compiled bytecode completely identically, which is the second half of why the two languages coexist so cleanly in one Android project.

---

## 4. Null Safety

> **TL;DR:** The compiler distinguishes nullable (`String?`) from non-null (`String`) types, and gives you specific operators (`?.`, `?:`, `!!`) to handle the difference explicitly instead of crashing at runtime.

The Task Tracker example, showing nullable vs. non-null types:

```kotlin
data class Task(
    val id: String,
    val title: String,
    val dueDate: String?   // nullable — a task might not have a due date
)

fun formatDueDate(task: Task): String {
    // Safe call: returns null instead of crashing if dueDate is null
    val formatted = task.dueDate?.uppercase()

    // Elvis operator: provide a fallback when the left side is null
    return formatted ?: "No due date"
}
```

| Operator | Meaning |
|---|---|
| `String?` | A type that **can** hold null (vs. `String`, which the compiler guarantees is never null) |
| `?.` (safe call) | Executes the call only if the receiver isn't null; otherwise evaluates to null |
| `?:` (Elvis) | Provides a fallback value when the left-hand expression is null |
| `!!` (not-null assertion) | Forces a nullable type to be treated as non-null — **throws `NullPointerException` if it actually is null**. Use sparingly; it reintroduces the exact crash Kotlin is designed to prevent. |
| `lateinit var` | For non-null properties initialized *after* construction (e.g., in `onCreate()`), avoiding the need to make them nullable just to delay initialization |
| `by lazy { }` | Delays initialization until first access, computed once and cached — see [section 13](#13-delegated-properties) |

**Likely follow-up:** "When is `!!` actually acceptable?" — rarely, but sometimes justified when you have external guarantees the type system can't express (e.g., a value you've already null-checked two lines above but the compiler lost track across a lambda boundary). If you're using it often, it's usually a sign you should restructure the nullability instead.

---

## 5. Variables, Types & Type Inference

```kotlin
val taskCount: Int = 5        // val = read-only (like Java's `final`)
var isCompleted = false       // var = mutable; type inferred as Boolean

val title = "Buy groceries"   // inferred as String — no need to write `val title: String = ...`
```

- **`val` vs `var`:** `val` is a read-only reference (the object it points to can still be mutable internally, e.g. a `val list = mutableListOf(...)` — you can't reassign `list`, but you can still add to it).
- **Type inference** means you rarely need to write explicit types for local variables — the compiler infers them from the assigned value, while still being fully statically typed (unlike a dynamically typed language).

---

## 6. Functions: Defaults, Named Args, Extensions

### Default and named arguments — replacing Java's overloads/builder pattern

```kotlin
fun createTask(
    title: String,
    dueDate: String? = null,   // default value — callers can omit this entirely
    priority: Int = 0,
    isCompleted: Boolean = false
): Task = Task(id = generateId(), title = title, dueDate = dueDate)

// Callers only specify what they need — no need for 4 overloaded constructors
createTask(title = "Buy groceries")
createTask(title = "Submit report", dueDate = "2026-09-20", priority = 2)
```

| Approach | How Java achieves this | How Kotlin achieves this |
|---|---|---|
| Optional parameters | Method overloading — one method per combination you want to support | One function signature with default values |
| Flexible construction | A separate Builder class | Default + named arguments on the constructor/function itself |

### Extension functions — adding behavior without inheritance

```kotlin
// Adds a method to Task without modifying its class or subclassing it
fun Task.isOverdue(today: String): Boolean =
    dueDate != null && dueDate < today

// Usage reads like a native member function
if (task.isOverdue(currentDate)) { showOverdueBadge() }
```
This is how Android KTX itself is built — e.g., `Context.getSystemService<T>()` or `SharedPreferences.edit { }` are extension functions added onto Android framework classes Google doesn't let you subclass or modify directly.

---

## 7. Classes & Objects

> **TL;DR:** Kotlin replaces most Java class boilerplate outright — `data class` auto-generates `equals`/`hashCode`/`toString`/`copy`, and `object`/`companion object` replace Java's manual singleton and `static` patterns.

### Data classes — the Task model itself

```kotlin
// One declaration auto-generates equals(), hashCode(), toString(), and copy() —
// no manual boilerplate the way a Java POJO would need
data class Task(
    val id: String,
    val title: String,
    val dueDate: String?,
    val isCompleted: Boolean = false
)
```
One line gives you, for free, what would be 40+ lines of boilerplate in a Java POJO:
- `equals()` / `hashCode()` — structural, by property value
- `toString()`
- `copy()`

```kotlin
val original = Task(id = "1", title = "Buy groceries", dueDate = null)
val completed = original.copy(isCompleted = true) // new instance, only isCompleted changed
```

### Primary/secondary constructors & `init` blocks

```kotlin
class TaskRepository(private val dao: TaskDao) {   // primary constructor, right in the class header
    private val cache = mutableMapOf<String, Task>()

    init {
        Log.d("TaskRepository", "Repository initialized")
    }

    // Secondary constructor — delegates to the primary via this(dao), then adds extra setup
    constructor(dao: TaskDao, seedData: List<Task>) : this(dao) {
        seedData.forEach { cache[it.id] = it }
    }
}
```

### `object` — singletons without the boilerplate

```kotlin
// The compiler handles thread-safe lazy initialization for you — no getInstance(), no locking
object TaskIdGenerator {
    private var counter = 0
    fun next(): String = "task_${counter++}"
}

val id = TaskIdGenerator.next()
```

| | Java | Kotlin |
|---|---|---|
| Thread-safe singleton | Private constructor + static instance field + double-checked locking (or an enum trick) | `object TaskIdGenerator { }` — done |

### `companion object` — Kotlin's answer to Java's `static`

```kotlin
// Private constructor forces callers through the factory method below
class Task private constructor(val id: String, val title: String) {
    companion object {
        fun create(title: String): Task = Task(TaskIdGenerator.next(), title)
    }
}

val task = Task.create("Buy groceries") // looks like a static factory method
```
Kotlin has no `static` keyword at all — a `companion object` is actually a real (singleton) object tied to the class, which is why `@JvmStatic` exists: to tell the compiler to *also* expose a companion member as a true static method for Java callers.

---

## 8. Sealed Classes & `when` — Modeling State

> **TL;DR:** Model a screen's UI state as a `sealed` hierarchy so invalid states are unrepresentable, and the compiler forces you to handle every case in `when`.

This is where Kotlin's type system pays off directly for Android architecture.

```kotlin
// sealed = the compiler knows this is the COMPLETE list of possible states —
// nothing outside this file can add a fourth subtype
sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(val tasks: List<Task>) : TaskUiState
    data class Error(val message: String) : TaskUiState
}

@Composable
fun TaskScreen(state: TaskUiState) {
    when (state) {
        is TaskUiState.Loading -> LoadingSpinner()
        is TaskUiState.Success -> TaskList(state.tasks)
        is TaskUiState.Error -> ErrorMessage(state.message)
        // no `else` branch needed — the compiler knows these three are the ONLY possibilities
        // and will refuse to compile if a new state is added here without being handled
    }
}
```

**Likely follow-up:** "Sealed class vs. enum — when do you use which?"

| | `enum` | `sealed class` |
|---|---|---|
| Fixed set of values | Yes | Yes |
| Extra data per case | No — every constant is the same shape | Yes — each subtype can carry different associated data |
| Example | `enum class Priority { LOW, MEDIUM, HIGH }` | `Error(message)` vs. `Success(tasks)` — different data per case |

---

## 9. Interfaces & Smart Casts

```kotlin
interface TaskValidator {
    fun isValid(task: Task): Boolean
    fun errorMessage(task: Task): String = "Invalid task: ${task.title}" // default implementation allowed
}

fun describe(task: Any) {
    if (task is Task) {
        // Smart cast: inside this block, `task` is automatically treated as type Task,
        // no explicit cast needed — the compiler tracked the `is` check for you
        println(task.title)
    }
}
```

| Feature | Java | Kotlin |
|---|---|---|
| Interface default methods | Supported since Java 8 — gap has narrowed | Supported (as shown above) |
| Cast after a type check | Still requires an explicit `(Task) task` cast, even right after `instanceof` | No cast needed — smart cast applies automatically |

---

## 10. Higher-Order Functions & Lambdas

> **TL;DR:** A function that takes another function as a parameter or returns one — the mechanism behind every collection operator (`map`, `filter`) and behind concise callback APIs like `setOnClickListener { }`.

### What "higher-order" actually means

A **normal** function takes data (`Int`, `String`, `Task`) as parameters. A **higher-order** function takes a *function* as a parameter, or *returns* a function. Kotlin lets you treat functions as values — you can store them in a variable, pass them around, and call them later, just like an `Int` or a `String`.

### Reading a function type

Before writing one, it helps to be able to read the type itself:

```kotlin
(Task) -> Boolean
// │       │
// │       └─ what it returns: a Boolean
// └───────── what it takes in: one Task parameter
```

So `predicate: (Task) -> Boolean` means: *"`predicate` is a parameter that itself is a function — one that takes a `Task` and returns a `Boolean`."*

### Step 1 — declaring a higher-order function

```kotlin
// Higher-order function: `predicate` is itself a function parameter
fun List<Task>.filterBy(predicate: (Task) -> Boolean): List<Task> = this.filter(predicate)
```

### Step 2 — three ways to actually call it

```kotlin
// Option A — trailing lambda syntax (idiomatic, used almost everywhere in Kotlin/Android)
val overdueTasks = allTasks.filterBy { it.dueDate != null && it.dueDate < today }

// Option B — same thing, written with explicit parentheses (less common, but equivalent)
val overdueTasks2 = allTasks.filterBy({ task -> task.dueDate != null && task.dueDate < today })

// Option C — passing an existing named function instead of a lambda, via ::
fun isOverdueTask(task: Task): Boolean = task.dueDate != null && task.dueDate < today
val overdueTasks3 = allTasks.filterBy(::isOverdueTask)
```

**Why Option A is what you'll see everywhere:** Kotlin has a special rule — *if a function's last parameter is a function type, and you're calling it with a lambda, you can move that lambda outside the parentheses.* If it's the *only* parameter, you can drop the parentheses entirely. That's exactly what turns `setOnClickListener({ ... })` into the much more familiar `setOnClickListener { ... }`.

```kotlin
// This is why Android callback APIs read the way they do:
button.setOnClickListener { view ->
    // `view` here is the single parameter Android's listener function type provides
    showToast("Clicked!")
}

// it's really calling a function that looks conceptually like this:
fun setOnClickListener(listener: (View) -> Unit) { /* ... */ }
```

### `it` — the implicit single parameter

When a lambda has exactly **one** parameter and you don't name it, Kotlin lets you refer to it as `it` instead of writing `task -> task.dueDate`:

```kotlin
allTasks.filterBy { it.dueDate != null }   // `it` = the single Task parameter, implicitly
allTasks.filterBy { task -> task.dueDate != null }  // identical — just named explicitly
```

Use an explicit name instead of `it` once you have **nested** lambdas — `it` referring to the *innermost* one gets confusing fast:

```kotlin
allTasks.groupBy { it.isCompleted }.mapValues { entry -> entry.value.filter { task -> task.dueDate != null } }
// naming `entry` and `task` explicitly here avoids three competing meanings of `it`
```

### The other half: a function that *returns* a function

Higher-order also covers functions that **hand back** a function, instead of (or in addition to) taking one in:

```kotlin
// Returns a function — specifically, a Task -> Boolean, pre-configured with a cutoff date
fun overdueCheckerFor(cutoffDate: String): (Task) -> Boolean {
    return { task -> task.dueDate != null && task.dueDate < cutoffDate }
}

val isOverdueToday = overdueCheckerFor(today)   // `isOverdueToday` is itself now a function
val overdue = allTasks.filter(isOverdueToday)   // use it directly wherever a (Task) -> Boolean is expected
```
This pattern — a function that builds and returns a customized function — is how you create reusable, parameterized predicates/validators without duplicating the filtering logic each time.

| | Java (pre-8) | Java 8+ | Kotlin |
|---|---|---|---|
| Passing behavior as a parameter | Anonymous inner class, several lines of boilerplate | Lambda expression, but requires a declared functional interface | Lambda with trailing-lambda syntax and implicit `it` — no functional interface declaration needed |

**Likely follow-up:** "Where does this show up in real Android code, besides `filter`/`map`?" — almost everywhere a callback exists: `setOnClickListener { }`, Compose's `Button(onClick = { })`, Retrofit/coroutine callbacks, `LaunchedEffect { }`, `Modifier.clickable { }` — any API that takes "a block of code to run later" is a higher-order function under the hood.

---

## 11. Scope Functions: `let`, `run`, `with`, `apply`, `also`

> **TL;DR:** All five run a block against an object — they only differ in **what they return** (the lambda's result vs. the object itself) and **how you refer to the receiver** (`it` vs `this`). A frequent whiteboard/rapid-fire interview topic.

| Function | Refers to receiver as | Returns | Typical use |
|---|---|---|---|
| `let` | `it` (or a named param) | The lambda's result | Null-check + transform: `task.dueDate?.let { formatDate(it) }` |
| `run` | `this` | The lambda's result | Grouping a sequence of calls that produce a result |
| `with` | `this` (not an extension — takes the receiver as an argument) | The lambda's result | Calling multiple members on an object without repeating its name |
| `apply` | `this` | **The object itself** | Configuring an object right after creating it (very common for View/UI setup) |
| `also` | `it` | **The object itself** | Side effects (logging, debugging) without breaking a chained call |

```kotlin
// apply — configure and return the same object (classic for building a View or a request object)
val task = Task(id = "1", title = "").apply {
    // if these were `var` properties, you'd set them here without repeating `task.` each time
}

// let — only run the block if dueDate isn't null, and use the transformed result
val label = task.dueDate?.let { formatDate(it) } ?: "No due date"

// also — log as a side effect, without interrupting the chain
val savedTask = repository.save(task).also { Log.d("TaskRepo", "Saved: ${it.id}") }
```

---

## 12. Collections & Functional Operators

> **TL;DR:** Standard functional collection operators (`map`, `filter`, `groupBy`, ...), plus when to reach for `Sequence` instead of `List` for lazy, one-element-at-a-time evaluation.

```kotlin
val allTasks: List<Task> = listOf(/* ... */)

val titles: List<String> = allTasks.map { it.title }              // transform each element
val pending: List<Task> = allTasks.filter { !it.isCompleted }      // keep only matches
val grouped: Map<Boolean, List<Task>> = allTasks.groupBy { it.isCompleted } // bucket by a key
val totalTitleLength: Int = allTasks.sumOf { it.title.length }     // numeric aggregation
val hasOverdue: Boolean = allTasks.any { it.isOverdue(today) }     // short-circuits on first match
```

### `List` vs `MutableList`, and `Sequence` vs `List`

| | `List` | `MutableList` |
|---|---|---|
| Can add/remove elements? | No (read-only view) | Yes |
| Common constructors | `listOf(...)` | `mutableListOf(...)` |

| | Collection operators (`List.map`, etc.) | `Sequence` operators (`asSequence().map()`) |
|---|---|---|
| Evaluation | **Eager** — each operator processes the *entire* list before the next one starts | **Lazy** — elements are processed one at a time through the whole chain |
| Best for | Small/medium lists, simple chains | Large lists or long operator chains, where avoiding intermediate list allocations matters |

**Likely follow-up:** "Why would `asSequence()` ever be faster?" — with a plain `List`, a chain like `.map { }.filter { }.take(5)` builds a full intermediate list at *each* step even though you only need 5 final results. A `Sequence` processes lazily element-by-element, so it can stop as soon as it has 5 matches, without ever materializing full intermediate lists for the whole dataset.

---

## 13. Delegated Properties

```kotlin
class TaskViewModel(private val repo: TaskRepository) : ViewModel() {

    // by lazy — computed once, on first access, then cached
    private val today: String by lazy { getCurrentDateString() }

    // by Delegates.observable — run a callback every time the value changes
    var selectedTaskId: String? by Delegates.observable(null) { _, old, new ->
        Log.d("TaskViewModel", "Selection changed from $old to $new")
    }
}
```
Android's own `by viewModels()` (`private val viewModel: TaskViewModel by viewModels()`) is this exact same mechanism — a property delegate that handles the lazy creation and scoping of the ViewModel for you, instead of you writing that boilerplate by hand every time.

---

## 14. Kotlin ↔ Java Interop

> **TL;DR:** Since both compile to the same bytecode ([section 2](#2-how-kotlin-compiles-to-bytecode)), calling one from the other mostly "just works." A few annotations exist purely to bridge naming/exception conventions Java callers expect.

| Annotation | Purpose |
|---|---|
| `@JvmStatic` | Exposes a `companion object` member as a genuine static method to Java callers (otherwise Java sees `Task.Companion.create(...)` instead of `Task.create(...)`) |
| `@JvmOverloads` | Generates the overloaded method signatures Java needs, from a single Kotlin function with default parameters |
| `@JvmField` | Exposes a Kotlin property as a plain public field, skipping the auto-generated getter/setter, for simpler Java access |
| `@Throws` | Declares a checked exception for Java callers — Kotlin itself has **no checked exceptions**, so without this annotation Java code calling into Kotlin won't know it needs a `try/catch` |

### How to actually use `@JvmField`

Without it, every Kotlin `val`/`var` property compiles down to a **private field plus a public getter/setter** — that's just how Kotlin properties work under the hood. `@JvmField` tells the compiler to skip that and expose the field itself, directly.

```kotlin
// Without @JvmField
class Task(val title: String)
```
```java
// Java caller MUST go through the generated getter — direct field access isn't available
String title = task.getTitle();
```

```kotlin
// With @JvmField — put it directly on the property
class Task(@JvmField val title: String)
```
```java
// Java caller can now access it as a plain field, no getter call needed
String title = task.title;
```

**Where you'd actually reach for this:** mainly when writing Kotlin that a large, existing Java codebase needs to consume with minimal friction — e.g. a shared library module, or gradually migrating a Java project where some Java code still expects plain field access instead of getter calls.

**A constraint worth knowing:** `@JvmField` only works on a property with a plain backing field and **no custom `get()`/`set()` logic** — if your property does anything beyond storing a value (validation, computed logic, etc.), the compiler will reject the annotation, since there's no way to represent custom logic as a bare field.

**Likely follow-up:** "Does Kotlin have checked exceptions?" — no. All exceptions in Kotlin are unchecked, a deliberate design choice (checked exceptions are widely considered one of Java's more debated features, often leading to empty `catch` blocks just to satisfy the compiler). This is also why calling Java code that throws checked exceptions from Kotlin doesn't force you to handle them.

---

## 15. The Running Example, Part 1: Data & Domain Layer

> **TL;DR:** The data model, repository, and UI-state pieces of the Task Tracker feature — the layers that hold and shape data, before anything touches the UI. Part 2 covers the ViewModel and Compose UI that consume these.

```kotlin
// Data layer — data class + nullable field
data class Task(val id: String, val title: String, val dueDate: String?, val isCompleted: Boolean = false)

// Repository — class with a primary constructor, coroutines, single source of truth
class TaskRepository(private val dao: TaskDao, private val api: TaskApi) {
    fun observeTasks(): Flow<List<Task>> = dao.observeTasks()
    suspend fun refresh() { dao.insertAll(api.getTasks()) }
}

// UI state — sealed interface
sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(val tasks: List<Task>) : TaskUiState
    data class Error(val message: String) : TaskUiState
}
```

- **`Task`** — the [data class](#7-classes--objects) from section 7, with a [nullable](#4-null-safety) `dueDate`.
- **`TaskRepository`** — a plain class with a primary constructor; `observeTasks()` returns a `Flow`, exposing Room as the single source of truth.
- **`TaskUiState`** — the [sealed interface](#8-sealed-classes--when--modeling-state) pattern from section 8, restricting the screen to exactly three possible states.

---

## 16. The Running Example, Part 2: ViewModel & UI Layer

> **TL;DR:** The ViewModel and Compose screen that consume the data/domain layer from Part 1 — where coroutines, `StateFlow`, scope functions, and `when`-exhaustiveness all come together on one screen.

```kotlin
// ViewModel — coroutines, StateFlow, scope functions, collection operators
class TaskViewModel(private val repo: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeTasks()
                .map { tasks -> tasks.sortedBy { it.dueDate } }
                .collect { sorted -> _uiState.value = TaskUiState.Success(sorted) }
        }
    }

    fun onRefresh() = viewModelScope.launch {
        runCatching { repo.refresh() }
            .onFailure { _uiState.value = TaskUiState.Error(it.message ?: "Unknown error") }
    }
}

// UI — Compose, when-exhaustiveness, extension function
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val state by viewModel.uiState.collectAsState()
    when (state) {
        is TaskUiState.Loading -> CircularProgressIndicator()
        is TaskUiState.Success -> TaskList((state as TaskUiState.Success).tasks)
        is TaskUiState.Error -> Text("Error: ${(state as TaskUiState.Error).message}")
    }
}
```

- **`TaskViewModel`** — launches a coroutine in `viewModelScope`, collects the repository's `Flow`, and uses `runCatching` (a higher-order/scope-function-style helper) to turn a thrown exception into an `Error` state. (Full coroutine/`Flow` mechanics are covered in the companion Android interview doc's coroutines section.)
- **`TaskScreen`** — a `@Composable` that collects `StateFlow` as Compose state and uses the exhaustive `when` from [section 8](#8-sealed-classes--when--modeling-state) to render each of the three possible states.

Every feature covered in this doc appears somewhere across these two parts — that's not a coincidence; together they're roughly what a real, idiomatic Kotlin Android feature looks like end to end.

---

## 17. Quick Reference: Kotlin vs. Java Syntax

| Concept | Java | Kotlin |
|---|---|---|
| Variable | `final String x = "a";` | `val x = "a"` |
| Null-safe field access | `if (x != null) x.length();` | `x?.length` |
| Data class | Manual `equals`/`hashCode`/`toString`/getters | `data class Foo(val x: Int)` |
| Singleton | Private constructor + static instance | `object Foo { }` |
| Static member | `static` keyword | `companion object { }` |
| Switch/when | `switch` (fall-through by default) | `when` (no fall-through, can be exhaustive) |
| String interpolation | `"Hi " + name + "!"` | `"Hi $name!"` |
| Lambda | `(x) -> x * 2` (functional interface required) | `{ x -> x * 2 }` |
| Checked exceptions | Yes (`throws`) | None |
| Default parameters | Not supported (needs overloads) | `fun f(x: Int = 0)` |

---

## 18. Rapid-Fire Q&A

- **Q: Is Kotlin purely object-oriented?** No — it supports both OOP and functional programming styles (top-level functions, lambdas, immutable data).
- **Q: What is a `const val`?** A compile-time constant (must be a top-level or `object`/companion property, primitive/String type) — inlined directly into bytecode at usage sites, unlike a regular `val` which is a runtime-assigned property.
- **Q: `==` vs `===` in Kotlin?** `==` calls `.equals()` (structural equality — Java's `.equals()` equivalent); `===` checks referential identity (same object in memory — Java's `==` equivalent). This swap trips up people coming from Java.
- **Q: What's a top-level function?** A function declared directly in a file, outside any class — Kotlin doesn't require every function to live inside a class the way Java does.
- **Q: What does `Unit` correspond to in Java?** Roughly `void`, but `Unit` is an actual singleton object/type, which is what allows Kotlin to treat every function as returning *something*, keeping the type system fully consistent (useful for generics and higher-order functions).
- **Q: What is `Nothing`, and how does it differ from `Unit`?** `Nothing` is the type of a function that **never returns normally** — it either always throws or loops forever (e.g., a `TODO()` function). It's a subtype of every other type, which is why `throw` can be used anywhere an expression is expected.
