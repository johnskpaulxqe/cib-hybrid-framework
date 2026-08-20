package runners;

import base.BaseTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"hooks", "stepdefinitions"},
        // tags = "not @wip",
        tags = "@disabled-temporarily",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/full/cucumber.json",
                "html:target/cucumber-reports/full/cucumber.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
)
public class FullSuiteRunner extends BaseTest {
}
