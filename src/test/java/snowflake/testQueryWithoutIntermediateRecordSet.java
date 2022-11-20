package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testQueryWithoutIntermediateRecordSet  extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqlfilename = common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/snowflake/view/QueryWithoutIntermediateRecordSet.sql";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.getErrormessage() == null || sqlparser.getErrormessage().trim().equals(""));
        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            assertTrue(sqlparser.sqlstatements.get(i).getSyntaxErrors().size() == 0);
            assertTrue(sqlparser.sqlstatements.get(i).getSyntaxHints().size() == 0);
        }

    }
}
