package postgresql;
/*
 * Date: 11-5-20
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testConstant extends TestCase {

    public void testTypedConst(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select REAL '1.23', 1.23::REAL from t";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr0 = column0.getExpr();
        assertTrue(expr0.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(expr0.getLeftOperand().toString().equalsIgnoreCase("'1.23'"));
        assertTrue(expr0.getTypeName().getDataType() == EDataType.real_t);
        assertTrue(expr0.toString().equalsIgnoreCase("REAL '1.23'"));

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TExpression expr1 = column1.getExpr();
        assertTrue(expr1.toString().equalsIgnoreCase("1.23::REAL"));
        assertTrue(expr1.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(expr1.getLeftOperand().toString().equalsIgnoreCase("1.23"));
        assertTrue(expr1.getTypeName().toString().equalsIgnoreCase("REAL"));
        //System.out.println(expr1.toString());
    }

    public void testdollarString(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select $delim$abc$junk$delim$, $$Dianne's horse$$, $SomeTag$Dianne's horse$SomeTag$ from t";
//        sqlparser.sqltext = "select $delim$k$delim1$ from t";

//        sqlparser.tokenizeSqltext();
//        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
//            System.out.println(sqlparser.sourcetokenlist.get(i).toString());
//        }

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr0 = column0.getExpr();
        TConstant constant0 = expr0.getConstantOperand();
        assertTrue(constant0.toString().equalsIgnoreCase("$delim$abc$junk$delim$"));

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TExpression expr1 = column1.getExpr();
        TConstant constant1 = expr1.getConstantOperand();
        assertTrue(constant1.toString().equalsIgnoreCase("$$Dianne's horse$$"));

        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expr2 = column2.getExpr();
        TConstant constant2 = expr2.getConstantOperand();
        assertTrue(constant2.toString().equalsIgnoreCase("$SomeTag$Dianne's horse$SomeTag$"));

        sqlparser.sqltext = "$function$\n" +
                "BEGIN\n" +
                "    RETURN ($1 ~ $q$[\\t\\r\\n\\v\\\\]$q$);\n" +
                "END;\n" +
                "$function$";

        sqlparser.tokenizeSqltext();
        assertTrue(sqlparser.sourcetokenlist.size()==1);
        sqlparser.sourcetokenlist.get(0).toString().equalsIgnoreCase("$function$\n" +
                "BEGIN\n" +
                "    RETURN ($1 ~ $q$[\\t\\r\\n\\v\\\\]$q$);\n" +
                "END;\n" +
                "$function$");

    }

    public void testString(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select B'1001',x'100', U&'d\\0061t\\+000061' from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TConstant constant = expr.getConstantOperand();
        assertTrue(constant.getStartToken().tokencode == TCustomLexer.bconst);

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TExpression expr1 = column1.getExpr();
        TConstant constant1 = expr1.getConstantOperand();
        assertTrue(constant1.getStartToken().tokencode == TCustomLexer.xconst);

        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expr2 = column2.getExpr();
        TConstant constant2 = expr2.getConstantOperand();
        assertTrue(constant2.toString().equalsIgnoreCase("U&'d\\0061t\\+000061'"));

    }

    public void testNumeric(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select 3.5,.001,5e2,1.925e-3 from t";
        assertTrue(sqlparser.parse() == 0);

//        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//        TResultColumn column = select.getResultColumnList().getResultColumn(3);
//        TExpression expr = column.getExpr();
//        TConstant constant = expr.getConstantOperand();
//        System.out.println(constant.toString());
    }

}
