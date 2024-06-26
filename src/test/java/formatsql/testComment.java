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

public class testComment extends TestCase {

    public static void testremove_comment(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        option.removeComment = true;

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "select department_id,\n" +
                 "       min( salary ) -- single line comment \n" +
                 "from   employees \n" +
                 "group  by department_id";

         sqlparser.parse();
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT   department_id,\n" +
                "         Min(salary)\n" +
                "FROM     employees\n" +
                "GROUP BY department_id"));
        //assertTrue("remove_comment is not supported",false);
        // System.out.println(result);
    }

    public static void testbegin_no_format(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE PROCEDURE uspnresults\n" +
                 "AS\n" +
                 "SELECT COUNT(contactid) FROM person.contact\n" +
                 "--begin_no_format\n" +
                 "SELECT COUNT(customerid) FROM\n" +
                 "sales.customer;\n" +
                 "--end_no_format\n" +
                 "GO";

         sqlparser.parse();
         String result = FormatterFactory.pp(sqlparser, option);
         //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE PROCEDURE uspnresults \n" +
                "AS \n" +
                "  SELECT Count(contactid)\n" +
                "  FROM   person.contact \n" +
                "--begin_no_format\n" +
                "SELECT Count(customerid) FROM\n" +
                "sales.customer;\n" +
                "--end_no_format \n" +
                "GO"));

    }

    public static void testcomma_with_comment(){
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
       option.removeComment = false;

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT                                                                                                                                            \n"
        		+ "\n"
        		+ "       OWNAMT as OWNAMT1--comment1   \n"
        		+ "\n"
        		+ "     , CMPYAMT as CMPYAMT1 --comment2  \n"
        		+ "\n"
        		+ "     , INSUAMT as INSUAMT1--comment3  \n"
        		+ "\n"
        		+ "  FROM AST.APADGNRL GNRL                                                                                                                                  \n"
        		+ "\n"
        		+ "     , AST.APAMACPT ACPT                                                                                                                                    \n"
        		+ "\n"
        		+ " WHERE ACPT.INSTCD       = GNRL.INSTCD";

        sqlparser.parse();
        String result = FormatterFactory.pp(sqlparser, option);
       assertTrue(result.trim().equalsIgnoreCase("SELECT OWNAMT  AS OWNAMT1,--comment1   \n"
       		+ "       CMPYAMT AS CMPYAMT1, --comment2  \n"
       		+ "       INSUAMT AS INSUAMT1--comment3  \n"
       		+ "FROM   AST.APADGNRL GNRL,\n"
       		+ "       AST.APAMACPT ACPT\n"
       		+ "WHERE  ACPT.INSTCD = GNRL.INSTCD"));
       //assertTrue("remove_comment is not supported",false);
       // System.out.println(result);
   }

}
