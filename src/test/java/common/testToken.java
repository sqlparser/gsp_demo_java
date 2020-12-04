package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import junit.framework.TestCase;

public class testToken extends TestCase {

    public void testPosinList(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select f from t\n" +
                "where f>1\n";
        assertTrue(sqlparser.parse() == 0);
        for (int i=0;i<sqlparser.sourcetokenlist.size();i++){
            assertTrue(i == sqlparser.sourcetokenlist.get(i).posinlist);
        }
    }

    public void testOffset(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select f from t\n" +
                "where f>1\n";
        assertTrue(sqlparser.parse() == 0);
        for (int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            String textFromOffset = sqlparser.sqltext.toString().substring((int)st.offset,(int)st.offset+st.toString().length());
            assertTrue(st.toString().equalsIgnoreCase(textFromOffset));
        }
    }

}