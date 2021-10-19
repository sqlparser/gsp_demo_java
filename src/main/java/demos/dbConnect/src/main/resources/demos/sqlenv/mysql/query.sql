SELECT
	NULL as sourceCode,
	'view' as dbOjbType,
	concat('`',v.TABLE_NAME,'`') AS dbObjName,
	NULL AS groupName,
	concat('`',v.TABLE_SCHEMA,'`') AS databaseName,
	concat('`',v.TABLE_SCHEMA,'`') AS schemaName
FROM
information_schema.views v where v.TABLE_SCHEMA = '%s'
UNION ALL
SELECT
	NULL as sourceCode,
	'trigger' as dbOjbType,
	concat('`',v.TRIGGER_NAME,'`') AS dbObjName,
	NULL AS groupName,
	concat('`',v.TRIGGER_SCHEMA,'`') AS databaseName,
	concat('`',v.TRIGGER_SCHEMA,'`') AS schemaName
FROM
information_schema.`TRIGGERS` v where v.TRIGGER_SCHEMA = '%s'
UNION ALL
SELECT
  	NULL as sourceCode,
  	'function' as dbOjbType,
	concat('`',v.ROUTINE_NAME,'`') AS dbObjName,
	NULL AS groupName,
	concat('`',v.ROUTINE_SCHEMA,'`') AS databaseName,
	concat('`',v.ROUTINE_SCHEMA,'`') AS schemaName
FROM
	information_schema.routines v
WHERE
    routine_type = 'FUNCTION' and v.ROUTINE_SCHEMA = '%s'
UNION ALL    
SELECT
  	NULL as sourceCode,
  	'procedure' as dbOjbType,
	concat('`',v.ROUTINE_NAME,'`') AS dbObjName,
	NULL AS groupName,
	concat('`',v.ROUTINE_SCHEMA,'`') AS databaseName,
	concat('`',v.ROUTINE_SCHEMA,'`') AS schemaName
FROM
	information_schema.routines v
WHERE
    routine_type = 'PROCEDURE' and v.ROUTINE_SCHEMA = '%s';  
