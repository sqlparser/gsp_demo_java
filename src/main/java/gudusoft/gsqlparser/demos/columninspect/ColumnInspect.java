package gudusoft.gsqlparser.demos.columninspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSyntaxError;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.sqlenv.*;

import java.io.File;
import java.util.*;

public class ColumnInspect {

    public static void main(String[] args) {
        if (args.length < 8) {
            System.out.println("Usage: java ColumnInspect [/t] [dbname] [/f] [sql file path] [/metadata] [metadata json path] [/db] [database] [/schema] [schema]");
            System.out.println("/t: required, specify the database type.");
            System.out.println("/f: required, specify the SQL script file path to analyze.");
            System.out.println("/metadata: required, specify a JSON file describing the database metadata.");
            System.out.println("/db: required, specify the database to which the script to analyze belongs.");
            System.out.println("/schema: optional, specify the schema to which the script to analyze belongs.");
            System.out.println();
            System.out.println("The metadata JSON is the format TSQLEnv reads: a top-level \"databases\"");
            System.out.println("array, each entry with a \"name\" and a \"tables\" array, each table with a");
            System.out.println("\"name\", a \"schema\" and a \"columns\" array of objects carrying \"name\".");
            System.out.println("Earlier revisions of this demo read the same JSON from a live server over");
            System.out.println("JDBC; it now takes the file directly, so it needs no database.");
            return;
        }
        List<String> argList = Arrays.asList(args);
        if (!argList.contains("/t")) {
            System.err.println("the /t command be required.");
        }
        if (!argList.contains("/f")) {
            System.err.println("the /f command be required.");
        }
        if (!argList.contains("/metadata")) {
            System.err.println("the /metadata command be required.");
        }
        if (!argList.contains("/db")) {
            System.err.println("the /db command be required.");
        }
        String db = args[argList.indexOf("/db") + 1];
        String schema = null;
        if (argList.contains("/schema")) {
            schema = args[argList.indexOf("/schema") + 1];
        }

        String fileName = args[argList.indexOf("/f") + 1];
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("file not exists: " + fileName);
            return;
        }

        EDbVendor vendor = TGSqlParser.getDBVendorByName(args[argList.indexOf("/t") + 1]);
        TGSqlParser sqlparser = new TGSqlParser(vendor);
        sqlparser.sqlfilename = fileName;
        sqlparser.parse();

        String metadataJson = readMetadata(args[argList.indexOf("/metadata") + 1]);
        if (metadataJson != null) {
            try {
                JSONObject metadata = JSONObject.parseObject(metadataJson);
                JSONArray databases = metadata.getJSONArray("databases");
                Map<String, Map<String, JSONObject>> map = new LinkedHashMap<>();
                for (Object database : databases) {
                    JSONObject databaseJson = (JSONObject) database;
                    String databaseName = databaseJson.getString("name");
                    if (TSQLEnv.compareIdentifier(vendor, ESQLDataObjectType.dotCatalog, db, databaseName)) {
                        JSONArray ts = databaseJson.getJSONArray("tables");
                        for (Object t : ts) {
                            JSONObject tb = (JSONObject) t;
                            if (schema != null) {
                                String sch = tb.getString("schema");
                                if (TSQLEnv.compareIdentifier(vendor, ESQLDataObjectType.dotSchema, schema, sch)) {
                                    String name = tb.getString("name");
                                    JSONArray columns = tb.getJSONArray("columns");
                                    Map<String, JSONObject> p = new HashMap<>();
                                    for (Object column : columns) {
                                        JSONObject cl = (JSONObject) column;
                                        p.put(cl.getString("name"), cl);
                                    }
                                    map.put(name, p);
                                }
                            } else {
                                String name = tb.getString("name");
                                JSONArray columns = tb.getJSONArray("columns");
                                Map<String, JSONObject> p = new HashMap<>();
                                for (Object column : columns) {
                                    JSONObject cl = (JSONObject) column;
                                    p.put(cl.getString("name"), cl);
                                }
                                map.put(name, p);
                            }
                        }
                    }
                }
                for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
                    columnInspectByTable(vendor, sqlparser.sqlstatements.get(i), map);
                }
            } catch (Exception e) {
                System.err.println("Get datasource metadata failed. " + e.getMessage());
            }
        }
    }

    private static void columnInspectByTable(EDbVendor vendor, TCustomSqlStatement stmt, Map<String, Map<String, JSONObject>> map) {
        System.out.println("-------------------------------------------");
        System.out.println("SQL : " + stmt.toString());
        ArrayList<TSyntaxError> syntaxErrors = stmt.getSyntaxErrors();
        if (null != syntaxErrors && syntaxErrors.size() > 0) {
            for (TSyntaxError syntaxError : syntaxErrors) {
                System.out.println(syntaxError.hint);
            }
            return;
        }
        TTableList tTables = stmt.tables;
        for (int i = 0; i < tTables.size(); i++) {
            TTable table = tTables.getTable(i);
            String tableName = table.getName();
            System.out.println();
            System.out.println(tableName + "：");

            Map<String, JSONObject> tbs = null;
            for (Map.Entry<String, Map<String, JSONObject>> entry : map.entrySet()) {
                String key = entry.getKey();
                if (TSQLEnv.compareIdentifier(vendor, ESQLDataObjectType.dotTable, tableName, key)) {
                    tbs = entry.getValue();
                }
            }

            TResultColumnList columns = stmt.getResultColumnList();
            if (null != tbs) {
                for (int j = 0; j < columns.size(); j++) {
                    String resultColumn = columns.getResultColumn(j).toString();
                    if ("*".equals(resultColumn)) {
                        tbs.forEach((k, v) -> System.out.println("column name:" + v.get("name").toString() + ", data type: " + v.get("dataType").toString()));
                    } else {
                        TExpression expr = columns.getResultColumn(j).getExpr();
                        if (expr != null) {
                            if (expr.getOperatorToken() != null) {
                                resultColumn = columns.getResultColumn(j).getStartToken().toString();
                            }
                        }
                        JSONObject jsonObject = null;
                        for (Map.Entry<String, JSONObject> entry : tbs.entrySet()) {
                            String key = entry.getKey();
                            if (TSQLEnv.compareIdentifier(vendor, ESQLDataObjectType.dotColumn, resultColumn, key)) {
                                jsonObject = entry.getValue();
                            }
                        }
                        if (null != jsonObject) {
                            String columnName = columns.getResultColumn(j).getAliasClause() != null ? columns.getResultColumn(j).getAliasClause().toString() :
                                    resultColumn;
                            System.out.println("column name:" + columnName + ", data type: " + jsonObject.get("dataType").toString());
                        } else {
                            System.out.println("column name:" + resultColumn);
                        }
                    }
                }
            } else {
                for (int j = 0; j < columns.size(); j++) {
                    String resultColumn = columns.getResultColumn(j).toString();
                    if ("*".equals(resultColumn)) {
                        System.out.println("column name: *");
                    } else {
                        TExpression expr = columns.getResultColumn(j).getExpr();
                        if (expr != null) {
                            if (expr.getOperatorToken() != null) {
                                resultColumn = columns.getResultColumn(j).getStartToken().toString();
                            }
                        }
                        System.out.println("column name:" + resultColumn);
                    }
                }
            }
        }
    }

    /**
     * Read the metadata JSON this demo inspects against.
     *
     * <p>This used to be {@code createSQLDataSource(...).exportJSON()}, which
     * opened a JDBC connection through {@code gudusoft.dbadapter.TSQLDataSource}
     * from the vendored {@code lib/sqlflow-exporter.jar}. Every line below the
     * call already worked off the returned JSON string, so reading that same
     * JSON from a file keeps the demo whole and drops both the driver and the
     * vendored jar. Export it from a live server with the standalone SQLFlow
     * metadata exporter if you want real data.
     */
    private static String readMetadata(String path) {
        File metadataFile = new File(path);
        if (!metadataFile.isFile()) {
            System.err.println("metadata file not exists: " + path);
            System.exit(1);
        }
        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(metadataFile.toPath());
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            System.err.println("cannot read metadata file " + path + ": " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

}
