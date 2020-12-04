package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftAnalyzeCompression;
import junit.framework.TestCase;


public class testAnalyzeCompression extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "analyze compression listing;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAnalyzeCompression);
        TRedshiftAnalyzeCompression analyzeCompression = (TRedshiftAnalyzeCompression)sqlparser.sqlstatements.get(0);
        assertTrue(analyzeCompression.getTableName().toString().equalsIgnoreCase("listing"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "analyze compression sales(qtysold, commission, saletime)";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAnalyzeCompression);
        TRedshiftAnalyzeCompression analyzeCompression = (TRedshiftAnalyzeCompression)sqlparser.sqlstatements.get(0);
        assertTrue(analyzeCompression.getTableName().toString().equalsIgnoreCase("sales"));
        assertTrue(analyzeCompression.getColumnList().size() == 3);
        assertTrue(analyzeCompression.getColumnList().getObjectName(2).toString().equalsIgnoreCase("saletime"));
    }

}