package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testIssues extends TestCase {

    public static void testI7L1MP(){
    	String statement = "select * from isb-cgc-cbq.TARGET_versioned.vcf_hg38_gdc_r22;";
        // Another sample query - SELECT * FROM ostk-gcp-ostkedwviews-prod.opd_views.item_daily_juice_fix where ostk_full_sku = '41053812-000-000' and partner_num = 96824;
        TGSqlParser gsp = new TGSqlParser(EDbVendor.dbvbigquery);
        gsp.setSqltext(statement);
        int errorCode = gsp.parse();

        GFmtOpt option = GFmtOptFactory.newInstance();
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

        String result = FormatterFactory.pp(gsp, option);

        assertTrue(result.trim().equalsIgnoreCase("SELECT *\n"
        		+ "FROM   isb-cgc-cbq.TARGET_versioned.vcf_hg38_gdc_r22;"));
     }
    
    public static void testI7ODIZ(){
        String statement = "SELECT * FROM test_query_ingestion.test1 WHERE test1_col3 > sysdate-2000;";
        // Another sample query for db type = dbvansi - create table valib.paylist (IdNum char(4), Gender char(1), Jobcode char(3), Salary num, Birth num);
        TGSqlParser gsp = new TGSqlParser(EDbVendor.dbvoracle);
        gsp.setSqltext(statement);
        int errorCode = gsp.parse();

        GFmtOpt option = GFmtOptFactory.newInstance();
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

        String result = FormatterFactory.pp(gsp, option);

        assertTrue(result.trim().equalsIgnoreCase("SELECT *\n"
        		+ "FROM   test_query_ingestion.test1\n"
        		+ "WHERE  test1_col3 > SYSDATE - 2000;"));
     }    
}
