package formatsql;
/*
 * Date: 11-3-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TCompactMode;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testCompactMode extends TestCase {

    /**
     * turn sql into a single line, or multiple lines with a fixed line width
     * no need to format this sql
     * be careful when there are single line comment in SQL statement.
     */
    public static void testSingleLine(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "select department_id,\n" +
                 "       min( salary ) -- single line comment \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
        option.compactMode = TCompactMode.Cpmugly;
        option.lineWidth = 60;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("SELECT department_id, Min( salary ) \n" +
                 "/* -- single line comment */ FROM employees GROUP BY \n" +
                 "department_id"));

         //System.out.println(result);
    }

}
