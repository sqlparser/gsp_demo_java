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
    
    
	public static void testComment1() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
		sqlparser.sqltext = "select /* multi line comment */ * from t;";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("SELECT /* multi line comment */ *\nFROM   t;"));
	}
	
	public static void testComment2() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
		sqlparser.sqltext = "select a, f(b, 'c') d, e from Schema.Table where /*guid = 'aaaaaaaaaaaaaaaaaaaa' AND*/ start_dt >= 1140310;";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("SELECT a,\n"
				+ "       F(b, 'c') d,\n"
				+ "       e\n"
				+ "FROM   SCHEMA.Table\n"
				+ "WHERE  /*guid = 'aaaaaaaaaaaaaaaaaaaa' AND*/ start_dt >= 1140310;"));
	}
	
	public static void testComment3() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
		sqlparser.sqltext = "create or replace view view_set(RTRACE_HMY, RTRACE_KEY, RTRACE_RIP_FLAG)\n"
				+ "as (\n"
				+ "--===================================================\n"
				+ "--== Resident Tracebility Staging ==\n"
				+ "-- Created By : Ketan Goel\n"
				+ "-- Description : This procedure is to create resident\n"
				+ "-- tracebility union of Transer and non\n"
				+ "-- transfer tenants and roommates.\n"
				+ "-- Story : R360-351\n"
				+ "--===================================================\n"
				+ "select * from PARSER_AUTOMATION_DO_NOT_TOUCH.PUBLIC.EMPLOYEE1\n"
				+ "union\n"
				+ "select * from PARSER_AUTOMATION_DO_NOT_TOUCH.PUBLIC.EMPLOYEE2);";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("CREATE OR REPLACE VIEW view_set(RTRACE_HMY,\n"
				+ "                                RTRACE_KEY,\n"
				+ "                                RTRACE_RIP_FLAG) \n"
				+ "AS \n"
				+ "  ( SELECT *        \n"
				+ "--===================================================\n"
				+ "--== Resident Tracebility Staging ==\n"
				+ "-- Created By : Ketan Goel\n"
				+ "-- Description : This procedure is to create resident\n"
				+ "-- tracebility union of Transer and non\n"
				+ "-- transfer tenants and roommates.\n"
				+ "-- Story : R360-351\n"
				+ "--===================================================\n"
				+ "    FROM   PARSER_AUTOMATION_DO_NOT_TOUCH.PUBLIC.EMPLOYEE1\n"
				+ "    UNION\n"
				+ "    SELECT *\n"
				+ "    FROM   PARSER_AUTOMATION_DO_NOT_TOUCH.PUBLIC.EMPLOYEE2);"));
	}
	
	public static void testComment4() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = "select /* tt1 */ ss1.ca_county /* tt2 */, /* tt3 */ss1.d_year from source_table ss1";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("SELECT /* tt1 */ ss1.ca_county /* tt2 */,/* tt3 */\n"
				+ "                 ss1.d_year\n"
				+ "FROM   source_table ss1"));
	}
	
	
}
