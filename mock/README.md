
# Cucumbersome Mock
The steps in this library enable Mock Creation in test using [Cucumber](https://cucumber.io)

## Configuration

### Java Configuration

```java
MockConfig
   .mockOptions()
     .clearServices();
     .addService("default",mockServer);
```

### Configuration File
The following properties can be configured in the file `cucumbersome.properties`

```
mock.services=test1,test2

mock.services.test1.port=8080
mock.services.test1.host=myservice

mock.services.test2.port=8080
mock.services.test2.host=myservice2

```

Where the `mock.services` is a list of services we want to mock, separated by comma.
```
mock.services=<name>
```
If no services are specified a single service called "default" will be created.

Per each services we can then specify the properties host and port:
```
mock.services.<name>.port=8081
mock.services.<name>.host=localhost
mock.services.<name>.fixedPort=9090
```

if host and port are not specified for a service the default will be used:

| Property Name | Default value         |
|:--------------|:----------------------|
| port          | random available port |
| host          | <serviceName>         |
| fixedPort     | NONE                  |


 using default parameters.

## Steps in this library

Please note that the list below is porvided as exemple and may not include all the steps available in the library.

#### Create a mock definition for the service test

```gherkin
Given the mock with path "/" and method "GET" is added to service "test"
```
---
Note: support regex in path and method

### Request Body Match

#### Mock service if request body match json
```gherkin
And the mock receives a request with body matching json
 """
  {
   "test": "test"
  }
 """
```

#### Mock service if request body match json path
```gherkin
And the mock receives a request with body matching the json path "$..store.book.author"

```

#### Mock service if request body contains string
```gherkin
And the mock receives a request with body containing the string "test"
```

#### Mock service if request body is equal to string
```gherkin
And the mock receives a request with body equal to string
  """
  ExactMatch
  """
```
#### Mock service if request body is equal to string
```gherkin
And the mock receives a request with body matching regex "Exact.*"
```

#### Mock service if request body is equal to xml
```gherkin
And the mock receives a request with xml body:
  """
  <note>
    <to>Tove</to>
    <from>Jani</from>
    <heading>Reminder</heading>
    <body>Don't forget me this weekend!</body>
  </note>    
  """
```

#### Mock service if request body match xpath
```gherkin
And the mock receives a request with body matching the xpath "/note/to"
```

### Request Header Match

#### Mock service if request contains header
```gherkin
And the mock receives a request with header "MyHeader" 
```

#### Mock service if request contains header with value
```gherkin
Given the mock receives request with header "MyHeader" with value "MyHeaderValue"
```

#### Mock service if request contains headers with value
```gherkin
Given the http request headers are:
  | myHeader  | myValue  |  
  | myHeader2 | myValue2 |   
```

---
Note: support regex in name and value

#### Mock service if request query param matches
```gherkin
Given the mock receives request with query parameter "queryParam" with value "queryParamValue"
```

#### Mock service if request query params match
```gherkin
And the mock receives a request with query parameters:
  | query  | myQuery |
  | query2 | myQuery2|
```
---
Note: support regex in name and value

### Mock response

#### Add status code to the mock response
```gherkin
And the mock responds with status code 200
```
#### Add status code and limit number of mock responses
```gherkin
And the mock responds with status code "200" exactly 3 times
```

#### Add header to the mock response for the service test
```gherkin
And the mock response will have header "Content-Type" with value "application/json"
```

#### Add delay mock response 
```gherkin
And the mock response will have a delay of 5s
```


## Installation

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>com.mariocairone.cucumbersome</groupId>
  <artifactId>mock</artifactId>
  <version>{{version}}</version>
  <scope>test</scope>
</dependency>
```

Create the test class with the package glue

```java
import org.junit.ClassRule;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import static com.mariocairone.cucumbersome.steps.mock.config.MockConfig.mockOptions;
import com.mariocairone.cucumbersome.steps.mock.container.MockServerContainer;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
"json:target/cucumber/cucumber.json",
"junit:target/cucumber/cucumber.xml"},
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/mockserver",
    strict = true)
public class CucumbersomeMockIT  {

  @ClassRule
  public static MockServerContainer mockServer = new MockServerContainer();
  
  @BeforeClass
  public static void setup() {
  	mockOptions()
  		.addService("default",mockServer);
	 }
}
```

---
Note: The library include a `MockServiceContainer` class that can be used instead of the official `MockServiceContainer`. The implementation provided extend the official libriry adding the ability to use a fixed exposed port on the host machine to simplify local debug. 
Also please be sure to modify the features attribute to match your project requirement
