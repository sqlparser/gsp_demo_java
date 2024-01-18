package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testFunctionCall extends TestCase {
	
	public static void testCurrentDate1() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.alignAliasInSelectList = false;
		option.caseKeywords = TCaseOption.CoUppercase;
		option.caseIdentifier = TCaseOption.CoNoChange;
		option.caseFuncname = TCaseOption.CoUppercase;
		option.caseDatatype = TCaseOption.CoUppercase;
		option.caseWhenThenInSameLine = true;
		// http://www.dpriver.com/ppv3/whitespace_padding.php
		option.wsPaddingOperatorArithmetic = true;
		option.wsPaddingParenthesesInFunction = false;
		option.wsPaddingParenthesesInFunctionCall = false;
		option.wsPaddingParenthesesOfSubQuery = false;
		option.beStyleCreatetableLeftBEOnNewline = true;
		option.beStyleBlockIndentSize = 0;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
		sqlparser.sqltext = "SELECT case when CURRENT_DATE()> current_date then TRUE else FALSE end as status";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("SELECT CASE\n"
				+ "         WHEN CURRENT_DATE() > CURRENT_DATE THEN TRUE\n"
				+ "         ELSE FALSE\n"
				+ "       END AS status"));
		// System.out.println(result);
	}
	
	public static void testCurrentDate2() {
		GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "."
				+ new Exception().getStackTrace()[0].getMethodName());
		option.alignAliasInSelectList = false;
		option.caseKeywords = TCaseOption.CoUppercase;
		option.caseIdentifier = TCaseOption.CoNoChange;
		option.caseFuncname = TCaseOption.CoUppercase;
		option.caseDatatype = TCaseOption.CoUppercase;
		option.caseWhenThenInSameLine = true;
		// http://www.dpriver.com/ppv3/whitespace_padding.php
		option.wsPaddingOperatorArithmetic = true;
		option.wsPaddingParenthesesInFunction = false;
		option.wsPaddingParenthesesInFunctionCall = false;
		option.wsPaddingParenthesesOfSubQuery = false;
		option.beStyleCreatetableLeftBEOnNewline = true;
		option.beStyleBlockIndentSize = 0;

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
		sqlparser.sqltext = "SELECT case when current_date> CURRENT_DATE() then TRUE else FALSE end as status";

		sqlparser.parse();
		String result = FormatterFactory.pp(sqlparser, option);
		assertTrue(result.trim().equalsIgnoreCase("SELECT CASE\n"
				+ "         WHEN CURRENT_DATE > CURRENT_DATE() THEN TRUE\n"
				+ "         ELSE FALSE\n"
				+ "       END AS status"));
		// System.out.println(result);
	}

   public static void testParameters(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        option.functionCallParametersStyle = TAlignStyle.AsStacked;
        option.functionCallParametersComma = TLinefeedsCommaOption.LfBeforeComma;

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SET @a = dbo.Func1(@param1,                   @param2,                   @param3 + 1,\n" +
                 "                   @param4)  ";

         sqlparser.parse();
         String result = FormatterFactory.pp(sqlparser, option);
       assertTrue(result.trim().equalsIgnoreCase("SET @a = dbo.Func1(@param1\n" +
               "                   ,@param2\n" +
               "                   ,@param3 + 1\n" +
               "                   ,@param4)"));
         //System.out.println(result);
     }

    public static void testDecode(){
          GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());
        option.functionCallParametersStyle = TAlignStyle.AsStacked;
        option.functionCallParametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;

          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
          sqlparser.sqltext = "SELECT last_name,\n" +
                  "       DECODE(job_id, 'It_prog', 1.10 * salary,                      'st_clerk', 1.15 * salary,                      'sa_rep', 1.20 * salary,\n" +
                  "                      salary) revised_salary\n" +
                  "FROM   employees;";

          sqlparser.parse();
          String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("SELECT last_name,\n" +
                 "       Decode(job_id\n" +
                 "              , 'It_prog'\n" +
                 "              , 1.10 * salary\n" +
                 "              , 'st_clerk'\n" +
                 "              , 1.15 * salary\n" +
                 "              , 'sa_rep'\n" +
                 "              , 1.20 * salary\n" +
                 "              , salary) revised_salary\n" +
                 "FROM   employees;"));
         // System.out.println(result);
      }

}
