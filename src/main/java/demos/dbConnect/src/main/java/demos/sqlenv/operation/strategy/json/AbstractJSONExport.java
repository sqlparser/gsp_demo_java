package demos.sqlenv.operation.strategy.json;

import demos.sqlenv.DatabaseSQLDataSource;
import demos.sqlenv.DbSchemaSQLDataSource;
import demos.sqlenv.SchemaSQLDataSource;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.constant.SystemConstant;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.*;
import demos.sqlenv.operation.DbOperationService;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.util.CollectionUtil;
import gudusoft.gsqlparser.util.Identifier;
import gudusoft.gsqlparser.util.SQLUtil;
import gudusoft.gsqlparser.util.json.JSON;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CemB
 */
public abstract class AbstractJSONExport implements DbOperationService<String> {

    public String operate(TSQLDataSource datasource) {
        long startTime = System.currentTimeMillis();
        Statement statement = null;
        try {
            try {
                statement = datasource.getConnection().createStatement();
            } finally {
            }
            TSQLDataSource dataSource = datasource;

            Metadata exportMetadataModel = new Metadata();
            exportMetadataModel.setCreatedBy(SystemConstant.name + " " + SystemConstant.version);
            exportMetadataModel.setDialect(datasource.getDbType());
            exportMetadataModel.setUserAccountId(dataSource.getAccount());
            exportMetadataModel.setExportId(UUID.randomUUID().toString());
            exportMetadataModel.setPhysicalInstance(dataSource.getHostName());

            List<String> databases = exportDatabases(datasource, statement, exportMetadataModel);
            if (databases != null) {
                exportMetadataModel.appendDatabases(new ArrayList<Database>());
                for (String database : databases) {
                    if (!acceptDatabase(datasource, database)) {
                        continue;
                    }
                    if (!SQLUtil.isEmpty(database)) {
                        exportTables(datasource, database, database, exportMetadataModel);
                        exportQueries(datasource, database, database, exportMetadataModel);
                        exportDbLinks(datasource, database, database, exportMetadataModel);
                    }
                }
            }

            if (exportMetadataModel.getDatabases() != null) {
                for (Database database : exportMetadataModel.getDatabases()) {
                    if (database.getTables() != null) {
                        for (Table table : database.getTables()) {
                            table.setDatabase(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog,
                                    table.getDatabase()).getNormalizeIdentifier());
                            table.setSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                    table.getSchema()).getNormalizeIdentifier());
                            table.setName(
                                    new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable, table.getName())
                                            .getNormalizeIdentifier());
                            if (table.getColumns() != null) {
                                for (Column column : table.getColumns()) {
                                    column.setName(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotColumn,
                                            column.getName()).getNormalizeIdentifier());
                                }
                            }
                        }
                    }
                    if (database.getSynonyms() != null) {
                        for (Synonym synonym : database.getSynonyms()) {
                            synonym.setDatabase(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog,
                                    synonym.getDatabase()).getNormalizeIdentifier());
                            synonym.setSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                    synonym.getSchema()).getNormalizeIdentifier());
                            synonym.setName(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable,
                                    synonym.getName()).getNormalizeIdentifier());
                            synonym.setSourceDbLinkName(
                                    new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog,
                                            synonym.getSourceDbLinkName()).getNormalizeIdentifier());
                            synonym.setSourceSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                    synonym.getSourceSchema()).getNormalizeIdentifier());
                            synonym.setSourceName(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable,
                                    synonym.getSourceName()).getNormalizeIdentifier());
                        }
                    }
                    if (database.getSequences() != null) {
                        for (Sequence sequence : database.getSequences()) {
                            sequence.setDatabase(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog,
                                    sequence.getDatabase()).getNormalizeIdentifier());
                            sequence.setSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                    sequence.getSchema()).getNormalizeIdentifier());
                            sequence.setName(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable,
                                    sequence.getName()).getNormalizeIdentifier());
                        }
                    }
                }
            }

            if (exportMetadataModel.getQueries() != null) {
                for (Query query : exportMetadataModel.getQueries()) {
                    query.setDatabase(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, query.getDatabase())
                                    .getNormalizeIdentifier());
                    query.setSchema(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, query.getSchema())
                                    .getNormalizeIdentifier());
                    query.setName(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable, query.getName())
                            .getNormalizeIdentifier());
                }
            }

            exportMetadataModel.setExportTime(System.currentTimeMillis() - startTime);
            return JSON.toJSONString(exportMetadataModel).replace((char) 160, (char) 32);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(statement);
        }
        return null;
    }

    protected List<String> exportDatabases(TSQLDataSource datasource, Statement statement,
                                           Metadata exportMetadataModel) {
        try {
            String databasesSql = databasesSql(datasource);
            ResultSet resultSet = statement.executeQuery(databasesSql);
            List<String> dbNames = new ArrayList<String>();
            while (resultSet.next()) {
                dbNames.add(resultSet.getString(1).trim());
            }
            return dbNames;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
            return null;
        }
    }

    protected void exportTables(TSQLDataSource datasource, String database, String catalog,
                                Metadata exportMetadataModel) {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = datasource.getConnection();
            if (enableSetCatalog()) {
                connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
                        .getNormalizeIdentifier());
            }
            statement = connection.createStatement();
            Database sqlDatabase = new Database();
            sqlDatabase.setName(database);
            List<Table> tables = new CopyOnWriteArrayList<Table>();
            String tablesSql = tablesSql(datasource, database, catalog);
            ResultSet resultSet = statement.executeQuery(tablesSql);
            List<MetadataItem> metadataItems = new ArrayList<MetadataItem>();
            while (resultSet.next()) {
                MetadataItem item = fetchMetadataItem(resultSet, exportMetadataModel);
                if (!acceptSchema(datasource, item.getDatabaseColumn(), item.getSchemaColumn())) {
                    continue;
                }
                metadataItems.add(item);
            }
            resultSet.close();
            if (!CollectionUtil.isEmpty(metadataItems)) {
                Map<String, List<MetadataItem>> listMap = new LinkedHashMap<String, List<MetadataItem>>();
                for (MetadataItem v : metadataItems) {
                    String key = v.getTableNameColumn() + v.getSchemaColumn();
                    if (!listMap.containsKey(key)) {
                        listMap.put(key, new ArrayList<MetadataItem>());
                    }
                    listMap.get(key).add(v);
                }

                for (Map.Entry<String, List<MetadataItem>> entry : listMap.entrySet()) {
                    List<MetadataItem> v = entry.getValue();
                    Table table = new Table();
                    MetadataItem sqlResultSet = v.get(0);
                    table.setDatabase(sqlResultSet.getDatabaseColumn());
                    table.setSchema(sqlResultSet.getSchemaColumn());
                    table.setName(sqlResultSet.getTableNameColumn());
                    table.setIsView(sqlResultSet.getIsViewColumn());
                    List<Column> columns = new ArrayList<Column>();
                    for (MetadataItem value : v) {
                        Column column = new Column();
                        column.setName(value.getColumnNameColumn());
                        column.setDataType(value.getColumnDataTypeColumn());
                        column.setComment(value.getColumnCommentColumn());
                        columns.add(column);
                    }
                    table.setColumns(columns);
                    tables.add(table);
                }
            }
            sqlDatabase.setSynonyms(exportSynonyms(datasource, database, catalog, exportMetadataModel));
            sqlDatabase.setSequences(exportSequences(datasource, database, catalog, exportMetadataModel));
            sqlDatabase.setTables(tables);
            appendDatabase(exportMetadataModel, datasource, sqlDatabase);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            close(statement);
        }
    }

    protected void appendDatabase(Metadata exportMetadataModel, TSQLDataSource datasource, Database sqlDatabase) {
        Database database = null;

        for (Database item : exportMetadataModel.getDatabases()) {
            if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, item.getName()).equals(
                    new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, sqlDatabase.getName()))) {
                database = item;
                break;
            }
        }

        if (database == null) {
            exportMetadataModel.getDatabases().add(sqlDatabase);
        } else {
            if (sqlDatabase.getTables() != null) {
                database.getTables().addAll(sqlDatabase.getTables());
            }
            if (sqlDatabase.getSynonyms() != null) {
                database.getSynonyms().addAll(sqlDatabase.getSynonyms());
            }
            if (sqlDatabase.getSequences() != null) {
                database.getSequences().addAll(sqlDatabase.getSequences());
            }
        }
    }

    protected void exportQueries(TSQLDataSource datasource, String database, String catalog,
                                 Metadata exportMetadataModel) {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = datasource.getConnection();
            if (enableSetCatalog()) {
                connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
                        .getNormalizeIdentifier());
            }
            String queriesSql = queriesSql(datasource, database, catalog);

            List<Query> queries = new ArrayList<Query>();

            String[] querySqls = queriesSql.trim().split("\\s*;\\s*");
            for (int i = 0; i < querySqls.length; i++) {
                try {
                    statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(querySqls[i]);
                    Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
                    while (resultSet.next()) {
                        Query query = new Query();
                        query.setSourceCode(detectResult(1, resultSet));
                        query.setType(detectResult(2, resultSet));
                        query.setName(detectResult(3, resultSet));
                        query.setGroupName(detectResult(4, resultSet));
                        query.setDatabase(detectResult(5, resultSet));
                        String schema = detectResult(6, resultSet);

                        if (SQLUtil.isEmpty(query.getSourceCode())) {
                            continue;
                        }

                        if (!acceptSchema(datasource, query.getDatabase(), schema)) {
                            continue;
                        }
                        query.setSchema(schema);
                        String key = query.getSchema() + "." + query.getName();
                        if (!queryGroups.containsKey(key)) {
                            queryGroups.put(key, new ArrayList<Query>());
                        }
                        queryGroups.get(key).add(query);
                    }
                    resultSet.close();
                    statement.close();
                    Iterator<String> keyIter = queryGroups.keySet().iterator();
                    while (keyIter.hasNext()) {
                        List<Query> queryGroup = queryGroups.get(keyIter.next());
                        if (needMergeQuery()) {
                            Query query = mergeQuery(datasource, queryGroup);
                            if (acceptDataSourceQuery(datasource, query)) {
                                queries.add(query);
                            }
                        } else {
                            for (Query query : queryGroup) {
                                if (acceptDataSourceQuery(datasource, query)) {
                                    queries.add(query);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        exportMetadataModel.appendErrorMessage(e.getMessage());
                    }
                    e.printStackTrace();
                }
            }

            String queriespSql = queriesPSql(datasource, database, catalog);
            if (!SQLUtil.isEmpty(queriespSql)) {
                String[] querypSqls = queriespSql.trim().split("\\s*;\\s*");
                for (int i = 0; i < querypSqls.length; i++) {
                    try {
                        statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(querypSqls[i]);
                        Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
                        while (resultSet.next()) {
                            Query query = new Query();
                            query.setSourceCode(detectResult(1, resultSet));
                            query.setType(detectResult(2, resultSet));
                            query.setName(detectResult(3, resultSet));
                            query.setGroupName(detectResult(4, resultSet));
                            query.setDatabase(detectResult(5, resultSet));
                            String schema = detectResult(6, resultSet);
                            if (!acceptSchema(datasource, query.getDatabase(), schema)) {
                                continue;
                            }
                            query.setSchema(schema);

                            String key = query.getSchema() + "." + query.getName();
                            if (!queryGroups.containsKey(key)) {
                                queryGroups.put(key, new ArrayList<Query>());
                            }
                            queryGroups.get(key).add(query);
                        }
                        resultSet.close();
                        statement.close();
                        Iterator<String> keyIter = queryGroups.keySet().iterator();
                        while (keyIter.hasNext()) {
                            List<Query> queryGroup = queryGroups.get(keyIter.next());
                            if (needMergeQuery()) {
                                Query query = mergeQuery(datasource, queryGroup);
                                if (acceptDataSourceQuery(datasource, query)) {
                                    queries.add(query);
                                }
                            } else {
                                for (Query query : queryGroup) {
                                    if (acceptDataSourceQuery(datasource, query)) {
                                        queries.add(query);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (e.getMessage() != null) {
                            exportMetadataModel.appendErrorMessage(e.getMessage());
                        }
                        e.printStackTrace();
                    }
                }
            }
            exportMetadataModel.appendQueries(queries);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            close(statement);
        }
    }

    protected boolean acceptDataSourceQuery(TSQLDataSource datasource, Query query) {
        if (datasource.getExtractedStoredProcedures().length == 0 && datasource.getExtractedViews().length == 0)
            return true;
        if (datasource.getExtractedViews().length > 0 && query.getType().toLowerCase().indexOf("view") != -1) {
            for (String view : datasource.getExtractedViews()) {
                String[] segments = view.split("\\.");
                boolean result = acceptDataSourceQuery(datasource, query, segments);
                if (result) {
                    return result;
                }
            }
        }

        if (datasource.getExtractedStoredProcedures().length > 0
                && (query.getType().toLowerCase().indexOf("procedure") != -1
                || query.getType().toLowerCase().indexOf("trigger") != -1
                || query.getType().toLowerCase().indexOf("function") != -1)) {
            for (String procedures : datasource.getExtractedStoredProcedures()) {
                String[] segments = procedures.split("\\.");
                boolean result = acceptDataSourceQuery(datasource, query, segments);
                if (result) {
                    return result;
                }
            }
        }

        return false;
    }

    private boolean acceptDataSourceQuery(TSQLDataSource datasource, Query query, String[] segments) {
        if (datasource instanceof DbSchemaSQLDataSource && (segments.length == 2 || segments.length == 3)) {
            String database;
            String schema;
            String table;
            if (segments.length == 3) {
                database = segments[0].trim();
                schema = segments[1].trim();
                table = segments[2].trim();
            } else {
                database = segments[0].trim();
                schema = segments[0].trim();
                table = segments[1].trim();
            }

            String queryDatabase = query.getDatabase();
            String querySchema = query.getSchema();
            String queryName = query.getName();

            if (matchIdentifier(datasource, database, queryDatabase, ESQLDataObjectType.dotCatalog)
                    && matchIdentifier(datasource, schema, querySchema, ESQLDataObjectType.dotSchema)
                    && matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
                return true;
            }
        } else if (datasource instanceof DatabaseSQLDataSource && (segments.length == 2 || segments.length == 3)) {
            String database;
            String table;
            if (segments.length == 3) {
                database = segments[0].trim();
                table = segments[2].trim();
            } else {
                database = segments[0].trim();
                table = segments[1].trim();
            }

            String queryDatabase = query.getDatabase();
            String queryName = query.getName();

            if (matchIdentifier(datasource, database, queryDatabase, ESQLDataObjectType.dotCatalog)
                    && matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
                return true;
            }
        } else if (datasource instanceof SchemaSQLDataSource && (segments.length == 2 || segments.length == 3)) {
            String schema;
            String table;
            if (segments.length == 3) {
                schema = segments[1];
                table = segments[2];
            } else {
                schema = segments[0];
                table = segments[1];
            }

            String querySchema = query.getDatabase();
            String queryName = query.getName();

            if (matchIdentifier(datasource, schema, querySchema, ESQLDataObjectType.dotSchema)
                    && matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchIdentifier(TSQLDataSource datasource, String identifierPattern, String identifier,
                                    ESQLDataObjectType dataObjectType) {
        if (identifierPattern.endsWith("*")) {
            int index = identifierPattern.indexOf("*");
            if (index == 0) {
                return true;
            }

            String patternIdentifier = new Identifier(datasource.getVendor(), dataObjectType, identifierPattern)
                    .getNormalizeIdentifier();
            String normalizeIdentifier = new Identifier(datasource.getVendor(), dataObjectType, identifier)
                    .getNormalizeIdentifier();

            if (patternIdentifier.substring(0, index)
                    .equals(normalizeIdentifier.length() > index ? normalizeIdentifier.substring(0, index)
                            : normalizeIdentifier)) {
                return true;
            }
        } else {
            if (new Identifier(datasource.getVendor(), dataObjectType, identifierPattern)
                    .equals(new Identifier(datasource.getVendor(), dataObjectType, identifier))) {
                return true;
            }
        }

        return false;
    }

    protected boolean needMergeQuery() {
        return false;
    }

    protected Query mergeQuery(TSQLDataSource datasource, List<Query> queryGroup) {
        if (queryGroup == null || queryGroup.isEmpty())
            return null;
        Query query = queryGroup.get(0);
        for (int i = 1; i < queryGroup.size(); i++) {
            query.setSourceCode(query.getSourceCode() + queryGroup.get(i).getSourceCode());
        }
        return query;
    }

    protected void exportDbLinks(TSQLDataSource datasource, String database, String catalog,
                                 Metadata exportMetadataModel) {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = datasource.getConnection();
            if (enableSetCatalog()) {
                connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
                        .getNormalizeIdentifier());
            }
            String dbLinksSql = dbLinksSql(datasource, database);
            statement = connection.createStatement();
            List<DBLink> sqldbLinks = new ArrayList<DBLink>();
            if (!SQLUtil.isEmpty(dbLinksSql)) {
                ResultSet resultSet = statement.executeQuery(dbLinksSql);
                while (resultSet.next()) {
                    DBLink dbLink = new DBLink();
                    dbLink.setOwner(detectResult(1, resultSet));
                    dbLink.setName(detectResult(2, resultSet));
                    dbLink.setUserName(detectResult(3, resultSet));
                    dbLink.setHost(detectResult(4, resultSet));
                    sqldbLinks.add(dbLink);
                }
                resultSet.close();
            }
            exportMetadataModel.appendDbLinks(sqldbLinks);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            close(statement);
        }
    }

    protected List<Sequence> exportSequences(TSQLDataSource datasource, String database, String catalog,
                                             Metadata exportMetadataModel) {
        return null;
    }

    protected List<Synonym> exportSynonyms(TSQLDataSource datasource, String database, String catalog,
                                           Metadata exportMetadataModel) {
        Statement statement = null;
        List<Synonym> synonymModelItems = new ArrayList<Synonym>();
        Connection connection = null;

        try {
            connection = datasource.getConnection();
            if (enableSetCatalog()) {
                connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
                        .getNormalizeIdentifier());
            }
            statement = connection.createStatement();
            String synonymsSql = synonymsSql(datasource, database, catalog);
            if (!SQLUtil.isEmpty(synonymsSql)) {
                ResultSet resultSet = statement.executeQuery(synonymsSql);
                while (resultSet.next()) {
                    Synonym item = new Synonym();
                    item.setDatabase(detectResult(1, resultSet));
                    String schema = detectResult(2, resultSet);
                    if (!acceptSchema(datasource, item.getDatabase(), schema)) {
                        continue;
                    }
                    item.setSchema(schema);
                    item.setName(detectResult(3, resultSet));
                    item.setSourceSchema(detectResult(4, resultSet));
                    item.setSourceName(detectResult(5, resultSet));
                    item.setSourceDbLinkName(detectResult(6, resultSet));
                    synonymModelItems.add(item);
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            close(statement);
        }
        return synonymModelItems;
    }

    protected abstract String dbLinksSql(TSQLDataSource datasource, String database) throws Exception;

    protected abstract String synonymsSql(TSQLDataSource datasource, String database, String catalog) throws Exception;

    protected abstract String databasesSql(TSQLDataSource datasource) throws Exception;

    protected abstract String tablesSql(TSQLDataSource datasource, String database, String catalog) throws Exception;

    protected abstract String queriesSql(TSQLDataSource datasource, String database, String catalog) throws Exception;

    protected abstract String queriesPSql(TSQLDataSource datasource, String database, String catalog) throws Exception;

    protected MetadataItem fetchMetadataItem(ResultSet resultSet, Metadata exportMetadataModel) {
        MetadataItem result = new MetadataItem();
        try {
            result.setDatabaseColumn(detectResult(1, resultSet));
            result.setSchemaColumn(detectResult(2, resultSet));
            result.setTableNameColumn(detectResult(3, resultSet));
            result.setIsViewColumn(detectResult(4, resultSet));
            result.setColumnNameColumn(detectResult(5, resultSet));
            result.setColumnDataTypeColumn(detectResult(6, resultSet));
            result.setColumnCommentColumn(detectResult(7, resultSet));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                exportMetadataModel.appendErrorMessage(e.getMessage());
            }
            e.printStackTrace();
        }
        return result;
    }

    protected String detectResult(int index, ResultSet resultSet) throws SQLException {
        String var = resultSet.getString(index);
        if (!SQLUtil.isEmpty(var)) {
            return var;
        }
        return "";
    }

    protected String detectResult(String columnLabel, ResultSet resultSet) throws SQLException {
        String var = resultSet.getString(columnLabel);
        if (!SQLUtil.isEmpty(var)) {
            return var.trim();
        }
        return "";
    }

    protected void close(Object colseable) {
        try {
            if (colseable instanceof Closeable) {
                ((Closeable) colseable).close();
            }
            if (colseable instanceof Statement) {
                ((Statement) colseable).close();
            }
            if (colseable instanceof ResultSet) {
                ((ResultSet) colseable).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean acceptSchema(TSQLDataSource datasource, String databaseName, String schemaName) {
        if (datasource instanceof DbSchemaSQLDataSource) {
            String databaseSchemaName = databaseName + "." + schemaName;
            String patternSchemaName = "*." + schemaName;
            DbSchemaSQLDataSource ds = (DbSchemaSQLDataSource) datasource;
            if (ds.isSystemDbsSchemas(databaseSchemaName)) {
                return false;
            }
            if (!CollectionUtil.isEmpty(ds.getExtractedDbsSchemas())) {
                for (String databaseSchema : ds.getExtractedDbsSchemas()) {
                    if (databaseSchema.indexOf(".") != -1) {
                        if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema)
                                .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchemaName))) {
                            for (String databaseSchema1 : ds.getExcludedDbsSchemas()) {
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, databaseSchemaName))) {
                                    return false;
                                }
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, patternSchemaName))) {
                                    return false;
                                }
                            }
                            return true;
                        } else if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema)
                                .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        patternSchemaName))) {
                            for (String databaseSchema1 : ds.getExcludedDbsSchemas()) {
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, databaseSchemaName))) {
                                    return false;
                                }
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, patternSchemaName))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    } else {
                        String database = databaseSchemaName;
                        if (database.indexOf(".") != -1) {
                            database = database.split("\\.")[0];
                        }
                        if ((new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema)
                                .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, database)))
                                || "*".equals(databaseSchema)) {
                            for (String databaseSchema1 : ds.getExcludedDbsSchemas()) {
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, databaseSchemaName))) {
                                    return false;
                                }
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
                                        databaseSchema1)
                                        .equals(new Identifier(datasource.getVendor(),
                                                ESQLDataObjectType.dotSchema, patternSchemaName))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                return false;
            } else if (!CollectionUtil.isEmpty(ds.getExcludedDbsSchemas())) {
                for (String databaseSchema : ds.getExcludedDbsSchemas()) {
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchemaName))) {
                        return false;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, patternSchemaName))) {
                        return false;
                    }
                }
                for (String databaseSchema : ds.getExcludedDbsSchemas()) {
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseSchema).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseName))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        } else if (datasource instanceof SchemaSQLDataSource) {
            SchemaSQLDataSource ds = (SchemaSQLDataSource) datasource;
            if (ds.isSystemSchema(schemaName)) {
                return false;
            }
            if (!CollectionUtil.isEmpty(ds.getExtractedSchemas())) {
                for (String schema : ds.getExtractedSchemas()) {
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schema)
                            .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName))) {
                        return true;
                    }
                }
                return false;
            } else if (!CollectionUtil.isEmpty(ds.getExcludedSchemas())) {
                for (String schema : ds.getExcludedSchemas()) {
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schema)
                            .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    protected boolean acceptDatabase(TSQLDataSource datasource, String databaseName) {
        if (datasource instanceof DbSchemaSQLDataSource) {
            DbSchemaSQLDataSource ds = (DbSchemaSQLDataSource) datasource;
            if (ds.isSystemDbsSchemas(databaseName)) {
                return false;
            }
            if (!CollectionUtil.isEmpty(ds.getExtractedDbsSchemas())) {
                for (String databaseSchema : ds.getExtractedDbsSchemas()) {
                    String database = databaseSchema;
                    if (database.indexOf(".") != -1) {
                        database = database.split("\\.")[0];
                    }
                    if ("*".equals(database)) {
                        return true;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
                        if (!CollectionUtil.isEmpty(ds.getExcludedDbsSchemas())) {
                            for (String databaseSchema1 : ds.getExcludedDbsSchemas()) {
                                String database1 = databaseSchema1;
                                if ("*".equals(database1)) {
                                    return false;
                                }
                                if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database1)
                                        .equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog,
                                                databaseName))) {
                                    return false;
                                }
                            }
                        }

                        return true;
                    }
                }
                return false;
            } else if (!CollectionUtil.isEmpty(ds.getExcludedDbsSchemas())) {
                for (String databaseSchema : ds.getExcludedDbsSchemas()) {
                    String database = databaseSchema;
                    if ("*".equals(database)) {
                        return false;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        } else if (datasource instanceof SchemaSQLDataSource) {
            SchemaSQLDataSource ds = (SchemaSQLDataSource) datasource;
            if (ds.isSystemSchema(databaseName)) {
                return false;
            }
            if (!CollectionUtil.isEmpty(ds.getExtractedSchemas())) {
                for (String schema : ds.getExtractedSchemas()) {
                    if ("*".equals(schema)) {
                        return true;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schema).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseName))) {
                        return true;
                    }
                }
                return false;
            } else if (!CollectionUtil.isEmpty(ds.getExcludedSchemas())) {
                for (String schema : ds.getExcludedSchemas()) {
                    if ("*".equals(schema)) {
                        return false;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schema).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, databaseName))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        } else if (datasource instanceof DatabaseSQLDataSource) {
            DatabaseSQLDataSource ds = (DatabaseSQLDataSource) datasource;
            if (ds.isSystemDatabase(databaseName)) {
                return false;
            }
            if (!CollectionUtil.isEmpty(ds.getExtractedDatabases())) {
                for (String database : ds.getExtractedDatabases()) {
                    if ("*".equals(database)) {
                        return true;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
                        return true;
                    }
                }
                return false;
            } else if (!CollectionUtil.isEmpty(ds.getExcludedDatabases())) {
                for (String database : ds.getExcludedDatabases()) {
                    if ("*".equals(database)) {
                        return false;
                    }
                    if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database).equals(
                            new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    protected boolean enableSetCatalog() {
        return true;
    }
}