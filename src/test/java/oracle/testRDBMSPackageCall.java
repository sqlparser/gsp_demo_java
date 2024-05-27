package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testRDBMSPackageCall extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT DBMS_RANDOM.RANDOM FROM dual;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        TFunctionCall functionCall = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr().getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("DBMS_RANDOM.RANDOM"));
    }
}
