# WHIMC-StudentFeedback

Plugin to assess student in-game behaviors and provide feedback

## Building
Compile an uberjar from the command line by doing a "Build" via Maven:
```
$ mvn clean package
```
It should show up in the target directory use .

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

## Commands
| Command                                                     | Description                                                                                                                |
|-------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `/progress`                                                 | Display student progress on interest measures during session                                                               |
| `/leaderboard`                                              | Display leaderboard of averages of student interest measures during session                                                |
| `/agentdialogue`                                            | Display dialogue prompts for agent (should be called by right clicking on agent and not directly invoked)                  |
| `/structureassessment <observation> <type>`                 | Display progress bars for observation structure skills (should be called from Observations and not directly invoked)       |

