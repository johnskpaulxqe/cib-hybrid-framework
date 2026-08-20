package runners;

import base.BaseTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "stepdefinitions"},
        // tags = "@api",
        tags = "@disabled-temporarily",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/api/cucumber.json",
                "html:target/cucumber-reports/api/cucumber.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
)
public class ApiTestRunner extends BaseTest {
}
