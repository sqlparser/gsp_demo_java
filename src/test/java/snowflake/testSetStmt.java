package snowflake;



import gudusoft.gsqlparser.EDbVendor;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetStmt;
import junit.framework.TestCase;

public class testSetStmt extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "set v1 = 10";
        assertTrue(sqlparser.parse() == 0);

        TSetStmt setStmt = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setStmt.getVariableName().toString().equalsIgnoreCase("v1"));
        assertTrue(setStmt.getVariableValue().toString().equalsIgnoreCase("10"));
    }
}
