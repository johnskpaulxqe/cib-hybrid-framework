package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import utils.ConfigReader;
import utils.PlaywrightManager;
import utils.WaitUtils;

import java.util.List;

/**
 * Shared page methods every CIB page object inherits. Selectors are passed
 * as plain Strings (CSS, text=, or data-testid selectors) rather than
 * Selenium's By objects — Locators.java stores them as String constants.
 */
public abstract class BasePage {

    /** Always returns THIS thread's Page instance. */
    protected Page page() {
        return PlaywrightManager.getPage();
    }

    // ---------- Navigation ----------

    public void navigateTo(String url) {
        PlaywrightManager.navigateTo(url);
    }

    /** Navigates to a relative path off the environment's baseUrl, e.g. navigateToPath("/payments/initiate"). */
    public void navigateToPath(String path) {
        String baseUrl = ConfigReader.get("baseUrl");
        PlaywrightManager.navigateTo(baseUrl + path);
    }

    // ---------- Interactions (Playwright auto-waits before each of these) ----------

    public void click(String selector) {
        page().locator(selector).click();
    }

    /** Clears and fills — Playwright's fill() already clears the field first. */
    public void type(String selector, String text) {
        page().locator(selector).fill(text);
    }

    public String getText(String selector) {
        return page().locator(selector).textContent();
    }

    public void selectByVisibleText(String selector, String visibleText) {
        page().locator(selector).selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(visibleText));
    }

    public void check(String selector) {
        page().locator(selector).check();
    }

    public void uncheck(String selector) {
        page().locator(selector).uncheck();
    }

    /** Sets a range slider or numeric input's value via fill — for CIB rate/amount sliders if any. */
    public void setSliderValue(String selector, String value) {
        Locator locator = page().locator(selector);
        locator.evaluate("(el, val) => { el.value = val; el.dispatchEvent(new Event('input')); " +
                "el.dispatchEvent(new Event('change')); }", value);
    }

    // ---------- State checks (safe — no exception on miss) ----------

    public boolean isVisible(String selector) {
        try {
            return page().locator(selector).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(String selector) {
        try {
            return page().locator(selector).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isChecked(String selector) {
        try {
            return page().locator(selector).isChecked();
        } catch (Exception e) {
            return false;
        }
    }

    // ---------- Explicit waits (beyond Playwright's built-in auto-wait) ----------

    public void waitForVisible(String selector) {
        WaitUtils.waitForState(page().locator(selector), WaitForSelectorState.VISIBLE);
    }

    public void waitForHidden(String selector) {
        WaitUtils.waitForState(page().locator(selector), WaitForSelectorState.HIDDEN);
    }

    public void waitForTextContains(String selector, String expectedSubstring) {
        WaitUtils.waitForTextContains(page().locator(selector), expectedSubstring);
    }

    // ---------- Collections ----------

    public List<Locator> findElements(String selector) {
        return page().locator(selector).all();
    }

    public int getElementCount(String selector) {
        return page().locator(selector).count();
    }

    // ---------- Misc ----------

    public void scrollTo(String selector) {
        page().locator(selector).scrollIntoViewIfNeeded();
    }

    /** Accepts the next JS dialog (alert/confirm) that appears — must be registered before the triggering action. */
    public void acceptNextDialog() {
        page().onDialog(dialog -> dialog.accept());
    }

    public String getPageTitle() {
        return page().title();
    }

    public String getCurrentUrl() {
        return page().url();
    }
}
