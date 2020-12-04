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

public class testSelectList extends TestCase {

    public static void testSelect_Columnlist_Style(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

         sqlparser.sqltext = "select col1, col2,sum(col3) from table1";

        int ret = sqlparser.parse();
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName()+"."+new Exception().getStackTrace()[0].getMethodName());

        option.selectColumnlistStyle = TAlignStyle.AsWrapped;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("SELECT col1, col2,Sum(col3)\n" +
                "FROM   table1"));

        sqlparser.parse();
        option.selectColumnlistStyle = TAlignStyle.AsStacked;
        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("SELECT col1,\n" +
                "       col2,\n" +
                "       Sum(col3)\n" +
                "FROM   table1"));
    }

    public static void testSelect_Columnlist_Comma(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "select col1, col2,sum(col3) from table1";

        sqlparser.parse();
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName()+"."+new Exception().getStackTrace()[0].getMethodName());

        option.selectColumnlistComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("SELECT col1\n" +
                "       , col2\n" +
                "       , Sum(col3)\n" +
                "FROM   table1"));

       // System.out.println(result);

    }

    public static void testSelectItemInNewLine(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "select col1, col2,sum(col3) from table1";

        sqlparser.parse();
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName()+"."+new Exception().getStackTrace()[0].getMethodName());

        option.selectItemInNewLine = true;
        String result = FormatterFactory.pp(sqlparser, option);
       // System.out.println(result);
        assertTrue(result.equalsIgnoreCase("SELECT\n" +
                "  col1,\n" +
                "  col2,\n" +
                "  Sum(col3)\n" +
                "FROM   table1"));

        TGSqlParser sqlparser2 = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser2.sqltext = "select top 10 col1 as b, col2222 as c,sum(col3) as d from table1";
        option.selectItemInNewLine = true;
        option.alignAliasInSelectList = true;
        sqlparser2.parse();
        result = FormatterFactory.pp(sqlparser2, option);

        //System.out.println(result);
        assertTrue("selectItemInNewLine not work",result.equalsIgnoreCase("SELECT top 10\n" +
                "  col1      AS b,\n" +
                "  col2222   AS c,\n" +
                "  Sum(col3) AS d\n" +
                "FROM   table1"));
    }

    public static void testAlignAliasInSelectList(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "select col1 as b, col2222 as c,sum(col3) as d from table1";

        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName()+"."+new Exception().getStackTrace()[0].getMethodName());

        option.alignAliasInSelectList = false;
        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("SELECT col1 AS b,\n" +
                "       col2222 AS c,\n" +
                "       Sum(col3) AS d\n" +
                "FROM   table1"));

        sqlparser.parse();
        option.alignAliasInSelectList = true;

        result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.equalsIgnoreCase("SELECT col1      AS b,\n" +
                "       col2222   AS c,\n" +
                "       Sum(col3) AS d\n" +
                "FROM   table1"));
        //System.out.println(result);

    }

    /**
     * Not support  option.treatDistinctAsVirtualColumn yet
     */
    public static void testTreatDistinctAsVirtualColumn(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName()+"."+new Exception().getStackTrace()[0].getMethodName());
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "select distinct col1 as b, col2222 as c,sum(col3) as d from table1";

        option.treatDistinctAsVirtualColumn = true;
        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
        // System.out.println(result);
       assertTrue(result.trim().equalsIgnoreCase("SELECT DISTINCT \n" +
               "       col1      AS b,\n" +
               "       col2222   AS c,\n" +
               "       Sum(col3) AS d\n" +
               "FROM   table1"));
    }

}
