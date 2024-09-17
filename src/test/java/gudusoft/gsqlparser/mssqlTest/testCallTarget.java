package gudusoft.gsqlparser.mssqlTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionCallTarget;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCallTarget  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "\tSELECT DISTINCT p.COl1,\n" +
                "STUFF((SELECT distinct ',' + p1.[Col2]\n" +
                "        FROM sampleSchema.table1 p1\n" +
                "        WHERE p.COl1 = p1.COl1\n" +
                "        FOR XML PATH(''), TYPE\n" +
                "        ).value('.', 'NVARCHAR(MAX)'),1,1,'') Col2\n" +
                "FROM sampleSchema.table2 p WHERE SourceSchema +'.'+ SourceObject = @table";
       // System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.function_t);
        TFunctionCall f1 = expr.getFunctionCall();
        assertTrue(f1.getFunctionName().toString().equalsIgnoreCase("STUFF"));
        TExpression p1 = f1.getArgs().getExpression(0);
        assertTrue(p1.getExpressionType() == EExpressionType.function_t);
        TFunctionCall f2 = p1.getFunctionCall();
        assertTrue(f2.getFunctionName().toString().equalsIgnoreCase("value"));
        assertTrue(f2.getArgs().getExpression(0).toString().equalsIgnoreCase("'.'"));
        TExpressionCallTarget ct = f2.getCallTarget();
        TExpression expr2 = ct.getExpr();
        assertTrue(expr2.getExpressionType() == EExpressionType.subquery_t);
        TSelectSqlStatement select1 = expr2.getSubQuery();
        assertTrue(select1.getResultColumnList().getResultColumn(0).getExpr().toString().equalsIgnoreCase("',' + p1.[Col2]"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT CatalogDescription.value('             \n" +
                "    declare namespace PD=\"http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription\";             \n" +
                "       (/PD:ProductDescription/@ProductModelID)[1]', 'int') AS Result             \n" +
                "FROM Production.ProductModel             \n" +
                "WHERE CatalogDescription IS NOT NULL             \n" +
                "ORDER BY Result desc";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.function_t);
        TFunctionCall f2 = expr.getFunctionCall();
        assertTrue(f2.getFunctionName().toString().equalsIgnoreCase("CatalogDescription.value"));
        assertTrue(f2.getArgs().getExpression(1).toString().equalsIgnoreCase("'int'"));
        TExpressionCallTarget ct = f2.getCallTarget();
        TExpression expr2 = ct.getExpr();

        assertTrue(expr2.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expr2.getObjectOperand().toString().equalsIgnoreCase("CatalogDescription"));
    }

}
