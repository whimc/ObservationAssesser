# ObservationAssesser

Plugin to assess student observations using machine learning and provide text and open-learner model feedback. Should be automatically invoked with WHIMC-Observation and not manually called.

## Building
Compile an uberjar from the command line by doing a "Build" via Maven:
```
$ mvn clean package
```
It should show up in the target directory.

## Config
### MySQL
| Key | Type | Description |
|---|---|---|
|`mysql.host`|`string`|The host of the database|
|`mysql.port`|`integer`|The port of the database|
|`mysql.database`|`string`|The name of the database to use|
|`mysql.username`|`string`|Username for credentials|
|`mysql.password`|`string`|Password for credentials|

#### Example
```yaml
mysql:
  host: localhost
  port: 3306
  database: minecraft
  username: user
  password: pass
```