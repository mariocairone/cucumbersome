
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
```

if host and port are not specified for a service the default will be used:

| Property Name | Default value         |
|:--------------|:----------------------|
| port          | random available port |
| host          | <serviceName>         |


 using default parameters.

## Steps in this library

#### Create a mock definition for the service test

```gherkin
Given the mock with path "/" and method "GET" is added to service "test"
```
#### Mock service if request body match
```gherkin
 And the mock receive request with body
 """
  {
   "test": "test"
  }
 """
```

#### Mock service if request header match
```gherkin
Given the mock receive request with header "MyHeader" with value "MyHeaderValue"
```
#### Mock service if request query param match
```gherkin
Given the mock receive request with query parameter "queryParam" with value "queryParamValue"
```

#### Add status code to the mock response
```gherkin
And the mock responds with status code 200
And the mock responds with status code "200" exactly 3 times
```
#### Add header to the mock response for the service test
```gherkin
And the mock response will have header "Content-Type" with value "application/json"
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
import static com.mariocairone.cucumbersome.steps.mock.MockConfig.mockOptions;

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
Note: be sure to modify the features attribute to match your requirement
