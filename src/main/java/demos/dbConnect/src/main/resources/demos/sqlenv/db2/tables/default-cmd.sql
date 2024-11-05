SELECT
    '%s' as dbName,
  t.TABSCHEMA as schemaName,
  t.TABNAME as tableName,
  case when t.TYPE = 'V' then 'true'
       when t.TYPE = 'A' then 'false'
       else 'false'
  end as isView,
  c.COLNAME,
  c.TYPENAME,
  null as comments
from
  syscat.tables t,
  syscat.columns c
where
  t.TABNAME = c.TABNAME
  and t.TABSCHEMA = c.TABSCHEMA
  and t.TABSCHEMA not in ('SYSCAT')
order by t.TABSCHEMA, t.TABNAME,  c.COLNO