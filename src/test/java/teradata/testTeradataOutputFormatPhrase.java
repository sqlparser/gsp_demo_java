package test.teradata;


import gudusoft.gsqlparser.EDataTypeAttribute;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.TDatatypeAttribute;
import gudusoft.gsqlparser.nodes.TExplicitDataTypeConversion;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTeradataOutputFormatPhrase extends TestCase {

    public void test11(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select b.c (FORMAT 'X(30)') (TITLE 'Internal Hex Representation of TableName') from b";
        boolean ret = sqlparser.parse() == 0;
        assertTrue(sqlparser.getErrormessage(),ret);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column0.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expr.getObjectOperand().toString().equalsIgnoreCase("b.c"));

        TExplicitDataTypeConversion dataTypeConversion = expr.getDataTypeConversionList().getElement(0);

        assertTrue(dataTypeConversion.getDataTypeAttributeList1().size() == 1);
        TDatatypeAttribute datatypeAttribute0 = dataTypeConversion.getDataTypeAttributeList1().getElement(0);
        assertTrue(datatypeAttribute0.getAttributeType() == EDataTypeAttribute.format_t);
        assertTrue(datatypeAttribute0.getAttributeValue().toString().equalsIgnoreCase ("'X(30)'"));

        dataTypeConversion = expr.getDataTypeConversionList().getElement(1);
        TDatatypeAttribute datatypeAttribute1 = dataTypeConversion.getDataTypeAttributeList1().getElement(0);
        assertTrue(datatypeAttribute1.getAttributeType() == EDataTypeAttribute.title_t);
        assertTrue(datatypeAttribute1.getAttributeValue().toString().equalsIgnoreCase ("'Internal Hex Representation of TableName'"));

    }

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT SUM(Salary) (FORMAT '$$99,999.99')\n" +
                "FROM Employee;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        sqlparser.sqltext = "SELECT SUM(Salary) (FORMAT '$$99,999.99') as alias1\n" +
                "FROM Employee;";
        assertTrue(sqlparser.parse() == 0);

        sqlparser.sqltext = "SELECT Salary (FORMAT '$$99,999.99') as alias1\n" +
                "FROM Employee;";
        assertTrue(sqlparser.parse() == 0);

        sqlparser.sqltext = "SELECT Salary (FORMAT '$$99,999.99') (CHAR(12), UC) as alias1\n" +
                "FROM Employee;";
        assertTrue(sqlparser.parse() == 0);

        sqlparser.sqltext = "SELECT SUM(Salary)+1 (FORMAT '$$99,999.99') (CHAR(12), UC) as alias1\n" +
                "FROM Employee;";
        assertTrue(sqlparser.parse() == 0);
        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column0.getExpr();
//        System.out.println(expr.toString());
//        System.out.println(column0.toString());
        assertTrue(expr.getExpressionType() == EExpressionType.arithmetic_plus_t);
        assertTrue(expr.toString().equalsIgnoreCase("SUM(Salary)+1"));
        assertTrue(expr.getDataTypeConversionList().size() == 2);

        TExplicitDataTypeConversion dataTypeConversion0 = expr.getDataTypeConversionList().getElement(0);
        assertTrue(dataTypeConversion0.getDataTypeAttributeList1().size() == 1);
        TDatatypeAttribute  datatypeAttribute = dataTypeConversion0.getDataTypeAttributeList1().getElement(0);
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.format_t);
        assertTrue(datatypeAttribute.getAttributeValue().equalsIgnoreCase("'$$99,999.99'"));

        TExplicitDataTypeConversion dataTypeConversion1 = expr.getDataTypeConversionList().getElement(1);
        assertTrue(dataTypeConversion1.getDataType().toString().equalsIgnoreCase("CHAR(12)"));
        assertTrue(dataTypeConversion1.getDataTypeAttributeList1().size() == 1);
        datatypeAttribute = dataTypeConversion1.getDataTypeAttributeList1().getElement(0);
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.uppercase_t);

    }



    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT 47.5(FORMAT 'zzzz'), 48.5(FORMAT 'zzzz') ;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT 13451 / 10000.000 (FORMAT 'zz.z');";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        //Use a statement like the following to display a date in uppercase:
        sqlparser.sqltext = "SELECT DATE (FORMAT 'MMMbdd,bYYYY') (CHAR(12), UC);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

    }


}
