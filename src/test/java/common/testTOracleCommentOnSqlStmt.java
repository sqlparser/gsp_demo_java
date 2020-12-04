package common;
/*
 * Date: 2010-12-31
 * Time: 14:10:48
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;

public class testTOracleCommentOnSqlStmt extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "COMMENT ON TABLE \"BBCWW_CUST\".\"CH_SUBTITLE_LANGUAGES\"  IS 'created on 04.02.2008 by Laviniam for Morpheus Probel FD, Subtitle Languages UDF from Channel screen';";
        assertTrue(sqlparser.parse() == 0);

        TCommentOnSqlStmt comment = (TCommentOnSqlStmt)sqlparser.sqlstatements.get(0);
        assertTrue(comment.getDbObjType() == TObjectName.ttobjTable);
        assertTrue(comment.getObjectName().toString().equalsIgnoreCase("\"BBCWW_CUST\".\"CH_SUBTITLE_LANGUAGES\""));
        assertTrue(comment.getMessage().toString().equalsIgnoreCase("'created on 04.02.2008 by Laviniam for Morpheus Probel FD, Subtitle Languages UDF from Channel screen'"));

    }
}
