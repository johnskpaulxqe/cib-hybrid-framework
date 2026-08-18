package runners;

import base.BaseTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "stepdefinitions"},
        tags = "@db",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/db/cucumber.json",
                "html:target/cucumber-reports/db/cucumber.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
)
public class DbTestRunner extends BaseTest {
}
