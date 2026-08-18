package utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

/**
 * ThreadLocal-scoped Playwright lifecycle manager.
 * Mirrors the role DriverManager played in the Selenium framework:
 * one Playwright instance + Browser + BrowserContext + Page per thread,
 * so parallel scenarios never share state.
 */
public class PlaywrightManager {

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private PlaywrightManager() {
        // static-only utility class
    }

    /**
     * Initializes Playwright + Browser + Context + Page for the current thread.
     * Reads browser type and headless flag from config.json (via ConfigReader),
     * with -Dbrowser and -Dheadless system property overrides.
     */
    public static void initDriver() {
        String browserName = System.getProperty("browser", ConfigReader.get("browser", "chromium"));
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", String.valueOf(ConfigReader.getBoolean("headless", true))));

        LogUtil.info(PlaywrightManager.class, "Initializing Playwright | browser={} headless={}", browserName, headless);

        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(headless)
            .setArgs(java.util.List.of("--window-size=1920,1080"));

        // Optional: use the real, locally-installed Chrome instead of Playwright's bundled
        // Chromium — e.g. -Dchannel=chrome, or "channel": "chrome" in config.json. Leave unset
        // to use bundled Chromium (the default, and the recommended choice for most runs).
        String channel = System.getProperty("channel", ConfigReader.get("channel", ""));
        if (!channel.isBlank()) {
            launchOptions.setChannel(channel);
        }

        Browser browser = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit", "safari" -> playwright.webkit().launch(launchOptions);
            default -> playwright.chromium().launch(launchOptions);
        };
        browserThreadLocal.set(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setScreenSize(1920, 1080)
                .setIgnoreHTTPSErrors(true));
        context.setDefaultTimeout(ConfigReader.getInt("defaultTimeoutMs", 30000));
        contextThreadLocal.set(context);

        Page page = context.newPage();
        pageThreadLocal.set(page);

        LogUtil.info(PlaywrightManager.class, "Playwright ready on thread {}", Thread.currentThread().getId());
    }

    /** Always returns THIS thread's Page instance. */
    public static Page getPage() {
        Page page = pageThreadLocal.get();
        if (page == null) {
            throw new IllegalStateException(
                    "Page is not initialized for this thread. Call PlaywrightManager.initDriver() first.");
        }
        return page;
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    /**
     * Navigates and waits for the DOM to be ready. Does NOT hard-block on NETWORKIDLE —
     * pages with persistent background traffic (analytics/telemetry beacons, or a real
     * Chrome profile's own update/safe-browsing checks) may never go fully idle, which
     * would otherwise throw a TimeoutError on an otherwise-successful navigation. A short,
     * best-effort NETWORKIDLE wait is still attempted, but a timeout there is only logged,
     * not thrown — callers should use WaitUtils.waitForVisible() on a known element for
     * the real "is the page actually usable" signal.
     */
    public static void navigateTo(String url) {
        Page page = getPage();
        LogUtil.info(PlaywrightManager.class, "Navigating to {}", url);
        page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        try {
            page.waitForLoadState(LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(5000));
        } catch (PlaywrightException e) {
            LogUtil.warn(PlaywrightManager.class,
                    "NETWORKIDLE not reached within 5s after navigating to {} — continuing anyway (page has persistent background traffic)", url);
        }
    }

    /**
     * Tears down Page, Context, Browser, and Playwright for the current thread
     * and clears all ThreadLocals to prevent memory leaks — same purpose as
     * DriverManager.quitDriver() in the Selenium framework.
     */
    public static void quitDriver() {
        try {
            Page page = pageThreadLocal.get();
            if (page != null) page.close();

            BrowserContext context = contextThreadLocal.get();
            if (context != null) context.close();

            Browser browser = browserThreadLocal.get();
            if (browser != null) browser.close();

            Playwright playwright = playwrightThreadLocal.get();
            if (playwright != null) playwright.close();

            LogUtil.info(PlaywrightManager.class, "Playwright torn down on thread {}", Thread.currentThread().getId());
        } finally {
            pageThreadLocal.remove();
            contextThreadLocal.remove();
            browserThreadLocal.remove();
            playwrightThreadLocal.remove();
        }
    }
}