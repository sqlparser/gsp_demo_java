package gudusoft.gsqlparser.commonTest;
/*
 * Date: 2010-8-17
 * Time: 10:45:34
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import junit.framework.TestCase;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testToString extends TestCase {

    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "select t1.f1, t2.f2 as f2 from table1 t1 left join table2 t2 on t1.f1 = t2.f2 ";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("t1.f1"));
        assertTrue(select.getResultColumnList().getResultColumn(1).toString().equalsIgnoreCase("t2.f2"));
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("table1"));
        assertTrue(select.tables.getTable(1).toString().equalsIgnoreCase("table2"));
        assertTrue(select.joins.getJoin(0).toString().equalsIgnoreCase("table1 t1 left join table2 t2 on t1.f1 = t2.f2"));
        //System.out.println(select.joins.getJoin(0).toString());
    }

    public void test2(){
        parser.sqltext = "SELECT R.* FROM REGIONS R";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT R.* FROM REGIONS R"));
    }


    public void testComment(){
        String sqlText="--comment \nIF Object_id('Object') IS NOT NULL" +
                             " BEGIN --comments \n" +
                             " --comments \n DROP PROCEDURE proc1"+
                             " \n --comments\n END --comments";
          TGSqlParser sqlparser=new TGSqlParser(EDbVendor.dbvmssql);

       // System.out.println(sqlText);

        //System.out.println("------ end of origin ----");
         StringBuilder sb = new StringBuilder(1024);

         try {
         sqlparser.setSqltext(sqlText);
         int success=sqlparser.parse();
         if(success==0){
              TStatementList sqlstmts=sqlparser.sqlstatements;
             for(int  i=0;i<sqlstmts.size();i++){
                 TCustomSqlStatement customSQL=sqlstmts.get(i);
                 if (i == 0){//first statement
                     if (customSQL.getStartToken() != sqlparser.sourcetokenlist.get(0)){
                         for(int j=0;j<customSQL.getStartToken().posinlist;j++){
                             //System.out.print(sqlparser.sourcetokenlist.get(j).toString());
                             sb.append(sqlparser.sourcetokenlist.get(j).toString());
                         }
                     }
                 }

                 if (i == sqlstmts.size()-1){//last statement
                     //System.out.print(customSQL.toString());
                     sb.append(customSQL.toString());
                     if (customSQL.getEndToken() != sqlparser.sourcetokenlist.get(sqlparser.sourcetokenlist.size()-1)){
                         for(int j=customSQL.getEndToken().posinlist+1;j<sqlparser.sourcetokenlist.size();j++){
                             //System.out.print(sqlparser.sourcetokenlist.get(j).toString());
                             sb.append(sqlparser.sourcetokenlist.get(j).toString());
                         }
                     }
                 }else {
                     //System.out.println(customSQL.toString());
                     sb.append(customSQL.toString()+ TBaseType.linebreak);
                 }
             }
            // System.out.println(sb.toString());
             //System.out.println(sqlText);
             //assertTrue(sb.toString().equalsIgnoreCase(sqlText.trim()));
         }else{
              System.err.println("Number of Errors: "+sqlparser.getErrorCount()+"\nError Message: "+sqlparser.getErrormessage());

         }
         }catch (Exception e) {
              //Ignore
          }


    }

    public void testSpaceBefroreNewline(){
        parser.sqltext = "SELECT A FROM B WHERE C = 3 \r\nAND D = 4 \r\nAND E = 5";
        assertTrue(parser.parse() == 0);
        //System.out.print(parser.sqlstatements.get(0).toString());
       // assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT R.* FROM REGIONS R"));
    }


    public void testIncludingCommentOption(){
        TGSqlParser sqlparser=new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SEL         MPK,  PI_DEALERA, PI_EKSPERTA, PI_DEALERA_MICRO\n" +
                "FROM   DB_WI/*TMP_DSP*/.OPICS_ALIOR_DEALER_ODDZIAL/*0630*/ WHERE\n" +
                "AKT_HD = 1;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).toString().equalsIgnoreCase("SEL         MPK,  PI_DEALERA, PI_EKSPERTA, PI_DEALERA_MICRO\n" +
                "FROM   DB_WI/*TMP_DSP*/.OPICS_ALIOR_DEALER_ODDZIAL/*0630*/ WHERE\n" +
                "AKT_HD = 1;"));

        sqlparser.sqlstatements.get(0).setIncludingComment(false);
        assertTrue(sqlparser.sqlstatements.get(0).toString().equalsIgnoreCase("SEL         MPK,  PI_DEALERA, PI_EKSPERTA, PI_DEALERA_MICRO\n" +
                "FROM   DB_WI.OPICS_ALIOR_DEALER_ODDZIAL WHERE\n" +
                "AKT_HD = 1;"));

        // assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT R.* FROM REGIONS R"));
    }

    public void testTeradataCreateProcedure(){
        TGSqlParser sqlparser=new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE SP_Employee ()\n" +
                "\n" +
                "BEGIN\n" +
                "  \n" +
                "    INSERT INTO Employee (EmpName, EmpNo, DeptNo )\n" +
                "    VALUES ('abc', 101, 12345);\n" +
                "  \n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);
        TCreateProcedureStmt procedure = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        TInsertSqlStatement insert = (TInsertSqlStatement)procedure.getStatements().get(0);
        assertTrue(insert.getTargetTable().getName().equalsIgnoreCase("Employee"));
        assertTrue(insert.toString().equalsIgnoreCase("INSERT INTO Employee (EmpName, EmpNo, DeptNo )\n" +
                "    VALUES ('abc', 101, 12345)"));

    }

    public void testCreateAggregate(){
        TGSqlParser sqlparser=new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "create procedure sys.sp_SetAutoSAPasswordAndDisable\n" +
                "as\n" +
                "CREATE AGGREGATE Concatenate(@input nvarchar(4000))\n" +
                "RETURNS nvarchar(4000)\n" +
                "EXTERNAL NAME [StringUtilities].[Microsoft.Samples.SqlServer.Concatenate];";
        assertTrue(sqlparser.parse() == 0);
        TMssqlCreateProcedure procedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(procedure.getStatements().get(0).toString().equalsIgnoreCase("CREATE AGGREGATE Concatenate(@input nvarchar(4000))\n" +
                "RETURNS nvarchar(4000)\n" +
                "EXTERNAL NAME [StringUtilities].[Microsoft.Samples.SqlServer.Concatenate]") );
    }


    public void testRedshiftSubquery(){
        TGSqlParser sqlparser=new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "CREATE table stg213_bw4_finance_universal_journal as \n" +
                "SELECT\n" +
                "\t\tNULLIF(TRIM(AC_LEDGER),'')\tas\tledger_code\n" +
                "\t\t,NULLIF(TRIM(COMP_CODE),'')\tas\tcompany_code\n" +
                "\t\t,NULLIF(TRIM(FISCYEAR),'')::integer\tas\tfiscal_year\n" +
                "\t\t,NULLIF(TRIM(FISCVARNT),'')\tas\tfiscal_year_variant\n" +
                "\t\t,NULLIF(TRIM(AC_DOC_NO),'')\tas\taccounting_document_number\n" +
                "\t\t,NULLIF(LTRIM(TRIM(AC_DOC_LN),'0'),'')\tas\taccounting_document_line_item\n" +
                "\t\t,NULLIF(TRIM(RECORDMODE),'')\tas\trecord_mode\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZBFC_MJAI\"),'')\tas\tbfc_major_ai\n" +
                "\t\t,NULLIF(TRIM(CLR_DOC_NO),'')\tas\tclearing_document_number\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZDESTID\"),'')\tas\tgeography\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZREPRTUNT\"),'')\tas\told_reporting_unit\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZNWRPUNT\"),'')\tas\treporting_unit\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZOLDCMPCD\"),'')\tas\told_company_code\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZMATCAT\"),'')\tas\tproduct_category\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZMAT_FLAG\"),'')\tas\tmaterial_record_type\n" +
                "\t\t,CASE \n" +
                "\t\t\tWHEN NULLIF(TRIM(BAL_FLAG),'') = 'X' THEN TRUE\n" +
                "\t\t\tELSE FALSE \n" +
                "\t\tEND as\tbalancesheet_flag\n" +
                "\t\t,NULLIF(TRIM(\"/BIC/ZLDAISRG\"),'')\tas\tleadai_srg\n" +
                "\t\t,NULLIF(LTRIM(TRIM(\"/BIC/ZMATERIAL\"),'0'),'')\tas\tmaterial_id\n" +
                "\t\t,NULLIF(TRIM(COUNTRY),'')\tas\tcountry;";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement subquery = createTable.getSubQuery();
        assertTrue(subquery.toString() != null);
    }

}