package formatsql;
/*
 * Date: 11-3-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testLineNumber extends TestCase {

    public static void testlinenumber_enabled(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "select department_id,\n" +
                 "       min( salary ) -- single line comment \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
        option.linenumberEnabled  = true;
        option.linenumberLeftMargin = 1;
        option.linenumberRightMargin = 4;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.equalsIgnoreCase(" 1    SELECT   department_id,\n" +
                 " 2             Min(salary) -- single line comment \n" +
                 " 3    FROM     employees\n" +
                 " 4    GROUP BY department_id"));
//         System.out.println(result);
    }

}
