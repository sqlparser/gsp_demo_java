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

public class testWhitespace extends TestCase {

    public static void testWSPadding_OperatorArithmetic(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "SELECT * \n" +
                 "FROM   dual \n" +
                 "WHERE  1=-1 \n" +
                 "       AND(1!=2 \n" +
                 "            OR 2^=3) \n" +
                 "       AND 3<>4 \n" +
                 "       AND 4>+5;  ";
         sqlparser.parse();
         option.wsPaddingOperatorArithmetic = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue("-1 shouldn't be - 1, +5 shouldn't be + 5, should be a space before 3",result.trim().equalsIgnoreCase("SELECT *\n" +
                "FROM   dual\n" +
                "WHERE  1 = -1\n" +
                "       AND ( 1 != 2\n" +
                "             OR 2 ^= 3 )\n" +
                "       AND 3 <> 4\n" +
                "       AND 4 > +5;"));
      //  System.out.println(result);
     }

    public static void testWSPadding_ParenthesesInFunction(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION sales.Fn_salesbystore\n" +
                 "  ( @storeid INT) \n" +
                 "RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN 0;";
         sqlparser.parse();
         option.wsPaddingParenthesesInFunction = true;
        option.beStyleFunctionRightBEOnNewline = false;
         String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION sales.Fn_salesbystore ( @storeid INT ) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN 0;"));

        sqlparser.parse();
        option.wsPaddingParenthesesInFunction = false;
       option.beStyleFunctionRightBEOnNewline = false;
        result = FormatterFactory.pp(sqlparser, option);
       // System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION sales.Fn_salesbystore (@storeid INT) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN 0;"));
     }

    public static void testWSPadding_ParenthesesInExpression(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SELECT ( ( ( a - b) - c)) FROM   t ";
         sqlparser.parse();
         option.wsPaddingParenthesesInExpression = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT ( ( ( a - b ) - c ) )\n" +
                "FROM   t"));

        //System.out.println(result);

        sqlparser.parse();
        option.wsPaddingParenthesesInExpression = false;
        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT (((a - b) - c))\n" +
                "FROM   t"));

       // System.out.println(result);
     }

    public static void testWSPadding_ParenthesesOfSubQuery(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SELECT last_name \n" +
                 "FROM   employees \n" +
                 "WHERE  salary > ( SELECT salary \n" +
                 "                 FROM   employees \n" +
                 "                 WHERE  last_name = 'Abel');";
         sqlparser.parse();
         option.wsPaddingParenthesesOfSubQuery = true;

         String result = FormatterFactory.pp(sqlparser, option);

        assertTrue(result.trim().equalsIgnoreCase("SELECT last_name\n" +
                "FROM   employees\n" +
                "WHERE  salary > ( SELECT salary\n" +
                "                  FROM   employees\n" +
                "                  WHERE  last_name = 'Abel' );"));

        //System.out.println(result);

        sqlparser.parse();
        option.wsPaddingParenthesesOfSubQuery = false;
        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT last_name\n" +
                "FROM   employees\n" +
                "WHERE  salary > (SELECT salary\n" +
                "                 FROM   employees\n" +
                "                 WHERE  last_name = 'Abel');"));
        //System.out.println(result);
     }

    public static void testWSPadding_ParenthesesInFunctionCall(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SELECT department_id,\n" +
                 "       Min( salary) \n" +
                 "FROM   employees \n" +
                 "GROUP  BY department_id";
         sqlparser.parse();

        option.wsPaddingParenthesesInFunctionCall = true;
        String result = FormatterFactory.pp(sqlparser, option);

        assertTrue(result.trim().equalsIgnoreCase("SELECT   department_id,\n" +
                "         Min( salary )\n" +
                "FROM     employees\n" +
                "GROUP BY department_id"));
         //System.out.println(result);

        sqlparser.parse();
        option.wsPaddingParenthesesInFunctionCall = false;
        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   department_id,\n" +
                "         Min(salary)\n" +
                "FROM     employees\n" +
                "GROUP BY department_id"));

        //System.out.println(result);
     }

    public static void testWSPadding_ParenthesesOfTypename(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE TABLE datatype \n" +
                 "  (fld0 GENERICTYPE,\n" +
                 "   fld1 CHAR( 2),\n" +
                 "   fld3 NCHAR( 1)); ";

        sqlparser.parse();

        option.wsPaddingParenthesesOfTypename = true;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue("wsPaddingParenthesesOfTypename = true not work",result.trim().equalsIgnoreCase("CREATE TABLE datatype(fld0 GENERICTYPE,\n" +
                "                      fld1 CHAR( 2 ),\n" +
                "                      fld3 NCHAR( 1 ));"));
        //System.out.println(result);

        sqlparser.parse();
        option.wsPaddingParenthesesOfTypename = false;
        result = FormatterFactory.pp(sqlparser, option);

        assertTrue("wsPaddingParenthesesOfTypename = false not work",result.trim().equalsIgnoreCase("CREATE TABLE datatype(fld0 GENERICTYPE,\n" +
                "                      fld1 CHAR(2),\n" +
                "                      fld3 NCHAR(1));"));
        //System.out.println(result);
     }

}
