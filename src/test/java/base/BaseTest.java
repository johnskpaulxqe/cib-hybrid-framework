package base;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.LogUtil;

/**
 * Suite-level setup and teardown — runs ONCE per suite, not per scenario.
 * Every runner extends this (not AbstractTestNGCucumberTests directly) so
 * the @BeforeSuite/@AfterSuite hooks actually fire regardless of whether
 * the run is launched via testng.xml, an IDE class runner, or Maven CLI.
 *
 * Scope comparison with Hooks.java:
 *   BaseTest  → once per suite  → config validation, report init/flush
 *   Hooks     → once per scenario → browser/API/DB lifecycle
 *
 * NOTE: @CucumberOptions is deliberately NOT declared here — it is not
 * inherited by subclasses, and each runner needs its own tag filter
 * (smoke/regression/api/db). Declare @CucumberOptions on each concrete
 * runner instead.
 */
public abstract class BaseTest extends AbstractTestNGCucumberTests {

    @Override
    @org.testng.annotations.DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        String env = ConfigReader.getActiveEnv();
        String baseUrl = ConfigReader.get("baseUrl");
        LogUtil.info(BaseTest.class, "===== CIB Hybrid Framework — Suite starting =====");
        LogUtil.info(BaseTest.class, "Active environment: {} | Base URL: {}", env, baseUrl);

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("baseUrl is not configured for environment [" + env + "] — check config.json");
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReport();
        LogUtil.info(BaseTest.class, "===== CIB Hybrid Framework — Suite finished =====");
    }
}
