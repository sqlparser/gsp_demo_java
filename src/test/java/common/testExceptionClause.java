package common;

import gudusoft.gsqlparser.nodes.TExceptionHandler;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TBlockSqlNode;
import gudusoft.gsqlparser.nodes.TExceptionClause;

/*
* Date: 2010-5-12
* Time: 17:44:39
*/
public class testExceptionClause extends TestCase {

    private String sqlfile = null;
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        sqlfile = gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"oracle/exception_handler.sql";
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    public void testDo(){
        parser.sqlfilename = sqlfile;
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0) instanceof TCommonBlock);
        TCommonBlock block = (TCommonBlock)parser.sqlstatements.get(0);
        TBlockSqlNode blockSqlNode = (TBlockSqlNode) block.rootNode;
        assertTrue(blockSqlNode.getExceptionClause() != null);
        TExceptionClause exceptionClause = blockSqlNode.getExceptionClause();
        //System.out.println(exceptionClause.getHandlers().size());
        assertTrue(exceptionClause.getHandlers().size() == 3);
        TExceptionHandler exceptionHandler = null;
        
        exceptionHandler = (TExceptionHandler)exceptionClause.getHandlers().getElement(0);
        assertTrue(exceptionHandler.getStmts().size() == 1);
        assertTrue(exceptionHandler.getExceptionNames().getObjectName(0).toString().compareToIgnoreCase("past_due") == 0);

        exceptionHandler = (TExceptionHandler)exceptionClause.getHandlers().getElement(1);
        assertTrue(exceptionHandler.getStmts().size() == 2);
        assertTrue(exceptionHandler.getExceptionNames().getObjectName(0).toString().compareToIgnoreCase("past_due") == 0);
        assertTrue(exceptionHandler.getExceptionNames().getObjectName(1).toString().compareToIgnoreCase("past_due2") == 0);

        exceptionHandler = (TExceptionHandler)exceptionClause.getHandlers().getElement(2);
        assertTrue(exceptionHandler.getStmts().size() == 1);
        assertTrue(exceptionHandler.getExceptionNames().getObjectName(0).toString().equalsIgnoreCase("OTHERS"));

    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

}
