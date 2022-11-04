package greenplum;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESetStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetStmt;
import junit.framework.TestCase;

public class testSetVariable  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "SET statement_timeout = 0;";
        assertTrue(sqlparser.parse() == 0);
        TSetStmt setStmt = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setStmt.getSetStatementType() == ESetStatementType.variable);
        assertTrue(setStmt.getVariableName().toString().equalsIgnoreCase("statement_timeout"));
        assertTrue(setStmt.getVariableValue().toString().equalsIgnoreCase("0"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "SET search_path = dwd, pg_catalog;";
        assertTrue(sqlparser.parse() == 0);
        TSetStmt setStmt = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setStmt.getSetStatementType() == ESetStatementType.variable);
        assertTrue(setStmt.getVariableName().toString().equalsIgnoreCase("search_path"));
        assertTrue(setStmt.getVariableValueList().getExpression(0).toString().equalsIgnoreCase("dwd"));
        assertTrue(setStmt.getVariableValueList().getExpression(1).toString().equalsIgnoreCase("pg_catalog"));
    }
}
