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

public class testFromClause extends TestCase {

    public static void testSelect_fromclause_Style(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "SELECT last_name,\n" +
                "       department_name dept_name \n" +
                "FROM   employees,\n" +
                "       departments;  ";

        sqlparser.parse();
        option.selectFromclauseStyle = TAlignStyle.AsWrapped;
        String result = FormatterFactory.pp(sqlparser, option);
      //  System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("SELECT last_name,\n" +
                "       department_name dept_name\n" +
                "FROM   employees, departments;"));
    }

    public static void testSelect_fromclause_Comma(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "SELECT last_name,\n" +
                "       department_name dept_name \n" +
                "FROM   employees,\n" +
                "       departments;  ";


        sqlparser.parse();
        option.selectFromclauseComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT last_name,\n" +
                "       department_name dept_name\n" +
                "FROM   employees\n" +
                "       , departments;"));
        //System.out.println(result);
    }

    public static void testFromClauseInNewLine(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "SELECT last_name,\n" +
                "       department_name dept_name \n" +
                "FROM   employees,\n" +
                "       departments;  ";


        sqlparser.parse();
        option.fromClauseInNewLine = true;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT last_name,\n" +
                "       department_name dept_name\n" +
                "FROM  \n" +
                "  employees,\n" +
                "  departments;"));
        //System.out.println(result);

    }

    public static void testSelect_FromclauseJoinOnInNewline(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "SELECT p.name AS product,\n" +
                "       p.listprice AS 'List Price',\n" +
                "       p.discount AS 'discount' \n" +
                "FROM   \n" +
                "  production.product p \n" +
                "  JOIN production.productsubcategory s \n" +
                "    ON p.productsubcategoryid = s.productsubcategoryid \n" +
                "WHERE  s.name LIKE @product \n" +
                "       AND p.listprice < @maxprice;";

        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        option.selectFromclauseJoinOnInNewline = false;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT p.name      AS product,\n" +
                "       p.listprice AS 'List Price',\n" +
                "       p.discount  AS 'discount'\n" +
                "FROM   production.product p\n" +
                "       JOIN production.productsubcategory s ON p.productsubcategoryid = s.productsubcategoryid\n" +
                "WHERE  s.name LIKE @product\n" +
                "       AND p.listprice < @maxprice;"));


    }

    public static void testAlignJoinWithFromKeyword(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "SELECT p.name AS product,\n" +
                "       p.listprice AS 'List Price',\n" +
                "       p.discount AS 'discount' \n" +
                "FROM   \n" +
                "  production.product p \n" +
                "  JOIN production.productsubcategory s \n" +
                "    ON p.productsubcategoryid = s.productsubcategoryid \n" +
                "WHERE  s.name LIKE @product \n" +
                "       AND p.listprice < @maxprice;";


        assertTrue(sqlparser.parse() == 0);
        option.alignJoinWithFromKeyword = true;
        String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("SELECT p.name      AS product,\n" +
                "       p.listprice AS 'List Price',\n" +
                "       p.discount  AS 'discount'\n" +
                "FROM   production.product p\n" +
                "JOIN   production.productsubcategory s\n" +
                "       ON p.productsubcategoryid = s.productsubcategoryid\n" +
                "WHERE  s.name LIKE @product\n" +
                "       AND p.listprice < @maxprice;"));
    }

}
