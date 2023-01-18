package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_case extends TestCase {

    public void testSimpleCase(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION get_available_flightid(date) RETURNS SETOF integer AS\n" +
                "$BODY$\n" +
                "BEGIN\n" +
                "\tCASE x\n" +
                "\tWHEN 1, 2 THEN\n" +
                "\tmsg := 'one or two';\n" +
                "\tELSE\n" +
                "\tmsg := 'other value than one or two';\n" +
                "\tEND CASE;\n" +
                "\n" +
                "END\n" +
                "$BODY$\n" +
                "LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getParameterDeclarations().size() == 1);
        TParameterDeclaration param = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(param.getDataType().getDataType() == EDataType.date_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.integer_t);

        assertTrue(createFunction.getBodyStatements().size() == 1);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_casestmt);
        TCaseStmt caseStmt = (TCaseStmt)stmt;
        TCaseExpression caseExpression = caseStmt.getCaseExpr();
        assertTrue(caseExpression.getInput_expr().toString().equalsIgnoreCase("x"));

        assertTrue(caseExpression.getWhenClauseItemList().size() == 1);
        TWhenClauseItem whenClauseItem = caseExpression.getWhenClauseItemList().getWhenClauseItem(0);
        assertTrue(whenClauseItem.getConditionList().size() == 2);
        assertTrue(whenClauseItem.getConditionList().getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(whenClauseItem.getConditionList().getExpression(1).toString().equalsIgnoreCase("2"));

        assertTrue(whenClauseItem.getStatement_list().size() == 1);
        TCustomSqlStatement thenStmt = whenClauseItem.getStatement_list().get(0);
        assertTrue(thenStmt.sqlstatementtype == ESqlStatementType.sst_assignstmt);
        TAssignStmt assignStmt = (TAssignStmt)thenStmt;
        assertTrue(assignStmt.getLeft().toString().equalsIgnoreCase("msg"));
        assertTrue(assignStmt.getExpression().toString().equalsIgnoreCase("'one or two'"));

        //assertTrue(whenClauseItem.getReturn_expr().toString().equalsIgnoreCase("msg := 'one or two'"));

        assertTrue(caseExpression.getElse_statement_list().size() == 1);
        TCustomSqlStatement elseStmt = caseExpression.getElse_statement_list().get(0);
        assertTrue(elseStmt.sqlstatementtype == ESqlStatementType.sst_assignstmt);
        assignStmt = (TAssignStmt)elseStmt;
        assertTrue(assignStmt.getLeft().toString().equalsIgnoreCase("msg"));
        assertTrue(assignStmt.getExpression().toString().equalsIgnoreCase("'other value than one or two'"));


    }

    public void testSearchedCase(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION get_available_flightid(date) RETURNS SETOF integer AS\n" +
                "$BODY$\n" +
                "BEGIN\n" +
                "\n" +
                "\tCASE\n" +
                "\tWHEN x BETWEEN 0 AND 10 THEN\n" +
                "\t\tmsg := 'value is between zero and ten';\n" +
                "\tWHEN x BETWEEN 11 AND 20 THEN\n" +
                "\t\tmsg := 'value is between eleven and twenty';\n" +
                "\tEND CASE;\n" +
                "\n" +
                "END\n" +
                "$BODY$\n" +
                "LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getParameterDeclarations().size() == 1);
        TParameterDeclaration param = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(param.getDataType().getDataType() == EDataType.date_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.integer_t);

        assertTrue(createFunction.getBodyStatements().size() == 1);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_casestmt);
        TCaseStmt caseStmt = (TCaseStmt)stmt;
        TCaseExpression caseExpression = caseStmt.getCaseExpr();

        assertTrue(caseExpression.getWhenClauseItemList().size() == 2);
        TWhenClauseItem whenClauseItem = caseExpression.getWhenClauseItemList().getWhenClauseItem(0);
        assertTrue(whenClauseItem.getConditionList().size() == 1);
        TExpression between = whenClauseItem.getConditionList().getExpression(0);
        assertTrue(between.getExpressionType() == EExpressionType.between_t);
        assertTrue(between.getBetweenOperand().toString().equalsIgnoreCase("x"));
        assertTrue(between.getLeftOperand().toString().equalsIgnoreCase("0"));
        assertTrue(between.getRightOperand().toString().equalsIgnoreCase("10"));

        assertTrue(whenClauseItem.getStatement_list().size() == 1);
        TCustomSqlStatement thenStmt = whenClauseItem.getStatement_list().get(0);
        assertTrue(thenStmt.sqlstatementtype == ESqlStatementType.sst_assignstmt);
        TAssignStmt assignStmt = (TAssignStmt)thenStmt;
        assertTrue(assignStmt.getLeft().toString().equalsIgnoreCase("msg"));
        assertTrue(assignStmt.getExpression().toString().equalsIgnoreCase("'value is between zero and ten'"));

    }
}
