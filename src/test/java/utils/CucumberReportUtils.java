package utils;

import com.microsoft.playwright.Page;
import io.cucumber.java.Scenario;

/**
 * Helpers for attaching evidence to the Cucumber HTML report via
 * Scenario.attach() — screenshots, API request/response bodies, DB query
 * results, environment info.
 */
public class CucumberReportUtils {

    private CucumberReportUtils() {
        // static-only utility class
    }

    public static void attachScreenshot(Scenario scenario, Page page, String title) {
        byte[] screenshot = page.screenshot();
        scenario.attach(screenshot, "image/png", title);
    }

    public static void attachScreenshotOnFailure(Scenario scenario, Page page) {
        if (scenario.isFailed()) {
            attachScreenshot(scenario, page, "Failure screenshot: " + scenario.getName());
        }
    }

    public static void attachBase64Screenshot(Scenario scenario, String base64, String title) {
        byte[] bytes = java.util.Base64.getDecoder().decode(base64);
        scenario.attach(bytes, "image/png", title);
    }

    public static void logStep(Scenario scenario, String message) {
        scenario.log(message);
    }

    public static void attachText(Scenario scenario, String text, String title) {
        scenario.attach(text, "text/plain", title);
    }

    public static void attachJson(Scenario scenario, String json, String title) {
        scenario.attach(json, "application/json", title);
    }

    /** Attaches both request and response body together — the standard evidence pair for an API-tagged step. */
    public static void attachRequestResponse(Scenario scenario, String requestBody, String responseBody, String name) {
        attachJson(scenario, requestBody, name + " - Request");
        attachJson(scenario, responseBody, name + " - Response");
    }

    public static void logScenarioResult(Scenario scenario) {
        String result = scenario.isFailed() ? "FAILED" : "PASSED";
        LogUtil.info(CucumberReportUtils.class, "Scenario [{}] result: {}", scenario.getName(), result);
    }

    public static void attachEnvironmentInfo(Scenario scenario) {
        String info = "Environment: " + ConfigReader.getActiveEnv()
                + " | Base URL: " + ConfigReader.get("baseUrl")
                + " | Browser: " + ConfigReader.get("browser", "chromium");
        attachText(scenario, info, "Environment Info");
    }
}
