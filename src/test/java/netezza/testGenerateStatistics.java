package netezza;
/*
 * Date: 13-11-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.netezza.TNetezzaGenerateStatistics;
import junit.framework.TestCase;

public class testGenerateStatistics extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GENERATE STATISTICS ON X;";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGenerateStatistics getStatistics = (TNetezzaGenerateStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(getStatistics.getTableName().toString().equalsIgnoreCase("X"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GENERATE STATISTICS ON cows (cnumber)";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGenerateStatistics getStatistics = (TNetezzaGenerateStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(getStatistics.getTableName().toString().equalsIgnoreCase("cows"));
        assertTrue(getStatistics.getColumns().getObjectName(0).toString().equalsIgnoreCase("cnumber"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "GENERATE EXPRESS STATISTICS ON cows";
        assertTrue(sqlparser.parse() == 0);
        TNetezzaGenerateStatistics getStatistics = (TNetezzaGenerateStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(getStatistics.getTableName().toString().equalsIgnoreCase("cows"));
        assertTrue(getStatistics.isExpress());
    }

}
