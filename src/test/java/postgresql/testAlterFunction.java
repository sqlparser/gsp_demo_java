package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterFunctionStmt;
import junit.framework.TestCase;

public class testAlterFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "ALTER FUNCTION sqlflow.add(integer, integer) OWNER TO bigking;";
        assertTrue(sqlparser.parse() == 0);

        TAlterFunctionStmt alter = (TAlterFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alter.getFunctionName().toString().equalsIgnoreCase("sqlflow.add"));
        assertTrue(alter.getOwnerName().toString().equalsIgnoreCase("bigking"));
    }
}
