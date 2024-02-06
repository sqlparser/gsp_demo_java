package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TLog;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testPostgres extends testInterpreterBase {

    public void test1() {
        String expectedValue = "Quantity here is 30\n" +
                "Quantity here is 80\n" +
                "Outer quantity here is 50\n" +
                "Quantity here is 50\n" +
                "52";
        String inputSQL = "CREATE FUNCTION somefunc() RETURNS integer AS $$\n" +
                "<< outerblock >>\n" +
                "DECLARE\n" +
                "    quantity integer := 30;\n" +
                "BEGIN\n" +
                "    RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 30\n" +
                "    quantity := 50;\n" +
                "    --\n" +
                "    -- Create a subblock\n" +
                "    --\n" +
                "    DECLARE\n" +
                "        quantity integer := 80;\n" +
                "    BEGIN\n" +
                "        RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 80\n" +
                "        RAISE NOTICE 'Outer quantity here is %', outerblock.quantity;  -- Prints 50\n" +
                "    END;\n" +
                "\n" +
                "    RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 50\n" +
                "\n" +
                "    RETURN quantity;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;\n" +
                "\n" +
                "select somefunc()+2;";
        assertTrue(doEvaluate(EDbVendor.dbvpostgresql,inputSQL,expectedValue));
    }

    public void test2() {
        String expectedValue = "f1 = 5, f2 = abc";
        String inputSQL = "CREATE OR REPLACE PROCEDURE test_sp1(f1 int, f2 varchar)\n" +
                "AS $$\n" +
                "BEGIN\n" +
                "  RAISE INFO 'f1 = %, f2 = %', f1, f2;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;\n" +
                "\n" +
                "call test_sp1(5, 'abc');";
        assertTrue(doEvaluate(EDbVendor.dbvpostgresql,inputSQL,expectedValue));
    }

    public void test3() {
        String expectedValue = "Value here is 20\nValue here is 80\nValue here is 50";
        String inputSQL = "CREATE PROCEDURE update_value() AS $$\n" +
                "DECLARE\n" +
                "value integer := 20;\n" +
                "BEGIN\n" +
                "\tRAISE NOTICE 'Value here is %', value; -- Value here is 20\n" +
                "\tvalue := 50;\n" +
                "--\n" +
                "-- Create a subblock\n" +
                "--\n" +
                "\tDECLARE\n" +
                "\tvalue integer := 80;\n" +
                "\tBEGIN\n" +
                "\t\tRAISE NOTICE 'Value here is %', value; -- Value here is 80\n" +
                "\tEND;\n" +
                "\n" +
                "\tRAISE NOTICE 'Value here is %', value; -- Value here is 50\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;\n" +
                "\n" +
                "call update_value();";
        assertTrue(doEvaluate(EDbVendor.dbvpostgresql,inputSQL,expectedValue));
    }
}
