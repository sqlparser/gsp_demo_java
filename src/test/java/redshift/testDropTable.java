package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import junit.framework.TestCase;


public class testDropTable extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "drop table feedback, buyers cascade;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdroptable);
        TDropTableSqlStatement dropTable = (TDropTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(dropTable.getTableNameList().size() == 2);
        assertTrue(dropTable.getTableNameList().getObjectName(0).toString().equalsIgnoreCase("feedback"));
        assertTrue(dropTable.getTableNameList().getObjectName(1).toString().equalsIgnoreCase("buyers"));
    }
}