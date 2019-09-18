

# Cucumber Http
The steps in this module enable REST endpoints test using  [Cucumber](https://cucumber.io)

## Configuration

### Java Configuration

```java
HttpConfig
  .httpOptions()
    .withNoContentPlaceholder("-")
    .withLogResponse(logResponse)
    .withLogRequest(logRequest)
    .withRelaxedHttps(relaxedHttps)
    .withBaseUri(baseUri);
    .withBasePath(basePath)
    .withPort(port);
```

### Configuration File
The following properties can be configured in the file `cucumbersome.properties`

```
http.request.log=true
http.response.log=true
http.request.baseUrl=http://localhost
http.request.basePath=/
http.request.port=8080
http.nocontent.placeholder=-
```

## Steps in this library

###  Configure REST Client
```gherkin
Given the http request baseURI is "http://localhost"
And the http request port is 8080    
And the http request basePath is "/"
```
###  Prepare HTTP request
#### Add Authorization header
```gherkin
Given the http request has Authorization header "AL 123:123"
```
#### Add basic Authentication
```gherkin
Given the http request basic Authentication username is "mule" and password is "mule"
```
#### Add Oauth2 token  
```gherkin
Given the http request OAuth2 Authentication token is "asvdasdvgargerhswreth"
```
#### Set Content-Type
```gherkin
Given the http request Content-Type is "application/json"
```
####  Set Accept
```gherkin
Given the http request accept Content-Type "*/*"
```
####  Set Cookies
```gherkin
Given the http request Cookies are:
   | MyCoockie | MyCoockieValue |   
```
####  Set Headers
```gherkin
Given the http request headers are:
  | MyHeader | MyHeaderValue |
```

####  Set Query Params
```gherkin
Given the http request query params are:
  | param | paramValue |
```
####  Set Body

* In-Line

```gherkin
Given the http request body is:
"""
{
  "test": "test"
}
"""   
```

* From File

```gherkin
Given the http request body is:
"""
readFile("body.json")
"""   
```

####  Set Form Params
```gherkin
Given the http request form params are:
   | param | paramValue |   
```

###  Execute HTTP request

####  Execute http request

```gherkin
Given the http request GET "/" is executed
Given the http request POST "/" is executed
Given the http request PUT "/" is executed
Given the http request PATCH "/" is executed
Given the http request HEAD "/" is executed
Given the http request TRACE "/" is executed
Given the http request OPTIONS "/" is executed    
Given the http request DELETE "/" is executed        
```
####  Execute http request and set the body
```gherkin
Given the http request POST "/withData" with following body is executed:
"""
{
  "test": "test"
}
"""
Given the http request PUT "/withData" with following body is executed:
"""
test: "test"

|YamlToJson  
"""
Given the http request PATCH "/withData" with following body is executed:
"""
readFile("body.json")
"""   
```

####  Execute HTTP request with multipart body from file
```gherkin
Given the http request POST "/withFile" with multipart body file "file" from "file.txt" is executed
```
###  Assert HTTP response

####  Assert http response status code
```gherkin
Then the http response status should be 200
Then the http response status should be "{{ status }}"
```
####  Assert http response body

* Empty body

```gherkin
Then the http response body should be empty
Then the http response should contain an empty array
```

* Full Body Content

```gherkin
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
```

* Body json path

```gherkin   
And the http response body should contain "[0].foo"
And the http response body should contain "[0].foo" with value "bar"
And the http response body should not contain "[0].foo" with value "wee"    
And the http response body entity "[2].foos" should be:
"""
["bar","wee"]
"""
```
* Body Array

```gherkin   
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
```

####  Assert http response headers
```gherkin
And the http response headers should contain "MyHeader"
And the http response headers should not contain "foo"
And the http response headers should contain "MyHeader" with value "MyHeaderValue"
And the http response headers should not contain "MyHeader" with value "foo"
And the http response headers should contain "MultiHeader" with value "MultiHeaderValue1"
And the http response headers should not contain "MultiHeader" with value "foo"
```

####  Store and reuse response property using variable
```gherkin
Given the http request GET "/response" is executed
Then the http response status should be 200
And the http response entity "foo" is stored in variable "foo"
When the http request GET "/response/{{ foo }}" is executed
Then the http response status should be 200
```


## Installation

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>com.mariocairone.cucumbersome</groupId>
  <artifactId>http</artifactId>
  <version>{{version}}</version>
  <scope>test</scope>
</dependency>
```

Create the test class with the package glue

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
"json:target/cucumber/cucumber.json",
"junit:target/cucumber/cucumber.xml"},
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/rest",
    strict = true)
public class CucumbersomeHttpIT   {

}
```
---
Note: be sure to modify the features attribute to match your requirement
