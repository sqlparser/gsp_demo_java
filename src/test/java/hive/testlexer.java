package hive;

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: tako
 * Date: 13-7-21
 * Time: 下午12:16
 * To change this template use File | Settings | File Templates.
 */


public class testlexer extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "A=B";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(1).tokencode == TBaseType.hive_equal);
        assertTrue(sqlparser.sourcetokenlist.get(1).toString().equalsIgnoreCase("="));

        sqlparser.sqltext = "A==B";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(1).tokencode == TBaseType.hive_equal);
        assertTrue(sqlparser.sourcetokenlist.get(1).toString().equalsIgnoreCase("=="));

        sqlparser.sqltext = "A<=>B";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(1).tokencode == TBaseType.safe_equal);
        assertTrue(sqlparser.sourcetokenlist.get(1).toString().equalsIgnoreCase("<=>"));

        sqlparser.sqltext = "123L=123S=123Y";   //big/small/tiny int
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("123L"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_BigintLiteral);
        assertTrue(sqlparser.sourcetokenlist.get(2).toString().equalsIgnoreCase("123S"));
        assertTrue(sqlparser.sourcetokenlist.get(2).tokencode == TBaseType.hive_SmallintLiteral);
        assertTrue(sqlparser.sourcetokenlist.get(4).toString().equalsIgnoreCase("123Y"));
        assertTrue(sqlparser.sourcetokenlist.get(4).tokencode == TBaseType.hive_TinyintLiteral);

        sqlparser.sqltext = "12.3BD=1.23e-10=10E+2";   //big/small/tiny int
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("12.3BD"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_DecimalLiteral);
        assertTrue(sqlparser.sourcetokenlist.get(2).toString().equalsIgnoreCase("1.23e-10"));
        assertTrue(sqlparser.sourcetokenlist.get(2).tokencode== TBaseType.hive_number);
        assertTrue(sqlparser.sourcetokenlist.get(4).toString().equalsIgnoreCase("10E+2"));
        assertTrue(sqlparser.sourcetokenlist.get(4).tokencode== TBaseType.hive_number);

//
        sqlparser.sqltext = "12B=123b=10K";   //big/small/tiny int
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("12B"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_ByteLengthLiteral);
        assertTrue(sqlparser.sourcetokenlist.get(2).toString().equalsIgnoreCase("123b"));
        assertTrue(sqlparser.sourcetokenlist.get(2).tokencode== TBaseType.hive_ByteLengthLiteral);
        assertTrue(sqlparser.sourcetokenlist.get(4).toString().equalsIgnoreCase("10K"));
        assertTrue(sqlparser.sourcetokenlist.get(4).tokencode== TBaseType.hive_ByteLengthLiteral);
//
//
        sqlparser.sqltext = "`abc*`";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("`abc*`"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.ident);
//
        sqlparser.sqltext = "0XA9F7";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("0XA9F7"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_CharSetLiteral);
//
        sqlparser.sqltext = "'a\\'b'";//"'a\\'b"+""+"'";
        sqlparser.tokenizeSqltext();
       // System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("'a\\'b'"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_StringLiteral);
//
        sqlparser.sqltext = "\"a\\\"'b\"";//"a\"'b"
        sqlparser.tokenizeSqltext();
       //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("\"a\\\"'b\""));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_StringLiteral);
//
        sqlparser.sqltext = "_as-._:k";
        sqlparser.tokenizeSqltext();
       //System.out.println(sqlparser.sqltext);
        //System.out.println(sqlparser.sourcetokenlist.get(0).toString());
        //System.out.println(sqlparser.sourcetokenlist.get(1).toString());
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("_as-._:k"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.hive_CharSetName);
//
        sqlparser.sqltext = "asme=9ss=ax_x_";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("asme"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.ident);
        assertTrue(sqlparser.sourcetokenlist.get(2).toString().equalsIgnoreCase("9ss"));
        assertTrue(sqlparser.sourcetokenlist.get(2).tokencode == TBaseType.ident);
        assertTrue(sqlparser.sourcetokenlist.get(4).toString().equalsIgnoreCase("ax_x_"));
        assertTrue(sqlparser.sourcetokenlist.get(4).tokencode == TBaseType.ident);
//
        sqlparser.sqltext = "where";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("where"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.rrw_where);
//
        sqlparser.sqltext = "-- where";
        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("-- where"));
        assertTrue(sqlparser.sourcetokenlist.get(0).tokencode == TBaseType.cmtdoublehyphen);
    }

}
