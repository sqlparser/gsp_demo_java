package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testBTEQ extends TestCase {

    public void testLogon(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".Logon 127.0.0.1/Tduser \n" +
                "\tPassword:\n" +
                "\t\n" +
                "select a from b;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 2);

    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".HELP BTEQ\n" +
                ".LOGON mydbs/myid;\n" +
                ".SET SIDETITLES ON; .SET FOLDLINE ON ALL\n" +
                "SELECT DATE;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 5);

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "BT;\n" +
                "select a from b;\n" +
                "select a from b;\n" +
                "select a from b;\n" +
                "ET;\n" +
                "= 10";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 6);

    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".GOTO mylabel\n" +
                ".REMARK 'A'\n" +
                ".REMARK 'B'\n" +
                ".LABEL mylabel\n" +
                ".IF ERRORCODE=0\n" +
                ".REMARK 'C'\n" +
                ".ELSEIF ERRORCODE=1\n" +
                ".REMARK 'D'\n" +
                ".ELSE\n" +
                ".REMARK 'E'\n" +
                ".ENDIF";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 11);
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".SET SESSION TRANS BTET\n" +
                ".LOGON server/user, password\n" +
                "BT;\n" +
                "SELECT date;\n" +
                ".COMPILE FILE spSample1\n" +
                "ET;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 6);
    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT date\n" +
                "; ECHO '.SHOW';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 2);
    }

    public void test6(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".show errormap\n" +
                ".set errorlevel 4155 severity 12\n" +
                ".set errorlevel 4800 severity 8\n" +
                ".show errormap";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 4);
    }

}
