package context;

import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import pages.LoginPage;
import pages.PaymentInitiationPage;

import java.util.HashMap;
import java.util.Map;

/**
 * The single shared memory for one scenario. PicoContainer injects the SAME
 * TestContext instance into every step definition class within a scenario,
 * so LoginSteps, PaymentSteps, ApiSteps, and DbSteps can all read/write the
 * same state without knowing about each other directly.
 *
 * Lifecycle: PicoContainer creates a fresh instance per scenario and
 * discards it when the scenario ends — no manual reset needed.
 */
public class TestContext {

    // ---------- Cucumber scenario reference (for Hooks — screenshots, pass/fail) ----------
    private Scenario scenario;

    // ---------- Page object instances — created once, shared across all step classes ----------
    private LoginPage loginPage;
    private PaymentInitiationPage paymentInitiationPage;

    // ---------- Auth state ----------
    private String authToken;
    private String loggedInUsername;

    // ---------- API handoff (When step stores it, Then step reads it) ----------
    private Response lastApiResponse;

    // ---------- Entity IDs created during a scenario (for downstream verification / cleanup) ----------
    private String createdPaymentId;
    private String createdFxDealReference;

    // ---------- DB hook state ----------
    private boolean dbConnectionOpened;

    // ---------- Generic ad-hoc store ----------
    private final Map<String, Object> scenarioData = new HashMap<>();

    // ---------- Scenario ----------

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    // ---------- Page objects (lazy-created, reused) ----------

    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage();
        }
        return loginPage;
    }

    public PaymentInitiationPage getPaymentInitiationPage() {
        if (paymentInitiationPage == null) {
            paymentInitiationPage = new PaymentInitiationPage();
        }
        return paymentInitiationPage;
    }

    // ---------- Auth ----------

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

    // ---------- API ----------

    public Response getLastApiResponse() {
        return lastApiResponse;
    }

    public void setLastApiResponse(Response lastApiResponse) {
        this.lastApiResponse = lastApiResponse;
    }

    // ---------- Entity IDs ----------

    public String getCreatedPaymentId() {
        return createdPaymentId;
    }

    public void setCreatedPaymentId(String createdPaymentId) {
        this.createdPaymentId = createdPaymentId;
    }

    public String getCreatedFxDealReference() {
        return createdFxDealReference;
    }

    public void setCreatedFxDealReference(String createdFxDealReference) {
        this.createdFxDealReference = createdFxDealReference;
    }

    // ---------- DB ----------

    public boolean isDbConnectionOpened() {
        return dbConnectionOpened;
    }

    public void setDbConnectionOpened(boolean dbConnectionOpened) {
        this.dbConnectionOpened = dbConnectionOpened;
    }

    // ---------- Generic store ----------

    public void put(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object get(String key) {
        return scenarioData.get(key);
    }

    public String getString(String key) {
        Object value = scenarioData.get(key);
        return value == null ? null : value.toString();
    }
}
