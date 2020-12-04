package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testOrderByClause extends TestCase {
    public static void testSelect_Groupby_Style(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "SELECT e.employee_id,\n" +
                 "       d.locatioin_id \n" +
                 "FROM   employees e,departments d \n" +
                 "order by e.employee_id,d.locatioin_id,d.locatioin_id2 ";

         sqlparser.parse();
         option.selectColumnlistStyle = TAlignStyle.AsStacked;
         String result = FormatterFactory.pp(sqlparser, option);

         assertTrue(result.trim().equalsIgnoreCase("SELECT   e.employee_id,\n" +
                 "         d.locatioin_id\n" +
                 "FROM     employees e,\n" +
                 "         departments d\n" +
                 "ORDER BY e.employee_id,\n" +
                 "         d.locatioin_id,\n" +
                 "         d.locatioin_id2"));
         //System.out.println(result);
     }

    public static void testSelect_Columnlist_Comma(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "SELECT e.employee_id,\n" +
                 "       d.locatioin_id \n" +
                 "FROM   employees e,departments d \n" +
                 "order by e.employee_id,d.locatioin_id,d.locatioin_id2 ";

         sqlparser.parse();
         option.selectColumnlistStyle = TAlignStyle.AsStacked;
         option.selectColumnlistComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("SELECT   e.employee_id\n" +
                 "         , d.locatioin_id\n" +
                 "FROM     employees e,\n" +
                 "         departments d\n" +
                 "ORDER BY e.employee_id\n" +
                 "         , d.locatioin_id\n" +
                 "         , d.locatioin_id2"));

         //System.out.println(result);
     }

    public static void testSelectItemInNewLine(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "SELECT e.employee_id,\n" +
                 "       d.locatioin_id \n" +
                 "FROM   employees e,departments d \n" +
                 "order by e.employee_id,d.locatioin_id,d.locatioin_id2 ";

         sqlparser.parse();
         option.selectItemInNewLine = true;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("SELECT  \n" +
                 "  e.employee_id,\n" +
                 "  d.locatioin_id\n" +
                 "FROM     employees e,\n" +
                 "         departments d\n" +
                 "ORDER BY\n" +
                 "  e.employee_id,\n" +
                 "  d.locatioin_id,\n" +
                 "  d.locatioin_id2"));
         //System.out.println(result);
     }
}
