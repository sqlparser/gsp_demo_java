SELECT
	v.text sourceCode,
	'view' as dbOjbType,
	'"'||v.VIEW_NAME||'"' as dbObjName,
	'"'||v.OWNER||'"."'||v.VIEW_NAME||'"' as groupName,
	'"'||'%database'||'"' as databaseName,
	'"'||v.OWNER||'"' as schemaName
FROM all_views v where v.OWNER = '%schema' and v.OWNER not in ('SYS', 'SYSTEM');

SELECT
	v.text sourceCode,
	'procedure' as dbOjbType,
	'"'||v.name||'"' as dbObjName,
	'"'||v.OWNER||'"."'||v.name||'"' as groupName,
	'"'||'%database'||'"' as databaseName,
	'"'||v.OWNER||'"' as schemaName
FROM all_source v where type='PROCEDURE' and v.OWNER = '%schema' and v.OWNER not in ('SYS', 'SYSTEM')
UNION ALL
SELECT
	v.text sourceCode,
	'trigger' as dbOjbType,
	'"'||v.name||'"' as dbObjName,
	'"'||v.OWNER||'"."'||v.name||'"' as groupName,
	'"'||'%database'||'"' as databaseName,
	'"'||v.OWNER||'"' as schemaName
FROM all_source v where type='TRIGGER' and v.OWNER = '%schema' and v.OWNER not in ('SYS', 'SYSTEM')
UNION ALL
SELECT
	v.text sourceCode,
	'function' as dbOjbType,
	'"'||v.name||'"' as dbObjName,
	'"'||v.OWNER||'"."'||v.name||'"' as groupName,
	'"'||'%database'||'"' as databaseName,
	'"'||v.OWNER||'"' as schemaName
FROM all_source v where type='FUNCTION' and v.OWNER = '%schema' and v.OWNER not in ('SYS', 'SYSTEM');

SELECT
	mv.QUERY sourceCode,
	'materialized view' as dbOjbType,
	'"'||mv.MVIEW_NAME||'"' as dbObjName,
	'"'||mv.OWNER||'"."'||mv.MVIEW_NAME||'"' as groupName,
	'"'||'%database'||'"' as databaseName,
	'"'||mv.OWNER||'"' as schemaName
FROM all_mviews mv where mv.OWNER = '%schema' and mv.OWNER not in ('SYS', 'SYSTEM');