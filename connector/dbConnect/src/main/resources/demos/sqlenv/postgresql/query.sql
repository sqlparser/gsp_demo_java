select
  'CREATE VIEW '|| v.table_name || ' AS ' || v.view_definition as sourceCode,
  'view' as dbOjbType,
  '"' || v.table_name || '"' as dbObjName,
  null as groupName,
  '"' || v.table_catalog || '"' as databaseName,
  '"' || v.table_schema || '"' as schemaName
from information_schema.views v 
where v.table_catalog = '%s'
UNION ALL
select
       DISTINCT(case when l.lanname = 'internal' then p.prosrc
            else pg_get_functiondef(p.oid)
            end) as sourceCode,
         lower(r.ROUTINE_TYPE) as dbOjbType,
         '"' || p.proname || '"' as dbObjName,
 	     null as groupName,
 	     '"' || r.routine_catalog || '"' as databaseName,
       '"' || n.nspname || '"' as schemaName
from pg_proc p
 join pg_namespace n on p.pronamespace = n.oid
 join pg_language l on p.prolang = l.oid
 join pg_type t on t.oid = p.prorettype
 join information_schema.routines r on r.ROUTINE_SCHEMA = n.nspname and r.ROUTINE_NAME = p.proname
where r.routine_catalog = '%s' and n.nspname not in ('pg_catalog', 'information_schema') and l.lanname in('sql', 'plpgsql')
order by schemaName,
         dbObjName;