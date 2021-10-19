SELECT
	''||'' AS sourceCode,
	'procedure'||'' AS type,
	'"' || v.PROCEDURESIGNATURE || '"' AS procedureName,
	'"' || u.GROUPNAME || '"' AS groupName,
	'"' || o.DATABASE || '"' AS databaseName,
	'"' || v.OWNER || '"' AS schemaName,
	v.RETURNS AS procedureReturn,
	v.PROCEDURESOURCE AS procedureSource
FROM
	_V_PROCEDURE v
LEFT JOIN _V_GROUP u ON
	u.OBJID = v.OBJID
LEFT JOIN _v_objs_owned o ON
   o.objid = v.objid
WHERE
	o.DATABASE = '%s';