package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignOption;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public static void testBEStyle_createtable_leftBEOnNewline(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
        option.beStyleCreatetableLeftBEOnNewline = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept\n" +
                "  (deptno NUMBER(2),\n" +
                "   dname  VARCHAR2(14),\n" +
                "   loc    VARCHAR2(13))"));
         //System.out.println(result);
     }

    public static void testBEStyle_createtable_rightBEOnNewline(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
        option.beStyleCreatetableRightBEOnNewline = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept(deptno NUMBER(2),\n" +
                "                  dname  VARCHAR2(14),\n" +
                "                  loc    VARCHAR2(13)\n" +
                ")"));
        //System.out.println(result);
     }

    public static void testCreatetable_ListitemInNewLine(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
        option.createtableListitemInNewLine = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept(\n" +
                "  deptno NUMBER(2),\n" +
                "  dname  VARCHAR2(14),\n" +
                "  loc    VARCHAR2(13))"));
        //System.out.println(result);
     }

    public static void testCreatetable_Fieldlist_Align_option(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
        option.createtableFieldlistAlignOption = TAlignOption.AloRight;
        option.beStyleCreatetableLeftBEOnNewline = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept\n" +
                "  (deptno NUMBER(2),\n" +
                "    dname VARCHAR2(14),\n" +
                "      loc VARCHAR2(13))"));
        //System.out.println(result);
     }

    public static void testDefaultAligntype(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
         option.defaultAligntype  = TAlignStyle.AsWrapped;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept(deptno NUMBER(2), dname VARCHAR2(14), loc VARCHAR2(13))"));
        //System.out.println(result);
     }

    public static void testDefaultCommaOption(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext = "CREATE TABLE dept(deptno NUMBER(2),\n" +
                 "                  dname  VARCHAR2(14),\n" +
                 "                  loc    VARCHAR2(13)) ";

         sqlparser.parse();
         option.defaultAligntype  = TAlignStyle.AsStacked;
         option.defaultCommaOption = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("CREATE TABLE dept(deptno  NUMBER(2)\n" +
                 "                  , dname VARCHAR2(14)\n" +
                 "                  , loc   VARCHAR2(13))"));
        //System.out.println(result);
     }

}
