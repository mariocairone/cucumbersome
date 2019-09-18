Feature: Mockserver

  #  Background: The Mock Server is running
  #   Given the mock server is running
  #   And the variable "serviceName" with value "default"
  #   And the http request port is "{{ mockServerPorts.get("default")}}"
  # Mock Rest endpoint
  Scenario: Mock Rest endpoint
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request GET "/" is executed
    And the http response status should be 200

  # Mock POST
  Scenario: Request Mock when request match body
    Given the mock with path "/data" and method "POST" is added to service "default"
    When the mock receive request with header "Content-Type" with value "application/json"
    And the mock receive request with body:
      """
      {
      	"test": "test"
      }
      """
    And the mock response will have a delay of 5s
    And the mock responds with status code 200
    Given the http request headers are:
      | Content-Type  | application/json |
      | Authorization | AL 123:123       |
    When the http request POST "/data" with following body is executed:
      """
      {
      "test": "test"
      }  
      """
    Then the http response status should be 200

  # Mock POST
  Scenario: Mock respose
    Given the mock with path "/error" and method "GET" is added to service "default"
    Given the mock receive request with header "MyHeader" with value "MyHeaderValue"
    Given the mock receive request with query parameter "queryParam" with value "queryParamValue"
    Given the mock response will have header "MyHeader" with value "MyHeader"
    Given the mock response will have body
      """
      {
      "test": "test"
      }  
      """
    Given the mock response will have body "{{readFile("json.json")}}"
    Given the mock responds with status code "200" exactly 3 times

  # Mock POST
  Scenario: Mock respose failure
    Given the mock with path "/error" and method "GET" is added to service "default"
    And the mock responds with status code 404
    Given the http request GET "/error" is executed
    Then the http response status should be 404
