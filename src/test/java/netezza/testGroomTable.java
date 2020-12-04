package netezza;
/*
 * Date: 13-11-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.netezza.EModeChoice;
import gudusoft.gsqlparser.nodes.netezza.TModeChoice;
import gudusoft.gsqlparser.stmt.netezza.TNetezzaGroomTable;
import junit.framework.TestCase;

public class testGroomTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GROOM TABLE table_name VERSIONS";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGroomTable groomTable = (TNetezzaGroomTable)sqlparser.sqlstatements.get(0);
        assertTrue(groomTable.getTableName().toString().equalsIgnoreCase("table_name"));
        TModeChoice modeChoice = groomTable.getModeChoice();
        assertTrue(modeChoice.getModeChoice() == EModeChoice.versions);

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GROOM TABLE X;";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGroomTable groomTable = (TNetezzaGroomTable)sqlparser.sqlstatements.get(0);
        assertTrue(groomTable.getTableName().toString().equalsIgnoreCase("x"));

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GROOM TABLE table_name PAGES ALL";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGroomTable groomTable = (TNetezzaGroomTable)sqlparser.sqlstatements.get(0);
        assertTrue(groomTable.getTableName().toString().equalsIgnoreCase("table_name"));
        TModeChoice modeChoice = groomTable.getModeChoice();
        assertTrue(modeChoice.getModeChoice() == EModeChoice.pagesAll);
    }

}
