package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;


public class testTestPutline extends TestCase {

    public void testOperatorProcedence() {
        String expectedValue = "b+d=71.1";
        String inputSQL = "DECLARE\n" +
                "b REAL; -- Scope of b begins\n" +
                "d REAL;\n" +
                "BEGIN\n" +
                "\tb := 4.5;\n" +
                "\td := 66.6;\n" +
                "\tDBMS_OUTPUT.PUT_LINE ('b+d='||(b+d)); \n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testNestScope() {
        String expectedValue = "outer d=66.6";
        String inputSQL = "-- Outer block:\n" +
                "DECLARE\n" +
                "a CHAR; -- Scope of a (CHAR) begins\n" +
                "b REAL; -- Scope of b begins\n" +
                "d REAL;\n" +
                "BEGIN\n" +
                "-- Visible: a (CHAR), b\n" +
                "-- First sub-block:\n" +
                "\t\n" +
                "\ta := 'c';\n" +
                "\tb := 4.5;\n" +
                "\td := 66.6;\n" +
                "\tDECLARE\n" +
                "\ta INTEGER; -- Scope of a (INTEGER) begins\n" +
                "\tc REAL; -- Scope of c begins\n" +
                "\tBEGIN\n" +
                "\t\t-- Visible: a (INTEGER), b, c\n" +
                "\t\ta := 2;\n" +
                "\t\tc := 3.2;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('a+c='||to_char(a+c));\n" +
                "\t\t\n" +
                "\t\tNULL;\n" +
                "\tEND; -- Scopes of a (INTEGER) and c end\n" +
                "\t-- Second sub-block:\n" +
                "\tDECLARE\n" +
                "\td REAL; -- Scope of d begins\n" +
                "\tBEGIN\n" +
                "\t    d := 78.9;\n" +
                "\t\t-- Visible: a (CHAR), b, d\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('inner d='||to_char(d));\n" +
                "\t\tNULL;\n" +
                "\tEND; -- Scope of d ends\n" +
                "\t-- Visible: a (CHAR), b\n" +
                "\tDBMS_OUTPUT.PUT_LINE ('outer d='||to_char(d));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testCreateProcedure() {
        String expectedValue = "Table updated? Yes, bonus = 125.0.";
        String inputSQL = "DECLARE\n" +
                "PROCEDURE p (\n" +
                "sales NUMBER,\n" +
                "quota NUMBER,\n" +
                "emp_id NUMBER\n" +
                ")\n" +
                "IS\n" +
                "\tbonus NUMBER := 0;\n" +
                "\tupdated VARCHAR2(3) := 'No';\n" +
                "BEGIN\n" +
                "IF sales > (quota + 200) THEN\n" +
                "\tbonus := (sales - quota)/4;\n" +
                "\n" +
                "\tupdated := 'Yes';\n" +
                "END IF;\n" +
                "\tDBMS_OUTPUT.PUT_LINE (\n" +
                "\t'Table updated? ' || updated || ', ' ||\n" +
                "\t'bonus = ' || bonus || '.'\n" +
                "\t);\n" +
                "END p;\n" +
                "BEGIN\n" +
                "\tp(10100, 10000, 120);\n" +
                "\tp(10500, 10000, 121);\n" +
                "END;\n" +
                "/";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }


    public void testWhile() {
        String expectedValue = "Hello, world!";
        String inputSQL = "DECLARE\n" +
                "\tdone BOOLEAN := FALSE;\n" +
                "BEGIN\n" +
                "WHILE done LOOP\n" +
                "\tDBMS_OUTPUT.PUT_LINE ('This line does not print.');\n" +
                "\tdone := TRUE; -- This assignment is not made.\n" +
                "END LOOP;\n" +
                "\n" +
                "WHILE NOT done LOOP\n" +
                "\tDBMS_OUTPUT.PUT_LINE ('Hello, world!');\n" +
                "\tdone := TRUE;\n" +
                "END LOOP;\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testIfElse() {
        String expectedValue = "less10.0";
        String inputSQL = "DECLARE\n" +
                "\tsales NUMBER := 10;\n" +
                "BEGIN\n" +
                "\tIF sales > 10 THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('great' ||sales);\n" +
                "\tELSE\n" +
                "\t    DBMS_OUTPUT.PUT_LINE('less' || sales);\n" +
                "\tEND IF;\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testIfThen() {
        String expectedValue = "great10.0";
        String inputSQL = "DECLARE\n" +
                "\tsales NUMBER := 10;\n" +
                "BEGIN\n" +
                "\tIF sales > 1 THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('great' ||sales);\n" +
                "\tELSE\n" +
                "\t    DBMS_OUTPUT.PUT_LINE('less' || sales);\n" +
                "\tEND IF;\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testDiv() {
        String expectedValue = "2.5";
        String inputSQL = "DECLARE\n" +
                "\tbonus NUMBER := 0;\n" +
                "\tquota NUMBER := 0;\n" +
                "BEGIN\n" +
                "bonus := (10 - quota)/4;\n" +
                "DBMS_OUTPUT.PUT_LINE(bonus);\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }
    public void testNestedParenthesis() {
        String expectedValue = "a = 3";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := ((1+2)*(3+4))/7;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE('a = ' || TO_CHAR(a));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testUminus() {
        String expectedValue = "-1";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := -1;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(a);\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

    public void testToChar() {
        String expectedValue = "b = 9";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := 1+2;\n" +
                "b INTEGER := (1+2)**2;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(a);\n" +
                "DBMS_OUTPUT.PUT_LINE('a = ' || TO_CHAR(a));\n" +
                "DBMS_OUTPUT.PUT_LINE('b = ' || TO_CHAR(b));\n" +
                "END;";
        assertTrue(doEvaluate(inputSQL,expectedValue));
    }

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
