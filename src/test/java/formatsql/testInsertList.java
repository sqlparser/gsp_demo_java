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

public class testInsertList extends TestCase {

    public static void testInsert_Columnlist_Style(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "INSERT INTO employees\n" +
                "            (employee_id,\n" +
                "             first_name,\n" +
                "             department_id) VALUES     (113,            NULL,            100);";

        option.insertColumnlistStyle = TAlignStyle.AsWrapped;
        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("INSERT INTO employees\n" +
                "            (employee_id, first_name, department_id)\n" +
                "VALUES      (113,\n" +
                "             NULL,\n" +
                "             100);"));
        //System.out.println(result);
    }

    public static void testInsert_Valuelist_Style(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "INSERT INTO employees\n" +
                "            (employee_id,\n" +
                "             first_name,\n" +
                "             department_id) VALUES     (113,            NULL,            100);";

        option.insertValuelistStyle = TAlignStyle.AsWrapped;
        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("INSERT INTO employees\n" +
                "            (employee_id,\n" +
                "             first_name,\n" +
                "             department_id)\n" +
                "VALUES      (113, NULL, 100);"));
        //System.out.println(result);
    }

    public static void testDefaultCommaOption(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "INSERT INTO employees\n" +
                "            (employee_id,\n" +
                "             first_name,\n" +
                "             department_id) VALUES     (113,            NULL,            100);";

        option.defaultCommaOption = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        option.insertColumnlistStyle = TAlignStyle.AsStacked;
        option.insertValuelistStyle = TAlignStyle.AsStacked;
        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("INSERT INTO employees\n" +
                "            (employee_id\n" +
                "             , first_name\n" +
                "             , department_id)\n" +
                "VALUES      (113\n" +
                "             , NULL\n" +
                "             , 100);"));
       // System.out.println(result);
    }

}
