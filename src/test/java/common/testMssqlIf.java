package common;
/*
 * Date: 2010-8-27
 * Time: 17:23:32
 */

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlPrint;

public class testMssqlIf extends TestCase {

    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "IF (SELECT AVG(price) FROM titles WHERE type = 'mod_cook') < $15\n" +
                "BEGIN\n" +
                "   PRINT 'The following titles are excellent mod_cook books:'\n" +
                "   PRINT ' '\n" +
                "   SELECT SUBSTRING(title, 1, 35) AS Title\n" +
                "   FROM titles\n" +
                "   WHERE type = 'mod_cook' \n" +
                "END\n" +
                "ELSE\n" +
                "   PRINT 'Average title price is more than $15.'";
        assertTrue(parser.parse() == 0);

        TMssqlIfElse ifstmt = (TMssqlIfElse)parser.sqlstatements.get(0);
        assertTrue(ifstmt.getCondition().toString().equalsIgnoreCase("(SELECT AVG(price) FROM titles WHERE type = 'mod_cook') < $15"));

        TMssqlBlock block = (TMssqlBlock)ifstmt.getStmt();
        assertTrue(block.getBodyStatements().size() == 3);
        assertTrue(block.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlprint);
        TMssqlPrint printStmt = (TMssqlPrint)block.getBodyStatements().get(0);
        assertTrue(printStmt.getMessages().getExpression(0).toString().equalsIgnoreCase("'The following titles are excellent mod_cook books:'"));
        assertTrue(ifstmt.getElseStmt().sqlstatementtype== ESqlStatementType.sstmssqlprint); 

    }

    public void test2(){
        parser.sqltext = "IF EXISTS (SELECT * FROM [dbo].[foo] WHERE id = 1)\n" +
                "BEGIN\n" +
                "                DROP TABLE [dbo].[TestTable1]\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "  SELECT * FROM [dbo].[TestTable2]\n" +
                "END";
        assertTrue(parser.parse() == 0);

        TMssqlIfElse ifstmt = (TMssqlIfElse)parser.sqlstatements.get(0);
        TExpression expression = ifstmt.getCondition();
        assertTrue(expression.getExpressionType() == EExpressionType.exists_t);

        TSelectSqlStatement selectSqlStatement = expression.getSubQuery();
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase("(SELECT * FROM [dbo].[foo] WHERE id = 1)"));

//        assertTrue(ifstmt.getCondition().toString().equalsIgnoreCase("(SELECT AVG(price) FROM titles WHERE type = 'mod_cook') < $15"));
//
//        TMssqlBlock block = (TMssqlBlock)ifstmt.getStmt();
//        assertTrue(block.getBodyStatements().size() == 3);
//        assertTrue(block.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlprint);
//        TMssqlPrint printStmt = (TMssqlPrint)block.getBodyStatements().get(0);
//        assertTrue(printStmt.getMessages().getExpression(0).toString().equalsIgnoreCase("'The following titles are excellent mod_cook books:'"));
//        assertTrue(ifstmt.getElseStmt().sqlstatementtype== ESqlStatementType.sstmssqlprint);

    }

}
