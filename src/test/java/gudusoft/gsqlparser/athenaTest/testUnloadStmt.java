package gudusoft.gsqlparser.athenaTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TUnloadStmt;
import junit.framework.TestCase;

public class testUnloadStmt extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvathena);

        sqlparser.sqltext = "UNLOAD (SELECT * FROM old_table) \n" +
                "TO 's3://DOC-EXAMPLE-BUCKET/unload_test_1/' \n" +
                "WITH (format = 'JSON');";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstunload);
        TUnloadStmt unloadStmt = (TUnloadStmt)sqlparser.sqlstatements.get(0);
        assertTrue(unloadStmt.getSelectSqlStatement().getTables().getTable(0).getTableName().toString().equalsIgnoreCase("old_table"));

    }
}
