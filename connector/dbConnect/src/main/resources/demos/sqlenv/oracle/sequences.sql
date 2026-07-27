select
  '"'||'%database'||'"' as databaseName,
  '"'||s.SEQUENCE_OWNER||'"' as schemaName,
  '"'||s.SEQUENCE_NAME||'"' as name,
  s.INCREMENT_BY as incrementBy 
from ALL_SEQUENCES s
where 
  s.SEQUENCE_OWNER = '%schema'
  and s.SEQUENCE_OWNER NOT IN ('SYS','SYSTEM')