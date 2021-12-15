package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testObjectName extends TestCase {

    public void testInformixPrefixDatabase(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "SELECT * FROM sysadmin:a.b";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
       // System.out.println(table.getPrefixDatabase());
        assertTrue(table.getPrefixDatabase().equalsIgnoreCase("sysadmin"));
        assertTrue(table.getPrefixSchema().equalsIgnoreCase("a"));

    }

    public void testPrefixSchema(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = " create view A as select * from a";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = ((TCreateViewSqlStatement)sqlparser.sqlstatements.get(0)).getSubquery();
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getPrefixDatabase() == "");
        assertTrue(table.getPrefixSchema() == "");

    }

    public void testQualifiedName4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "SELECT dv.sc.orders.order_id oid, dv.sc.orders.customer_id cid, dv.sc.orders.order_total ottl,\n" +
                "dv.sc.orders.sales_rep_id sid\n" +
                "FROM dv.sc.orders";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TObjectName columnName = select.getResultColumnList().getResultColumn(0).getExpr().getObjectOperand();
        assertTrue(columnName.getColumnNameOnly().equalsIgnoreCase("order_id"));
        assertTrue(columnName.getTableString().equalsIgnoreCase("orders"));
        assertTrue(columnName.getSchemaString().equalsIgnoreCase("sc"));
        assertTrue(columnName.getDatabaseString().equalsIgnoreCase("dv"));
    }

    public void testQualifiedTableName(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "SELECT DISTINCT\n" +
                " \"LocalProcedureDate\",\n" +
                " SUM(\"DurationInMinutes\") AS \"DurationMin\"\n" +
                "FROM \"_SYS_BIC\".\"IntuitiveSurgical.SFDC.Reporting/ProcedureSummary\" \n";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("\"_SYS_BIC\""));
        assertTrue(table.getTableName().getTableString().equalsIgnoreCase("\"IntuitiveSurgical.SFDC.Reporting/ProcedureSummary\""));

    }

    public void testQualifiedTableNameBigQuery(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "SELECT DISTINCT\n" +
                " \"LocalProcedureDate\",\n" +
                " SUM(\"DurationInMinutes\") AS \"DurationMin\"\n" +
                "FROM `schemaName.tableName`";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        //System.out.println(table.getTableName().getSchemaString());
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("`schemaName`"));
        assertTrue(table.getTableName().getTableString().equalsIgnoreCase("`tableName`"));
    }

    public void testQualifiedTableNameDb2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SELECT DISTINCT\n" +
                " \"LocalProcedureDate\",\n" +
                " SUM(\"DurationInMinutes\") AS \"DurationMin\"\n" +
                "FROM \"context.evDB100\".\"context.jpDBAGTP01\"";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        //System.out.println(table.getTableName().getSchemaString());
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("\"context.evDB100\""));
        assertTrue(table.getTableName().getTableString().equalsIgnoreCase("\"context.jpDBAGTP01\""));
    }

}
