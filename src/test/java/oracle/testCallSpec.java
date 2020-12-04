package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCallSpec;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testCallSpec extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE PROCEDURE plsToC_insertIntoEmpTab_proc (\n" +
                "   empno PLS_INTEGER)\n" +
                "AS LANGUAGE C\n" +
                "   NAME \"C_insertEmpTab\"\n" +
                "   LIBRARY insert_lib\n" +
                "   PARAMETERS (\n" +
                "      CONTEXT, \n" +
                "      empno);";
       // System.out.print(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure f = (TPlsqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(f.getProcedureName().toString().equalsIgnoreCase("plsToC_insertIntoEmpTab_proc"));
        TCallSpec spec = f.getCallSpec();
        assertTrue(spec.getLang().equalsIgnoreCase("C"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION func1 RETURN VARCHAR2\n" +
                "AS LANGUAGE C NAME \"func1\" LIBRARY lib1\n" +
                "WITH CONTEXT PARAMETERS(CONTEXT, x INT, y STRING, z OCIDATE);";
        // System.out.print(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction f = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(f.getFunctionName().toString().equalsIgnoreCase("func1"));
        TCallSpec spec = f.getCallSpec();
        assertTrue(spec.getLang().equalsIgnoreCase("C"));
        assertTrue(spec.getDeclaration().equalsIgnoreCase("\"func1\""));
        assertTrue(spec.getLibName().equalsIgnoreCase("lib1"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION func1 RETURN VARCHAR2\n" +
                "AS LANGUAGE C NAME \"func1\" LIBRARY lib1\n" +
                "WITH CONTEXT PARAMETERS(CONTEXT, x INT, y STRING, z OCIDATE);";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction f = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(f.getFunctionName().toString().equalsIgnoreCase("func1"));
        TCallSpec spec = f.getCallSpec();

//        for (int i=spec.getStartToken().posinlist;i<spec.getEndToken().posinlist+1;i++){
//            System.out.println(i+spec.getStartToken().container.get(i).toString());
//        }

        assertTrue(spec.getStartToken().container.get(16).toString().equalsIgnoreCase("LANGUAGE"));
        assertTrue(spec.getStartToken().container.get(50).toString().equalsIgnoreCase(")"));
    }

}
