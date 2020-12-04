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

public class testCaseExpression extends TestCase {

    public static void testCaseWhenThenInSameLine(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SELECT productnumber,\n" +
                 "       name,\n" +
                 "       'Price Range' = CASE                          WHEN listprice = 0                          THEN 'Mfg item - not for resale' \n" +
                 "                         WHEN listprice < 50                          THEN 'Under $50' \n" +
                 "                         WHEN listprice >= 50                               AND listprice < 250 \n" +
                 "                         THEN 'Under $250'                          WHEN listprice >= 250 \n" +
                 "                              AND listprice < 1000                          THEN 'Under $1000'                        ELSE 'Over $1000' END \n" +
                 "FROM   production.product \n" +
                 "ORDER  BY productnumber;  ";

         sqlparser.parse();
        option.caseWhenThenInSameLine = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   productnumber,\n" +
                "         name,\n" +
                "         'Price Range' = CASE\n" +
                "                           WHEN listprice = 0 THEN 'Mfg item - not for resale'\n" +
                "                           WHEN listprice < 50 THEN 'Under $50'\n" +
                "                           WHEN listprice >= 50\n" +
                "                                AND listprice < 250 THEN 'Under $250'\n" +
                "                           WHEN listprice >= 250\n" +
                "                                AND listprice < 1000 THEN 'Under $1000'\n" +
                "                           ELSE 'Over $1000'\n" +
                "                         END\n" +
                "FROM     production.product\n" +
                "ORDER BY productnumber;"));
        //System.out.println(result);
     }

    public static void testIndent_CaseFromSwitch(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SELECT productnumber,\n" +
                 "       name,\n" +
                 "       'Price Range' = CASE                          WHEN listprice = 0                          THEN 'Mfg item - not for resale' \n" +
                 "                         WHEN listprice < 50                          THEN 'Under $50' \n" +
                 "                         WHEN listprice >= 50                               AND listprice < 250 \n" +
                 "                         THEN 'Under $250'                          WHEN listprice >= 250 \n" +
                 "                              AND listprice < 1000                          THEN 'Under $1000'                        ELSE 'Over $1000' END \n" +
                 "FROM   production.product \n" +
                 "ORDER  BY productnumber;  ";

         sqlparser.parse();
        option.caseWhenThenInSameLine = false;
        option.indentCaseFromSwitch = 4;
        option.indentCaseThen = 2;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   productnumber,\n" +
                "         name,\n" +
                "         'Price Range' = CASE\n" +
                "                             WHEN listprice = 0\n" +
                "                               THEN 'Mfg item - not for resale'\n" +
                "                             WHEN listprice < 50\n" +
                "                               THEN 'Under $50'\n" +
                "                             WHEN listprice >= 50\n" +
                "                                  AND listprice < 250\n" +
                "                               THEN 'Under $250'\n" +
                "                             WHEN listprice >= 250\n" +
                "                                  AND listprice < 1000\n" +
                "                               THEN 'Under $1000'\n" +
                "                             ELSE 'Over $1000'\n" +
                "                         END\n" +
                "FROM     production.product\n" +
                "ORDER BY productnumber;"));
        //System.out.println(result);
     }
}
