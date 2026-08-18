# Contributing to the CIB Hybrid Framework

## Branch naming

`feature/<short-description>`, `fix/<short-description>`, `docs/<short-description>`, `chore/<short-description>`

## PR process

1. Rebase onto `main` before opening a PR.
2. `mvn test-compile` must pass locally.
3. Run the smoke suite locally: `mvn test -Dtest=SmokeTestRunner -Dheadless=true`
4. Open the PR — CI runs Build → Smoke → API automatically.

## Coding standards

- **No `Thread.sleep()`** for synchronization — Playwright auto-waits on actions; for custom async conditions use `WaitUtils.fluentWaitForCondition(...)`.
- **No raw `Page`/`Locator` calls in step definitions** — every interaction goes through a page object method.
- **No hardcoded URLs, credentials, or environment values** — everything comes from `ConfigReader` / `config.json` / `TestDataLoader`.
- **Commit messages** follow Conventional Commits: `feat:`, `fix:`, `docs:`, `chore:`, `test:`.

## Adding a new UI scenario

1. Add/extend a page object in `pages/` (extends `BasePage`).
2. Add selectors to `Locators.java` under a new nested class if it's a new page.
3. Add step definitions in `stepdefinitions/`.
4. Add the scenario to the relevant `.feature` file under `src/test/resources/features/ui/`, tagged `@smoke` and/or `@regression`.

## Adding a new API scenario

1. Add the endpoint to `testdata/apidata.json` under `endpoints`.
2. Add any request body under `requestBodies`.
3. Add/extend step definitions in `ApiSteps.java`, using `ApiUtils` for simple calls or `RequestBuilder` for path params/custom headers.
4. Add the scenario to `src/test/resources/features/api/api.feature`, tagged `@api`.

## Adding a new DB-backed entity

1. Create `<Entity>Repository.java` in `repository/`, extending `BaseRepository` — copy the pattern in `PaymentRepository.java`.
2. Add queries to `testdata/dbdata.json` if reusable across scenarios.
3. Add step definitions in `DbSteps.java` (or a new `<Entity>DbSteps.java` if the file is getting large).
4. Always add a cleanup method (`deleteTest<Entity>`) and call it from an `@After` step or hook for DB-writing scenarios.

## Adding a new utility class

Follow the existing pattern: static-only class, private constructor, methods delegate to `ConfigReader`/`LogUtil` rather than duplicating logic. Add it to the relevant phase table in this repo's build history (or just to `README.md`'s project structure section) so the next contributor can find it.
