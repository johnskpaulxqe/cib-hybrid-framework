# How to Use This Framework

## 10-step adoption checklist

1. Clone the repo and open in IntelliJ IDEA or Eclipse.
2. Run `mvn test-compile` to confirm the skeleton compiles as-is.
3. Run the one-time Playwright browser install: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"`.
4. Update `config.json` — replace the placeholder `baseUrl`, `apiBaseUrl`, and DB connection details for QA/UAT.
5. Replace placeholder selectors in `Locators.java` with real ones from the actual CIB application markup (prefer `data-testid` attributes if your dev team can add them).
6. Replace placeholder page objects (`LoginPage`, `PaymentInitiationPage`) and add real ones for the actual modules (Trade Finance, FX Deals, Cash Management, Reports, etc.) — copy the existing pattern.
7. Replace placeholder test data in `users.json` / `apidata.json` / `dbdata.json` with real credentials (via env var placeholders, never plaintext) and real endpoints/queries.
8. Replace `PaymentRepository` with repositories for your actual DB tables, or extend it if `cib_payments` matches your schema.
9. Rewrite the feature files under `features/{ui,api,db}/` to reflect real CIB user journeys, keeping the `@smoke`/`@regression`/`@api`/`@db` tagging convention.
10. Run the smoke suite end-to-end, fix any red IDE imports (expected until all files are dropped in), then commit.

## First run commands

```bash
# Compile
mvn test-compile

# Smoke suite (headless Chromium, QA env)
mvn test -Dtest=SmokeTestRunner -Dbrowser=chromium -Dheadless=true -Denv=QA

# Full regression (headed Firefox, UAT env)
mvn test -Dtest=RegressionTestRunner -Dbrowser=firefox -Dheadless=false -Denv=UAT

# API only
mvn test -Dtest=ApiTestRunner -Denv=QA

# DB only
mvn test -Dtest=DbTestRunner -Denv=QA
```

## Switching environments

Same priority chain as before:
```
-Denv=xxx              ← CLI / IDE run configuration (highest)
ACTIVE_ENV=xxx          ← OS environment variable (e.g. set by CI)
config.json "environment" field
"QA"                    ← hardcoded fallback
```

**In IntelliJ:** Right-click a runner → *Modify Run Configuration* → VM options: `-Denv=UAT -Dbrowser=firefox -Dheadless=false`

## Swapping the database driver

Currently wired for MySQL/MariaDB via `mysql-connector-j`. To swap:

1. Replace the dependency in `pom.xml` (e.g. `org.postgresql:postgresql` for PostgreSQL, `com.oracle.database.jdbc:ojdbc11` for Oracle, `com.microsoft.sqlserver:mssql-jdbc` for SQL Server).
2. Update `dbUrl` in `config.json` to the matching JDBC URL format.
3. `DBUtils.java` needs no changes — `DriverManager.getConnection()` is driver-agnostic as long as the right JDBC jar is on the classpath.

## Adding a locator, page object, and wiring it into TestContext

```java
// 1. Locators.java — add a nested class
public static class TradeFinance {
    public static final String LC_NUMBER_INPUT = "#lcNumber";
    public static final String CREATE_DEAL_BUTTON = "[data-testid='create-deal-button']";
}

// 2. New page object
public class TradeFinancePage extends BasePage {
    public void createLetterOfCredit(String lcNumber) {
        navigateToPath("/trade-finance/new");
        type(Locators.TradeFinance.LC_NUMBER_INPUT, lcNumber);
        click(Locators.TradeFinance.CREATE_DEAL_BUTTON);
    }
}

// 3. TestContext.java — add lazy accessor
private TradeFinancePage tradeFinancePage;
public TradeFinancePage getTradeFinancePage() {
    if (tradeFinancePage == null) tradeFinancePage = new TradeFinancePage();
    return tradeFinancePage;
}

// 4. New step definition class, injected the same way as the others
public class TradeFinanceSteps {
    private final TradeFinancePage tradeFinancePage;
    public TradeFinanceSteps(TestContext ctx) {
        this.tradeFinancePage = ctx.getTradeFinancePage();
    }
}
```

## Common first-run error fixes

| Error | Fix |
|---|---|
| `IllegalStateException: Page is not initialized for this thread` | You called a page object method outside a `@ui`/`@smoke`/`@regression`-tagged scenario — `PlaywrightManager.initDriver()` only fires for those tags in `Hooks.java`. |
| `baseUrl is not configured for environment [QA]` | `config.json` placeholder URL wasn't replaced, or `-Denv=` points to an environment block that doesn't exist in `config.json`. |
| Extent report has no step detail | Confirm `com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:` is in the runner's `plugin` list and `extent.properties` is on the classpath. |
| Browser fails to launch in CI | Confirm the `mvn exec:java ... CLI install --with-deps chromium` step ran before the test step in the pipeline. |

## Quick reference command sheet

```bash
mvn test -Dtest=SmokeTestRunner                          # smoke, default env/browser
mvn test -Dcucumber.filter.tags="@smoke and not @wip"    # tag override on any runner
mvn test -Denv=UAT -Dbrowser=firefox -Dheadless=false     # env + browser override
mvn test -Dtest=RegressionTestRunner -Dlog.level=DEBUG    # verbose logging
```

## File locations reference

| File | Path |
|---|---|
| Environment config | `src/test/resources/config.json` |
| Test data | `src/test/resources/testdata/*.json` |
| Feature files | `src/test/resources/features/{ui,api,db}/*.feature` |
| Cucumber HTML report | `target/cucumber-reports/<runner>/cucumber.html` |
| Extent report | `target/extent-reports/report.html` |
| Logs | `target/logs/test-run.log` |
| Screenshots | `target/screenshots/` |
