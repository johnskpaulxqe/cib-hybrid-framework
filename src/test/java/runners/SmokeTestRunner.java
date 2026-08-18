package runners;

import base.BaseTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "stepdefinitions"},
        tags = "@12345",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/smoke/cucumber.json",
                "html:target/cucumber-reports/smoke/cucumber.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
)
public class SmokeTestRunner extends BaseTest {
        @Override
        @org.testng.annotations.DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
