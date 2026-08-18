# CIB Hybrid Automation Framework

Hybrid test automation framework for the **Corporate and Institutional Banking (CIB) Service** — UI (Playwright), API (REST Assured), and Database (JDBC/MySQL) layers, driven by Cucumber BDD and executed via TestNG.

## Stack

| Concern | Library | Version |
|---|---|---|
| UI driver | Playwright | 1.45.0 |
| BDD | Cucumber 7 + PicoContainer DI | 7.18.0 |
| Test framework | TestNG | 7.10.2 |
| API | REST Assured | 5.4.0 |
| Database | MySQL Connector/J | 8.4.0 |
| Reporting | Extent Reports 5 + Cucumber7 adapter | 5.1.1 / 1.14.0 |
| Logging | SLF4J + Logback | 2.0.13 / 1.5.6 |
| JSON | Jackson Databind | 2.17.1 |

## Project structure

```
cib-hybrid-framework/
├── .github/workflows/github-actions.yml
├── azure-pipelines.yml
├── docs/
│   ├── CONTRIBUTING.md
│   └── HOW_TO_USE.md
├── pom.xml
├── testng.xml
└── src/test/
    ├── java/
    │   ├── base/          BaseTest.java
    │   ├── context/       TestContext.java
    │   ├── hooks/         Hooks.java
    │   ├── pages/         BasePage.java, LoginPage.java, PaymentInitiationPage.java
    │   ├── repository/    BaseRepository.java, PaymentRepository.java
    │   ├── runners/       Smoke/Regression/Api/Db/FullSuite runners
    │   ├── stepdefinitions/  LoginSteps, PaymentSteps, ApiSteps, DbSteps
    │   └── utils/         PlaywrightManager, ConfigReader, LogUtil, WaitUtils,
    │                       ScreenshotUtils, CustomAssert, JsonReader, TestDataLoader,
    │                       Locators, PageVerifier, ApiUtils, RequestBuilder,
    │                       ResponseValidator, DBUtils, ExtentReportManager,
    │                       CucumberReportUtils
    └── resources/
        ├── config.json
        ├── logback.xml
        ├── extent.properties / extent-config.xml
        ├── features/{ui,api,db}/*.feature
        └── testdata/{users,apidata,dbdata}.json
```

## Placeholder modules

This scaffold ships with placeholder CIB modules to demonstrate the pattern — swap these for the real application's pages/endpoints/tables:

`Login`, `Dashboard`, `AccountSummary`, `PaymentInitiation`, `TradeFinance`, `FXDeals`, `CashManagement`, `Reports`.

## Quick start

```bash
git clone <your-repo-url>
cd cib-hybrid-framework
mvn test-compile

# One-time Playwright browser install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"

# Run smoke suite
mvn test -Dtest=SmokeTestRunner -Dbrowser=chromium -Dheadless=true -Denv=QA
```

Reports land in `target/cucumber-reports/` (per-runner subfolder) and `target/extent-reports/report.html`.

## Tagging strategy

| Tag | Runner | When |
|---|---|---|
| `@smoke` | `SmokeTestRunner` | Every push / PR |
| `@regression` | `RegressionTestRunner` | Nightly / release |
| `@api` | `ApiTestRunner` | Every push |
| `@db` | `DbTestRunner` | Nightly |
| *(none — full suite)* | `FullSuiteRunner` | Nightly / release only |

See `docs/HOW_TO_USE.md` for the full adoption checklist and `docs/CONTRIBUTING.md` for coding standards.
