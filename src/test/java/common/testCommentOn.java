package common;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;
import junit.framework.TestCase;

public class testCommentOn extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "COMMENT ON COLUMN customer_dimension_vmart_node01.customer_name IS 'Last name only';";
        assertTrue(sqlparser.parse() == 0);
        TCommentOnSqlStmt commentOnSqlStmt = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(commentOnSqlStmt.getDbObjectType() == EDbObjectType.column);
        assertTrue(commentOnSqlStmt.getObjectName().toString().equalsIgnoreCase("customer_dimension_vmart_node01.customer_name"));
        assertTrue(commentOnSqlStmt.getMessage().toString().equalsIgnoreCase("'Last name only'"));
    }

    public void testNetezza(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "COMMENT ON COLUMN LOAD_INPUT_DATASET.STATUS_CD IS '[0=Loading;1=Ready;2=Failed;3=Archived;4=Purged]';';";
        assertTrue(sqlparser.parse() == 0);
        TCommentOnSqlStmt commentOnSqlStmt = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(commentOnSqlStmt.getDbObjectType() == EDbObjectType.column);
        assertTrue(commentOnSqlStmt.getObjectName().toString().equalsIgnoreCase("LOAD_INPUT_DATASET.STATUS_CD"));
        assertTrue(commentOnSqlStmt.getMessage().toString().equalsIgnoreCase("'[0=Loading;1=Ready;2=Failed;3=Archived;4=Purged]'"));
    }

    public void testOracle(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "comment on column sql_policy.policy_id is 'test'";
        assertTrue(sqlparser.parse() == 0);
        TCommentOnSqlStmt commentOnSqlStmt = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(commentOnSqlStmt.getDbObjectType() == EDbObjectType.column);
        assertTrue(commentOnSqlStmt.getObjectName().toString().equalsIgnoreCase("sql_policy.policy_id"));
        assertTrue(commentOnSqlStmt.getObjectName().getTableString().equalsIgnoreCase("sql_policy"));
        assertTrue(commentOnSqlStmt.getMessage().toString().equalsIgnoreCase("'test'"));
    }

    public void testPostgres(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "COMMENT ON TABLE ACT_DIM_HLHA IS 'Type lieu HAD historise';";
        assertTrue(sqlparser.parse() == 0);
        TCommentOnSqlStmt commentOnSqlStmt = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(commentOnSqlStmt.getDbObjectType() == EDbObjectType.table);
        assertTrue(commentOnSqlStmt.getObjectName().toString().equalsIgnoreCase("ACT_DIM_HLHA"));
        assertTrue(commentOnSqlStmt.getMessage().toString().equalsIgnoreCase("'Type lieu HAD historise'"));
    }
}
