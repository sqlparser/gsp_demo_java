package db2;


import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.db2.TCreateVariableStmt;
import junit.framework.TestCase;

public class testCreateVariable extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CREATE variable SV_OP_USR_PROFILE_NAME varchar2(256);";

        assertTrue(sqlparser.parse() == 0);

        TCreateVariableStmt createVariableStmt = (TCreateVariableStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createVariableStmt.getVariableName().toString().equalsIgnoreCase("SV_OP_USR_PROFILE_NAME"));
        assertTrue(createVariableStmt.getVariableDatatype().getDataType() == EDataType.varchar2_t);
    }
}
