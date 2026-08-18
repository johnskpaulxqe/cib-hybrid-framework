@ui
Feature: Payment Initiation
  As a corporate banking user
  I want to initiate wire transfers
  So that I can move funds between accounts on behalf of my organization

  Background:
    Given I am logged in as "corporateAdmin"
    And I am on the payment initiation page

  @smoke
  Scenario: Successfully initiate a domestic wire transfer
    When I initiate a wire transfer from "Operating Account - USD" to account "ACC-500200" for "10000.00" "USD" with reference "INV-2026-0001"
    Then the payment should be submitted successfully
    And the confirmation status should be "SUBMITTED"
    And a payment ID should be generated

  @regression
  Scenario: Initiate a cross-currency wire transfer
    When I initiate a wire transfer from "Operating Account - USD" to account "ACC-500300" for "5000.00" "EUR" with reference "INV-2026-0002"
    Then the payment should be submitted successfully

  @regression
  Scenario Outline: Initiate wire transfers across multiple currencies
    When I initiate a wire transfer from "Operating Account - USD" to account "<toAccount>" for "<amount>" "<currency>" with reference "<reference>"
    Then the payment should be submitted successfully

    Examples:
      | toAccount   | amount    | currency | reference       |
      | ACC-500400  | 2500.00   | GBP      | INV-2026-0003    |
      | ACC-500500  | 15000.00  | USD      | INV-2026-0004    |
      | ACC-500600  | 7500.50   | JPY      | INV-2026-0005    |
