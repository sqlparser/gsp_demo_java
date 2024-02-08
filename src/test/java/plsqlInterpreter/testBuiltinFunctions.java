package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TLog;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testBuiltinFunctions extends testInterpreterBase {
    public void testABS() {
        String expectedValue = "100";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := -100;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ABS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "100.543";
        inputSQL = "DECLARE\n" +
                "a NUMBER(8,3) := -100.543;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ABS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "6";
        inputSQL = "DECLARE\n" +
                "a INTEGER := 6;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ABS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "0";
        inputSQL = "DECLARE\n" +
                "a INTEGER := 0;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ABS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testACOS() {
        String expectedValue = "1.2661036727794992";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := .3;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ACOS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testSIN() {
        String expectedValue = "0.5000000000000299";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 30 * 3.14159265359/180;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SIN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testSUBSTR() {
        String expectedValue = "CDEF";
        String inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,3,4));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,-5,4));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "ABCD";
        inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,1,4));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,0,4));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "CDEFG";
        inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,3));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        inputSQL = "DECLARE\n" +
                "  a  VARCHAR(25) := 'ABCDEFG';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SUBSTR(a,-5));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testTRIM() {
        String expectedValue = "AB CD";
        String inputSQL = "DECLARE\n" +
                "a VARCHAR(25) := ' AB CD ';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(TRIM(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testUPPER() {
        String expectedValue = "ABCD";
        String inputSQL = "DECLARE\n" +
                "a VARCHAR(25) := 'abcD';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(UPPER(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testLOWER() {
        String expectedValue = "mr. scott mcmillan";
        String inputSQL = "DECLARE\n" +
                "a VARCHAR(25) := 'MR. SCOTT MCMILLAN';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(LOWER(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testMOD() {
        String expectedValue = "3";
        String inputSQL = "DECLARE\n" +
                "a INTEGER := 11;\n" +
                "b INTEGER := 4;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(MOD(a, b));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "11";
        inputSQL = "DECLARE\n" +
                "a INTEGER := 11;\n" +
                "b INTEGER := 0;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(MOD(a, b));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    //REPLACE
    public void testREPLACE() {
        String expectedValue = "BLACK and BLUE";
        String inputSQL = "DECLARE\n" +
                "a VARCHAR(25) := 'JACK and JUE';\n" +
                "b VARCHAR(25) := 'J';\n" +
                "c VARCHAR(25) := 'BL';\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(REPLACE(a, b, c));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    //SIGN
    public void testSIGN() {
        String expectedValue = "1";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 6.9;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SIGN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "0";
        inputSQL = "DECLARE\n" +
                "a NUMBER := 0;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SIGN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "-1";
        inputSQL = "DECLARE\n" +
                "a NUMBER := -6;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(SIGN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testROUND() {
        String expectedValue = "15.2";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 15.193;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ROUND(a, 1));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "0.0";
        inputSQL = "DECLARE\n" +
                "a NUMBER := 0;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ROUND(a, 6));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "20.0";
        inputSQL = "DECLARE\n" +
                "a NUMBER := 15.193;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ROUND(a, -1));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "16.0";
        inputSQL = "DECLARE\n" +
                "a NUMBER := 15.993;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(ROUND(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testTAN() {
        String expectedValue = "-0.9656887748";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 136 * 3.14159265359/180;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(TAN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));

        expectedValue = "-1.0";
        inputSQL = "DECLARE\n" +
                "a NUMBER := 135 * 3.14159265359/180;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(TAN(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testLOG() {
        String expectedValue = "2.0";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 10;\n" +
                "b NUMBER := 100;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(LOG(a, b));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testCOS() {
        String expectedValue = "-1.0";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 180 * 3.14159265359/180;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(COS(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }

    public void testCOSH() {
        String expectedValue = "1.0";
        String inputSQL = "DECLARE\n" +
                "a NUMBER := 0;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.PUT_LINE(COSH(a));\n" +
                "END;";
        assertTrue(doEvaluate(EDbVendor.dbvoracle,inputSQL,expectedValue));
    }


}
