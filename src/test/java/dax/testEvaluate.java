package dax;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.dax.TDaxEvaluateStmt;
import junit.framework.TestCase;

public class testEvaluate extends TestCase {
        public void test1(){

            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdax);

            sqlparser.sqltext = "EVALUATE\n" +
                    "CALCULATETABLE (\n" +
                    "    ADDCOLUMNS (\n" +
                    "        VAR\n" +
                    "            OnePercentOfSales = [SalesAmount] * 0.01\n" +
                    "        RETURN\n" +
                    "            FILTER (\n" +
                    "                VALUES ( Product[Product Name] ),\n" +
                    "                [SalesAmount] >= OnePercentOfSales\n" +
                    "            ),\n" +
                    "        \"SalesOfProduct\", [SalesAmount]\n" +
                    "    ),\n" +
                    "    Product[Color] = \"Black\"\n" +
                    ")";

            assertTrue(sqlparser.parse() == 0);
            assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdaxevaluate);
            TDaxEvaluateStmt evaluateStmt = (TDaxEvaluateStmt)sqlparser.sqlstatements.get(0);
            TExpression tableExpr = evaluateStmt.getTableExpr();
            assertTrue(tableExpr.getExpressionType() == EExpressionType.function_t);
            TFunctionCall calculateTable = tableExpr.getFunctionCall();
            assertTrue(calculateTable.getFunctionType() == EFunctionType.calculatetable_t);
            assertTrue(calculateTable.getFunctionName().toString().equalsIgnoreCase("CALCULATETABLE"));
            assertTrue(calculateTable.getArgs().size() == 2);

        }
 }
