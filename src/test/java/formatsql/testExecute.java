package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testExecute extends TestCase {

    public static void testLinebreakBeforeParamInExec(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "EXEC Sptrackmember   @p_member_id,  '2.2',  @p_weeknum   ";

         sqlparser.parse();
         option.linebreakBeforeParamInExec = false;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("EXEC sptrackmember @p_member_id, '2.2', @p_weeknum"));

        sqlparser.parse();
        option.linebreakBeforeParamInExec = true;
        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("EXEC sptrackmember \n" +
                "  @p_member_id,\n" +
                "  '2.2',\n" +
                "  @p_weeknum"));
        //System.out.println(result);
     }
}
