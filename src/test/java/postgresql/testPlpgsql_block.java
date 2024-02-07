package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TRaiseStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_block extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION somefunc() RETURNS integer AS $$\n" +
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
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.integer_t);

        assertTrue(createFunction.getBodyStatements().size() == 5);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_raisestmt);
        TRaiseStmt raiseStmt = (TRaiseStmt)stmt;
        assertTrue(raiseStmt.getRaiseLevel() == ERaiseLevel.notice);
        assertTrue(raiseStmt.getFormatString().toString().equalsIgnoreCase("'Quantity here is %'"));
        assertTrue(raiseStmt.getExprList().getExpression(0).toString().equalsIgnoreCase("quantity"));

        stmt = createFunction.getBodyStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_assignstmt);
        TAssignStmt assignStmt = (TAssignStmt)stmt;
        assertTrue(assignStmt.getLeft().toString().equalsIgnoreCase("quantity"));
        assertTrue(assignStmt.getExpression().toString().equalsIgnoreCase("50"));

        stmt = createFunction.getBodyStatements().get(2);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_plsql_block);
        TCommonBlock block = (TCommonBlock)stmt;

        assertTrue(block.getDeclareStatements().size() == 1);
        TVarDeclStmt declStmt = (TVarDeclStmt)block.getDeclareStatements().get(0);
        assertTrue(declStmt.getDeclareType() == EDeclareType.variable);
        assertTrue(declStmt.getElementName().toString().equalsIgnoreCase("quantity"));
        assertTrue(declStmt.getDataType().getDataType() == EDataType.integer_t);
        assertTrue(declStmt.getDefaultValue().toString().equalsIgnoreCase("80"));

        assertTrue(block.getBodyStatements().size() == 2);
        stmt = block.getBodyStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_raisestmt);
        raiseStmt = (TRaiseStmt)stmt;
        assertTrue(raiseStmt.getRaiseLevel() == ERaiseLevel.notice);
        assertTrue(raiseStmt.getFormatString().toString().equalsIgnoreCase("'Outer quantity here is %'"));
        assertTrue(raiseStmt.getExprList().getExpression(0).toString().equalsIgnoreCase("outerblock.quantity"));

    }
}
