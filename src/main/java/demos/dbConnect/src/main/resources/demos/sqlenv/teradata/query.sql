select RequestText as sourceCode,
    case 
    when TableKind='V' Then 'view'
    when TableKind='P' Then 'procedure'
    when TableKind='E' Then 'procedure'
    when TableKind='G' Then 'trigger'
    when TableKind='M' Then 'macro'
    when TableKind='A' Then 'function'
    when TableKind='B' Then 'function'
    when TableKind='C' Then 'function'
    when TableKind='L' Then 'function'
    when TableKind='R' Then 'function'
    when TableKind='S' Then 'function'
    when TableKind='F' Then 'function'
    else TableKind END as dbOjbType,
    '"' || TRIM(TableName) || '"' as dbObjName,
    null as groupName,
    '"' || TRIM(DatabaseName) || '"' as databaseName,
    '"' || TRIM(DatabaseName) || '"'  as schemaName
from Tables where DatabaseName='%s' and TableKind not in ('T', 'U', 'X') and RequestText is not null and dbOjbType in ('view','procedure','trigger','macro','function');