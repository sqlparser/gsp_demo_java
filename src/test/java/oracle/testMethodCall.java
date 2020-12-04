package oracle;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import junit.framework.TestCase;

public class testMethodCall  extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "    BEGIN\n" +
                "      g_aansprakenSet(p_aanspraak.klantnummer_gerechtigde)(l_datum_ingang).aansprakentabel := p_aanspraak;\n" +
                "    END addToSet;";
        assertTrue(sqlparser.parse() == 0);
        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TAssignStmt assignment  = (TAssignStmt)block.getBodyStatements().get(0);
        TExpression left = assignment.getLeft();
        assertTrue(left.getExpressionType() == EExpressionType.arrayaccess_t);
       // TFunctionCall f = basicStmt.getExpr().getFunctionCall();
       // assertTrue(f.getArgs().getExpression(0).toString().equalsIgnoreCase("a => b"));
    }

    public void testMethodName(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "    BEGIN \n" +
                "schema1.proc1; \n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);
        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TBasicStmt basicStmt  = (TBasicStmt)block.getBodyStatements().get(0);
        assertTrue(basicStmt.getExpr().getExpressionType() == EExpressionType.simple_object_name_t);
        TObjectName proc = basicStmt.getExpr().getObjectOperand();
        assertTrue(proc.getSchemaString().equalsIgnoreCase("schema1"));
        assertTrue(proc.getObjectString().equalsIgnoreCase("proc1"));
    }
}
