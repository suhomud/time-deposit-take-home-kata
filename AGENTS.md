# AI Agent Guidelines

## General Principles

- Prefer simple, maintainable solutions over unnecessary abstractions.
- Follow the existing architecture, conventions, and coding style.
- Keep changes focused on the requested task.
- Avoid introducing dependencies, frameworks, or design patterns without a clear benefit.
- Consider readability, maintainability, and future extensibility when making technical decisions.
- Do not make assumptions about business requirements. Ask for clarification when necessary.

## Code Quality

- Follow language and framework best practices.
- Apply SOLID principles where they provide practical value, not mechanically.
- Keep business logic separate from infrastructure concerns where appropriate.
- Use meaningful names and avoid unnecessary comments.
- Handle errors explicitly and avoid silently ignoring failures.
- Preserve backward compatibility unless breaking changes are explicitly requested.

## Testing

- Add or update tests when changing behavior.
- Cover important business rules, edge cases, and failure scenarios.
- Prefer focused unit tests for business logic and integration tests for interactions with external systems.
- Run relevant tests after making changes.

## Working Process

- Understand the existing implementation before proposing changes.
- Prefer small, incremental changes that are easy to review.
- Explain significant architectural decisions and their trade-offs.
- Consider existing functionality that might be affected by a change.
- Do not refactor unrelated code without a clear reason.
- Do not modify or remove existing tests merely to make a failing implementation pass.

## Communication

- Keep explanations concise and technically accurate.
- Challenge questionable approaches and suggest alternatives when appropriate.
- Be transparent about assumptions, limitations, and uncertainty.
- Distinguish between required changes and optional improvements.
- Prefer practical recommendations over theoretical complexity.