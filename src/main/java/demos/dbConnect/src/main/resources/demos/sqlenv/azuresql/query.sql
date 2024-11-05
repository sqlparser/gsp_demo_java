select
 	b.text as sourceCode,
    case a.type
    when 'V' Then 'view'
    when 'P' Then 'procedure'
    when 'TR' Then 'trigger'
    when 'FN' Then 'function'
    when 'TF' Then 'function'
    when 'IF' Then 'function'
    else a.type END as dbOjbType,
 	'['+ a.name + ']' AS dbObjName,
 	null as groupName,
 	'['+ '%s' + ']' as databaseName,
 	'['+ c.name + ']' as schemaName
 from
 	sys.objects a,
 	sys.syscomments b ,
 	sys.schemas c
 where
 	a.object_id = b.id
     and a.schema_id = c.schema_id
     and a.type in ('TR', 'FN', 'P', 'TF', 'IF', 'V')