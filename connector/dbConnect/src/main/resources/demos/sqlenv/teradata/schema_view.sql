SELECT
'"' || '%s' || '"' as databaseName,
'"' || '%s' || '"' as schemaName,
'"' || TRIM(TABLENAME) || '"' as tableName,
'true' as isView,
'"' || TRIM(COLUMNNAME) || '"' as columnName,
TRIM(COLUMNTYPE) as dataType,
NULL as comments
FROM (
SELECT
t.DATABASENAME,
t.TABLENAME,
COLUMNNAME,
case  c.COLUMNTYPE
                    when 'AT' then 'time'  
                    when 'BF' then 'byte'
                    when 'BO' then 'blob'
                    when 'BV' then 'varbyte'
                    when 'CF' then 'char'
                    when 'CO' then 'clob'
                    when 'CV' then 'varchar'
                    when 'D ' then 'decimal'
                    when 'DA' then 'date'
                    when 'DH' then 'interval day to hour'
                    when 'DM' then 'interval day to minute'
                    when 'DS' then 'interval day to second'
                    when 'DY' then 'interval day'
                    when 'F ' then 'float'
                    when 'HM' then 'interval hour to minute'
                    when 'HR' then 'interval hour'
                    when 'HS' then 'interval hour to second'
                    when 'I1' then 'byteint'
                    when 'I2' then 'smallint'
                    when 'I8' then 'bigint'
                    when 'I ' then 'int'
                    when 'MI' then 'interval minute'
                    when 'MO' then 'interval month'
                    when 'MS' then 'interval minute to second'
                    when 'N ' then 'number'
                    when 'PD' then 'period(date)'
                    when 'PS' then 'period(timestamp('
                    when 'PT' then 'period(time('
                    when 'SC' then 'interval second'
                    when 'SZ' then 'timestamp with time zone'
                    when 'TS' then 'timestamp'
                    when 'TZ' then 'time with time zone'
                    when 'YI' then 'interval year'
                    when 'YM' then 'interval year to month'                         
                end
            ||  case when c.columntype in ('BF','BV') then '(' || cast (cast (c.ColumnLength            as format '-(9)9') as varchar (10)) || ')'  else '' end
            ||  case when c.columntype in ('CF','CV') then '(' || cast (cast (c.ColumnLength            as format '-(9)9') as varchar (10)) || ') character set ' || case c.CharType when 1 then 'latin' when 2 then 'unicode' end   else '' end           
            ||  case when c.columntype in ('AT','TS') then '(' || cast (cast (c.DecimalFractionalDigits as format '9'    ) as varchar (1))  || ')'  else '' end
            ||  case when c.columntype in ('PS','PT') then '(' || cast (cast (c.DecimalFractionalDigits as format '9'    ) as varchar (1))  || '))' else '' end
            ||  case when c.columntype in ('D'      ) then '(' || cast (cast (c.DecimalTotalDigits      as format '-(9)9') as varchar (10)) || ',' || cast (cast (c.DecimalFractionalDigits   as format '9') as varchar (1)) || ')' else '' end
as COLUMNTYPE, ColumnId
FROM DBC.COLUMNSV c
JOIN DBC.Tables  t on t.TableName = c.TableName and t.TableKind='V'
WHERE t.DATABASENAME='%s'
) TBL
ORDER BY ColumnId;
