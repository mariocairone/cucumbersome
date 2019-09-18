
# Cucumbersome Core

The cucumber core module define the basic elements for the framework.

## Settings
The framework settings are managed by a Singleton instance of the Settings class.
The Settings class is responsible to load the properties file from the file `cucumbersome.properties`

## BaseStepDefs

The BaseStepDefs must be extended by all the Steps Definition classes.
By Extending this Class all the Steps Definition Class will have access to the template engine and the Global and local variables.
