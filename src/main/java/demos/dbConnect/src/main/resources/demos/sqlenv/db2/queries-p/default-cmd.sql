SELECT
	NULL AS sourceCode,
	v.TABNAME AS viewName,
	NULL AS groupName,
	'%s' AS databaseName,
	v.TABSCHEMA AS schemaName
FROM
	SYSCAT.TABLES v