## Tool for creating database migration scripts to be used by [Liquibase](https://www.liquibase.org/).

### The main objective
**Killer (and main) feature:** Change datatype of primary key column(s) using [Liquibase](https://www.liquibase.org/).
Changing datatype of column(s) used as PK is a rather complex procedure which involves following steps (may be DB-specific):
1. temporarily drop PK (otherwise column can not be changed coz it is used in PK index)
2. modify PK column datatype
3. restore PK

When foreign keys (FK) in other tables (referencing modified column) exist the things go even more complicated, involving
following steps:
1. temporarily drop FK(s) in dependent tables (otherwise PK can not be dropped)
2. temporarily drop PK
3. modify PK column datatype
4. modify all FK column(s) datatype
5. restore PK
6. restore FK(s)

The "changegen" tool automates all listed steps. You need to provide just table name, column name and new datatype -
 the tool will generate all Liquibase statements for droping & restoring PK and FKs (if exist).
Internally changegen connects to database (readonly) and uses INFORMATION_SCHEMA to obtain all information about column relations.

### Supported databases
Currently only MSSQL and H2 DBs are supported. To support other DBs just add dependency to required JDBC driver.


### How to use
1. Copy [src/main/resources/datasource.properties.example](src/main/resources/datasource.properties.example) to
[src/main/resources/datasource.properties](src/main/resources/datasource.properties) and modify DB connection configs
2. Modify table name, column name and datatype in main() method of [Main](src/main/java/io/github/sunlaud/changegen/Main.java) class
3. Launch Main class
4. Changesets for Liquibase will be generated and dumped to console

**Note:** mind cases of table & column names, tool relies on JDBC functionality which is case-sensitive (need to fix?)

### Todo
* create CLI launcher
* output a well-formed &lt;changeSet/&gt; instead of just simple DDL statements
* add more JDBC drivers
* (?) split changes into different changesets for DBs that doesn't support transactional DDL modifications
