package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import junit.framework.TestCase;

public class testDelete extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "delete from category\n" +
                "using event\n" +
                "where event.catid=category.catid and category.catid=9;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdelete);
        TDeleteSqlStatement delete = (TDeleteSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(delete.getTargetTable().toString().equalsIgnoreCase("category"));
        assertTrue(delete.getReferenceJoins().getJoin(0).getTable().toString().equalsIgnoreCase("event"));

    }
}