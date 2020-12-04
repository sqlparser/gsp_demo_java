package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESequenceOptionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateSequenceStmt;
import gudusoft.gsqlparser.stmt.TDropSequenceStmt;
import junit.framework.TestCase;

public class testSequence extends TestCase {

    public void testCreate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE SEQUENCE Test.CountBy1\n" +
                "    START WITH 1\n" +
                "    INCREMENT BY 1 ;";
        assertTrue(sqlparser.parse() == 0);
        TCreateSequenceStmt stmt = (TCreateSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getOptions().get(0).getSequenceOptionType() == ESequenceOptionType.startWith);
        assertTrue(stmt.getOptions().get(0).getOptionValue().toString().equalsIgnoreCase("1"));
    }

    public void testDrop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DROP SEQUENCE CountBy1 ; ;";
        assertTrue(sqlparser.parse() == 0);

        TDropSequenceStmt stmt = (TDropSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getSequenceName().toString().equalsIgnoreCase("CountBy1"));
    }

    public void testDropNetezza(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "DROP SEQUENCE sequence1;";
        assertTrue(sqlparser.parse() == 0);

        TDropSequenceStmt stmt = (TDropSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getSequenceName().toString().equalsIgnoreCase("sequence1"));
    }
}
