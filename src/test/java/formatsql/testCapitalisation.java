package formatsql;
/*
 * Date: 11-3-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testCapitalisation extends TestCase {

    public static void testDefault(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "select department_id,\n" +
                 "       min( salary ) \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   department_id,\n" +
                "         Min(salary)\n" +
                "FROM     employees\n" +
                "GROUP BY department_id"));
       //  System.out.println(result);
    }

    public static void testAllUpper(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "select department_id,\n" +
                 "       min( salary ) \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
        option.caseDatatype = TCaseOption.CoUppercase;
        option.caseFuncname = TCaseOption.CoUppercase;
        option.caseIdentifier = TCaseOption.CoUppercase;
        option.caseKeywords = TCaseOption.CoUppercase;
        option.caseQuotedIdentifier = TCaseOption.CoUppercase;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   DEPARTMENT_ID,\n" +
                "         MIN(SALARY)\n" +
                "FROM     EMPLOYEES\n" +
                "GROUP BY DEPARTMENT_ID"));
         //System.out.println(result);
    }

    public static void testAllLower(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "Select department_id,\n" +
                 "       min( salary ) \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
        option.caseDatatype = TCaseOption.CoLowercase;
        option.caseFuncname = TCaseOption.CoLowercase;
        option.caseIdentifier = TCaseOption.CoLowercase;
        option.caseKeywords = TCaseOption.CoLowercase;
        option.caseQuotedIdentifier = TCaseOption.CoLowercase;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("select   department_id,\n" +
                "         min(salary)\n" +
                "from     employees\n" +
                "group by department_id"));
         //System.out.println(result);
    }

    public static void testAllUnchanged(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "Select department_id,\n" +
                 "       miN( Salary ) \n" +
                 "from   employees \n" +
                 "GROUP  by department_id";

         sqlparser.parse();
        option.caseDatatype = TCaseOption.CoNoChange;
        option.caseFuncname = TCaseOption.CoNoChange;
        option.caseIdentifier = TCaseOption.CoNoChange;
        option.caseKeywords = TCaseOption.CoNoChange;
        option.caseQuotedIdentifier = TCaseOption.CoNoChange;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("Select   department_id,\n" +
                "         miN(Salary)\n" +
                "from     employees\n" +
                "GROUP by department_id"));
         //System.out.println(result);
    }

}
