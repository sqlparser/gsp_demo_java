package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.oracle.TOracleCreateLibraryStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testCreatePackage extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create package pack as  \n" +
                "    PROCEDURE proc1 (\n" +
                "       p_Typ      IN char,\n" +
                "       p_ent  IN dw_k_mel.enr%type  default  0,\n" +
                "       p_deh IN dw_k_mel.meld_nr%type  default -1\n" +
                "    );\n" +
                " \n" +
                "    PROCEDURE proc2 (\n" +
                "       p_Typ  IN char,\n" +
                "       p_Text IN varchar2\n" +
                "    );\n" +
                " \n" +
                "    FUNCTION fun return dw_k_mel.en%type;\n" +
                " \n" +
                "    PROCEDURE proc3 (\n" +
                "       p_ent IN number\n" +
                "    );\n" +
                " \n" +
                " END pack;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreatePackage createPackage = (TPlsqlCreatePackage)sqlparser.sqlstatements.get(0);
        assertTrue(createPackage.getPackageName().toString().equalsIgnoreCase("pack"));
        assertTrue(createPackage.getDeclareStatements().size() == 4);
        TPlsqlCreateProcedure p0 = (TPlsqlCreateProcedure)createPackage.getDeclareStatements().get(0);

        assertTrue(p0.toString().equalsIgnoreCase("PROCEDURE proc1 (\n" +
                "       p_Typ      IN char,\n" +
                "       p_ent  IN dw_k_mel.enr%type  default  0,\n" +
                "       p_deh IN dw_k_mel.meld_nr%type  default -1\n" +
                "    )"));
        TPlsqlCreateFunction p2 = (TPlsqlCreateFunction) createPackage.getDeclareStatements().get(2);;
        assertTrue(p2.toString().equalsIgnoreCase("FUNCTION fun return dw_k_mel.en%type"));
    }
}
