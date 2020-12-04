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

public class testWhereClause extends TestCase {

    public static void testAndOrUnderWhere(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "SELECT e.employee_id,\n" +
                 "       d.locatioin_id\n" +
                 "FROM   employees e,\n" +
                 "       departments d\n" +
                 "WHERE  e.department_id = d.department_id\n" +
                 "   AND e.last_name = 'Matos' and exists(\n" +
                 "\t\t\t\tSELECT e.employee_id\n" +
                 "\t\t\t\tFROM   employees e,\n" +
                 "\t\t\t\t       departments d\n" +
                 "\t\t\t\tWHERE  e.department_id = d.department_id\n" +
                 "\t\t\t\t   AND e.last_name   \n" +
                 "   );";

         sqlparser.parse();
        option.andOrUnderWhere = true;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("SELECT e.employee_id,\n" +
                 "       d.locatioin_id\n" +
                 "FROM   employees e,\n" +
                 "       departments d\n" +
                 "WHERE  e.department_id = d.department_id\n" +
                 "   AND e.last_name = 'Matos'\n" +
                 "   AND EXISTS( SELECT e.employee_id\n" +
                 "               FROM   employees e,\n" +
                 "                      departments d\n" +
                 "               WHERE  e.department_id = d.department_id\n" +
                 "                  AND e.last_name  );"));
       //  System.out.println(result);
     }

}
