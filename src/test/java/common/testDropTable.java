package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import junit.framework.TestCase;

public class testDropTable extends TestCase {

     public void testMySQLDropIndex(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "DROP TABLE jr_story;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdroptable);
        TDropTableSqlStatement dropTable = (TDropTableSqlStatement) sqlparser.sqlstatements.get(0);
         assertTrue(dropTable.getTableName().toString().equalsIgnoreCase("jr_story"));
     }

}