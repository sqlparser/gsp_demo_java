package sybase;
/*
 * Date: 14-11-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.sybase.TSybaseWritetext;
import junit.framework.TestCase;

public class testWritetext extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "writetext unitable.ut @val with log \"Hello world\"";
        int i = sqlparser.parse() ;
        assertTrue(i == 0);
        TSybaseWritetext writetext  = (TSybaseWritetext)sqlparser.sqlstatements.get(0);
        assertTrue(writetext.getColumnName().toString().equalsIgnoreCase("unitable.ut"));
        assertTrue(writetext.getText_pointer().toString().equalsIgnoreCase("@val"));
        assertTrue(writetext.getWriteData().toString().equalsIgnoreCase("\"Hello world\""));
    }

}
