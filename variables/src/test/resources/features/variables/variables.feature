Feature: Core Steps 
Set System properties and Variables

  Scenario: Set system property
    Given the system property "mule.env" with value "local"

  Scenario: Set system properties
    Given the system properties:
    |prop1|value1|
    |prop2|value2|

  Scenario: Set local variable
    Given the variable "string" with value "string"
    And the variable "var" with value "{{ string }}"
    And the variable "int" with value 2

  Scenario: Set variables
    Given the variables:
    |var1|value1|
    |var2|value2|

  Scenario: Set global variable
    Given the global variable "string" with value "string"
    And the global variable "var" with value "{{ string }}"
    And the global variable "int" with value 2

  Scenario: Access global variable defined in other scenario
    And the global variable "var2" with value "{{ string }}"

  Scenario: Set global variables
    Given the global variables:
    |var1|value1|
    |var2|value2|
    
  Scenario: Set variable with quote 
    Given the variable "var" with value "string "with" quoted text"
    And the logger print "{{var}}"
    
