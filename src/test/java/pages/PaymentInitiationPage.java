package pages;

import utils.Locators;
import utils.PageVerifier;

public class PaymentInitiationPage extends BasePage {

    public void navigateToPaymentInitiation() {
        navigateToPath("/payments/initiate");
        waitForHidden(utils.Locators.Common.LOADING_SPINNER);
    }

    public void selectFromAccount(String accountLabel) {
        selectByVisibleText(Locators.PaymentInitiation.FROM_ACCOUNT_DROPDOWN, accountLabel);
    }

    public void enterToAccount(String accountNumber) {
        type(Locators.PaymentInitiation.TO_ACCOUNT_INPUT, accountNumber);
    }

    public void enterAmount(String amount) {
        type(Locators.PaymentInitiation.AMOUNT_INPUT, amount);
    }

    public void selectCurrency(String currencyCode) {
        selectByVisibleText(Locators.PaymentInitiation.CURRENCY_DROPDOWN, currencyCode);
    }

    public void enterReference(String reference) {
        type(Locators.PaymentInitiation.REFERENCE_INPUT, reference);
    }

    public void clickSubmit() {
        click(Locators.PaymentInitiation.SUBMIT_BUTTON);
    }

    /** Full wire transfer initiation flow in one call — the pattern step definitions call into. */
    public void initiateWireTransfer(String fromAccount, String toAccount, String amount,
                                      String currency, String reference) {
        navigateToPaymentInitiation();
        selectFromAccount(fromAccount);
        enterToAccount(toAccount);
        enterAmount(amount);
        selectCurrency(currency);
        enterReference(reference);
        clickSubmit();
    }

    public String getConfirmedPaymentId() {
        waitForVisible(Locators.PaymentInitiation.CONFIRMATION_PAYMENT_ID);
        return getText(Locators.PaymentInitiation.CONFIRMATION_PAYMENT_ID);
    }

    public String getConfirmedStatus() {
        return getText(Locators.PaymentInitiation.CONFIRMATION_STATUS);
    }

    public void verifySubmissionSuccess() {
        PageVerifier.verifyPaymentSubmitted(page());
    }
}
