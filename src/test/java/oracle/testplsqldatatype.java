package oracle;
/*
 * Date: 13-1-14
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.stmt.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlTableTypeDefStmt;
import junit.framework.TestCase;

public class testplsqldatatype extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE \"PROC4\"( \"A1\" IN NUMBER, \"A2\" NUMBER ) IS\n" +
                "\tf0 NATURAL;\n" +
                "\tg1 NATURALN;\n" +
                "\th2 POSITIVE;\n" +
                "\ti3 POSITIVEN;\n" +
                "\tj4 SIGNTYPE;\n" +
                "\tk5 SIMPLE_INTEGER := 2147483645;\n" +
                "\tab6 ROWID;\n" +
                "\tac7 UROWID;\n" +
                "\taf8 STRING(10);\n" +
                "\tag9 BOOLEAN;\n" +
                "\tah10 DATE;\n" +
                "\tah11 NVARCHAR(10);\n" +
                "\tam  INTERVAL DAY(3) TO SECOND(3);\n" +
                "BEGIN\n" +
                "  NULL;\n" +
                "END;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure = (TPlsqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TStatementList declares = createProcedure.getDeclareStatements();
        //System.out.println(declares.get(0).sqlstatementtype);
        TVarDeclStmt varDeclStmt0 = (TVarDeclStmt)declares.get(0);
        TVarDeclStmt varDeclStmt1 = (TVarDeclStmt)declares.get(1);
        TVarDeclStmt varDeclStmt2 = (TVarDeclStmt)declares.get(2);
        TVarDeclStmt varDeclStmt3 = (TVarDeclStmt)declares.get(3);
        TVarDeclStmt varDeclStmt4 = (TVarDeclStmt)declares.get(4);
        TVarDeclStmt varDeclStmt5 = (TVarDeclStmt)declares.get(5);
        TVarDeclStmt varDeclStmt6 = (TVarDeclStmt)declares.get(6);
        TVarDeclStmt varDeclStmt7 = (TVarDeclStmt)declares.get(7);
        TVarDeclStmt varDeclStmt8 = (TVarDeclStmt)declares.get(8);
        TVarDeclStmt varDeclStmt9 = (TVarDeclStmt)declares.get(9);
        TVarDeclStmt varDeclStmt10 = (TVarDeclStmt)declares.get(10);
        TVarDeclStmt varDeclStmt11 = (TVarDeclStmt)declares.get(11);
        assertTrue(varDeclStmt0.getDataType().getDataType() == EDataType.natural_t);
        assertTrue(varDeclStmt1.getDataType().getDataType() == EDataType.naturaln_t);
        assertTrue(varDeclStmt2.getDataType().getDataType() == EDataType.positive_t);
        assertTrue(varDeclStmt3.getDataType().getDataType() == EDataType.positiven_t);
        assertTrue(varDeclStmt4.getDataType().getDataType() == EDataType.signtype_t);
        assertTrue(varDeclStmt5.getDataType().getDataType() == EDataType.simple_integer_t);
        assertTrue(varDeclStmt6.getDataType().getDataType() == EDataType.rowid_t);
        assertTrue(varDeclStmt7.getDataType().getDataType() == EDataType.urowid_t);
        assertTrue(varDeclStmt8.getDataType().getDataType() == EDataType.string_t);
        assertTrue(varDeclStmt9.getDataType().getDataType() == EDataType.boolean_t);
        assertTrue(varDeclStmt10.getDataType().getDataType() == EDataType.date_t);
        assertTrue(varDeclStmt11.getDataType().getDataType() == EDataType.nvarchar_t);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE TESTPROC1( A1 NUMBER )\n" +
                "IS\n" +
                "TYPE TYP1 IS TABLE OF PLS_INTEGER INDEX BY VARCHAR2(64);\n" +
                "BEGIN\n" +
                "NULL;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure = (TPlsqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TStatementList declares = createProcedure.getDeclareStatements();
        TPlsqlTableTypeDefStmt varDeclStmt0 = (TPlsqlTableTypeDefStmt)declares.get(0);
        assertTrue(varDeclStmt0.getIndexByDataType().getDataType() == EDataType.varchar2_t);
        //System.out.println(varDeclStmt0.getIndexByDataType().getDataType());
    }
}
