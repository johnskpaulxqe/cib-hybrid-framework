@ui
Feature: CIB Login
  As a corporate banking user
  I want to securely log into the CIB portal
  So that I can access my organization's banking services

  Background:
    Given I am on the CIB login page

  @smoke
  Scenario: Successful login with valid corporate admin credentials
    When I login as "corporateAdmin"
    Then I should be on the dashboard
 
  @smoke @12345
  Scenario: Failed login with invalid credentials on PNC Log in
    When I'm on PNC Log in page
    And I enter username "invalid.user@examplebank.com" and password "WrongPassword123"
    And I click the login button
    Then I should see a login error
    And the login error should contain "Invalid username or password"

  @smoke @12345
  Scenario: Failed login with invalid credentials on PINACLE Log in
    When I'm on PINACLE Log in page
    And I enter PINACLE company ID "1234567" user ID "invalid.user" and password "WrongPassword123"
    And I click the login button
    Then I should see a login error
    And the login error should contain "Please try again"

  @regression
  Scenario: Login triggers MFA challenge for payment approver
    When I login as "paymentApprover"
    Then an MFA prompt should be displayed

  @regression
  Scenario: Successful login as trade finance officer
    When I login as "tradeFinanceOfficer"
    Then I should be on the dashboard

  @regression
  Scenario: Successful login as view-only user
    When I login as "viewOnlyUser"
    Then I should be on the dashboard

  @regression
  Scenario: Login blocked for locked account
    When I'm on PNC Log in page
    And I enter username "locked.user@examplebank.com" and password "SomePassword123"
    And I click the login button
    Then I should see a login error
    And the login error should contain "account has been locked"

  @smoke @12345
  Scenario: Verify PNC corporate landing page has join link
    Given I navigate to the PNC corporate landing page
    Then I should see the "See Why Clients Join PNC" link
    And I should be on the PNC corporate landing page
