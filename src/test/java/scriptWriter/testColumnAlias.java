package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testColumnAlias extends TestCase
{
    public void testSelectModifier1( ) {
        String rawSql = "/*snapshot execution*/\n" +
                "SELECT\n" +
                "SCHEMA_NAME AS 'Schema name',\n" +
                "SCHEMA_OWNER 'Schema owner',\n" +
                "SCHEMA_IS_VIRTUAL 'Schema is virtual',\n" +
                "SCHEMA_COMMENT 'Schema comment'\n" +
                "FROM SYS.EXA_SCHEMAS\n" +
                "WHERE SCHEMA_NAME = 'MTAB_SCRIPTS'";
        //System.out.println(rawSql);
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlParser.sqltext = rawSql;
        int ret = sqlParser.parse();
        if (ret != 0) {
            System.err.println("Error parsing:" + sqlParser.getErrormessage());
            assertTrue(false);
        } else {
            TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
            String finalSql = select.toScript();
            //System.out.println("Final sql: " + finalSql);
             assertTrue(finalSql.contains("SCHEMA_OWNER"));
        }
    }
}
