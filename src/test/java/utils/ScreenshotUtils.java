package utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Screenshot capture for both the Cucumber HTML report (file-based attach)
 * and the Extent report (Base64 embed) — same dual-purpose role as the
 * Selenium version's ScreenshotUtils.
 */
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    private ScreenshotUtils() {
        // static-only utility class
    }

    /** Captures a PNG to disk with an auto-generated, sanitised filename. Returns the file path. */
    public static String captureAndSave(Page page, String scenarioName) {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
            String fileName = sanitize(scenarioName) + "_" + LocalDateTime.now().format(TS_FORMAT) + ".png";
            Path fullPath = Paths.get(SCREENSHOT_DIR, fileName);

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(fullPath)
                    .setType(ScreenshotType.PNG)
                    .setFullPage(true));

            LogUtil.info(ScreenshotUtils.class, "Screenshot saved: {}", fullPath);
            return fullPath.toString();
        } catch (Exception e) {
            LogUtil.error(ScreenshotUtils.class, "Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }

    /** Captures a screenshot as raw bytes and returns Base64 — for embedding directly in Extent Reports. */
    public static String captureAsBase64(Page page) {
        try {
            byte[] bytes = page.screenshot(new Page.ScreenshotOptions()
                    .setType(ScreenshotType.PNG)
                    .setFullPage(true));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LogUtil.error(ScreenshotUtils.class, "Failed to capture Base64 screenshot: {}", e.getMessage());
            return null;
        }
    }

    /** Primary hook call from Hooks.java @After — only captures if config allows and the scenario failed. */
    public static String captureOnFailure(Page page, String scenarioName, boolean hasFailed) {
        if (!hasFailed) return null;
        boolean shouldCapture = ConfigReader.getBoolean("screenshotOnFailure", true);
        if (!shouldCapture) return null;
        return captureAndSave(page, scenarioName);
    }

    /** Sanitises a scenario name into a safe filename fragment (max 80 chars, alphanumerics + underscores only). */
    private static String sanitize(String rawName) {
        String cleaned = rawName.replaceAll("[^a-zA-Z0-9]+", "_");
        return cleaned.length() > 80 ? cleaned.substring(0, 80) : cleaned;
    }

    static {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
        } catch (IOException ignored) {
            // directory creation failure surfaces on first actual capture attempt instead
        }
    }
}
