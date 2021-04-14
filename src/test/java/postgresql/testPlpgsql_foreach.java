package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.postgresql.TForEachStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_foreach extends TestCase {
    public void test1(){
         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
         sqlparser.sqltext = "CREATE FUNCTION sum(int[]) RETURNS int8 AS $$\n" +
                 "DECLARE\n" +
                 "s int8 := 0;\n" +
                 "x int;\n" +
                 "BEGIN\n" +
                 "\tFOREACH x IN ARRAY $1\n" +
                 "\tLOOP\n" +
                 "\t\ts := s + x;\n" +
                 "\tEND LOOP;\n" +
                 "\tRETURN s;\n" +
                 "END;\n" +
                 "$$ LANGUAGE plpgsql;";
         assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 2);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstpostgresqlforeach);
        TForEachStmt forEachStmt = (TForEachStmt)stmt;
        assertTrue(forEachStmt.getVariableName().toString().equalsIgnoreCase("x"));
        assertTrue(forEachStmt.getArrayExpr().toString().equalsIgnoreCase("$1"));
        assertTrue(forEachStmt.getBodyStatements().size() == 1);
        stmt = forEachStmt.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_assignstmt);
        TAssignStmt assignStmt = (TAssignStmt)stmt;
        assertTrue(assignStmt.getLeft().toString().equalsIgnoreCase("s"));
        assertTrue(assignStmt.getExpression().toString().equalsIgnoreCase("s + x"));

    }

}
