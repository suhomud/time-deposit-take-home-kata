# Time Deposit Refactoring Kata - Take-Home Assignment

## XA Bank Time Deposit

### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

6. **AI-Assisted Development**:
    - Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise.
    - Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration).
    - Ensure your AI setup is practical and reproducible.
    - Include any custom rules, system prompts, or agent configurations used.
    - Include a brief summary of which parts of the solution were AI-assisted and why.

### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.
- Leverage AI-assisted development tools for code generation, testing, and refactoring.

### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.

---

# Implementation Yevhen Sukhomud

## Getting Started

### Requirements
- Docker

### Run the application

From the repository root:

```bash
cd kotlin
docker compose up --build
```

### API

The API contract and interactive documentation are available through
[Swagger UI](http://localhost:8080/swagger-ui.html).

Endpoints:
- `GET /api/time-deposits` — Retrieve all deposits and their withdrawal history.
- `POST /api/time-deposits/balance-updates` — Calculate and update balances for all deposits.

Both endpoints can be executed directly through Swagger UI.

### Run tests

From the `kotlin` directory, using JDK 21, Maven, and Docker:

```bash
mvn clean verify
```

Integration tests use [Testcontainers](https://java.testcontainers.org/modules/databases/postgres/)
to start a temporary PostgreSQL 17 container. No running Compose services are required.

To run only unit tests, without Docker:

```bash
mvn -Dtest='*Test,!*IntegrationTest' test
```

## AI-Assisted Development

I used AI to discuss implementation ideas, review parts of the code, and help write some tests. The solution was not generated entirely by AI.

### Tools

- **ChatGPT** — Used to discuss architectural decisions, explore
  implementation alternatives, and assist with writing selected parts
  of the code.
- **OpenAI Codex** — Used to review and validate the implementation,
  identify potential issues, and assist with writing some of the tests.

### Agent Configuration

The repository includes an [AGENTS.md](AGENTS.md) file with general
guidelines for AI-assisted development, covering code quality,
maintainability, testing.

## Assumptions and Trade-offs

### Package Compatibility

`TimeDeposit` and `TimeDepositCalculator` remain in their original `org.ikigaidigital`
package to preserve compatibility with existing callers.

### Monetary Precision

The original [TimeDeposit](kotlin/src/main/kotlin/org/ikigaidigital/TimeDeposit.kt)
model uses `Double` for balances. I preserved this for backward compatibility.

The [TimeDepositPersistenceAdapter](kotlin/src/main/kotlin/org/ikigaidigital/infrastructure/adapter/outbound/persistence/TimeDepositPersistenceAdapter.kt)
converts between `BigDecimal` (persistence) and `Double` (domain), which may
introduce precision loss.

For a production financial system, I would prefer `BigDecimal` throughout
the domain, provided the existing contract could be changed.
