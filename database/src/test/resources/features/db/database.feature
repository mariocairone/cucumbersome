Feature: Database SQL

  Scenario: Print the Database Content
    Given the database table "models" is printed

  Scenario: Retrieve users list
    Given the database table "models" contains only the following rows:
      | id | created             | modified            | email                 | fullname | password  |
      |  1 | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin@superbiz.org  | Carlos   | passw0rd  |
      |  2 | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd2 |
      |  3 | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin3@superbiz.org | Carlos3  | passw0rd3 |
    Then the database table "models" exists
    Then the database table "models" should contain 3 rows
    Then the database table "models" should contain at most 3 rows
    Then the database table "models" should contain at least 3 rows
    Then the database table "models" should contain more than 2 rows
    Then the database table "models" should contain less than 4 rows
    And the database table "models" should have the following rows:
      | id | created             | modified            | email                | fullname | password |
      |  1 | 2014-07-16 00:00:00 | 2014-07-16 00:00:00 | cchacin@superbiz.org | Carlos   | passw0rd |

  Scenario: Test insert row and verify full and partial table
    Given the database table "models" contains the following row:
      | id | created             | modified            | email                 | fullname | password |
      |  4 | 2015-02-11 00:00:00 | 2015-02-11 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd |
    And the database table "models" should have the following rows:
      | id | created             | modified            | email                 | fullname | password |
      |  4 | 2015-02-11 00:00:00 | 2015-02-11 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd |
    And the database table "models" should have the following rows:
      | id |
      |  4 |
    And the database table "models" should have the following rows:
      | email                 |
      | cchacin2@superbiz.org |
    And the database table "models" should have the following rows:
      | id | email                 | fullname | password |
      |  4 | cchacin2@superbiz.org | Carlos2  | passw0rd |

  Scenario: Test Select row and stor in variable
    Given the database query below is executed:
      """
      	SELECT * FROM "models" WHERE id = 1
      """
    And the database query results are stored in the variable "model"
    And the logger print "{{ "password : " + model.get(0).get("password") }}"

  Scenario: Test clean all records in table
    Given the database table "models" contains the following rows:
      | id | created             | modified            | email                 | fullname | password |
      |  5 | 2015-02-11 00:00:00 | 2015-02-11 00:00:00 | cchacin2@superbiz.org | Carlos2  | passw0rd |
    When the database table "models" is empty
    And the database table "models" should not have the following rows:
      | id |
      |  5 |
    Then the database table "models" should contain 0 rows

  Scenario: Test Database settings
    Given the database username is "mule"
    And the database password is "password"
    And the database url is "{{postgres.getJdbcUrl()}}"
    And the database schema is "partner_it"
    And the database catalog is "default"
