Feature: Mockserver



  Scenario: Mock Rest endpoint matching path and method
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock responds with status code 200
    Given the http request GET "/" is executed
    And the http response status should be 200


  Scenario: Mock Rest endpoint matching path and method regex
    Given the mock with path "/ping.*" and method "G.*" is added to service "default"
    And the mock responds with status code 200
    Given the http request GET "/pingPong" is executed
    And the http response status should be 200

  Scenario: Mock Rest endpoint matching json body
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body matching json
      """
      {
      "test": "test"
      }
      """
    And the mock responds with status code 200 
    When the http request POST "/data" with following body is executed:
      """
      {
      "test": "test"
      }
      """
    Then the http response status should be 200
  
    
  Scenario: Mock Rest endpoint matching string in request body
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body containing the string "test"
    And the mock responds with status code 200
 
    When the http request POST "/data" with following body is executed:
      """
      {
      "test": "test"
      }
      """
    Then the http response status should be 200  
  
    
  Scenario: Mock Rest endpoint matching json path in request body 
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body matching the json path "$..store.book.author"
    And the mock responds with status code 200
    When the http request POST "/data" with following body is executed:
      """
      {
      "test": "test",
       "store" : {
        "book": {
        	"author": "Mario Cairone"
        }
       }
      }
      """
    Then the http response status should be 200   
    
   
  Scenario: Mock Rest endpoint matching exact string as request body 
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body equal to string
      """
      ExactMatch
      """
    And the mock response will have a delay of 5s
    And the mock responds with status code 200
    When the http request POST "/data" with following body is executed:
      """
      ExactMatch
      """
    Then the http response status should be 200
  
  
  Scenario: Mock Rest endpoint matching regex in request body 
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body matching regex "Exact.*"
    And the mock response will have a delay of 5s
    And the mock responds with status code 200
    When the http request POST "/data" with following body is executed:
      """
      ExactMatch
      """
    Then the http response status should be 200  
 
  Scenario: Mock Rest endpoint matching xml body 
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with xml body:
    """
		<note>
			<to>Tove</to>
			<from>Jani</from>
			<heading>Reminder</heading>
			<body>Don't forget me this weekend!</body>
		</note>    
    """
    And the mock responds with status code 200
    When the http request POST "/data" with following body is executed:
    """
		<note>
			<to>Tove</to>
			<from>Jani</from>
			<heading>Reminder</heading>
			<body>Don't forget me this weekend!</body>
		</note>    
    """
    Then the http response status should be 200 

  Scenario: Mock Rest endpoint matching xpath in xml body 
    Given the mock with path "/data" and method "POST" is added to service "default"
    And the mock receives a request with body matching the xpath "/note/to"
    And the mock responds with status code 200
    When the http request POST "/data" with following body is executed:
    """
		<note>
			<to>Tove</to>
			<from>Jani</from>
			<heading>Reminder</heading>
			<body>Don't forget me this weekend!</body>
		</note>    
    """
    Then the http response status should be 200
    
         
  Scenario: Mock Rest endpoint matching header name
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with header "x-transaction-id" 
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id| 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 200

         
  Scenario: Mock Rest endpoint matching header name regex
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with header "x-.*" 
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id| 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 200  
      
  Scenario: Mock Rest endpoint matching request header name and value
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with header "x-transaction-id" with value "1234567"
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id| 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 200   

      
  Scenario: Do not mock rest endpoint matching request header name but not values
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with header "x-transaction-id" with value "XXXXXX"
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id | 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 404 
    
  
      
  Scenario: do not mock rest endpoint when request contains header 
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request without header "x-transaction-id"
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id| 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 404  
    
      
  Scenario: Mock Rest endpoint when request has missed header
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request without header "invalid"
    And the mock response will have header "Content-Type" with value "application/json"
    And the mock responds with status code 200
    Given the http request headers are:
    | x-transaction-id| 1234567 |      
    When the http request GET "/" is executed
    Then the http response status should be 200  

  Scenario: Mock Rest endpoint when request match query parameter
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with query parameter "query" with value "myQuery"
    And the mock responds with status code 200
    Given the http request query params are:
    | query | myQuery |      
    When the http request GET "/" is executed
    Then the http response status should be 200  
 
 @test
   Scenario: Mock Rest endpoint when request match query parameters
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock receives a request with query parameters:
    | query  | myQuery |
    | query2 | myQuery2|
    
    And the mock responds with status code 200
    Given the http request query params are:
    | query  | myQuery  |
    | query2 | myQuery2 |
    | extra  | extra    |   
    When the http request GET "/" is executed
    Then the http response status should be 200 
       

  Scenario: Mock Rest endpoint with response delay
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock response will have a delay of 5s
    And the mock responds with status code 200
    When the http request GET "/" is executed
    Then the http response status should be 200  
    And the http response time should be more than 4s
    And the http response time should be less than 6s


  Scenario: Mock Rest endpoint return response with header
    Given the mock with path "/" and method "GET" is added to service "default"
    And the mock response will have header "MyHeader" with value "MyHeader"
    Then the mock responds with status code 200
    When the http request GET "/" is executed
    Then the http response status should be 200  
		And the http response headers should contain "MyHeader" with value "MyHeader"


  Scenario: Mock Rest endpoint return response with body
    Given the mock with path "/" and method "GET" is added to service "default"
    Given the mock response will have body
      """
      {
      "test": "test"
      }  
      """    
    Then the mock responds with status code 200
    When the http request GET "/" is executed
    Then the http response status should be 200  
		And the http response body should be
      """
      {
      "test": "test"
      }  
      """   


  Scenario: Mock Rest endpoint return response with body from file
    Given the mock with path "/" and method "GET" is added to service "default"
    Given the mock response will have body "{{ readFile("json.json") }}"
    Then the mock responds with status code 200
    When the http request GET "/" is executed
    Then the http response status should be 200  
		And the http response body should be:
      """
      {
      "test": "test"
      }  
      """  		
  
  Scenario: Mock respose
    Given the mock with path "/error" and method "GET" is added to service "default"
    Given the mock receives a request with header "MyHeader" with value "MyHeaderValue"
    Given the mock receives a request with query parameter "queryParam" with value "queryParamValue"
    Given the mock response will have header "MyHeader" with value "MyHeader"
    Given the mock response will have body
      """
      {
      "test": "test"
      }  
      """
    Given the mock response will have body "{{readFile("json.json")}}"
    Given the mock responds with status code "200" exactly 3 times  
