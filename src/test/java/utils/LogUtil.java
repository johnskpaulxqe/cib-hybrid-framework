package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Thin wrapper around SLF4J. Provides MDC helpers so parallel Cucumber
 * scenarios can be told apart in the log file — every line shows which
 * scenario and which thread produced it — plus banner helpers for a
 * clearer, more readable log file (framework startup, per-scenario
 * start/end markers).
 */
public class LogUtil {

    private static final String SCENARIO_KEY = "scenario";
    private static final String THREAD_KEY = "thread";

    private LogUtil() {
        // static-only utility class
    }

    // ═══════════════════════════════════════════════════════════
    // LOGGER FACTORY METHODS
    // ═══════════════════════════════════════════════════════════

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    // ═══════════════════════════════════════════════════════════
    // CONVENIENCE STATIC METHODS
    // ═══════════════════════════════════════════════════════════

    public static void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    public static void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }

    public static void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    public static void error(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).error(message, args);
    }

    /** Finest granularity — not visible unless log level is set to TRACE. */
    public static void trace(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).trace(message, args);
    }

    // ═══════════════════════════════════════════════════════════
    // MDC HELPERS — Mapped Diagnostic Context
    // ═══════════════════════════════════════════════════════════

    /** Call from Hooks.java @Before, so every subsequent log line is tagged with the scenario name and thread. */
    public static void setScenarioContext(String scenarioName) {
        MDC.put(SCENARIO_KEY, scenarioName);
        MDC.put(THREAD_KEY, Thread.currentThread().getName());
    }

    /** Attaches an arbitrary key-value pair to the MDC context for the current thread, e.g. setContext("env", "staging"). */
    public static void setContext(String key, String value) {
        MDC.put(key, value);
    }

    /** Call from Hooks.java @After, to avoid leaking context into the next scenario on a reused thread. */
    public static void clearContext() {
        MDC.clear();
    }

    // ═══════════════════════════════════════════════════════════
    // FRAMEWORK STARTUP BANNER
    // ═══════════════════════════════════════════════════════════

    /** Call once from BaseTest.java @BeforeSuite so the start of a run is clearly visible in log files and CI output. */
    public static void logFrameworkStartup() {
        Logger log = LoggerFactory.getLogger("Framework");

        String env = ConfigReader.getActiveEnv();
        String browser = System.getProperty("browser", ConfigReader.get("browser", "chromium"));

        log.info("╔══════════════════════════════════════════╗");
        log.info("║     CIB Hybrid Framework Starting        ║");
        log.info("║  Environment : {}║", padRight(env, 26));
        log.info("║  Browser     : {}║", padRight(browser, 26));
        log.info("╚══════════════════════════════════════════╝");
    }

    // ── logScenarioStart() ───────────────────────────────────

    /** Logs a visible separator before each scenario starts. Call from Hooks.java @Before. */
    public static void logScenarioStart(String scenarioName) {
        Logger log = LoggerFactory.getLogger("Scenario");
        log.info("┌─────────────────────────────────────────────────────");
        log.info("│  ▶ SCENARIO: {}", scenarioName);
        log.info("└─────────────────────────────────────────────────────");
    }

    // ── logScenarioEnd() ─────────────────────────────────────

    /** Logs the scenario result (PASSED / FAILED) after it ends. Call from Hooks.java @After. */
    public static void logScenarioEnd(String scenarioName, boolean failed) {
        Logger log = LoggerFactory.getLogger("Scenario");
        log.info("┌─────────────────────────────────────────────────────");
        if (failed) {
            log.error("│  ✖ FAILED : {}", scenarioName);
        } else {
            log.info("│  ✔ PASSED : {}", scenarioName);
        }
        log.info("└─────────────────────────────────────────────────────");
    }

    // ═══════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════

    /** Right-pads a string to a fixed width for banner alignment. */
    private static String padRight(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }
}