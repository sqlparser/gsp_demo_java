package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TLog;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;


public class testTestPutline extends testInterpreterBase {

    public void testBlockNested2() {
        String expectedValue = "Credit rating over limit (1.0). Rating: 3.0";
        String inputSQL = "CREATE OR REPLACE PROCEDURE check_credit (credit_limit NUMBER) AS\n" +
                "\trating NUMBER := 3;\n" +
                "\tFUNCTION check_rating RETURN BOOLEAN IS\n" +
                "\t\trating NUMBER := 1;\n" +
                "\t\tover_limit BOOLEAN;\n" +
                "\tBEGIN\n" +
                "\t\tIF check_credit.rating <= credit_limit THEN -- reference global variable\n" +
                "\t\t\tover_limit := FALSE;\n" +
                "\t\t\tELSE\n" +
                "\t\t\tover_limit := TRUE;\n" +
                "\t\t\trating := credit_limit; -- reference local variable\n" +
                "\t\tEND IF;\n" +
                "\t\tRETURN over_limit;\n" +
                "\tEND check_rating;\n" +
                "\t\n" +
                "BEGIN\n" +
                "\tIF check_rating THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE\n" +
                "\t\t('Credit rating over limit (' || TO_CHAR(credit_limit) || '). '\n" +
                "\t\t|| 'Rating: ' || TO_CHAR(rating));\n" +
                "\t\tELSE\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE\n" +
                "\t\t('Credit rating OK. ' || 'Rating: ' || TO_CHAR(rating));\n" +
                "\tEND IF;\n" +
                "END;\n" +
                "\n" +
                "/\n" +
                "BEGIN\n" +
                "\tcheck_credit(1);\n" +
                "END;";

        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockNested1() {
        String expectedValue = "Credit rating OK. Rating: 3.0";
        String inputSQL = "CREATE OR REPLACE PROCEDURE check_credit (credit_limit NUMBER) AS\n" +
                "\trating NUMBER := 3;\n" +
                "\tFUNCTION check_rating RETURN BOOLEAN IS\n" +
                "\t\trating NUMBER := 1;\n" +
                "\t\tover_limit BOOLEAN;\n" +
                "\tBEGIN\n" +
                "\t\tIF check_credit.rating <= credit_limit THEN -- reference global variable\n" +
                "\t\t\tover_limit := FALSE;\n" +
                "\t\t\tELSE\n" +
                "\t\t\tover_limit := TRUE;\n" +
                "\t\t\trating := credit_limit; -- reference local variable\n" +
                "\t\tEND IF;\n" +
                "\t\tRETURN over_limit;\n" +
                "\tEND check_rating;\n" +
                "\t\n" +
                "BEGIN\n" +
                "\tIF check_rating THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE\n" +
                "\t\t('Credit rating over limit (' || TO_CHAR(credit_limit) || '). '\n" +
                "\t\t|| 'Rating: ' || TO_CHAR(rating));\n" +
                "\t\tELSE\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE\n" +
                "\t\t('Credit rating OK. ' || 'Rating: ' || TO_CHAR(rating));\n" +
                "\tEND IF;\n" +
                "END;\n" +
                "\n" +
                "/\n" +
                "BEGIN\n" +
                "\tcheck_credit(5);\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel6() {
        String expectedValue = "In procedure q, x = b\n" +
                "In procedure p, x = a";
        String inputSQL = "DECLARE\n" +
                "\tPROCEDURE p\n" +
                "\tIS\n" +
                "\t\tx VARCHAR2(1);\n" +
                "\tBEGIN\n" +
                "\t\tx := 'a'; -- Assign the value 'a' to x\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('In procedure p, x = ' || x);\n" +
                "\tEND;\n" +
                "\tPROCEDURE q\n" +
                "\tIS\n" +
                "\t\tx VARCHAR2(1);\n" +
                "\tBEGIN\n" +
                "\t\tx := 'b'; -- Assign the value 'b' to x\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('In procedure q, x = ' || x);\n" +
                "\tEND;\n" +
                "BEGIN\n" +
                "\tq;\n" +
                "\tp;\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel5() {
        String expectedValue = "In procedure p, x = a\n" +
                "In procedure q, x = b";
        String inputSQL = "DECLARE\n" +
                "\tPROCEDURE p\n" +
                "\tIS\n" +
                "\t\tx VARCHAR2(1);\n" +
                "\tBEGIN\n" +
                "\t\tx := 'a'; -- Assign the value 'a' to x\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('In procedure p, x = ' || x);\n" +
                "\tEND;\n" +
                "\tPROCEDURE q\n" +
                "\tIS\n" +
                "\t\tx VARCHAR2(1);\n" +
                "\tBEGIN\n" +
                "\t\tx := 'b'; -- Assign the value 'b' to x\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('In procedure q, x = ' || x);\n" +
                "\tEND;\n" +
                "BEGIN\n" +
                "\tp;\n" +
                "\tq;\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel4() {
        String expectedValue = "echo.x = 0.0\n" +
                "x = 0.0";
        String inputSQL = "<<echo>>\n" +
                "DECLARE\n" +
                "x NUMBER := 5;\n" +
                "\tPROCEDURE echo AS\n" +
                "\t\tx NUMBER := 0;\n" +
                "\tBEGIN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('echo.x = ' || echo.x);\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('x = ' || x);\n" +
                "\tEND;\n" +
                "BEGIN\n" +
                "echo;\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel3() {
        String expectedValue = "x = 0.0\n" +
                "echo1.x = 5.0";
        String inputSQL = "<<echo1>>\n" +
                "DECLARE\n" +
                "x NUMBER := 5;\n" +
                "\tPROCEDURE echo AS\n" +
                "\t\tx NUMBER := 0;\n" +
                "\tBEGIN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('x = ' || x);\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('echo1.x = ' || echo1.x);\n" +
                "\tEND;\n" +
                "BEGIN\n" +
                "echo;\n" +
                "END;\n";
       // System.out.println(inputSQL);
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel2() {
        String expectedValue = "Different Birthday19991909";
        String inputSQL = "<<outer>> -- label\n" +
                "DECLARE\n" +
                "birthdate integer := 1909;\n" +
                "BEGIN\n" +
                "\tDECLARE\n" +
                "\tbirthdate integer := 1999;\n" +
                "\tBEGIN\n" +
                "\t\tIF birthdate = outer.birthdate THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('Same Birthday'||birthdate||outer.birthdate);\n" +
                "\t\tELSE\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('Different Birthday'||birthdate||outer.birthdate);\n" +
                "\t\tEND IF;\n" +
                "\tEND;\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testBlockLabel1() {
        String expectedValue = "Same Birthday19991999";
        String inputSQL = "<<outer>> -- label\n" +
                "DECLARE\n" +
                "birthdate integer := 1999;\n" +
                "BEGIN\n" +
                "\tDECLARE\n" +
                "\tbirthdate integer := 1999;\n" +
                "\tBEGIN\n" +
                "\t\tIF birthdate = outer.birthdate THEN\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('Same Birthday'||birthdate||outer.birthdate);\n" +
                "\t\tELSE\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE ('Different Birthday'||birthdate||outer.birthdate);\n" +
                "\t\tEND IF;\n" +
                "\tEND;\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testNestScope() {
        String expectedValue = "a+c=5.2\n" +
                "inner d=78.9\n" +
                "outer d=66.6";
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testCreateProcedure() {
        String expectedValue = "Table updated? No, bonus = 0.0.\n" +
                "Table updated? Yes, bonus = 125.0.";
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }
    public void testNestedParenthesis() {
        String expectedValue = "a = 3";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := ((1+2)*(3+4))/7;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE('a = ' || TO_CHAR(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testUminus() {
        String expectedValue = "-1";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := -1;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(a);\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testToChar() {
        String expectedValue = "3\n" +
                "a = 3\n" +
                "b = 9";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := 1+2;\n" +
                "b INTEGER := (1+2)**2;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(a);\n" +
                "DBMS_OUTPUT.PUT_LINE('a = ' || TO_CHAR(a));\n" +
                "DBMS_OUTPUT.PUT_LINE('b = ' || TO_CHAR(b));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
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
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testCHR() {
        String expectedValue = "chr=A";
        String inputSQL = "DECLARE\n" +
                "  name     VARCHAR(25) NOT NULL := 'Smith';\n" +
                "  surname  VARCHAR(25) := 'Jones';\n" +
                "BEGIN\n" +
                "  DBMS_OUTPUT.PUT_LINE('chr=' || chr(65));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testCONCAT() {
        String expectedValue = "name=SmithJones";
        String inputSQL = "DECLARE\n" +
                "  name     VARCHAR(25) NOT NULL := 'Smith';\n" +
                "  surname  VARCHAR(25) := 'Jones';\n" +
                "BEGIN\n" +
                "  DBMS_OUTPUT.PUT_LINE('name=' || concat(name,surname));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

//    boolean doEvaluate(String inputSQL, String expectedValue){
//        EDbVendor dbVendor = EDbVendor.dbvoracle;
//        TSQLEnv sqlEnv = new TSQLEnv(dbVendor) {
//            @Override
//            public void initSQLEnv() {
//            }
//        };
//
//        TGlobalScope globalScope = new TGlobalScope(sqlEnv);
//        TGSqlParser sqlParser = new TGSqlParser(dbVendor);
//        sqlParser.sqltext = inputSQL;
//
//        int ret = sqlParser.parse();
//        if (ret != 0){
//            System.out.println(sqlParser.getErrormessage());
//            return false;
//        }
//
//
//        TLog.clearLogs();
//        TLog.enableInterpreterLogOnly();
//        TLog.setOutputSimpleMode(true);
//
//        TASTEvaluator astEvaluator = new TASTEvaluator(sqlParser.sqlstatements,globalScope);
//        astEvaluator.eval();
//
//        String retValue =  TBaseType.dumpLogsToString();
//        return  TBaseType.compareStringsLineByLine(retValue,expectedValue);
//    }

}
