select
  '"' || t.table_catalog || '"' as databaseName,
  '"' || t.table_schema || '"' as schemaName,
  '"' || t.table_name || '"' as tableName,
  case when t.table_type = 'VIEW' then 'true'
       when t.table_type = 'BASE TABLE' then 'false'
       else 'false'
  end as isView,
  '"' || c.column_name || '"' as columnName,
  c.data_type as dataType,
  null as comments
from
  information_schema.tables t,
  information_schema.columns c
where
  t.table_catalog = '%s'
  and t.table_catalog = c.table_catalog
  and t.table_schema = c.table_schema
  and t.table_name = c.table_name
  and t.table_schema not in ('pg_catalog', 'information_schema')
order by t.table_catalog, t.table_schema, t.table_name, c.ordinal_position;