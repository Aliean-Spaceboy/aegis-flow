# AegisFlow

Initial Spring Boot backend foundation for **AegisFlow** (Task 001).

## Purpose

Establish a clean, minimal, verified Spring Boot Maven project using Java 17 bytecode/language compatibility, four approved dependencies, three empty immutable Java record DTOs, and a generic in-memory `HashMap` lookup utility.

> **Status:** Initial foundation only — **not** a production financial system.

## Technology Stack

- **Language Target:** Java 17 (`<java.version>17</java.version>`, bytecode major version `61`)
- **Framework:** Spring Boot `4.1.2` (GA)
- **Build Tool:** Apache Maven (`3.9.9` used during verification)
- **Packaging:** JAR (`com.aegisflow:aegisflow:0.0.1-SNAPSHOT`)

## Approved Direct Dependencies

Exactly four direct application dependencies are configured in [pom.xml](pom.xml):

1. `org.springframework.boot:spring-boot-starter-web` (compile/runtime) — Embedded Tomcat and Spring Web MVC stack. *(Note: in Spring Boot 4.1.2, `spring-boot-starter-web` is retained for compatibility and marked deprecated in favor of `spring-boot-starter-webmvc`.)*
2. `org.springframework.boot:spring-boot-devtools` (`runtime`, `optional=true`) — Developer-time automatic restart support (excluded from repackaged fat JAR).
3. `org.springframework.boot:spring-boot-starter-test` (`test`) — JUnit Jupiter, AssertJ, Mockito, and Spring Boot test support.
4. `org.projectlombok:lombok` (`provided`, `optional=true`) — Compile-time annotation processor configured via `maven-compiler-plugin` `annotationProcessorPaths`.

## Package Structure

```text
aegisflow/
├── pom.xml
├── README.md
├── PROJECT_MEMORY.md
├── WALKTHROUGH.md
└── src/
    ├── main/
    │   ├── java/com/aegisflow/
    │   │   ├── AegisFlowApplication.java
    │   │   ├── dto/
    │   │   │   ├── TransferRequest.java
    │   │   │   ├── LedgerReceipt.java
    │   │   │   └── LoanApplicationDto.java
    │   │   └── util/
    │   │       └── InMemoryHashMapLookup.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/aegisflow/
            ├── AegisFlowApplicationTests.java
            ├── dto/
            │   └── DtoRecordsTest.java
            └── util/
                └── InMemoryHashMapLookupTest.java
```

## How to Run Tests and Build

From the `aegisflow/` directory:

```bash
mvn clean test
mvn package
```

## How to Run the Application

```bash
mvn spring-boot:run
```

Or run the packaged JAR directly:

```bash
java -jar target/aegisflow-0.0.1-SNAPSHOT.jar
```

By default, Tomcat listens on port `8080` (`--server.port=0` can be passed for an ephemeral port).

## Current Limitations

- **No persistence or database:** No JDBC, JPA, SQL driver, Redis, or external storage is configured.
- **Volatile in-memory utility:** `InMemoryHashMapLookup<K, V>` stores data strictly in JVM heap memory for the lifetime of an instance; all entries are lost when the process stops, and the utility is **not** thread-safe.
- **Empty DTOs:** `TransferRequest`, `LedgerReceipt`, and `LoanApplicationDto` are intentionally zero-component empty Java records with no fields, validation, or annotations.
- **No API endpoints or business logic:** No REST controllers, services, repositories, security, or domain workflows exist yet.

