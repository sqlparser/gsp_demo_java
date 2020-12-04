package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftAnalyze;
import junit.framework.TestCase;


public class testAnalyze extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "analyze verbose;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAnalyze);
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "analyze listing;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAnalyze);
        TRedshiftAnalyze analyze = (TRedshiftAnalyze)sqlparser.sqlstatements.get(0);
        assertTrue(analyze.getTableName().toString().equalsIgnoreCase("listing"));
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "analyze venue(venueid, venuename); ";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAnalyze);
        TRedshiftAnalyze analyze = (TRedshiftAnalyze)sqlparser.sqlstatements.get(0);
        assertTrue(analyze.getTableName().toString().equalsIgnoreCase("venue"));
        assertTrue(analyze.getColumnList().size() == 2);
        assertTrue(analyze.getColumnList().getObjectName(0).toString().equalsIgnoreCase("venueid"));
    }

}