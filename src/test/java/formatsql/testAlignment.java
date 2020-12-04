package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testAlignment extends TestCase {

    public static void testSelect_keywords_alignOption(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "DELETE FROM job_history jh \n" +
                 "WHERE  employee_id = (SELECT employee_id \n" +
                 "FROM   employee e \n" +
                 "WHERE  jh.employee_id = e.employee_id \n" +
                 "AND start_date = (SELECT Min(start_date) \n" +
                 "FROM   job_history jh \n" +
                 "WHERE  jh.employee_id = e.employee_id) \n" +
                 "AND 5 > (SELECT Count( * ) \n" +
                 "FROM   job_history jh \n" +
                 "WHERE  jh.employee_id = e.employee_id \n" +
                 "GROUP  BY employee_id \n" +
                 "HAVING Count( * ) >= 4)); ";

         sqlparser.parse();
        option.selectKeywordsAlignOption = TAlignOption.AloRight;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("DELETE FROM job_history jh\n" +
                "      WHERE employee_id = (SELECT employee_id\n" +
                "                             FROM employee e\n" +
                "                            WHERE jh.employee_id = e.employee_id\n" +
                "                                  AND start_date = (SELECT Min(start_date)\n" +
                "                                                      FROM job_history jh\n" +
                "                                                     WHERE jh.employee_id = e.employee_id)\n" +
                "                                  AND 5 > (  SELECT Count(*)\n" +
                "                                               FROM job_history jh\n" +
                "                                              WHERE jh.employee_id = e.employee_id\n" +
                "                                           GROUP BY employee_id\n" +
                "                                             HAVING Count(*) >= 4));"));
//        assertTrue(result.trim().equalsIgnoreCase("DELETE FROM job_history jh\n" +
//                "      WHERE employee_id = (SELECT employee_id\n" +
//                "                             FROM employee e\n" +
//                "                            WHERE jh.employee_id = e.employee_id\n" +
//                "                                  AND start_date = (SELECT Min(start_date)\n" +
//                "                                                      FROM job_history jh\n" +
//                "                                                     WHERE jh.employee_id = e.employee_id)\n" +
//                "                                  AND 5 > (  SELECT Count(*)\n" +
//                "                                               FROM job_history jh\n" +
//                "                                              WHERE jh.employee_id = e.employee_id\n" +
//                "                                           GROUP BY employee_id  HAVING Count(*) >= 4));"));

       // System.out.println(result);
     }

    public static void testSelect_keywords_alignOption_delete(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "INSERT INTO employees\n" +
                 "(employee_id,\n" +
                 " first_name,\n" +
                 " last_name,\n" +
                 " email,\n" +
                 " phone_number,\n" +
                 " hire_date,\n" +
                 " job_id,\n" +
                 " salary,\n" +
                 " commission_pct,\n" +
                 " manager_id,\n" +
                 " department_id) \n" +
                 "VALUES(113,\n" +
                 "'Louis',\n" +
                 "'Popp',\n" +
                 "'Ldd',\n" +
                 "'515.124.222',\n" +
                 "sysdate,\n" +
                 "'Ac_account',\n" +
                 "8900,\n" +
                 "NULL,\n" +
                 "205,\n" +
                 "100)\n" +
                 "            \n" +
                 "                                      ";

         sqlparser.parse();
        option.selectKeywordsAlignOption = TAlignOption.AloRight;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("INSERT INTO employees\n" +
                "            (employee_id,\n" +
                "             first_name,\n" +
                "             last_name,\n" +
                "             email,\n" +
                "             phone_number,\n" +
                "             hire_date,\n" +
                "             job_id,\n" +
                "             salary,\n" +
                "             commission_pct,\n" +
                "             manager_id,\n" +
                "             department_id)\n" +
                "     VALUES (113,\n" +
                "             'Louis',\n" +
                "             'Popp',\n" +
                "             'Ldd',\n" +
                "             '515.124.222',\n" +
                "             sysdate,\n" +
                "             'Ac_account',\n" +
                "             8900,\n" +
                "             NULL,\n" +
                "             205,\n" +
                "             100)"));
        //System.out.println(result);
     }

    public static void testSelect_keywords_alignOption_update(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "UPDATE employees \n" +
                 "SET department_id = 70 \n" +
                 "WHERE employee_id = 113" ;

         sqlparser.parse();
        option.selectKeywordsAlignOption = TAlignOption.AloRight;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("UPDATE employees\n" +
                "   SET department_id = 70\n" +
                " WHERE employee_id = 113"));
        //System.out.println(result);
     }

}
