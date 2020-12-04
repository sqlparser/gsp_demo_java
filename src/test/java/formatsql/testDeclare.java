package formatsql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;


public class testDeclare extends TestCase {

    public static void testLinebreakAfterDeclare(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @s  VARCHAR(1000),        @s2 VARCHAR(10)";

        sqlparser.parse();
        option.linebreakAfterDeclare = true;
        option.indentLen = 2;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("DECLARE\n" +
                "  @s  VARCHAR(1000),\n" +
                "  @s2 VARCHAR(10)"));
        //System.out.println(result);
    }
}
