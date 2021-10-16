SELECT
	'"' || clo.TABLE_CAT || '"' AS databaseName ,
	'"' || clo.TABLE_SCHEM || '"' AS schemaName,
	'"' || clo.TABLE_NAME || '"' AS tableName ,
	CASE
		WHEN t.TABLE_TYPE = 'SYSTEM VIEW' THEN 'true'
		WHEN t.TABLE_TYPE = 'VIEW' THEN 'true'
		ELSE 'false'
	END AS isView,
	'"' || clo.COLUMN_NAME || '"' AS columnName,
	clo.TYPE_NAME AS dataType,
	clo.REMARKS as comments
FROM
	_V_JDBC_COLUMNS1 clo,
	_V_JDBC_TABLES2 t
WHERE
	clo.TABLE_SCHEM = t.TABLE_SCHEM
	AND clo.TABLE_CAT = t.TABLE_CAT
	AND clo.TABLE_NAME = t.TABLE_NAME
	AND clo.TABLE_CAT = '%s'
	order by clo.TABLE_SCHEM,clo.TABLE_NAME;