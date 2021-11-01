package common;
/*
 * Date: 2010-5-13
 * Time: 17:17:50
 */

import gudusoft.gsqlparser.stmt.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TBlockSqlNode;

public class testVariableDeclare extends TestCase {
    private String sqlfile = null;
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        sqlfile = gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"oracle/exception_handler.sql";
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void testDo(){
        parser.sqlfilename = sqlfile;
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0) instanceof TCommonBlock);
        TCommonBlock block = (TCommonBlock)parser.sqlstatements.get(0);
        TBlockSqlNode blockSqlNode = (TBlockSqlNode) block.rootNode;

        assertTrue(blockSqlNode.getDeclareStmts() != null);
        assertTrue(blockSqlNode.getDeclareStmts().getStatementSqlNode(0).getStmt() instanceof TVarDeclStmt);
        assertTrue(blockSqlNode.getDeclareStmts().getStatementSqlNode(1).getStmt() instanceof TVarDeclStmt);

        TVarDeclStmt varDecl = (TVarDeclStmt) blockSqlNode.getDeclareStmts().getStatementSqlNode(0).getStmt();
        assertTrue(varDecl.getElementName().toString().compareToIgnoreCase("past_due") == 0);

        varDecl = (TVarDeclStmt) blockSqlNode.getDeclareStmts().getStatementSqlNode(1).getStmt();
        assertTrue(varDecl.getElementName().toString().compareToIgnoreCase("acct_num") == 0);

        // sub declared block
        assertTrue(blockSqlNode.getStmts() != null);
        TCommonBlock block1 = (TCommonBlock)blockSqlNode.getStmts().getStatementSqlNode(0).getStmt();
        TBlockSqlNode blockSqlNode1 = (TBlockSqlNode) block1.rootNode;

        // declare stmts in subblock
        assertTrue(blockSqlNode1.getDeclareStmts().size() == 4);

        TVarDeclStmt varDecl1 = (TVarDeclStmt) blockSqlNode1.getDeclareStmts().getStatementSqlNode(0).getStmt();
        assertTrue(varDecl1.getElementName().toString().compareToIgnoreCase("past_due") == 0);

        varDecl1 = (TVarDeclStmt) blockSqlNode1.getDeclareStmts().getStatementSqlNode(3).getStmt();
        assertTrue(varDecl1.getElementName().toString().compareToIgnoreCase("todays_date") == 0);
    }


}
