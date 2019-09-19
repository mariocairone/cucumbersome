# Cucumbersome Variables

The variables module enable the integration with the template engine context [basis-template](https://github.com/badlogic/basis-template).

We can define 2 type of variables:
* Global Variable will be available in different Scenarios.
* Local Variable will be available only within the same Scenario.

The variable will be resolved by the template engine.

## Steps in this library

#### Set System Property

```gherkin
Given set system property "mule.env" with value "local"
And the system properties
|prop1|value1|
|prop2|value2|    
```

#### Set variable

```gherkin
Given the variable "string" with value "string"
And the variable "var" with value "{{ string }}"
And the variable "int" with value 2
And the global variable "int" with value 2
And the variables
|var1|value1|
|var2|value2|
And the global variables
|var1|value1|
|var2|value2|  
```

#### Log

```gherkin
And the logger print "{{var}}"
```

## Installation

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>com.mariocairone.cucumbersome</groupId>
  <artifactId>variables</artifactId>
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
    features = "classpath:features/variable",
    strict = true)
public class CucumbersomeVariablesIT  {

}
```
---
Note: be sure to modify the features attribute to match your requirement
