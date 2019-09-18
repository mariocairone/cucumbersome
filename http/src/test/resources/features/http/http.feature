Feature: REST scenarios

  Scenario: Configure REST Client
  Given the http request baseURI is "http://localhost"
  And the http request port is "{{ wiremockPort }}"
  And the http request basePath is "/"
  
  Scenario: Execute a GET request
    Given the http request GET "/" is executed

  Scenario: Execute a HEAD request
    Given the http request HEAD "/" is executed

  Scenario: Execute a TRACE request
    Given the http request TRACE "/" is executed

  Scenario: Execute a OPTIONS request
    Given the http request OPTIONS "/" is executed

  Scenario: Execute a DELETE request
    Given the http request DELETE "/" is executed

  Scenario: Execute a POST request
    Given the http request POST "/withData" with following body is executed:
      """
      {
        "test": "test"
      }
      """
    And the http request POST "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """

  Scenario: Execute a PUT request
    Given the http request PUT "/withData" with following body is executed:
      """
      {
        "test": "test"
      }
      """
    And the http request PUT "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """

  Scenario: Execute a PATCH request
    Given the http request PATCH "/withData" with following body is executed:
      """
      {
        "test": "test"
      }
      """
    And the http request PATCH "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """

  Scenario: Execute a HTTP request with Form Params
    Given the http request form params are:
      | param | paramValue |
    And the http request POST "/withFormParam" is executed

  Scenario: Execute HTTP request with Authorization header
    Given the http request has Authorization header "AL 123:123"
    And the http request GET "/" is executed

  Scenario: Execute HTTP request with basic Authentication
    Given the http request basic Authentication username is "mule" and password is "mule"
    And the http request GET "/" is executed

  Scenario: Execute HTTP request with Oauth2 token
    Given the http request OAuth2 Authentication token is "asvdasdvgargerhswreth"
    And the http request GET "/" is executed

  Scenario: Execute a HTTP request with Content-Type
    Given the http request Content-Type is "application/json"
    Given the http request POST "/withData" with following body is executed:
      """
      {
        "test": "test"
      }
      """

  Scenario: Execute a HTTP request with Accept
    Given the http request accept Content-Type "*/*"
    And the http request GET "/" is executed

  Scenario: Execute a HTTP request with Cookies
    Given the http request Cookies are:
      | MyCoockie | MyCoockieValue |
    And the http request GET "/" is executed

  Scenario: Execute HTTP request with header
    Given the http request headers are:
      | MyHeader | MyHeaderValue |
    And the http request GET "/withHeaders" is executed
    Given the variable "header" with value "MyHeaderValue"
    Given the http request headers are:
      | MyHeader | {{ header }} |
    And the http request GET "/withHeaders" is executed

  Scenario: Execute HTTP request with query params
    Given the http request query params are:
      | param | paramValue |
    And the http request GET "/withParams" is executed
    Given the variable "param" with value "paramValue"
    Given the http request query params are:
      | param | {{ param }} |
    And the http request GET "/withParams" is executed

  Scenario: Execute HTTP request with multipart body from file
    Given the http request POST "/withFile" with multipart body file "file" from "file.txt" is executed
    Then the http response status should be 200

  Scenario: Assert http response status code.
    Given the http request GET "/" is executed
    Then the http response status should be 200
    Given the variable "status" with value "200"
    Then the http response status should be "{{ status }}"

  Scenario: Assert http response empty body.
    Given the http request GET "/" is executed
    Then the http response body should be empty

  Scenario: Assert http response empty body.
    Given the http request GET "/emptyArray" is executed
    Then the http response body should be an empty array

  Scenario: Assert http response body.
    Given the variable "test" with value "bar"
    Given the http request POST "/withData" with following body is executed:
      """
       {{ readFile("json.json") }}
      """
    Then the http response body should be:
      """
      {
       "foo": "bar"
      }
      """
    Then the http response body should be:
      """
      {
       "foo": "{{ test }}"
      }
      """
    Given the http request GET "/withArray" is executed
    And the http response body should be:
      """
      [
      	{
      	 "foo": "bar"
      	},
       	{
      	 "foo": 3
      	},
      	{
      	 "foos": ["bar","wee"]
      	}
      ]
      """
    And the http response body should be:
      """
       {{ include "response-array.json" }}
      """
    Given the http request POST "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """
    Then the http response body should be:
      """
       {{ include "response.json" }}
      """

  Scenario: Assert http response body properties.
    Given the http request POST "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """
    Then the http response body should be:
      """
      {
       "foo": "bar"
      }
      """
    And the http response body should contain "foo"
    And the http response body should contain "foo" with value "bar"
    And the http response body should not contain "foo" with value "wee"
    And the http response body should not contain "bar"

  Scenario: Assert http response json path entity
    Given the http request POST "/withData" with following body is executed:
      """
       {{ include "json.json" }}
      """
    Then the http response body should be:
      """
      {
       "foo": "bar"
      }
      """
    And the http response body should contain "foo"
    Given the http request GET "/withArray" is executed
    And the http response body should be:
      """
       {{ include "response-array.json" }}
      """
    And the http response body should contain "[0].foo"
    And the http response body should contain "[0].foo" with value "bar"
    And the http response body should not contain "[0].foo" with value "wee"
    And the http response body entity "[2].foos" should be:
      """
      ["bar","wee"]
      """

  Scenario: Assert http response array size
    Given the variable "count" with value "3"
    Given the http request GET "/withArray" is executed
    Then the http response status should be 200
    And the http response body should be:
      """
       {{ include "response-array.json" }}
      """
    And the http response body should be an array with 3 elements
    And the http response body should be an array with "{{ count }}" elements
    And the http response body should be an array with at least 2 elements
    And the http response body should be an array with at most 4 elements
    And the http response body should be an array with more than 2 elements
    And the http response body should be an array with less than 4 elements
    And the http response body entity "[2].foos" should be an array with 2 elements
    And the http response body entity "[2].foos" should be an array with at least 1 element
    And the http response body entity "[2].foos" should be an array with at most 3 elements
    And the http response body entity "[2].foos" should be an array with more than 1 elements
    And the http response body entity "[2].foos" should be an array with less than 3 elements
    And the http response body entity "[2].foos" should be an array with less than "{{ count }}" elements

  Scenario: Assert http response headers
    Given the http request headers are:
      | MyHeader | MyHeaderValue |
    Given the http request GET "/withHeaders" is executed
    Then the http response status should be 200
    And the http response headers should contain "MyHeader"
    And the http response headers should not contain "foo"
    And the http response headers should contain "MyHeader" with value "MyHeaderValue"
    And the http response headers should not contain "MyHeader" with value "foo"
    And the http response headers should contain "MultiHeader" with value "MultiHeaderValue1"
    And the http response headers should not contain "MultiHeader" with value "foo"

  Scenario: Store and reuse response property using variable
    Given the http request GET "/response" is executed
    Then the http response status should be 200
    And the http response body should be:
      """
      {
        "foo": "bar"
      }
      """
    And the http response entity "foo" is stored in variable "foo"
    When the http request GET "/response/{{ foo }}" is executed
    Then the http response status should be 200
