package bigquery;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testIdentifier extends TestCase {

    public void testMultipleParts(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "select * from orbital-eon-20511-3.QLI_AUTOMATION_DO_NOT_TOUCH.books where book_id > 0 ;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectSqlStatement.getTables().getTable(0);
        TObjectName tableName = table.getTableName();
        assertTrue(tableName.getDatabaseToken().toString().equalsIgnoreCase("orbital-eon-20511-3"));
        assertTrue(tableName.getSchemaToken().toString().equalsIgnoreCase("QLI_AUTOMATION_DO_NOT_TOUCH"));
        assertTrue(tableName.getTableToken().toString().equalsIgnoreCase("books"));

    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus-dev`.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus-dev`"));
//        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
//            TSourceToken st = sqlparser.sourcetokenlist.get(i);
//            System.out.println(st.toString());
//        }
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus-f012`.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus-f012`"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus0-012`.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus0-012`"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus0-012f`.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus0-012f`"));
//        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
//            TSourceToken st = sqlparser.sourcetokenlist.get(i);
//            System.out.println(st.toString());
//        }
    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus0-012`-.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus0-012`"));
    }

    public void test6(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "INSERT INTO `solidatus0-012`---.JDBC_test.Customers";
        sqlparser.tokenizeSqltext();
        TSourceToken st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("`solidatus0-012`"));
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
        sqlparser.sqltext = "INSERT INTO `solidatus-dev`.JDBC_test.Customers\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`\n" +
                "    union all\n" +
                "SELECT\n" +
                "    *\n" +
                "    from\n" +
                "    `JDBC_test.Customers`";
        System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);


        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = insertSqlStatement.getTargetTable();
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("`solidatus-dev`"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("JDBC_test"));
        assertTrue(table.getTableName().getObjectString().equalsIgnoreCase("Customers"));
    }

    public void test9(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "SELECT f1, f1-f1-302907.gdddc FROM absolute-runner-302907.gudu_sqlflow.xxx1";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectSqlStatement.getTables().getTable(0);
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("absolute-runner-302907"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("gudu_sqlflow"));
        assertTrue(table.getTableName().getObjectString().equalsIgnoreCase("xxx1"));

        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(1);
        assertTrue(resultColumn.getColumnAlias().equalsIgnoreCase("gdddc"));
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.arithmetic_minus_t);
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("f1-f1"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("302907."));
    }


}
