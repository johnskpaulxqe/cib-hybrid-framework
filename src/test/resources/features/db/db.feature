@db
Feature: CIB Database Verification
  As an automated test suite
  I want to verify backend database state after payment operations
  So that UI/API actions are confirmed to persist correctly

  Background:
    Given the database is reachable
    And I authenticate via the API as "corporateAdmin"

  @smoke
  Scenario: Payment created via API is persisted in the database
    When I initiate a wire transfer via API
    Then the payment should exist in the database
    And the payment status in the database should be "SUBMITTED"

  @regression
  Scenario: Pending payment count reflects newly created payments
    When I initiate a wire transfer via API
    And I query pending payments for the logged in entity
    Then the pending payment count should be greater than 0

  @regression
  Scenario: Clean up test payment after verification
    When I initiate a wire transfer via API
    Then the payment should exist in the database
    And I clean up the test payment
