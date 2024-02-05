package plsqlInterpreter;

import gudusoft.gsqlparser.EDbVendor;

public class testRedshift extends testInterpreterBase{

    public void test1() {
        String expectedValue = "x is 15, y is 20, multiplier is 5";
        String inputSQL = "CREATE OR REPLACE PROCEDURE inner_proc(INOUT a int, b int, INOUT c int) LANGUAGE plpgsql\n" +
                "AS $$\n" +
                "BEGIN\n" +
                "  a := b * a;\n" +
                "  c := b * c;\n" +
                "END;\n" +
                "$$;\n" +
                "\n" +
                "CREATE OR REPLACE PROCEDURE outer_proc(multiplier int) LANGUAGE plpgsql\n" +
                "AS $$\n" +
                "DECLARE\n" +
                "  x int := 3;\n" +
                "  y int := 4;\n" +
                "BEGIN\n" +
                "  DROP TABLE IF EXISTS test_tbl;\n" +
                "  CREATE TEMP TABLE test_tbl(a int, b varchar(256));\n" +
                "  CALL inner_proc(x, multiplier, y);\n" +
                "  RAISE NOTICE 'x is %, y is %, multiplier is %', x,y,multiplier;\n" +
                "  insert into test_tbl values (x, y::varchar);\n" +
                "END;\n" +
                "$$;\n" +
                "\n" +
                "CALL outer_proc(5);";
        assertTrue(doEvaluate(EDbVendor.dbvredshift, inputSQL,expectedValue));
    }
}
