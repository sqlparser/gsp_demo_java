SELECT
	'CREATE OR REPLACE VIEW '|| v.VIEWNAME || ' AS ' || v.DEFINITION AS sourceCode,
	'view'||'' AS type,
	'"' || v.VIEWNAME || '"' AS viewName,
	'"' || u.GROUPNAME || '"' AS groupName,
	'"' || o.DATABASE || '"' AS databaseName,
	'"' || v.OWNER || '"' AS schemaName
FROM
	_V_VIEW v
LEFT JOIN _V_GROUP u ON
	u.OBJID = v.OBJID
LEFT JOIN _v_objs_owned o ON
   o.objid = v.objid
WHERE
	o.DATABASE = '%s';