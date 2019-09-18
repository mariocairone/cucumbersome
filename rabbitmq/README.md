
# Cucumbersome RabbitMQ

The steps in this module enable RabbitMQ integrations test using [Cucumber](https://cucumber.io)

## Configuration

### Java Configuration
```java
RabbitMqConfig
 .rabbitMqOptions()
 .withRabbitMqContainer(rabbitMqContainer);
 .withUseSsl(useSsl)
 .withRabbitPassword(rabbitPassword)
 .withRabbitUsername(rabbitUsername)	 	
 .withRabbitHost(rabbitHost)
 .withRabbitPort(rabbitPort)
 .withRabbitDefaultReadTimeout(rabbitDefaultReadTimeout)
 .withRabbitDefaultExchange(rabbitDefaultExchange)
 .withExchanges(new RabbitExchangeConfig(name, type, durable))
```

### Configuration File
The following properties can be configured in the file `cucumbersome.properties`

```
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=root
rabbitmq.password=toor
rabbitmq.virtualHost=defaulthost

rabbitmq.ssl=false
rabbitmq.default.exchange=default
rabbitmq.default.read.timeout=5

rabbitmq.exchange.test.name=test_default_exchange
rabbitmq.exchange.test.type=direct
rabbitmq.exchange.test.durable=false

```

## Steps in this library

### Configuration
#### Configure AMQP Client
```gherkin
Given the rabbitmq host is "{{rabbitMq.getContainerIpAddress()}}"
And the rabbitmq port is "{{rabbitMq.getFirstMappedPort()}}"
And the rabbitmq username is "mule"
And the rabbitmq password is "password"
And the rabbitmq virtualHost is "/"
```  
#### Configure a direct exchange and bind queue with routing key
```gherkin
Given the rabbitmq exchange "test_direct_exchange" of type "direct" is declared
And the rabbitmq queue "test_direct_queue" is bind to exchange "test_direct_exchange" with routing key "test"
```
####  Configure fanout exchange and bind queue
```gherkin
Given the rabbitmq exchange "test_fanout_exchange" of type "fanout" is declared
And the rabbitmq queue "test_fanout_queue" is bind to exchange "test_fanout_exchange"
```
####  Configure topic exchange and bind queue
```gherkin
Given the rabbitmq exchange "test_topic_exchange" of type "fanout" is declared
And the rabbitmq queue "test_topic_queue" is bind to exchange "test_topic_exchange" with routing key "test"
```
### Message
####  Create Message
```gherkin
Given the rabbitmq message is created
```
####  Send Message
```gherkin
When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
When the rabbitmq client sends message to fanout exchange "test_fanout_exchange"
When the rabbitmq client sends message to exchange "test_topic_exchange" with routing key "test"
```
####  Receive Message
```gherkin
Then the rabbitmq message is received from the queue "test_direct_queue"
```
#### Set Message body
```gherkin
And the rabbitmq message body is:
"""
{
 "test": "test"
}
"""
```
#### Assert received response body
```gherkin
And the rabbitmq message body should not be empty
And the rabbitmq message body should contain "[0].foo"
And the rabbitmq message body should contain "[0].foo" with value "bar"
And the rabbitmq message body should not contain "[0].bar"
And the rabbitmq message body should not contain "[0].foo" with value "wee"
And the rabbitmq message body entity "[2].foos" should be:
  """
  - bar
  - wee
  |YamlToJson
  """
And the rabbitmq message body should be an array with 3 elements
And the rabbitmq message body should be an array with at least 1 element
And the rabbitmq message body should be an array with at most 4 elements
And the rabbitmq message body should be an array with less than "5" elements
And the rabbitmq message body should be an array with more than "2" elements
And the rabbitmq message body entity "[2].foos" should be an array with 2 elements
And the rabbitmq message body entity "[2].foos" should be an array with at least 1 element
And the rabbitmq message body entity "[2].foos" should be an array with at most 4 elements
And the rabbitmq message body entity "[2].foos" should be an array with less than "3" elements   
And the rabbitmq message body entity "[2].foos" should be an array with more than "1" element   
```     
## Installation

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>com.mariocairone.cucumbersome</groupId>
  <artifactId>rabbitmq</artifactId>
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
									  "junit:target/cucumber/cucumber.xml"	},
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/rabbitmq",
    strict = true)
public class CucumbersomeIT  {

}
```
---
Note: be sure to modify the features attribute to match your requirement
