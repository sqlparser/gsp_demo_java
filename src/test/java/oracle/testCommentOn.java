package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;


import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;
import junit.framework.TestCase;

public class testCommentOn extends TestCase{
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "COMMENT ON TABLE \"SFMFG\".\"SFFND_OPER_TYPE_DEF\" IS 'Test'";
        assertTrue(sqlparser.parse() == 0);
        TCommentOnSqlStmt commentOnSqlStmt = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(commentOnSqlStmt.getObjectName().toString().equalsIgnoreCase("\"SFMFG\".\"SFFND_OPER_TYPE_DEF\""));
        assertTrue(commentOnSqlStmt.getObjectName().getTableString().equalsIgnoreCase("\"SFFND_OPER_TYPE_DEF\""));
        assertTrue(commentOnSqlStmt.getObjectName().getSchemaString().equalsIgnoreCase("\"SFMFG\""));

    }
}
