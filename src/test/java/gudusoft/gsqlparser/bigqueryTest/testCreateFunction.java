package gudusoft.gsqlparser.bigqueryTest;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE TEMP FUNCTION mydataset.multiplyInputs(x FLOAT64, y FLOAT64)\n" +
                "RETURNS FLOAT64\n" +
                "LANGUAGE js\n" +
                "AS \"\"\"\n" +
                "  return x*y;\n" +
                "\"\"\";";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt functionStmt = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(functionStmt.getFunctionDefinition().toString().equalsIgnoreCase("\"\"\"\n" +
                "  return x*y;\n" +
                "\"\"\""));
        assertTrue(functionStmt.getFunctionName().toString().equalsIgnoreCase("mydataset.multiplyInputs"));
        assertTrue(functionStmt.getParameterDeclarations().getParameterDeclarationItem(0).getParameterName().toString().equalsIgnoreCase("x"));
        assertTrue(functionStmt.getParameterDeclarations().getParameterDeclarationItem(0).getDataType().getDataType() == EDataType.float64_t);

    }
}
