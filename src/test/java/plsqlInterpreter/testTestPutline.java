package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;


public class testTestPutline extends TestCase {


    public void test1() {
        String expectedValue = "name=Smith";
        String inputSQL = "DECLARE\n" +
                "  name     VARCHAR(25) NOT NULL := 'Smith';\n" +
                "  surname  VARCHAR(25) := 'Jones';\n" +
                "BEGIN\n" +
                "  DBMS_OUTPUT.PUT_LINE('name=' || name);\n" +
                "  --DBMS_OUTPUT.PUT_LINE('surname=' || surname);\n" +
                "  --DBMS_OUTPUT.PUT_LINE('chr=' || chr(65));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }


    public void testCHR() {
        String expectedValue = "chr=A";
        String inputSQL = "DECLARE\n" +
                "  name     VARCHAR(25) NOT NULL := 'Smith';\n" +
                "  surname  VARCHAR(25) := 'Jones';\n" +
                "BEGIN\n" +
                "  DBMS_OUTPUT.PUT_LINE('chr=' || chr(65));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testCONCAT() {
        String expectedValue = "name=SmithJones";
        String inputSQL = "DECLARE\n" +
                "  name     VARCHAR(25) NOT NULL := 'Smith';\n" +
                "  surname  VARCHAR(25) := 'Jones';\n" +
                "BEGIN\n" +
                "  DBMS_OUTPUT.PUT_LINE('name=' || concat(name,surname));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    boolean doEvaluate(String inputSQL, String expectedValue){
        EDbVendor dbVendor = EDbVendor.dbvoracle;
        TSQLEnv sqlEnv = new TSQLEnv(dbVendor) {
            @Override
            public void initSQLEnv() {
            }
        };

        TGlobalScope globalScope = new TGlobalScope(sqlEnv);
        TGSqlParser sqlParser = new TGSqlParser(dbVendor);
        sqlParser.sqltext = inputSQL;

        int ret = sqlParser.parse();
        if (ret != 0){
            System.out.println(sqlParser.getErrormessage());
            return false;
        }


        TASTEvaluator astEvaluator = new TASTEvaluator(sqlParser.sqlstatements,globalScope);
        String retValue = astEvaluator.eval();
        //System.out.println("Return value from evaluator:\t"+astEvaluator.eval());
        return retValue.equalsIgnoreCase(expectedValue);

    }

}
