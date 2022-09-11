package teradata;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.teradata.TAlterZoneStmt;
import gudusoft.gsqlparser.stmt.teradata.TCreateZoneStmt;
import junit.framework.TestCase;

public class testAlterZone extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER ZONE zone_name DROP ROOT ;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradataalterzone);
        TAlterZoneStmt alterZoneStmt = (TAlterZoneStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterZoneStmt.getZoneName().toString().equalsIgnoreCase("zone_name"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE ZONE zone_name ROOT root_name ;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatezone);
        TCreateZoneStmt alterZoneStmt = (TCreateZoneStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterZoneStmt.getZoneName().toString().equalsIgnoreCase("zone_name"));
    }

}
