# Kotlin Coroutines — 60-Question MCQ Quiz

Multiple-choice practice questions covering Kotlin coroutines fundamentals through advanced/Android-specific usage. Each question has one correct answer and a short explanation.

## Contents
1. [Fundamentals & Suspend Functions](#1-fundamentals--suspend-functions) (Q1–10)
2. [Coroutine Builders](#2-coroutine-builders) (Q11–20)
3. [Dispatchers & Coroutine Context](#3-dispatchers--coroutine-context) (Q21–28)
4. [Structured Concurrency, Job & Cancellation](#4-structured-concurrency-job--cancellation) (Q29–36)
5. [Exception Handling](#5-exception-handling) (Q37–44)
6. [Flow](#6-flow) (Q45–54)
7. [Channels, Testing & Android Integration](#7-channels-testing--android-integration) (Q55–60)

---

## 1. Fundamentals & Suspend Functions

#### Q1. What is a coroutine in Kotlin?
- **A.** A separate operating system thread
- **B.** A lightweight, suspendable unit of computation that runs on top of threads
- **C.** A process managed by the JVM garbage collector
- **D.** A callback interface for asynchronous work

**Answer: B** — Coroutines are lightweight; thousands can run on a small thread pool because they suspend instead of blocking threads.

#### Q2. What keyword marks a function as a coroutine that can suspend execution?
- **A.** `async`
- **B.** `suspend`
- **C.** `yield`
- **D.** `defer`

**Answer: B** — `suspend fun` marks a function that can call other suspend functions and be paused/resumed without blocking a thread.

#### Q3. What does it mean for a suspend function to "suspend"?
- **A.** It permanently stops execution
- **B.** It pauses execution and releases the thread to do other work, resuming later without blocking
- **C.** It throws an exception until retried
- **D.** It runs on the UI thread only

**Answer: B** — Suspension frees the underlying thread for other work while the coroutine waits (e.g., for I/O), then resumes, possibly on a different thread.

#### Q4. Can a `suspend` function be called directly from regular (non-suspend) code?
- **A.** Yes, always
- **B.** No, it must be called from another suspend function or from within a coroutine builder
- **C.** Only from `main()`
- **D.** Only if annotated with `@JvmStatic`

**Answer: B** — Suspend functions require a suspending context (another suspend function or a coroutine started via `launch`/`async`/`runBlocking`).

#### Q5. What underlying mechanism allows suspend functions to work without blocking threads?
- **A.** Reflection
- **B.** Continuation-passing style (CPS) transformation by the Kotlin compiler
- **C.** Native OS-level green threads
- **D.** JVM bytecode interpretation mode

**Answer: B** — The compiler transforms suspend functions into a state machine using `Continuation` callbacks, enabling pause/resume semantics.

#### Q6. Which of these is NOT a valid coroutine builder?
- **A.** `launch`
- **B.** `async`
- **C.** `runBlocking`
- **D.** `suspend`

**Answer: D** — `suspend` is a modifier, not a builder. `launch`, `async`, and `runBlocking` are builders that create coroutines.

#### Q7. What does `delay(1000)` do inside a coroutine?
- **A.** Blocks the current thread for 1000ms
- **B.** Suspends the coroutine for 1000ms without blocking the underlying thread
- **C.** Schedules the coroutine to run 1000 times
- **D.** Cancels the coroutine after 1000ms

**Answer: B** — `delay` is a suspending function; it frees the thread during the wait, unlike `Thread.sleep()` which blocks it.

#### Q8. Why is `Thread.sleep()` discouraged inside a coroutine (versus `delay()`)?
- **A.** It doesn't compile
- **B.** It blocks the underlying thread, defeating the purpose of using coroutines
- **C.** It's slower than `delay()`
- **D.** There's no actual difference

**Answer: B** — `Thread.sleep()` blocks the real OS thread, preventing other coroutines sharing that thread/dispatcher from making progress.

#### Q9. What is a `CoroutineScope`?
- **A.** A UI container in Compose
- **B.** An interface defining a lifecycle boundary that coroutines are launched within, tying their lifetime together
- **C.** A thread pool implementation
- **D.** A class that stores coroutine return values

**Answer: B** — A `CoroutineScope` holds a `CoroutineContext` and defines the lifecycle for coroutines launched in it — cancelling the scope cancels its coroutines.

#### Q10. What does `coroutineScope { }` (the suspending function, lowercase) do?
- **A.** Creates a new top-level scope disconnected from the caller
- **B.** Creates a scope that suspends the caller until all its child coroutines complete, propagating failures
- **C.** Is identical to `GlobalScope.launch`
- **D.** Only works with `Dispatchers.Main`

**Answer: B** — `coroutineScope` is a suspend function that waits for all its children before returning, and if any child fails, it cancels the rest and rethrows.

---

## 2. Coroutine Builders

#### Q11. What does `launch` return?
- **A.** `Deferred<T>`
- **B.** `Job`
- **C.** The suspend function's return value
- **D.** `Unit` with no handle at all

**Answer: B** — `launch` returns a `Job` handle used to manage/cancel/join the coroutine, but it doesn't carry a result value.

#### Q12. What does `async` return?
- **A.** `Job`
- **B.** `Deferred<T>`, which holds a future result retrievable via `.await()`
- **C.** The computed value directly
- **D.** `Flow<T>`

**Answer: B** — `async` starts a coroutine that computes a value; `Deferred<T>` extends `Job` and adds `.await()` to retrieve the result.

#### Q13. What happens when you call `.await()` on a `Deferred`?
- **A.** It cancels the coroutine
- **B.** It suspends the caller until the result is available, then returns it (or rethrows its exception)
- **C.** It blocks the thread synchronously always
- **D.** It restarts the coroutine

**Answer: B** — `.await()` suspends (non-blocking) until the deferred coroutine completes, returning its result or propagating its exception.

#### Q14. Why is `runBlocking` generally discouraged in production Android code?
- **A.** It doesn't exist in Kotlin
- **B.** It blocks the calling thread until its coroutine body completes, which can freeze the UI thread
- **C.** It only works in unit tests
- **D.** It cannot call suspend functions

**Answer: B** — `runBlocking` bridges blocking and non-blocking code by blocking the current thread — dangerous on the main thread since it can cause ANRs.

#### Q15. When is `runBlocking` typically appropriate to use?
- **A.** Inside a ViewModel's `init` block
- **B.** In `main()` functions or unit tests where you need to bridge into suspend code synchronously
- **C.** Inside `LaunchedEffect` in Compose
- **D.** Whenever you want the fastest possible execution

**Answer: B** — It's a legitimate bridge for `main()` entry points and tests, not for use inside app lifecycle-aware code.

#### Q16. What's the effect of launching two `async` blocks and awaiting both sequentially vs. concurrently?
```kotlin
val a = async { fetchA() } // 1s
val b = async { fetchB() } // 1s
val result = a.await() + b.await()
```
- **A.** Total time ≈ 2s because `async` doesn't start until awaited
- **B.** Total time ≈ 1s because both start immediately and run concurrently
- **C.** Compile error
- **D.** Total time is unpredictable

**Answer: B** — Both `async` blocks start executing immediately upon creation; awaiting sequentially only waits for already-running work, so total time is ~max(1s, 1s) = 1s.

#### Q17. What does `supervisorScope { }` do differently from `coroutineScope { }`?
- **A.** Nothing, they're identical
- **B.** A failure in one child does not cancel sibling children (uses a `SupervisorJob` semantics)
- **C.** It runs children sequentially, not concurrently
- **D.** It ignores all exceptions silently

**Answer: B** — Under a supervisor, a child's failure doesn't automatically cancel its siblings, unlike regular structured concurrency.

#### Q18. What does `withContext(Dispatchers.IO) { ... }` do?
- **A.** Launches a new independent coroutine
- **B.** Suspends the current coroutine, switches to the IO dispatcher to run the block, then returns to the original context with the result
- **C.** Creates a `Deferred` you must await
- **D.** Permanently changes the dispatcher for the rest of the function

**Answer: B** — `withContext` is a suspend function that switches context for its block and returns the result on completion, resuming the original context afterward.

#### Q19. True or false: `withContext` runs concurrently with the code after it in the same coroutine.
- **A.** True — it's fire-and-forget
- **B.** False — it suspends the caller until the block finishes, so it's sequential, not concurrent
- **C.** True, but only on `Dispatchers.Default`
- **D.** False, because it always throws

**Answer: B** — Unlike `launch`/`async`, `withContext` doesn't start a concurrent branch; it just moves execution of the *same* logical coroutine to another dispatcher and waits for it.

#### Q20. Which builder would you choose to run two independent network calls in parallel and combine their results?
- **A.** Two sequential `withContext` calls
- **B.** Two `async` calls, then `awaitAll()` or sum their `.await()`s
- **C.** `runBlocking` nested twice
- **D.** `launch` for both, since `launch` returns values

**Answer: B** — `async`/`await` (or `awaitAll`) is the standard pattern for concurrent, result-producing work.

---

## 3. Dispatchers & Coroutine Context

#### Q21. What is `Dispatchers.Main` used for on Android?
- **A.** Background computation
- **B.** Running code on the Android UI/main thread (e.g., updating views)
- **C.** Disk I/O operations
- **D.** Networking exclusively

**Answer: B** — `Dispatchers.Main` confines execution to the main thread, required for UI updates.

#### Q22. What is `Dispatchers.IO` optimized for?
- **A.** CPU-bound math operations
- **B.** Blocking I/O operations like network calls, file, and database access
- **C.** UI rendering
- **D.** Garbage collection tuning

**Answer: B** — `Dispatchers.IO` uses a larger, elastic thread pool suited to blocking I/O work that would otherwise starve a small pool.

#### Q23. What is `Dispatchers.Default` optimized for?
- **A.** Blocking I/O
- **B.** CPU-intensive work such as sorting, parsing, or complex calculations
- **C.** UI updates
- **D.** It's an alias for `Dispatchers.Main`

**Answer: B** — `Dispatchers.Default` uses a thread pool sized to the number of CPU cores, appropriate for compute-heavy tasks.

#### Q24. What does `Dispatchers.Unconfined` do?
- **A.** Always runs on the main thread
- **B.** Starts the coroutine in the caller's thread and resumes it on whatever thread the suspending call completed on, without confining it to a specific pool
- **C.** Is the recommended default dispatcher for production code
- **D.** Prevents cancellation entirely

**Answer: B** — `Unconfined` doesn't dispatch to a specific thread/pool; it's mostly used for testing or specific low-level use cases, not general app code.

#### Q25. What is `CoroutineContext`?
- **A.** A single thread reference
- **B.** An indexed set of elements (Job, Dispatcher, CoroutineName, CoroutineExceptionHandler, etc.) that configures a coroutine's behavior
- **C.** The Android `Context` class
- **D.** A synonym for `CoroutineScope`

**Answer: B** — `CoroutineContext` is a composable set of elements; combining a `Job`, a `Dispatcher`, and other elements with `+` builds up a coroutine's configuration.

#### Q26. How do you combine a dispatcher and an exception handler into one context?
- **A.** `Dispatchers.IO.merge(handler)`
- **B.** `Dispatchers.IO + handler` (using the `CoroutineContext` `plus` operator)
- **C.** You cannot combine them
- **D.** `CoroutineScope(Dispatchers.IO, handler)`

**Answer: B** — `CoroutineContext` elements combine via the overloaded `+` operator, e.g. `Dispatchers.IO + CoroutineExceptionHandler { ... }`.

#### Q27. What thread does a coroutine resume on after `delay()` completes, by default?
- **A.** Always the main thread
- **B.** Whichever thread the dispatcher assigns — the thread isn't guaranteed to be the same one that suspended
- **C.** The thread that originally called `launch`
- **D.** A brand-new thread each time

**Answer: B** — Coroutines aren't tied to a specific thread; the dispatcher decides which thread from its pool resumes the coroutine after suspension.

#### Q28. What's the effect of `withContext(EmptyCoroutineContext)`?
- **A.** Cancels the coroutine
- **B.** No dispatcher change — it effectively just runs the block inline while still going through suspend/resume machinery
- **C.** Throws an exception
- **D.** Switches to `Dispatchers.Unconfined`

**Answer: B** — Without a dispatcher element, no thread-switch is forced; behavior is close to running inline within the current context.

---

## 4. Structured Concurrency, Job & Cancellation

#### Q29. What is "structured concurrency"?
- **A.** A design where coroutines can outlive their parent scope indefinitely
- **B.** A discipline where every coroutine has a defined parent/scope, so lifetimes are hierarchical and children complete or cancel with their parent
- **C.** A synonym for multithreading
- **D.** A Kotlin-specific term for `Thread` pooling

**Answer: B** — Structured concurrency ties coroutine lifetimes to their enclosing scope, preventing leaks and making cancellation propagate predictably.

#### Q30. What happens to child coroutines when a parent `Job` is cancelled?
- **A.** Nothing, they continue running
- **B.** They are also cancelled, since cancellation propagates down the job hierarchy
- **C.** They're paused, not cancelled
- **D.** Only the first child is cancelled

**Answer: B** — Cancelling a parent `Job` cancels all its children recursively.

#### Q31. Does cancelling a child coroutine cancel its parent?
- **A.** Yes, always
- **B.** No — a normal child's cancellation doesn't cancel the parent, only a failure (non-`CancellationException`) does under regular `Job`, not `SupervisorJob`
- **C.** Yes, but only under `SupervisorJob`
- **D.** Cancellation never propagates upward

**Answer: B** — Under a regular `Job`, an unhandled exception (not cancellation) in a child propagates up and cancels the parent and siblings; plain cancellation of one child doesn't.

#### Q32. Why must cooperative cancellation checks (like checking `isActive` or calling suspending functions) be present in long-running CPU loops for cancellation to work?
- **A.** Cancellation is automatic and doesn't require cooperation
- **B.** Coroutine cancellation is cooperative — a busy loop with no suspension points or `isActive` checks won't notice a cancellation request
- **C.** Kotlin forces a check every millisecond automatically
- **D.** Cancellation only works with `Dispatchers.IO`

**Answer: B** — Cancellation sets a flag/throws at suspension points; tight CPU-bound loops must explicitly check `isActive` or `ensureActive()` to respond.

#### Q33. What exception is thrown when a coroutine is cancelled?
- **A.** `IllegalStateException`
- **B.** `CancellationException`
- **C.** `InterruptedException`
- **D.** `TimeoutException` always

**Answer: B** — Cancellation works by throwing a `CancellationException` at the next suspension point, which propagates up the coroutine unless caught deliberately.

#### Q34. Is it safe to swallow `CancellationException` with a blanket `catch (e: Exception)`?
- **A.** Yes, it's just like any other exception
- **B.** No — it can break structured concurrency/cancellation propagation; you should rethrow it (or catch more specific exceptions)
- **C.** Yes, Kotlin ignores it automatically
- **D.** It can never be caught

**Answer: B** — Swallowing `CancellationException` prevents proper cancellation propagation; best practice is to catch specific exceptions or rethrow `CancellationException`.

#### Q35. What does `Job.join()` do?
- **A.** Cancels the job
- **B.** Suspends the caller until that job completes (successfully, by cancellation, or by failure)
- **C.** Merges two jobs into one
- **D.** Starts the job if it hasn't started

**Answer: B** — `join()` is a suspend function that waits for the job's completion without retrieving a result (unlike `Deferred.await()`).

#### Q36. What does `withTimeout(3000) { ... }` do if the block doesn't finish in time?
- **A.** Returns `null`
- **B.** Throws `TimeoutCancellationException`, cancelling the block
- **C.** Silently truncates the result
- **D.** Retries automatically

**Answer: B** — `withTimeout` cancels and throws if the deadline is exceeded; `withTimeoutOrNull` is the variant that returns `null` instead of throwing.

---

## 5. Exception Handling

#### Q37. How does exception handling differ between `launch` and `async`?
- **A.** They behave identically
- **B.** `launch` propagates exceptions immediately when uncaught; `async` stores the exception and only rethrows it when `.await()` is called
- **C.** `async` never throws exceptions
- **D.** `launch` never throws exceptions

**Answer: B** — An `async` coroutine's exception is encapsulated in its `Deferred` and only surfaces at `.await()`, while `launch` propagates immediately up the job hierarchy.

#### Q38. What is `CoroutineExceptionHandler` used for?
- **A.** Catching exceptions thrown by `async` at `.await()`
- **B.** Handling uncaught exceptions from `launch`-style coroutines at the root of a coroutine hierarchy (last-resort global handler)
- **C.** Retrying failed coroutines automatically
- **D.** Preventing all crashes app-wide

**Answer: B** — It's installed on a root scope's context to handle exceptions that would otherwise crash the app from `launch` coroutines; it does NOT catch exceptions from `async` awaited results.

#### Q39. Where must `CoroutineExceptionHandler` be installed to take effect?
- **A.** Anywhere, including on inner/nested `launch` calls
- **B.** On the outermost scope / root coroutine context — installing it on a child coroutine has no effect since exceptions propagate to the root
- **C.** Only inside `try/catch` blocks
- **D.** It cannot be installed on `viewModelScope`

**Answer: B** — The handler only fires when installed on the top-level scope context; nested coroutines' handlers are ignored because exceptions propagate up past them.

#### Q40. What happens if you wrap `launch { throw Exception() }` in a `try/catch` around the `launch` call itself?
```kotlin
try {
    scope.launch { throw Exception("boom") }
} catch (e: Exception) {
    println("caught")
}
```
- **A.** "caught" is printed
- **B.** The exception is NOT caught here — it happens asynchronously inside the child coroutine, not synchronously at the `launch` call site
- **C.** Compile error
- **D.** It's caught but the app still crashes

**Answer: B** — `launch` returns immediately; the exception occurs later inside the coroutine's own execution, so a `try/catch` around the builder call cannot intercept it. The `try/catch` must be inside the lambda.

#### Q41. With a regular (non-supervisor) `Job`, if one of three sibling `launch` children throws, what happens to the other two?
- **A.** Nothing, they run to completion
- **B.** They get cancelled too, because the exception propagates to the parent, which cancels all its children
- **C.** They restart automatically
- **D.** Only unrelated coroutines are affected

**Answer: B** — Regular structured concurrency cancels all siblings when one child fails, unless a `SupervisorJob`/`supervisorScope` is used.

#### Q42. How can you catch an exception from an `async` coroutine safely?
- **A.** Wrap the `async { }` call in try/catch
- **B.** Wrap the `.await()` call in try/catch
- **C.** Exceptions from `async` cannot be caught
- **D.** Use a `CoroutineExceptionHandler` on the scope

**Answer: B** — Since `async` stores the exception until `.await()` is called, that's where a `try/catch` needs to be to handle it.

#### Q43. What's a key risk of using `supervisorScope` incorrectly (e.g. as a bare `CoroutineScope(SupervisorJob())` at a deep nesting level)?
- **A.** It always crashes the app
- **B.** Exceptions can be silently swallowed if no exception handling/logging is added per child, since failures no longer propagate to cancel siblings or the parent
- **C.** It prevents all coroutines from launching
- **D.** It disables `delay()`

**Answer: B** — Since supervisor semantics stop automatic failure propagation, each child needs its own error handling (try/catch or per-child handler), or failures may go unnoticed.

#### Q44. What does `runCatching { suspendingCall() }` return?
- **A.** Nothing, it's void
- **B.** A `Result<T>` wrapping either the success value or the caught exception (but it should not blanket-catch `CancellationException`)
- **C.** Always throws regardless
- **D.** A `Deferred<T>`

**Answer: B** — `runCatching` returns `Result<T>`; developers must be careful it doesn't accidentally swallow `CancellationException`, which should generally be rethrown.

---

## 6. Flow

#### Q45. What is a `Flow<T>` in Kotlin coroutines?
- **A.** A single suspend function call returning one value
- **B.** A cold, asynchronous stream that can emit multiple values sequentially over time
- **C.** A hot stream that always starts emitting on creation
- **D.** A blocking iterator

**Answer: B** — `Flow` represents a cold asynchronous data stream — computation doesn't start until it's collected.

#### Q46. What does "cold" mean in the context of `Flow`?
- **A.** It runs on `Dispatchers.IO` only
- **B.** The flow's producer code doesn't execute until a terminal operator (like `collect`) is called, and re-executes for each new collector
- **C.** It caches its last emitted value for new subscribers
- **D.** It can only emit once

**Answer: B** — Unlike hot streams (e.g., `SharedFlow`/`StateFlow`), a cold `Flow`'s block runs independently for each collector, starting fresh each time.

#### Q47. Which function is used inside a `flow { }` builder to emit a value?
- **A.** `send()`
- **B.** `emit()`
- **C.** `yield()`
- **D.** `push()`

**Answer: B** — `emit()` is the suspend function used inside a `flow { }` builder body to emit values downstream.

#### Q48. What's the difference between `map` and `flatMapConcat` on a `Flow`?
- **A.** They're identical
- **B.** `map` transforms each value 1:1; `flatMapConcat` transforms each value into a new `Flow` and concatenates their emissions sequentially
- **C.** `flatMapConcat` runs flows in parallel
- **D.** `map` can only be used with `StateFlow`

**Answer: B** — `map` is a simple per-value transform; `flatMapConcat` handles cases where each value produces its own sub-flow, flattening them one at a time in order.

#### Q49. What does `StateFlow` guarantee that a plain `Flow` doesn't?
- **A.** It's cold and starts fresh per collector
- **B.** It's a hot, state-holder flow that always has a current value and conflates rapid updates (new collectors get the latest value immediately)
- **C.** It can never have duplicate consecutive values (this is `distinctUntilChanged` behavior, not guaranteed automatically)
- **D.** It supports backpressure via suspension of emit

**Answer: B** — `StateFlow` always holds a current value, is hot, and immediately gives new collectors the latest value; it also conflates (skips intermediate values) if collection is slow. (Note: it does drop duplicate *consecutive* equal values by design too, but the main distinguishing guarantee tested here is the hot/state-holder behavior.)

#### Q50. What's the key difference between `StateFlow` and `SharedFlow`?
- **A.** They are the same API under different names
- **B.** `StateFlow` always has an initial/current value and conflates; `SharedFlow` is more general — configurable replay/buffer, no required initial value
- **C.** `SharedFlow` is cold, `StateFlow` is hot
- **D.** `StateFlow` can emit multiple values per collector call, `SharedFlow` cannot

**Answer: B** — `StateFlow` is a specialized `SharedFlow` for representing state (single current value, conflated); `SharedFlow` is more flexible for events with configurable replay and buffering.

#### Q51. What operator would you use to run side-effecting code for each emission without transforming the value?
- **A.** `map`
- **B.** `onEach`
- **C.** `collect` only
- **D.** `filter`

**Answer: B** — `onEach` lets you perform an action per emission while passing the value through unchanged, often chained before a terminal operator.

#### Q52. What does `flowOn(Dispatchers.IO)` change?
- **A.** The dispatcher used by the collector
- **B.** The dispatcher used by the upstream flow's emission logic (everything above `flowOn` in the chain), not the downstream collector
- **C.** Nothing — it's purely documentation
- **D.** It cancels the flow after switching dispatchers

**Answer: B** — `flowOn` affects the context of upstream operators/producers only; code after `flowOn` (downstream, including the collector) is unaffected by that dispatcher change.

#### Q53. What does `catch { }` do in a Flow chain, and where should it be placed?
- **A.** It catches exceptions from downstream operators (placed after them)
- **B.** It catches exceptions from upstream emissions only, so it must be placed after the operators whose exceptions you want to catch, and before `collect`
- **C.** It catches all exceptions including those inside `collect`'s own lambda by default
- **D.** It's a synonym for `onCompletion`

**Answer: B** — `catch` only intercepts exceptions from upstream (producer side); it will not catch exceptions thrown inside the terminal `collect` block itself. Placement in the chain matters.

#### Q54. What is `combine` used for with multiple flows?
- **A.** Concatenating flows one after another
- **B.** Combining the latest values from multiple flows whenever any of them emits, producing a new combined value
- **C.** Merging flows without regard to timing, emitting whichever arrives first from any source
- **D.** Deduplicating flow values

**Answer: B** — `combine` re-emits a combined result using the latest value from each source flow every time any one of them emits a new value (after all have emitted at least once).

---

## 7. Channels, Testing & Android Integration

#### Q55. What is a `Channel<T>` used for?
- **A.** A read-only stream like `Flow`
- **B.** A concurrency primitive for communicating single values between coroutines, supporting send/receive with optional buffering
- **C.** A UI component in Compose
- **D.** A replacement for `LiveData`

**Answer: B** — `Channel` provides a hot, FIFO communication pipe between coroutines with `send`/`receive`, useful for one-off producer/consumer patterns (Flow's `channelFlow` builds on it).

#### Q56. What Android-provided scope is tied to a `ViewModel`'s lifecycle and automatically cancelled when the ViewModel is cleared?
- **A.** `GlobalScope`
- **B.** `viewModelScope`
- **C.** `lifecycleScope`
- **D.** `MainScope()` manually created

**Answer: B** — `viewModelScope` (from `androidx.lifecycle`) is automatically cancelled in `onCleared()`, preventing leaks tied to the ViewModel's lifetime.

#### Q57. What Android scope is tied to a Fragment/Activity's `Lifecycle` and commonly used with `repeatOnLifecycle`?
- **A.** `viewModelScope`
- **B.** `lifecycleScope`
- **C.** `GlobalScope`
- **D.** `applicationScope`

**Answer: B** — `lifecycleScope`, combined with `repeatOnLifecycle(Lifecycle.State.STARTED)`, is the recommended way to safely collect flows tied to UI lifecycle state.

#### Q58. Why is `GlobalScope` generally discouraged in app code?
- **A.** It doesn't support `launch`
- **B.** Coroutines launched in it live for the entire application process, with no structured lifecycle tie-in, risking leaks and making cancellation/testing harder
- **C.** It only works with `Dispatchers.Main`
- **D.** It's deprecated and no longer compiles

**Answer: B** — `GlobalScope` coroutines aren't cancelled with any UI/ViewModel lifecycle, so they can leak resources or keep running longer than intended.

#### Q59. In coroutine unit tests, what does `runTest { }` (from `kotlinx-coroutines-test`) provide over `runBlocking`?
- **A.** Nothing different
- **B.** A virtual/controllable time scheduler that can skip real delays (e.g., `advanceTimeBy`), making tests with `delay()` run near-instantly and deterministically
- **C.** It runs tests on real background threads only
- **D.** It disables coroutine cancellation in tests

**Answer: B** — `runTest` uses a `TestCoroutineScheduler` to fast-forward virtual time, letting tests avoid actually waiting for real-world delays while still exercising timing-dependent logic.

#### Q60. What is `TestDispatcher` (e.g. `StandardTestDispatcher`) used for in tests?
- **A.** To make coroutines run on real production dispatchers during tests
- **B.** To give deterministic, controllable scheduling of coroutines in tests, replacing `Dispatchers.Main`/`IO`/`Default` via dependency injection or `Dispatchers.setMain()`
- **C.** To disable coroutines entirely during testing
- **D.** It's only usable in instrumented (on-device) tests

**Answer: B** — Test dispatchers let you control execution order/timing deterministically; `Dispatchers.setMain(testDispatcher)` is commonly used to replace the Main dispatcher in unit tests.

---

*60/60 questions. Companion file: [jetpack-compose-mcq-quiz.md](jetpack-compose-mcq-quiz.md)*
