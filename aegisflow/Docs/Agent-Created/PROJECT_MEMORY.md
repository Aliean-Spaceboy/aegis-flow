# AegisFlow Project Memory

## 1. Procedural Memory

### How to Initialize and Build AegisFlow
- Project root lives at `e:\Agees-Flow\aegisflow`.
- Build and test with Maven from `aegisflow/`:
  - `mvn clean test` — compiles Java 17 bytecode (`--release 17`) and executes JUnit 6 / Spring Boot tests.
  - `mvn package` — builds executable Spring Boot fat JAR (`target/aegisflow-0.0.1-SNAPSHOT.jar`).
  - `mvn dependency:tree` — audits direct and transitive dependencies.
- Verify target bytecode version with `javap -v target\classes\...\*.class` (`major version: 61` = Java 17).

### Dependency Approval Rules
- Only four direct application dependencies are authorized:
  1. `org.springframework.boot:spring-boot-starter-web`
  2. `org.springframework.boot:spring-boot-devtools` (`runtime`, `optional`)
  3. `org.springframework.boot:spring-boot-starter-test` (`test`)
  4. `org.projectlombok:lombok` (`provided`, `optional`, plus `maven-compiler-plugin` `annotationProcessorPaths`)
- No additional direct dependencies (JPA, JDBC, drivers, Security, Validation, Actuator, Redis, Kafka, MapStruct, etc.) may be added without explicit Product Owner approval.
- Versions are managed by `spring-boot-starter-parent` (`4.1.2`); avoid independent version pinning.

### Java and Maven Conventions
- Group ID: `com.aegisflow`, Artifact ID: `aegisflow`, Packaging: `jar`.
- Target Java version: `17`.
- Standard package layout: `com.aegisflow` (application entry point), `com.aegisflow.dto` (immutable record DTOs), `com.aegisflow.util` (pure Java utilities).

---

## 2. Contextual Memory

### Current Project Architecture
- Minimal Spring Boot `4.1.2` web foundation with zero controllers, services, repositories, entities, or security layers.
- Single configuration file: `src/main/resources/application.properties` (`spring.application.name=AegisFlow`).

### Current Package & Class Inventory
- `com.aegisflow.AegisFlowApplication` — `@SpringBootApplication` entry point.
- `com.aegisflow.dto.TransferRequest` — empty `public record TransferRequest() {}`.
- `com.aegisflow.dto.LedgerReceipt` — empty `public record LedgerReceipt() {}`.
- `com.aegisflow.dto.LoanApplicationDto` — empty `public record LoanApplicationDto() {}`.
- `com.aegisflow.util.InMemoryHashMapLookup<K, V>` — generic non-thread-safe, heap-only lookup backed by an encapsulated `HashMap<K, V>`.

### Implementation State
- Task 001 foundation is complete and verified; awaiting Product Owner authorization before adding fields, endpoints, persistence, or dependencies.

---

## 3. Episodic Memory

### Task 001 Execution Log
- **Environment inspection:**
  - Workspace `e:\Agees-Flow` had no prior `aegisflow` directory and no `.git` repository.
  - Installed JDKs in `C:\Program Files\Java`: `jdk-11` and `jdk-22` (active `java` 22.0.2). No standalone JDK 17 installation was present; Maven compiles with `<java.version>17</java.version>` (`--release 17`), verified to emit class file major version `61`.
  - Maven version: Apache Maven `3.9.9`.
  - Spring Boot version check via `https://start.spring.io/metadata/client` and Maven Central confirmed `4.1.2` (`4.1.2.RELEASE`) as the current stable GA release supporting Java 17 minimum.
- **Implementation:**
  - Created `pom.xml`, `AegisFlowApplication.java`, `application.properties`, the three empty record DTOs, `InMemoryHashMapLookup.java`, and 3 test classes (`AegisFlowApplicationTests`, `DtoRecordsTest`, `InMemoryHashMapLookupTest`).
- **Commands executed & verification results:**
  - `mvn clean test`: Passed (`11` tests run, `0` failures, `0` errors, `0` skipped; exit code `0`).
  - `mvn package`: Passed (`target/aegisflow-0.0.1-SNAPSHOT.jar` built; exit code `0`).
  - `mvn dependency:tree -DoutputType=text`: Passed; confirmed exactly 4 direct dependencies (`spring-boot-starter-web`, `spring-boot-devtools`, `spring-boot-starter-test`, `lombok`).
  - `javap -v` on all 5 compiled main classes: Confirmed `major version: 61` (Java 17).
  - `jar tf target\aegisflow-0.0.1-SNAPSHOT.jar`: Confirmed `lombok`, `spring-boot-devtools`, and `spring-boot-starter-test` are excluded from `BOOT-INF/lib/`.
  - Live JAR startup (`java -jar target\aegisflow-0.0.1-SNAPSHOT.jar --server.port=0`): Embedded Apache Tomcat `11.0.27` initialized and started cleanly in `1.962` seconds (`Started AegisFlowApplication in 1.962 seconds`).
- **Discrepancy noted:**
  - Phase 4 tree listed `application.yml`, while Phase 7 explicitly required `application.properties`; `application.properties` was created to follow Phase 7.

