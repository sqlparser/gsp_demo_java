package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.TCloseStmt;
import gudusoft.gsqlparser.stmt.TCursorDeclStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_update extends TestCase {

    public void test1(){
                TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
                sqlparser.sqltext = "CREATE FUNCTION scan_rows(int[]) RETURNS void AS $$\n" +
                        "DECLARE\n" +
                        "\tcurs1 refcursor;\n" +
                        "\tcurs2 CURSOR FOR SELECT\n" +
                        "\t*\n" +
                        "\tFROM tenk1;\n" +
                        "\tcurs3 CURSOR (key integer) FOR SELECT\n" +
                        "\t*\n" +
                        "\tFROM tenk1 WHERE unique1 = key;\n" +
                        "BEGIN\n" +
                        "UPDATE foo SET dataval = myval WHERE CURRENT OF curs1;\n" +
                        "\n" +
                        "CLOSE curs1;\n" +
                        "END;\n" +
                        "$$ LANGUAGE plpgsql;";
                assertTrue(sqlparser.parse() == 0);

                TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);

                //System.out.println(createFunction.getDeclareStatements().size());
                //System.out.println(createFunction.getDeclareStatements().get(0).toString());
                assertTrue(createFunction.getDeclareStatements().size() == 3);
                TCustomSqlStatement stmt = createFunction.getDeclareStatements().get(0);
                assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstplsql_vardecl);
                TVarDeclStmt declStmt = (TVarDeclStmt)stmt;
                assertTrue(declStmt.getDeclareType() == EDeclareType.variable);
        assertTrue(declStmt.getElementName().toString().equalsIgnoreCase("curs1"));
        assertTrue(declStmt.getDataType().getDataType() == EDataType.refcursor_t);

        stmt = createFunction.getDeclareStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_cursordecl);
        TCursorDeclStmt cursorDeclStmt = (TCursorDeclStmt)stmt;
        assertTrue(cursorDeclStmt.getCursorName().toString().equalsIgnoreCase("curs2"));
        TSelectSqlStatement select = cursorDeclStmt.getSubquery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("tenk1"));

        stmt = createFunction.getDeclareStatements().get(2);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_cursordecl);
        cursorDeclStmt = (TCursorDeclStmt)stmt;
        assertTrue(cursorDeclStmt.getCursorName().toString().equalsIgnoreCase("curs3"));
        assertTrue(cursorDeclStmt.getCursorParameterDeclarations().getParameterDeclarationItem(0).getParameterName().toString().equalsIgnoreCase("key"));
        assertTrue(cursorDeclStmt.getCursorParameterDeclarations().getParameterDeclarationItem(0).getDataType().getDataType() == EDataType.integer_t);
        select = cursorDeclStmt.getSubquery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("tenk1"));

                assertTrue(createFunction.getBodyStatements().size() == 2);
                stmt = createFunction.getBodyStatements().get(0);
                assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstupdate);
                TUpdateSqlStatement updateStmt = (TUpdateSqlStatement)stmt;
                assertTrue(updateStmt.getTargetTable().getTableName().toString().equalsIgnoreCase("foo"));
                assertTrue(updateStmt.getResultColumnList().size() == 1);
                TExpression setExpr = updateStmt.getResultColumnList().getResultColumn(0).getExpr();
                assertTrue(setExpr.getExpressionType() == EExpressionType.assignment_t);
                assertTrue(setExpr.getLeftOperand().toString().equalsIgnoreCase("dataval"));
                assertTrue(setExpr.getRightOperand().toString().equalsIgnoreCase("myval"));
                TExpression whereCondition = updateStmt.getWhereClause().getCondition();
                assertTrue(updateStmt.getWhereClause().isCurerntOf());
                assertTrue(whereCondition.toString().equalsIgnoreCase("curs1"));

                stmt = createFunction.getBodyStatements().get(1);
                assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_closestmt);
                TCloseStmt closeStmt = (TCloseStmt)stmt;
                assertTrue(closeStmt.getCursorName().toString().equalsIgnoreCase("curs1"));

            }


}
