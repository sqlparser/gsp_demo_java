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
				+ "  (         \n"
				+ "--===================================================\n"
				+ "--== Resident Tracebility Staging ==\n"
				+ "-- Created By : Ketan Goel\n"
				+ "-- Description : This procedure is to create resident\n"
				+ "-- tracebility union of Transer and non\n"
				+ "-- transfer tenants and roommates.\n"
				+ "-- Story : R360-351\n"
				+ "--===================================================\n"
				+ "            SELECT *\n"
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
	
	public static void testComment5() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = "CREATE or REPLACE VIEW GOLDMAN.PUBLIC.testview2 copy grants as (select * from GOLDMAN.PUBLIC.TABLE2) -- single-line comment\n;";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("CREATE OR REPLACE VIEW GOLDMAN.PUBLIC.testview2 copy grants AS (SELECT * FROM GOLDMAN.PUBLIC.TABLE2)  -- single-line comment\n"
				+ "                                                                                                      ;"));
	}
	
	public static void testComment6() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = "CREATE or REPLACE VIEW GOLDMAN.PUBLIC.testview2 copy grants as (select * from GOLDMAN.PUBLIC.TABLE2) -- single-line comment;";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("CREATE OR REPLACE VIEW GOLDMAN.PUBLIC.testview2 copy grants AS (SELECT * FROM GOLDMAN.PUBLIC.TABLE2) -- single-line comment;"));
	}
	
	public static void testComment7() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = "-- singlie-line at the beginning \n select top 4 col1 --single-line comment in middle \n from testschema.testtable;";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("-- singlie-line at the beginning \n"
				+ "SELECT top 4 col1  --single-line comment in middle \n"
				+ "                   FROM testschema.testtable;"));
	}
	
	public static void testComment8() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.removeComment = false;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = "with person as ( -- reference query 7\n"
				+ " select c.id as person_id, c.name as -- reference query 8\n"
				+ " full_name from hive.extended_comments_test_db_hive2.Contacts as c -- reference query 8\n"
				+ " where -- reference query 8\n"
				+ " c.name like -- reference query 8\n"
				+ " 's%' -- reference query 6\n"
				+ " and ( -- reference query 8\n"
				+ " c.age > 18 -- reference query 8\n"
				+ " or -- reference query 8\n"
				+ " c.age < 12 -- reference query 8\n"
				+ " ) and c.id <= 122321213 union all -- reference query 8\n"
				+ " select l.id -- reference query 8\n"
				+ " as person_id,-- reference query 8\n"
				+ " l.name as fullname -- reference query 8\n"
				+ " from hive.extended_comments_test_db_hive2.Leads as l where l.name like 's%' ) -- reference query 9\n"
				+ " select person_id, full_name -- reference query 10\n"
				+ " from person where full_name like 's%' order by full_name; -- reference query 11";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase(
				  "WITH person\n"
				  + "     AS ( -- reference query 7\n"
				  + "          SELECT c.id   AS person_id,\n"
				  + "                c.name AS  -- reference query 8\n"
				  + "                           full_name\n"
				  + "         FROM   hive.extended_comments_test_db_hive2.Contacts AS c -- reference query 8\n"
				  + "         WHERE   -- reference query 8\n"
				  + "                 c.name LIKE  -- reference query 8\n"
				  + "                              's%' -- reference query 6\n"
				  + "                AND (  -- reference query 8\n"
				  + "                       c.age > 18 -- reference query 8\n"
				  + "                      OR  -- reference query 8\n"
				  + "                          c.age < 12 ) -- reference query 8\n"
				  + "                AND c.id <= 122321213\n"
				  + "         UNION ALL -- reference query 8\n"
				  + "         SELECT l.id  -- reference query 8  \n"
				  + "                        AS person_id, -- reference query 8\n"
				  + "                l.name AS fullname -- reference query 8\n"
				  + "         FROM   hive.extended_comments_test_db_hive2.Leads AS l\n"
				  + "         WHERE  l.name LIKE 's%')  -- reference query 9\n"
				  + "  SELECT   person_id,\n"
				  + "           full_name -- reference query 10\n"
				  + "  FROM     person\n"
				  + "  WHERE    full_name LIKE 's%'\n"
				  + "  ORDER BY full_name; -- reference query 11"));
	}
	
	
}
