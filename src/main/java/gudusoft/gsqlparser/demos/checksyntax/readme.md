## Description
Checking the SQL syntax without connecting to a database server using the General 
SQL Parser library.

## Usage
`java checksyntax [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>]`

Only SQL filename ended with .sql extentsion will be processed.

## Maven usage

From the repository root. The parser is resolved from Gudu's public Maven
repository by the root `pom.xml`, so there is nothing to install by hand:

```bash
mvn -q exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.checksyntax.checksyntax \
    -Dexec.args="/f your.sql /t oracle" \
    -Dexec.classpathScope=compile
```

Use `compile` scope, not `runtime` — some demos depend on `system`-scope jars
under `lib/`, which Maven leaves off the runtime classpath. See the root
`README.md`.

> This folder used to carry its own `pom.xml`, and this section used to tell you
> to `mvn install:install-file` a parser jar as
> `gudusoft.gsqlparser:gsqlparser:latest` and then edit that POM. Both are gone:
> the coordinate never resolved, `latest` is not a version Maven can use, and
> the root build has resolved the parser automatically since the repository was
> made buildable outside Gudu.
