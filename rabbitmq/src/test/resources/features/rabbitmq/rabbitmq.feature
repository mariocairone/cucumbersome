Feature: RabbitMQ

  Background: The RabbitMQ container is running
    #  Configure a direct exchange and bind queue with routing key
    Given the rabbitmq exchange "test_direct_exchange" of type "direct" is declared
    And the rabbitmq queue "test_direct_queue" is bind to exchange "test_direct_exchange" with routing key "test"
    #  Configure fanout exchange and bind queue
    Given the rabbitmq exchange "test_fanout_exchange" of type "fanout" is declared
    And the rabbitmq queue "test_fanout_queue" is bind to exchange "test_fanout_exchange"
    #  Configure topic exchange and bind queue
    Given the rabbitmq exchange "test_topic_exchange" of type "fanout" is declared
    And the rabbitmq queue "test_topic_queue" is bind to exchange "test_topic_exchange" with routing key "test"

  Scenario: Test rabbitmq send and receive message from direct exchange
    Given the rabbitmq message body is:
      """
      {
      	"test": "test"
      }
      """
    When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_direct_queue"

  Scenario: Test rabbitmq send and receive message from fanout exchange
    Given the rabbitmq message body is:
      """
      {
      	"test": "test"
      }
      """
    When the rabbitmq client sends message to fanout exchange "test_fanout_exchange"
    Then the rabbitmq message is received from the queue "test_fanout_queue"
    And the rabbitmq message body should not be empty

  Scenario: Test rabbitmq send and receive message from topic exchange
    Given the rabbitmq message body is:
      """
      {
      	"test": "test"
      }
      """
    When the rabbitmq client sends message to exchange "test_topic_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_topic_queue"
    And the rabbitmq message body should not be empty

  Scenario: Test rabbitmq message creation1
    Given the rabbitmq message body is:
      """
      {
      	"test": "test"
      }
      """
    When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_direct_queue"
    And the rabbitmq message body should not be empty

  Scenario: Test rabbitmq message creation1
    Given the rabbitmq message body is "Hello World"
    When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_direct_queue"
    And the rabbitmq message body should not be empty

  Scenario: Test rabbitmq message creation
    Given the rabbitmq message body is:
      """
      {
      	"test": "test"
      }
      """
    When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_direct_queue"

  Scenario: Test rabbitmq message body array assertions
    And the rabbitmq message body is:
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
    When the rabbitmq client sends message to exchange "test_direct_exchange" with routing key "test"
    Then the rabbitmq message is received from the queue "test_direct_queue"
    And the rabbitmq message body should not be empty
    And the rabbitmq message body should contain "[0].foo"
    And the rabbitmq message body should contain "[0].foo" with value "bar"
    And the rabbitmq message body should not contain "[0].bar"
    And the rabbitmq message body should not contain "[0].foo" with value "wee"
    And the rabbitmq message body entity "[2].foos" should be:
      """
      [ "bar","wee"]
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
    
    
    Scenario: Test Connection Parameter
    Given the rabbitmq host is "{{ rabbitMqContainerIp }}"
    And the rabbitmq port is "{{ rabbitMqContainerPort }}"
    And the rabbitmq username is "mule"
    And the rabbitmq password is "password"
    And the rabbitmq virtualHost is "/"

