package demos.sqlenv.metadata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.stmtformatter.SqlFormatter;
import gudusoft.gsqlparser.sqlenv.TSQLDataSource;
import gudusoft.gsqlparser.sqlenv.constant.DDLOperator;
import gudusoft.gsqlparser.sqlenv.constant.SystemConstant;

import java.util.*;

public class DDL {

    private EDbVendor vendor;
    private String hostName;
    private String exportId;
    private String dialect;

    public DDL(TSQLDataSource datasource) {
        this.hostName = datasource.getHostName();
        this.dialect = datasource.getDbCategory();
        this.exportId = UUID.randomUUID().toString();
        this.vendor = datasource.getVendor();
    }

    private final Map<String, List<Map<DDLOperator, String>>> ddls = new LinkedHashMap<String, List<Map<DDLOperator, String>>>();

    public void addDDLStmt(String database, Map<DDLOperator, String> stmts) {
        if (!ddls.containsKey(database)) {
            ddls.put(database, new ArrayList<Map<DDLOperator, String>>());
        }
        if (stmts != null) {
            ddls.get(database).add(stmts);
        }
    }

    public String[] getDDLDatabases() {
        return ddls.keySet().toArray(new String[0]);
    }

    public List<Map<DDLOperator, String>> getDDLStmts(String database) {
        return ddls.get(database);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("-- Created By: ").append(SystemConstant.name + " " + SystemConstant.version).append("\n");
        builder.append("-- Dialect: ").append(dialect).append("\n");
        builder.append("-- Export Id: ").append(exportId).append("\n");
        builder.append("-- Physical Instance: ").append(hostName).append("\n\n");

        GFmtOpt option = GFmtOptFactory.newInstance();
        option.parametersStyle = TAlignStyle.AsStacked;
        option.createtableListitemInNewLine = true;
        option.beStyleCreatetableRightBEOnNewline = true;
        TGSqlParser sqlparser = new TGSqlParser(vendor);
        SqlFormatter formatter = new SqlFormatter();

        Iterator<String> iter = ddls.keySet().iterator();
        while (iter.hasNext()) {
            String database = iter.next();
            if (ddls.size() > 1) {
                builder.append("USE ").append("[" + database.toUpperCase() + "];").append("\n\n");
            }
            for (Map<DDLOperator, String> ddl : ddls.get(database)) {
                if (ddl.containsKey(DDLOperator.drop)) {
                    String sql = ddl.get(DDLOperator.drop).trim();
                    sqlparser.sqltext = sql;
                    if (sqlparser.parse() == 0) {
                        builder.append(formatter.format(sqlparser, option)).append("\n");
                    } else {
                        builder.append(sql).append("\n");
                    }
                }
                if (ddl.containsKey(DDLOperator.create)) {
                    String sql = ddl.get(DDLOperator.create).trim();
                    sqlparser.sqltext = sql;
                    if (sqlparser.parse() == 0) {
                        builder.append(formatter.format(sqlparser, option)).append("\n");
                    } else {
                        builder.append(sql).append("\n");
                    }
                }
                builder.append("\n");
            }
        }

        return builder.toString().replace((char) 160, (char) 32);
    }

}
