select
  '"'||'%database'||'"' as databaseName,
  '"'||x.owner||'"' as schemaName,
  '"'||x.table_name||'"' as tableName,
  decode(obj.object_type, 'VIEW','true','false') as isView,
  '"'||x.column_name||'"' as columnName,
  x.data_type||
    case
      when x.data_type ='RAW' then '('|| x.data_length ||')'
      when x.data_precision is not null and nvl(x.data_scale,0)>0 then '('||x.data_precision||','||x.data_scale||')'
      when x.data_precision is not null and nvl(x.data_scale,0)=0 then '('||x.data_precision||')'
      when x.data_precision is null and x.data_scale is not null then '(38,'||x.data_scale||')'
      when x.char_length>0 then '('||x.char_length|| case x.char_used
                                                       when 'B' then ' byte'
                                                       when 'C' then ' char'
                                                       else null
                                                     end||')'
      end  as dataType,
  c.comments as comments
from all_tab_cols x,
     all_col_comments c,
     all_objects obj
where
  x.owner = '%schema'
  and c.column_name = x.column_name
  and c.owner =x.owner
  and c.table_name = x.table_name
  and x.owner =obj.owner
  and x.table_name = obj.object_name
  and obj.object_type in ('TABLE', 'VIEW')
  and x.owner NOT IN ('SYS','SYSTEM')
order by x.owner, x.table_name, isView, x.column_id
