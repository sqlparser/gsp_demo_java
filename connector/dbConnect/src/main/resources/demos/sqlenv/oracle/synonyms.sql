select
  '"'||'%database'||'"' as databaseName,
  '"'||s.OWNER||'"' as schemaName,
  '"'||s.SYNONYM_NAME||'"' as name,
  '"'||s.TABLE_OWNER||'"' as sourceSchema,
  '"'||s.TABLE_NAME||'"' as sourceName,
  case when s.DB_LINK is null then null else '"'||s.DB_LINK||'"' end as sourceDbLinkName 
from all_synonyms s,
	 all_objects o
where 
  s.OWNER = '%schema'
  and s.table_owner = o.owner 
  and s.table_name = o.object_name
  and o.object_type in  
					  ('MATERIALIZED VIEW'
					  ,'TABLE'
					  ,'SYNONYM'
					  ,'VIEW')
  and s.OWNER NOT IN ('SYS','SYSTEM')

