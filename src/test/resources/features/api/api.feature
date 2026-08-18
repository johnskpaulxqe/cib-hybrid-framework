@api
Feature: CIB API Layer
  As an automated test suite
  I want to exercise CIB API endpoints directly
  So that backend behavior is verified independently of the UI

  Background:
    Given I authenticate via the API as "corporateAdmin"

  @smoke
  Scenario: Successfully initiate a wire transfer via API
    When I initiate a wire transfer via API
    Then the API response status code should be 201
    And the API response field "paymentId" should not be null
    And the API response time should be within SLA

  @regression
  Scenario: Check payment status after API initiation
    When I initiate a wire transfer via API
    And I check the payment status via API
    Then the API response status code should be 200
    And the API response field "status" should equal "SUBMITTED"

  @regression
  Scenario: Successfully create an FX spot deal via API
    When I create an FX deal via API
    Then the API response status code should be 201
    And the API response field "dealReference" should not be null
