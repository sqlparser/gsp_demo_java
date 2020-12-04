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

public class testIntoClause extends TestCase {

    /**
     * gFmtOpt.IntoClauseInNewline not implemented
     * No need to implement in this version as it not in document before.
     */
   public static void testSelectIntoClause(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select col1, col2,sum(col3) INTO  Persons_backup from table1";
        sqlparser.parse();

        String result = FormatterFactory.pp(sqlparser, option);
       //assertTrue("gFmtOpt.IntoClauseInNewline not implemented",false);
     //   System.out.println("gFmtOpt.IntoClauseInNewline not implemented");
    }


}
