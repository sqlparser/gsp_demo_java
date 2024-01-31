package redshift;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class TestCreateFunction extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create function f_sql_greater (float, float)\n" +
                "  returns float\n" +
                "stable\n" +
                "as $$\n" +
                "  select case when $1 > $2 then $1\n" +
                "    else $2\n" +
                "  end\n" +
                "$$ language sql";
       // System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("f_sql_greater"));
        assertTrue(createFunction.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.float_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.float_t);
        assertTrue(createFunction.getRoutineLanguage().equalsIgnoreCase("sql"));

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.case_t);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create function f_sql_commission (float, float )\n" +
                "  returns float\n" +
                "stable\n" +
                "as $$\n" +
                "  select f_sql_greater ($1, $2)  \n" +
                "$$ language sql;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("f_sql_commission"));
        assertTrue(createFunction.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.float_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.float_t);
        assertTrue(createFunction.getRoutineLanguage().equalsIgnoreCase("sql"));

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);
    }

    public void testPython(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create function f_py_greater (a float, b float)\n" +
                "  returns float\n" +
                "stable\n" +
                "as $$\n" +
                "  if a > b:\n" +
                "    return a\n" +
                "  return b\n" +
                "$$ language plpythonu;";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("f_py_greater"));
        assertTrue(createFunction.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("a"));
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.float_t);

        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.float_t);
        assertTrue(createFunction.getRoutineLanguage().equalsIgnoreCase("plpythonu"));

    }
}
