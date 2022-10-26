package errorsql;
/*
 * Date: 13-3-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;

public class testCreateIndex extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "CREATE UNIQUE INDEX \"A\".\"IB\" ON \"A\".\"B\"\n" +
                "             (\"A\" ASC,\n" +
                "             \"B\" ASC )\n" +
                "             ALLOW REVERSE SCANS; --Error";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement createIndexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createIndexSqlStatement.getIndexName().toString().equalsIgnoreCase("\"A\".\"IB\""));
        assertTrue(createIndexSqlStatement.getTableName().toString().equalsIgnoreCase("\"A\".\"B\""));
        assertTrue(createIndexSqlStatement.getColumnNameList().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("\"A\""));
        assertTrue(createIndexSqlStatement.getColumnNameList().getOrderByItem(0).getSortType() == 1);
    }

}
