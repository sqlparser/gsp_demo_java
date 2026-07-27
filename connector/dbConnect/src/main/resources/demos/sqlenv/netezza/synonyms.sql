SELECT 
'"' || o.DATABASE || '"' AS databaseName,
'"' || v.OWNER || '"'  as schemaName,
'"' || v.SYNONYM_NAME || '"' as name,
'"' || v.REFSCHEMA || '"' AS sourceSchema,
'"' || v.REFOBJNAME || '"' AS sourceName,
'"' || v.REFDATABASE || '"' AS sourceDbLinkName
FROM _v_synonym v
LEFT JOIN _V_GROUP u ON
	u.OBJID = v.OBJID
LEFT JOIN _v_objs_owned o ON
   o.objid = v.objid
WHERE
	o.DATABASE = '%s';   