package common;
/*
 * Date: 12-5-9
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testGetTables extends TestCase {

    public void testAlterTable(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
       sqlparser.sqltext = "ALTER TABLE MYTAB DROP COLUMN D2";
       assertTrue(sqlparser.parse() == 0);
       assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
       assertTrue(sqlparser.sqlstatements.get(0).tables.size() == 1 );
        assertTrue(sqlparser.sqlstatements.get(0).tables.getTable(0).toString().equalsIgnoreCase("MYTAB"));
    }

    public void testTruncateTable(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
       sqlparser.sqltext = "truncate table mytab;";
       assertTrue(sqlparser.parse() == 0);
       assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstTruncate);
       assertTrue(sqlparser.sqlstatements.get(0).tables.size() == 1 );
        assertTrue(sqlparser.sqlstatements.get(0).tables.getTable(0).toString().equalsIgnoreCase("MYTAB"));
    }

    public void testDropTable(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
       sqlparser.sqltext = "Drop table MYTAB;";
       assertTrue(sqlparser.parse() == 0);
       assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdroptable);
       assertTrue(sqlparser.sqlstatements.get(0).tables.size() == 1 );
        assertTrue(sqlparser.sqlstatements.get(0).tables.getTable(0).toString().equalsIgnoreCase("MYTAB"));
    }

}
