package gudusoft.gsqlparser.teradataTest;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.teradata.TDataConversion;
import gudusoft.gsqlparser.nodes.teradata.TDataConversionItem;
import junit.framework.TestCase;
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

        assertTrue(expr.getDataConversions().size() == 2);
        TDataConversion dataConversion0 = expr.getDataConversions().get(0);
        assertTrue(dataConversion0.getDataConversionItems().size() == 1);
        assertTrue(dataConversion0.getDataConversionItems().get(0).getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        assertTrue(dataConversion0.getDataConversionItems().get(0).getDatatypeAttribute().getAttributeType() == EDataTypeAttribute.format_t);

        TDataConversion dataConversion1 = expr.getDataConversions().get(1);
        assertTrue(dataConversion1.getDataConversionItems().size() == 1);
        assertTrue(dataConversion1.getDataConversionItems().get(0).getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        assertTrue(dataConversion1.getDataConversionItems().get(0).getDatatypeAttribute().getAttributeType() == EDataTypeAttribute.title_t);
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
        assertTrue(expr.toString().equalsIgnoreCase("SUM(Salary)+1 (FORMAT '$$99,999.99') (CHAR(12), UC)"));
        assertTrue(expr.getDataConversions().size() == 2);

        TDataConversion dataConversion0 = expr.getDataConversions().get(0); //(FORMAT '$$99,999.99')
        assertTrue(dataConversion0.getDataConversionItems().size() == 1);
        TDataConversionItem item = dataConversion0.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        assertTrue(item.getDatatypeAttribute().getAttributeType() == EDataTypeAttribute.format_t);

        TDataConversion dataConversion1 = expr.getDataConversions().get(1); // (CHAR(12), UC)
        assertTrue(dataConversion1.getDataConversionItems().size() == 2);
        TDataConversionItem item0 = dataConversion1.getDataConversionItems().get(0); // CHAR(12)
        assertTrue(item0.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        TTypeName dataType = item0.getDataType();
        assertTrue(dataType.getDataType() == EDataType.char_t);

        TDataConversionItem item1 = dataConversion1.getDataConversionItems().get(1); // UC
        assertTrue(item1.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        assertTrue(item1.getDatatypeAttribute().getAttributeType() == EDataTypeAttribute.uppercase_t);

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
