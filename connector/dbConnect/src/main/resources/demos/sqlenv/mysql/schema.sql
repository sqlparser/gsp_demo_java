SELECT
	concat('`','%s','`') AS databaseName,
	concat('`',t.table_schema,'`') AS schemaName,
	concat('`',t.table_name,'`') AS tableName,
	CASE
WHEN t.table_type = 'SYSTEM VIEW' THEN
	'true'
WHEN t.table_type = 'VIEW' THEN
	'true'
WHEN t.table_type = 'BASE TABLE' THEN
	'false'
ELSE
	t.table_type
END AS isView,
 concat('`',c.column_name,'`') AS columnName,
 c.data_type AS dataType,
 NULL as comments
FROM
	information_schema.`TABLES` t,
	information_schema.`COLUMNS` c
WHERE
	t.table_schema = '%s'
AND t.table_catalog = c.table_catalog
AND t.table_schema = c.table_schema
AND t.table_name = c.table_name
AND t.table_schema NOT IN ('information_schema')
ORDER BY
	t.table_catalog,
	t.table_schema,
	t.table_name,
	c.ordinal_position