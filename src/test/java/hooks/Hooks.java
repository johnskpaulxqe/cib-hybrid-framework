package hooks;

import context.TestContext;
import io.cucumber.java.*;
import utils.*;

public class Hooks {

    private final TestContext ctx;

    public Hooks(TestContext ctx) {
        this.ctx = ctx;
    }

    // ---------- Before ----------

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        ctx.setScenario(scenario);
        LogUtil.setScenarioContext(scenario.getName());
        LogUtil.logScenarioStart(scenario.getName());
        LogUtil.info(Hooks.class, "Tags: {}", scenario.getSourceTagNames());
    }

    @Before(order = 2, value = "@ui or @smoke or @regression")
    public void beforeUiScenario() {
        PlaywrightManager.initDriver();
        LogUtil.info(Hooks.class, "Browser launched for UI scenario");
    }

    @Before(order = 2, value = "@api")
    public void beforeApiScenario() {
        ApiUtils.setup();
        LogUtil.info(Hooks.class, "API base URI configured for API scenario");
    }

    @Before(order = 2, value = "@db")
    public void beforeDbScenario() {
        DBUtils.getConnection();
        ctx.setDbConnectionOpened(true);
        LogUtil.info(Hooks.class, "DB connection opened for DB scenario");
    }

    // ---------- After each step (screenshot on failure, attached to Cucumber HTML report) ----------

    @AfterStep(value = "@ui or @smoke or @regression")
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                byte[] screenshot = PlaywrightManager.getPage().screenshot();
                scenario.attach(screenshot, "image/png", scenario.getName());
            } catch (Exception e) {
                LogUtil.warn(Hooks.class, "Could not attach failure screenshot: {}", e.getMessage());
            }
        }
    }

    // ---------- After ----------

    @After(order = 2, value = "@ui or @smoke or @regression")
    public void afterUiScenario(Scenario scenario) {
        try {
            ScreenshotUtils.captureOnFailure(PlaywrightManager.getPage(), scenario.getName(), scenario.isFailed());
        } finally {
            PlaywrightManager.quitDriver();
            LogUtil.info(Hooks.class, "Browser torn down for UI scenario");
        }
    }

    @After(order = 2, value = "@db")
    public void afterDbScenario() {
        if (ctx.isDbConnectionOpened()) {
            DBUtils.closeConnection();
            LogUtil.info(Hooks.class, "DB connection closed for DB scenario");
        }
    }

    @After(order = 1)
    public void afterScenario(Scenario scenario) {
        LogUtil.logScenarioEnd(scenario.getName(), scenario.isFailed());
        CustomAssert.resetSoftAssert();
        LogUtil.clearContext();
    }
}