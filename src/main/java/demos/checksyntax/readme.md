## Description
Checking the SQL syntax without connecting to a database server using the General 
SQL Parser library.

## Usage
`java checksyntax [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>]`

Only SQL filename ended with .sql extentsion will be processed.

## Maven usage 

1. In the lib directory to execute the command
```mvn
   mvn install:install-file -Dfile=gudusoft.gsqlparser-x.x.x.x.jar -DgroupId=gudusoft.gsqlparser -DartifactId=gsqlparser -Dversion=latest -Dpackaging=jar
   mvn install:install-file -Dfile=gudusoft.gsqlparser-2.8.3.8.jar -DgroupId=gudusoft.gsqlparser -DartifactId=gsqlparser -Dversion=latest -Dpackaging=jar
````

2.Configure parameters for the checksyntax class in the pom.xml.
```pom
<configuration>
    <mainClass>demos.checksyntax.checksyntax</mainClass>

    <!-- Parameters of the checksyntax class, eg: -->
    <arguments>
        <argument>/f</argument>
        <argument>d:\sql.sql</argument>
        <argument>/t</argument>
        <argument>oracle</argument>
    </arguments>
</configuration>
```

3.Compile and run
```mvn
mvn clean compile exec:java
```
