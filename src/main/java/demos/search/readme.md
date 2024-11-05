## Description
Search the specified parse tree node name in sql files under the directory recursively . 
The file name will be printed out if it include the specified parse tree node name. 

Please check [toXML demo](./visitors) to find out more information on how to use the visitor pattern introduced in this library.

## Usage
`java searchClause parse_tree_node_name directory`


Content in the test.sql:
```sql
SELECT
    CONVERT(VARCHAR(10), GETDATE(), 104) AS ActualDate 
FROM SomeTable
```

run this command:

`java searchClause TFunctionCall directory_include_sql_files`

will return:

`Find TFunctionCall in test.sql`


