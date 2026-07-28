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
            // No arguments is someone asking what this is, and exits 0 like
            // every other demo here. Some arguments but not enough is a
            // malformed invocation -- "/metadata" as the final token lands
            // here -- and must not report success to a caller reading $?.
            System.exit(args.length == 0 ? 0 : 1);
        }
        List<String> argList = Arrays.asList(args);

        // Every required option is resolved before anything is indexed. The
        // older shape here printed "the /x command be required." and carried on,
        // so a missing flag left indexOf() at -1, and -1 + 1 read args[0]: the
        // run failed later blaming whatever happened to sit in that slot. A
        // flag given as the last argument threw ArrayIndexOutOfBounds instead.
        String db = requiredOption(args, argList, "/db");
        String fileName = requiredOption(args, argList, "/f");
        String vendorName = requiredOption(args, argList, "/t");
        String metadataPath = requiredOption(args, argList, "/metadata");
        String schema = optionalOption(args, argList, "/schema");

        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("file not exists: " + fileName);
            System.exit(1);
        }

        EDbVendor vendor = TGSqlParser.getDBVendorByName(vendorName);
        TGSqlParser sqlparser = new TGSqlParser(vendor);
        sqlparser.sqlfilename = fileName;
        sqlparser.parse();

        String metadataJson = readMetadata(metadataPath);
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
            // Exit non-zero. /metadata is required, so a file that is
            // unparseable or structurally wrong (malformed JSON, or valid
            // JSON with no "databases" array) leaves the demo with nothing
            // to inspect against. Returning normally here reported success
            // to any caller checking the exit status, while missing and
            // unreadable files already exited 1 from readMetadata.
            System.err.println("Reading metadata from " + metadataPath
                    + " failed: " + e);
            System.err.println("Expected a JSON object with a \"databases\" array; "
                    + "see samples/columninspect/metadata.json.");
            System.exit(1);
        }
    }

    /**
     * Value of a required {@code /flag}, or exit 1 with a usable message.
     * Rejects both a missing flag and a flag given without a following value.
     */
    private static String requiredOption(String[] args, List<String> argList, String flag) {
        int i = argList.indexOf(flag);
        if (i == -1) {
            System.err.println(flag + " is required.");
            System.exit(1);
        }
        if (i + 1 >= args.length) {
            System.err.println(flag + " requires a value.");
            System.exit(1);
        }
        return args[i + 1];
    }

    /** Value of an optional {@code /flag}, or null. A flag with no value is still an error. */
    private static String optionalOption(String[] args, List<String> argList, String flag) {
        int i = argList.indexOf(flag);
        if (i == -1) {
            return null;
        }
        if (i + 1 >= args.length) {
            System.err.println(flag + " requires a value.");
            System.exit(1);
        }
        return args[i + 1];
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
