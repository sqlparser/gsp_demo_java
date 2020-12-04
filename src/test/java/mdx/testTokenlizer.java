package mdx;
/*
 * Date: 11-12-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import junit.framework.TestCase;

public class testTokenlizer extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT Measures.MEMBERS ON COLUMNS,\n" +
                "\n" +
                "Product.Style.CHILDREN ON ROWS\n" +
                "\n" +
                "FROM [Adventure Works] \n" +
                ";";
        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            //System.out.printf("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "Measures,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "MEMBERS,type:ttkeyword,code:630\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "COLUMNS,type:ttkeyword,code:559\n" +
                ",,type:ttcomma,code:44\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "Product,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "Style,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "CHILDREN,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ROWS,type:ttkeyword,code:661\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Adventure Works],type:ttidentifier,code:282\n" +
                " ,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT Measures.MEMBERS ON COLUMNS,\n" +
                "Product.Style.CHILDREN ON ROWS\n" +
                "FROM [Total Profit [Domestic]]]\t\n" +
                ";";
        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "Measures,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "MEMBERS,type:ttkeyword,code:630\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "COLUMNS,type:ttkeyword,code:559\n" +
                ",,type:ttcomma,code:44\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "Product,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "Style,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "CHILDREN,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ROWS,type:ttkeyword,code:661\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Total Profit [Domestic]]],type:ttidentifier,code:282\n" +
                "\t,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT Measures.MEMBERS^ ON COLUMNS,\n" +
                "Product.Style.CHILDREN ON ROWS\n" +
                "FROM [Total Profit [Domestic]]]\t\n" +
                ";";
        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "Measures,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "MEMBERS,type:ttkeyword,code:630\n" +
                "^,type:ttcaret,code:94\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "COLUMNS,type:ttkeyword,code:559\n" +
                ",,type:ttcomma,code:44\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "Product,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "Style,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "CHILDREN,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ROWS,type:ttkeyword,code:661\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Total Profit [Domestic]]],type:ttidentifier,code:282\n" +
                "\t,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT Measures.MEMBERS ON COLUMNS,\n" +
                "Product.Style.CHILDREN ON ROWS\n" +
                "FROM [Adventure Works] \n" +
                "where Product.Style.CHILDREN >= 1\n" +
                ";";

        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "Measures,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "MEMBERS,type:ttkeyword,code:630\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "COLUMNS,type:ttkeyword,code:559\n" +
                ",,type:ttcomma,code:44\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "Product,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "Style,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "CHILDREN,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ROWS,type:ttkeyword,code:661\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Adventure Works],type:ttidentifier,code:282\n" +
                " ,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "where,type:ttkeyword,code:317\n" +
                " ,type:ttwhitespace,code:259\n" +
                "Product,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "Style,type:ttidentifier,code:264\n" +
                ".,type:ttperiod,code:46\n" +
                "CHILDREN,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                ">=,type:ttmulticharoperator,code:293\n" +
                " ,type:ttwhitespace,code:259\n" +
                "1,type:ttnumber,code:263\n" +
                "linebreak,type:ttreturn,code:260\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "// This member returns the gross profit margin for product types\n" +
                "// and reseller types crossjoined by year.\n" +
                "SELECT \n" +
                "    [Date].[Calendar Time].[Calendar Year].Members *\n" +
                "      [Reseller].[Reseller Type].Children ON 0,\n" +
                "    [Product].[Category].[Category].Members ON 1\n" +
                "FROM // Select from the Adventure Works cube.\n" +
                "    [Adventure Works]\n" +
                "WHERE\n" +
                "    [Measures].[Gross Profit Margin]\n" +
                ";";

        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("// This member returns the gross profit margin for product types,type:ttCPPComment,code:258\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "// and reseller types crossjoined by year.,type:ttCPPComment,code:258\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Date],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Calendar Time],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Calendar Year],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "Members,type:ttkeyword,code:630\n" +
                " ,type:ttwhitespace,code:259\n" +
                "*,type:ttasterisk,code:42\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Reseller],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Reseller Type],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "Children,type:ttkeyword,code:555\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "0,type:ttnumber,code:263\n" +
                ",,type:ttcomma,code:44\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Product],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Category],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Category],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "Members,type:ttkeyword,code:630\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "1,type:ttnumber,code:263\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "// Select from the Adventure Works cube.,type:ttCPPComment,code:258\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Adventure Works],type:ttidentifier,code:282\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "WHERE,type:ttkeyword,code:317\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Measures],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Gross Profit Margin],type:ttidentifier,code:282\n" +
                "linebreak,type:ttreturn,code:260\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test6(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT \n" +
                "[Date].[Calendar Year].&[2004] ON 0\n" +
                "FROM [Adventure Works];";

        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[Date],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "[Calendar Year],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "&[2004],type:ttidentifier,code:285\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "0,type:ttnumber,code:263\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Adventure Works],type:ttidentifier,code:282\n" +
                ";,type:ttsemicolon,code:59"));

    }

    public void test7(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "SELECT \n" +
                "[x].&foo&[1]&bar.[y] ON 0\n" +
                "FROM [Adventure Works];";

        sqlparser.tokenizeSqltext();
        String outstr="";
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            TSourceToken st = sqlparser.sourcetokenlist.get(i);
            outstr += String.format("%s,type:%s,code:%d\n",st.tokentype == ETokenType.ttreturn?"linebreak":st.toString(),st.tokentype,st.tokencode);
        }
       // System.out.println(outstr);
        assertTrue(outstr.trim().equalsIgnoreCase("SELECT,type:ttkeyword,code:301\n" +
                " ,type:ttwhitespace,code:259\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "[x],type:ttidentifier,code:282\n" +
                ".,type:ttperiod,code:46\n" +
                "&foo,type:ttidentifier,code:286\n" +
                "&[1],type:ttidentifier,code:285\n" +
                "&bar,type:ttidentifier,code:286\n" +
                ".,type:ttperiod,code:46\n" +
                "[y],type:ttidentifier,code:282\n" +
                " ,type:ttwhitespace,code:259\n" +
                "ON,type:ttkeyword,code:323\n" +
                " ,type:ttwhitespace,code:259\n" +
                "0,type:ttnumber,code:263\n" +
                "linebreak,type:ttreturn,code:260\n" +
                "FROM,type:ttkeyword,code:329\n" +
                " ,type:ttwhitespace,code:259\n" +
                "[Adventure Works],type:ttidentifier,code:282\n" +
                ";,type:ttsemicolon,code:59"));

    }

}
