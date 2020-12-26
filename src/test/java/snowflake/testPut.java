package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TStageLocation;
import gudusoft.gsqlparser.stmt.snowflake.TPutStmt;
import junit.framework.TestCase;

public class testPut extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "put file:///tmp/sales.json @mystage auto_compress=true;";
        assertTrue(sqlparser.parse() == 0);

        TPutStmt putStmt = (TPutStmt)sqlparser.sqlstatements.get(0);
        assertTrue(putStmt.getFileName().equalsIgnoreCase("file:///tmp/sales.json"));
        TStageLocation stageLocation = putStmt.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("mystage"));
    }
}
