package bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testIdentifier extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus-dev.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus-dev"));
//        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
//            TSourceToken st = sqlparser.sourcetokenlist.get(i);
//            System.out.println(st.toString());
//        }
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus-f012.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus-f012"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus0-012.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus0-012"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus0-012f.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus0"));
//        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
//            TSourceToken st = sqlparser.sourcetokenlist.get(i);
//            System.out.println(st.toString());
//        }
    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus0-012-.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus0-012"));
    }

    public void test6(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus0-012---.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("solidatus0-012"));
    }

    public void test7(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus-dev`.`JDBC_test`.`Orders`\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`\n" +
                "    union all\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = insertSqlStatement.getTargetTable();
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("`solidatus-dev`"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("`JDBC_test`"));
        assertTrue(table.getTableName().getObjectString().equalsIgnoreCase("`Orders`"));
    }

    public void test8(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO solidatus-dev.JDBC_test.Customers\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`\n" +
                "    union all\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = insertSqlStatement.getTargetTable();
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("solidatus-dev"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("JDBC_test"));
        assertTrue(table.getTableName().getObjectString().equalsIgnoreCase("Customers"));
    }


}
