package utils;

/**
 * Central selector store, organized by page/section. Selectors are plain
 * Strings understood by Playwright's page.locator() — CSS, text=, or
 * data-testid based. Replace every placeholder value with the real
 * selector once the actual CIB application markup is available.
 */
public class Locators {

    private Locators() {
        // constants-only utility class
    }

    public static class Common {
        public static final String LOADING_SPINNER = "[data-testid='loading-spinner']";
        public static final String SUCCESS_TOAST = "[data-testid='toast-success']";
        public static final String ERROR_TOAST = "[data-testid='toast-error']";
        public static final String MODAL = "[data-testid='modal']";
        public static final String MODAL_CLOSE_BUTTON = "[data-testid='modal-close']";
        public static final String PAGE_HEADING = "h1[data-testid='page-heading']";
        public static final String BREADCRUMB = "[data-testid='breadcrumb']";
    }

    public static class Login {
        public static final String USERNAME = "#username";
        public static final String PASSWORD = "#password";
        public static final String LOGIN_BUTTON = "[data-testid='login-button']";
        public static final String ERROR_MESSAGE = "[data-testid='login-error']";
        public static final String MFA_CODE_INPUT = "#mfaCode";
        public static final String MFA_SUBMIT_BUTTON = "[data-testid='mfa-submit']";
        // PNC-specific login flow selectors
        public static final String PNC_TOP_NAV_SIGN_ON = "xpath=//div[@id='login-flyout-desktop']//span[contains(@class,'cmp-button__text') and normalize-space(.)='SIGN ON']";
        public static final String PNC_SIGN_ON_OPTION = "xpath=//span[contains(@class,'cmp-login__service-list-close') and contains(@class,'pinacle') and contains(@class,'last-item')]";
        public static final String PNC_USERNAME = "xpath=//form[contains(@class,'cmp-login__form') and contains(@class,'show')]//input[contains(@class,'cmp-login__user-id-input-field')]";
        public static final String PNC_PASSWORD = "xpath=//form[contains(@class,'cmp-login__form') and contains(@class,'show')]//input[contains(@class,'cmp-login__password-input-field')]";
        public static final String PNC_SIGN_ON_BUTTON = "xpath=//form[contains(@class,'cmp-login__form') and contains(@class,'show')]//input[@type='submit' and @value='Sign On']";
        // PINACLE-specific login flow selectors
        public static final String PINACLE_SIGN_ON_OPTION = "xpath=//div[@class='cmp-login cmp-container__flyout show']//a[@role='button'][normalize-space()='Log In to PINACLE']";
        public static final String PINACLE_COMPANY_ID = "xpath=//input[@id='userCompanyId']";
        public static final String PINACLE_USER_ID = "xpath=//input[@id='userOperatorId']";
        public static final String PINACLE_PASSWORD = "xpath=//input[@id='password']";
        public static final String PINACLE_SUBMIT_BUTTON = "xpath=//button[@class='cib-button color-primary']";
        public static final String PINACLE_ERROR_MESSAGE = "xpath=//p[@class='error-message']";
    }

    public static class Dashboard {
        public static final String WELCOME_MESSAGE = "[data-testid='welcome-message']";
        public static final String LOGGED_IN_USER_NAME = "[data-testid='user-name']";
        public static final String LOGOUT_BUTTON = "[data-testid='logout-button']";
        public static final String NAV_MENU = "[data-testid='nav-menu']";
    }

    public static class AccountSummary {
        public static final String ACCOUNT_TABLE_ROWS = "[data-testid='account-table'] tbody tr";
        public static final String TOTAL_BALANCE = "[data-testid='total-balance']";
        public static final String ACCOUNT_SEARCH_INPUT = "[data-testid='account-search']";
    }

    public static class PaymentInitiation {
        public static final String FROM_ACCOUNT_DROPDOWN = "#fromAccount";
        public static final String TO_ACCOUNT_INPUT = "#toAccount";
        public static final String AMOUNT_INPUT = "#amount";
        public static final String CURRENCY_DROPDOWN = "#currency";
        public static final String REFERENCE_INPUT = "#paymentReference";
        public static final String SUBMIT_BUTTON = "[data-testid='submit-payment']";
        public static final String CONFIRMATION_PAYMENT_ID = "[data-testid='confirmation-payment-id']";
        public static final String CONFIRMATION_STATUS = "[data-testid='confirmation-status']";
    }

    public static class TradeFinance {
        public static final String DEAL_TYPE_DROPDOWN = "#dealType";
        public static final String LC_NUMBER_INPUT = "#lcNumber";
        public static final String DEALS_TABLE_ROWS = "[data-testid='trade-finance-table'] tbody tr";
        public static final String CREATE_DEAL_BUTTON = "[data-testid='create-deal-button']";
    }

    public static class FXDeals {
        public static final String BUY_CURRENCY_DROPDOWN = "#buyCurrency";
        public static final String SELL_CURRENCY_DROPDOWN = "#sellCurrency";
        public static final String DEAL_AMOUNT_INPUT = "#dealAmount";
        public static final String BOOK_DEAL_BUTTON = "[data-testid='book-fx-deal']";
        public static final String DEAL_RATE_DISPLAY = "[data-testid='fx-deal-rate']";
    }

    public static class CashManagement {
        public static final String BALANCE_SUMMARY_CARD = "[data-testid='balance-summary-card']";
        public static final String SWEEP_CONFIGURATION_LINK = "[data-testid='sweep-config-link']";
    }

    public static class Reports {
        public static final String REPORT_TYPE_DROPDOWN = "#reportType";
        public static final String DATE_RANGE_PICKER = "[data-testid='date-range-picker']";
        public static final String GENERATE_REPORT_BUTTON = "[data-testid='generate-report']";
        public static final String DOWNLOAD_LINK = "[data-testid='report-download-link']";
    }

    // ---------- Dynamic helper builders ----------

    /** e.g. rowContainingText("Acme Corp") — finds a table row containing given text. */
    public static String rowContainingText(String text) {
        return "tr:has-text('" + text + "')";
    }

    public static String buttonWithText(String text) {
        return "button:has-text('" + text + "')";
    }

    public static String byTestId(String testId) {
        return "[data-testid='" + testId + "']";
    }

    public static String linkWithText(String text) {
        return "a:has-text('" + text + "')";
    }
}