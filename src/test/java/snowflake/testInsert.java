package snowflake;



import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EInsertSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TInsertCondition;
import gudusoft.gsqlparser.nodes.TInsertIntoValue;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testInsert extends TestCase {
    public void testUnconditionalInsert(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "insert overwrite all\n" +
                "  into t1\n" +
                "  into t2 (c1, c2, c3) values (n2, n1, default)\n" +
                "  into t3 (c1, c2, c3)\n" +
                "  into t4 values (n3, n2, n1)\n" +
                "select n1, n2, n3 from src;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertStmt = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertStmt.getInsertSource() == EInsertSource.subquery);
        assertTrue(insertStmt.getSubQuery().getTables().getTable(0).toString().equalsIgnoreCase("src"));
        assertTrue(insertStmt.getInsertIntoValues().size() == 4);
        TInsertIntoValue intoValue = insertStmt.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t1"));
        intoValue = insertStmt.getInsertIntoValues().getElement(1);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t2"));
        assertTrue(intoValue.getColumnList().size() == 3);
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        assertTrue(intoValue.getTargetList().size() == 1);
        TMultiTarget multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n2"));
        intoValue = insertStmt.getInsertIntoValues().getElement(3);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t4"));
        assertTrue(intoValue.getTargetList().size() == 1);
        multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n3"));
    }

    public void testConditionalInsert(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "insert all\n" +
                "  when n1 > 100 then\n" +
                "    into tab1\n" +
                "  when n1 > 10 then\n" +
                "    into tab2 (c1, c2, c3)\n" +
                "    into tab3 (c1, c2, c3) values (n2, n1, default) \n" +
                "  else\n" +
                "    into tab4 values (n3, n2, n1)\n" +
                "select n1,n2,n3 from srcTab;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertStmt = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertStmt.getInsertSource() == EInsertSource.subquery);
        assertTrue(insertStmt.getSubQuery().getTables().getTable(0).toString().equalsIgnoreCase("srcTab"));
        assertTrue(insertStmt.getInsertConditions().size() == 2);
        TInsertCondition insertCondition = insertStmt.getInsertConditions().getElement(0);
        assertTrue(insertCondition.getCondition().toString().equalsIgnoreCase("n1 > 100"));
        TInsertIntoValue intoValue = insertCondition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab1"));

        insertCondition = insertStmt.getInsertConditions().getElement(1);
        assertTrue(insertCondition.getCondition().toString().equalsIgnoreCase("n1 > 10"));
        assertTrue(insertCondition.getInsertIntoValues().size() == 2);
        intoValue = insertCondition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab2"));
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        intoValue = insertCondition.getInsertIntoValues().getElement(1);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab3"));
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        TMultiTarget multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n2"));

        assertTrue(insertStmt.getElseIntoValues().size() == 1);
        intoValue = insertStmt.getElseIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab4"));
        multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(2).toString().equalsIgnoreCase("n1"));

    }

}
