SELECT
	'['+ '%s' + ']' as databaseName,
	'['+ s.name + ']' as schemaName,
	'['+ t.name + ']' as tableName,
	CASE WHEN t.type = 'U' THEN 'false' ELSE 'true' END as isView,
	'['+ c.name + ']' as columnName,
	type_name(c.system_type_id) +
            CASE
                WHEN type_name(c.system_type_id) IN ('char', 'varchar', 'nchar', 'nvarchar', 'binary', 'varbinary')
                    THEN N'('
                        +
                        CASE WHEN userDefinedTypeProperties.CHARACTER_MAXIMUM_LENGTH = -1 THEN 'MAX'
                            ELSE CONVERT
                                (
                                    varchar(4)
                                    ,userDefinedTypeProperties.CHARACTER_MAXIMUM_LENGTH
                                )
                        END
                        + N')'
                WHEN type_name(c.system_type_id) IN ('decimal', 'numeric')
                    THEN N'(' + CONVERT(varchar(4), userDefinedTypeProperties.NUMERIC_PRECISION) + N', ' + CONVERT(varchar(4), userDefinedTypeProperties.NUMERIC_SCALE) + N')'
                WHEN type_name(c.system_type_id) IN ('time', 'datetime2', 'datetimeoffset')
                    THEN N'(' + CAST(userDefinedTypeProperties.DATETIME_PRECISION AS national character varying(36)) + N')'
                ELSE N''
            END as dataType,
	'' as comments

FROM
	[%s].sys.objects t
	INNER JOIN
	[%s].sys.schemas s on t.schema_id = s.schema_id
	INNER JOIN
	[%s].sys.columns c on t.object_id = c.object_id
    OUTER APPLY
    (
        SELECT
             c.is_nullable
            ,c.precision AS NUMERIC_PRECISION
            ,c.scale AS NUMERIC_SCALE
            ,c.max_length AS CHARACTER_MAXIMUM_LENGTH
            ,CASE WHEN c.user_type_id IS NULL THEN 0 ELSE 1 END AS IsTableType
            ,CONVERT(smallint,
                    CASE -- datetime/smalldatetime
                    WHEN c.system_type_id IN (40, 41, 42, 43, 58, 61) THEN ODBCSCALE(c.system_type_id, c.scale)
                    END
            ) AS DATETIME_PRECISION
    ) AS userDefinedTypeProperties
WHERE
	t.type in ('U','V')
ORDER BY
	schemaName,
	tableName,
	isView,
	c.column_id;

