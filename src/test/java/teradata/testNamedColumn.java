package teradata;


import gudusoft.gsqlparser.EDataTypeAttribute;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.teradata.TDataConversion;
import gudusoft.gsqlparser.nodes.teradata.TDataConversionItem;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testNamedColumn extends TestCase {

    public void test1(){

        // The Resolver analyzes this statement as follows:
        // 1 Look for X as a column in table T.
        // 2 If X is not found, then try to locate column X in view V (the named object).

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE VIEW V AS\n" +
                "SELECT a*5+3 (NAMED X), x*2 (NAMED Y)\n" +
                "FROM T;";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = createView.getSubquery();
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(1);
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.arithmetic_times_t);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("x*2 (NAMED Y)"));
     //   System.out.print(resultColumn.getExpr()..toString());
        TAliasClause a = resultColumn.getAliasClause();
        //System.out.println(a.toString());
        assertTrue(a.getAliasName().toString().equalsIgnoreCase("Y"));

    }

    public void test2(){
        // CREATE TABLE T (a INT, b INT);
        // Note that the phrase b (NAMED Y) resolves to T.b.


        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE VIEW V AS\n" +
                "SELECT a (NAMED b), b (NAMED y)\n" +
                "FROM T;";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = createView.getSubquery();
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(resultColumn.getExpr().getObjectOperand().toString().equalsIgnoreCase("a"));
        //System.out.print(resultColumn.getExpr()..toString());
        TAliasClause a = resultColumn.getAliasClause();
        assertTrue(a.getAliasName().toString().equalsIgnoreCase("b"));

    }

    // mantis: #598
    public void test3() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT 'a' || 'b' (NAMED \"x\");";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.concatenate_t);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("'a' || 'b' (NAMED \"x\")"));
        TAliasClause a = resultColumn.getAliasClause();
        assertTrue(a.getAliasName().toString().equalsIgnoreCase("\"x\""));

//        TExpression right = resultColumn.getExpr().getRightOperand();
   //     System.out.print(a.getAliasName().toString());
    }

    public void test4() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT x+y (NAMED \"x\");";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
       // assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.concatenate_t);
//        System.out.print(resultColumn.getExpr().toString());
//        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("x+y"));
        TAliasClause a = resultColumn.getAliasClause();
        assertTrue(a.getAliasName().toString().equalsIgnoreCase("\"x\""));

    }

    public void test11(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select cast(cal_dt as date) (named cal_dt1) from table1;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("cast(cal_dt as date) (named cal_dt1)"));
        TAliasClause a = resultColumn.getAliasClause();
        assertTrue(a.getAliasName().toString().equalsIgnoreCase("cal_dt1"));

    }

    public void test12(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT\n" +
                "((lastresptime - starttime) hour(2) to second) (Named ElapsedTime),\n" +
                "FROM DBC.DBQLogTbl";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("((lastresptime - starttime) hour(2) to second) (Named ElapsedTime)"));
        TDataConversion dataConversion = resultColumn.getExpr().getDataConversions().get(0);
        TDataConversionItem dataConversionItem = dataConversion.getDataConversionItems().get(0);
        assertTrue(dataConversionItem.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        assertTrue(dataConversionItem.getDatatypeAttribute().getAttributeType() == EDataTypeAttribute.named_t);
        assertTrue(dataConversionItem.getDatatypeAttribute().getNamedName().toString().equalsIgnoreCase("ElapsedTime"));

//        TExplicitDataTypeConversion dataTypeConversion = resultColumn.getExpr().getDataTypeConversionList().getElement(0);
//        TDatatypeAttribute datatypeAttribute = dataTypeConversion.getDataTypeAttributeList1().getElement(0);
//        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.named_t);
//        assertTrue(datatypeAttribute.getValue_identifier().toString().equalsIgnoreCase("ElapsedTime"));

        TExpression expression = resultColumn.getExpr().getLeftOperand();

        assertTrue(expression.getExpressionType() == EExpressionType.interval_t);

        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("ElapsedTime"));
        assertTrue(resultColumn.getAliasClause().getAliasName().toString().equalsIgnoreCase("ElapsedTime"));

    }

}