package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateProcedure;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ESqlStatementType.sstmssqldeclare;
import static gudusoft.gsqlparser.ESqlStatementType.sstteradatacreateprocedure;

public class testGetDiagnostics extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE PROCEDURE SIT07_PRES_BLC_PGM.P_BLC_EXECUTEANDLOG\n" +
                "\n" +
                "MAIN:\n" +
                "BEGIN\n" +
                "\n" +
                "DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                "BEGIN\n" +
                "\n" +
                "    SET VSQL_CODE = SQLCODE;\n" +
                "    SET VSQL_STATE = SQLSTATE;\n" +
                "    GET DIAGNOSTICS EXCEPTION 1 VERROR_TEXT = MESSAGE_TEXT;\n" +
                "    SET ORETURN_CODE = 2;\n" +
                "    EXECUTE VSQL_MSG;\n" +
                "END;\n" +
                "\n" +
                "END MAIN;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).sqlstatementtype == sstteradatacreateprocedure);
        TTeradataCreateProcedure procedure = (TTeradataCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(procedure.getProcedureName().toString().equalsIgnoreCase("SIT07_PRES_BLC_PGM.P_BLC_EXECUTEANDLOG"));
        assertTrue(procedure.getBodyStatements().size() == 1);
        assertTrue(procedure.getBodyStatements().get(0).sqlstatementtype == sstmssqldeclare);

    }

}