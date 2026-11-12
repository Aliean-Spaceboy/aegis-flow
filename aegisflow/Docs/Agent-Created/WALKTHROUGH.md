# AegisFlow Walkthrough — Task 001

> **Scope Notice:** Task 001 establishes only the Spring Boot project skeleton, three empty immutable DTO records, and `InMemoryHashMapLookup<K, V>`. Controllers, Services, Repositories, and Databases are explicitly **out of scope** for this step and do not exist yet.

---

## 1. Execution Flow

Because no HTTP endpoints, services, repositories, or database connections exist in Task 001, here is the exact runtime execution flow for what is currently implemented, followed by how these pieces map into the future `Controller -> DTO -> Service -> Repository -> DB` pipeline:

### A. Current Runtime Flow (Application Boot & In-Memory Utility Invocation)

1. **JVM & Spring Boot Entry (`AegisFlowApplication.main`)**:
   - `SpringApplication.run(AegisFlowApplication.class, args)` starts the Spring Boot bootstrap sequence.
   - `SpringBootApplication` triggers component scanning under `com.aegisflow` and loads `application.properties` (`spring.application.name=AegisFlow`).
2. **Auto-Configuration & Embedded Web Container**:
   - Because `spring-boot-starter-web` is on the classpath, Spring Boot creates a `ServletWebServerApplicationContext` and auto-configures `TomcatWebServer` (Apache Tomcat `11.0.27`) along with `DispatcherServlet` and Jackson HTTP message converters.
   - Because no JDBC/JPA starters exist, **no `DataSource` bean is created** and no database connection is attempted.
3. **In-Memory Lookup Execution (`InMemoryHashMapLookup<K, V>`)**:
   - A caller instantiates `new InMemoryHashMapLookup<K, V>()` (which allocates a private instance-scoped `HashMap<K, V>` on the JVM heap).
   - `put(key, value)` hashes `key` and writes the bucket entry into `entries`.
   - `get(key)` hashes `key` and returns the mapped value or `null` if absent.
   - `containsKey(key)` returns `true` only if the key mapping exists (even if its value were `null`), `remove(key)` deletes the mapping and returns the prior value, `clear()` drops all bucket references, and `size()` returns `entries.size()`.
   - When the JVM exits, the heap is reclaimed and all entries disappear.

### B. Mapping to Future Request Pipeline (`Controller -> DTO -> Service -> Repository -> DB`)

- **Controller** *(not yet implemented)*: Tomcat + `DispatcherServlet` will route HTTP JSON requests and deserialize bodies into `TransferRequest` / `LoanApplicationDto`, returning `LedgerReceipt`.
- **DTO (`com.aegisflow.dto.*`)** *(implemented as empty `record` placeholders)*: Will carry immutable request/response payloads across the web/service boundary.
- **Service** *(not yet implemented)*: Will contain business rules and may temporarily or locally use `InMemoryHashMapLookup` for fast per-operation or non-persistent lookups.
- **Repository / DB** *(not yet implemented)*: Will handle durable ACID persistence (strictly out of scope in Task 001).

---

## 2. Why This Design (Tradeoffs)

| Component / Choice | Why Chosen Over Simpler Alternative | What Breaks If Removed / Changed |
| --- | --- | --- |
| **`@SpringBootApplication` on `AegisFlowApplication`** | Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan` in one canonical entry point instead of manually wiring `AnnotationConfigWebApplicationContext`, Tomcat, and `DispatcherServlet`. | Removing `@SpringBootApplication` stops auto-configuration and component scanning; `SpringApplication.run(...)` fails to start the web server or register beans (`@SpringBootTest` context bootstrap fails). |
| **Java `record` (`TransferRequest`, `LedgerReceipt`, `LoanApplicationDto`)** | Enforces `final` class semantics, shallow immutability, zero mutable setters, and built-in `equals`/`hashCode`/`toString` directly in the language without Lombok boilerplate (`@Value`). | Replacing `record` with a plain `class` loses compiler-enforced immutability, `java.lang.Record` identity (`Class.isRecord()`), and structural `equals`/`hashCode`/`toString`, causing `DtoRecordsTest` to fail. |
| **Encapsulated `HashMap<K, V>` in `InMemoryHashMapLookup`** | Keeps state isolated per instance (`private final HashMap<K, V> entries`) with simple $O(1)$ average-case lookup, instead of a `static` global map (which causes cross-test/cross-caller state leakage) or Redis/Caffeine (forbidden dependencies). | Making `entries` `static` breaks instance isolation (`separateInstancesMaintainIndependentState` fails) and creates global mutable state. Exposing the raw `HashMap` breaks encapsulation. |
| **`spring-boot-starter-web`** | Provisions embedded Tomcat, Spring MVC, and Jackson converters as a cohesive starter instead of manually assembling Servlet API, Tomcat, and Jackson artifacts with mismatched versions. | Removing it disables web application context initialization and HTTP server startup (Spring Boot drops to a non-web context). |
| **`spring-boot-devtools` (`runtime`, `optional=true`)** | Enables fast local class-reload restarts during development while preventing downstream projects or production fat JARs from inheriting DevTools. | Removing `optional=true` or excluding it improperly risks shipping development restart hooks; removing the dependency entirely only removes auto-restart convenience (tests still pass). |
| **`spring-boot-starter-test` (`scope=test`)** | Bundles aligned JUnit Jupiter (`6.0.3`), AssertJ (`3.27.7`), Mockito (`5.23.0`), and `@SpringBootTest` without risking version skew or polluting production runtime. | Removing `<scope>test</scope>` leaks test frameworks (`junit`, `mockito`, `assertj`) into `BOOT-INF/lib/` of the production JAR; removing the starter breaks compilation of all test classes. |
| **`lombok` (`scope=provided`, `optional=true` + `annotationProcessorPaths`)** | Ensures Lombok is available to `javac` during `compile`/`testCompile` without bundling `lombok.jar` into the packaged fat JAR's `BOOT-INF/lib/`. | Removing `<annotationProcessorPaths>` in `maven-compiler-plugin` (on modern Maven/Spring Boot) can stop Lombok AST annotation processing from running when future classes use Lombok; removing `scope=provided` embeds unnecessary compile-only bytecode tooling in the runtime JAR. |
| **`<java.version>17</java.version>` on JDK 22 host** | Sets `--release 17` on `javac` so compiled `.class` files target Java 17 (`major version: 61`) even when built on a newer host JDK (22.0.2). | Removing `<java.version>17</java.version>` defaults compilation to Spring Boot parent's baseline (`17` here, or host JDK if unconfigured), risking accidental usage of Java 22 APIs that fail on Java 17 runtimes. |
| **Minimal `application.properties` (`spring.application.name=AegisFlow`)** | Gives Spring Boot an explicit application identity in logs without premature datasource, server, or actuator settings. | Removing `spring.application.name` reverts the log banner/app name to `application`, breaking `AegisFlowApplicationTests` assertion on `spring.application.name`. |

---

## 3. Interview Hooks

### Q1. Why can an empty Java `record` (`public record TransferRequest() {}`) be instantiated, and what subtle trap happens if Jackson tries to serialize/deserialize it later?
**Answer:** The compiler still generates a `public final` subclass of `java.lang.Record` with a zero-arg canonical constructor and `equals`/`hashCode`/`toString` (where all empty instances of the same type are equal and `hashCode()` returns `0`). However, when Jackson (`spring-boot-starter-web`) serializes a 0-component record, `FAIL_ON_EMPTY_BEANS` is enabled by default in classic Jackson `ObjectMapper` configurations unless configured or fields are added—so attempting to return an empty `TransferRequest()` from a REST controller without fields or Jackson configuration can throw `InvalidDefinitionException: No serializer found for class ...`.

### Q2. `InMemoryHashMapLookup` wraps a `HashMap<K, V>`. What breaks if a future Spring `@RestController` injects a single shared instance across concurrent HTTP requests, and how does `get(key)` behave with `null` values?
**Answer:** Embedded Tomcat serves each HTTP request on a separate worker thread. `HashMap` is **not** thread-safe: concurrent `put()` calls during resizing can lose updates, corrupt internal table/tree links, or throw `ConcurrentModificationException` (unlike `ConcurrentHashMap`). Additionally, `get(key)` returning `null` is ambiguous between "key is absent" and "key is explicitly mapped to `null`", which is why `containsKey(key)` is exposed separately—whereas `ConcurrentHashMap` forbids `null` keys and `null` values entirely.

### Q3. Why did we configure Lombok with BOTH `<scope>provided</scope>`/`<optional>true</optional>` in `<dependencies>` AND `<annotationProcessorPaths>` in `maven-compiler-plugin`? And how did we guarantee Java 17 compatibility when the machine runs JDK 22?
**Answer:** Starting with recent Maven Compiler Plugin / JDK releases, annotation processors on the compile classpath are not automatically discovered unless explicitly declared via `<annotationProcessorPaths>` (or `-proc:full`), so declaring `lombok` inside `annotationProcessorPaths` guarantees annotation processing runs cleanly in both `compile` and `testCompile` while `<scope>provided</scope>` keeps `lombok.jar` out of `BOOT-INF/lib/` in the Spring Boot fat JAR. To guarantee Java 17 compatibility on a JDK 22 host, `<java.version>17</java.version>` configures `--release 17`, which restricts both the standard library APIs visible at compile time to JDK 17 and emits class file major version `61` (verified via `javap -v`), preventing accidental Java 22 API/bytecode leakage.

