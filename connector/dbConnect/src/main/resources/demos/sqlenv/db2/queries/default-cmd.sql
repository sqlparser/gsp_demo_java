SELECT
	v.TEXT AS sourceCode,
	v.VIEWNAME AS viewName,
	NULL AS groupName,
	'%s' AS databaseName,
	v.VIEWSCHEMA AS schemaName
FROM
	SYSCAT.VIEWS v