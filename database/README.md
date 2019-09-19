# Cucumbersome Database

The steps in this library enable Database integration test using [Cucumber](https://cucumber.io)

## Configuration

### Java Configuration
```java
DatabaseConfig
  .databaseOptions()
   .withDatabaseContainer(postgres)
   .withDatabaseUrl(databaseUrl) 	
   .withDatabasePassword(databasePassword)
   .withDatabaseUsername(databaseUsername);
```
### Configuration File
The following properties can be configured in the file `cucumbersome.properties`

```
database.url={{postgres.getJdbcUrl()}}
database.username=mule
database.password=password
```

## Steps in this library

#### Configure Database Client
```gherkin
Given the database username is "mule"
And the database password is "password"
And the database url is "{{postgres.getJdbcUrl()}}"
```
#### Delete all and insert data in a database table
```gherkin
Given the database table "models" contains only the following rows
  | id | created             | modified            | email                 | fullname | password  |
  | 1  | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin@superbiz.org  | Carlos   | passw0rd  |
  | 2  | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd2 |
  | 3  | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin3@superbiz.org | Carlos3  | passw0rd3 |
```
#### Insert data in a database table
```gherkin
Given the database table "models" contains the following rows
  | id | created             | modified            | email                 | fullname | password |
  | 4  | 2015-02-11 00:00:00 | 2015-02-11 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd |
```

#### Assert database table exists
```gherkin
Then the database table "models" exists
```
#### Assert database table row number
```gherkin
Then the database table "models" should contain 3 rows
Then the database table "models" should contain at most 3 rows
Then the database table "models" should contain at least 3 rows
Then the database table "models" should contain more than 2 rows
Then the database table "models" should contain less than 4 rows
```
#### Assert database table content
```gherkin
And the database table "models" should have the following rows
  | id | created             | modified            | email                 | fullname | password |
  | 4  | 2015-02-11 00:00:00 | 2015-02-11 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd |
And the database table "models" should have the following rows
  | id |
  | 4  |
And the database table "models" should have the following rows
  | email 								 |
  | cchacin2@superbiz.org  |
And the database table "models" should have the following rows
  | id | email                 | fullname | password |
  | 4  | cchacin2@superbiz.org | Carlos2  | passw0rd |
```
#### delete all database table rows
```gherkin
When the database table "models" is empty
```

## Installation

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>com.mariocairone.cucumbersome.steps</groupId>
  <artifactId>database</artifactId>
  <version>{{version}}</version>
  <scope>test</scope>
</dependency>
```

Create the test class with the package glue

```java
import java.util.Map;
import org.junit.ClassRule;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.mariocairone.cucumbersome.settings.Settings;
import static com.mariocairone.cucumbersome.steps.database.DatabaseConfig.*;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
"json:target/cucumber/cucumber.json",
"junit:target/cucumber/cucumber.xml"},
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/database",
    strict = true)
public class CucumbersomeDatabaseIT {

  static final Map<String, Object> variables = Settings.getInstance().getGlobalVariables();

  @ClassRule
  public static  PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>()		
    .withInitScript("sample-data.sql")
    .withUsername("mario")
    .withPassword("password")
    .waitingFor(Wait.forListeningPort());



 @BeforeClass
  public static void setup() {
   databaseOptions()
    .withDatabaseContainer(postgres);
    variables.put("postgres",postgres);
 }

}
```

---
Note: be sure to modify the features attribute to match your requirement
