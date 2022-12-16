package teradata;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TDatatypeAttribute;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.nodes.teradata.TDataConversion;
import gudusoft.gsqlparser.nodes.teradata.TDataConversionItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDataConversion extends TestCase {

    public void test5 (){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT hire_date (time(6)) FROM Schema.table_name;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.simple_object_name_t);

        // data conversion, only one conversion
        assertTrue(expr.getDataConversions().size() == 1);
        TDataConversion dataConversion = expr.getDataConversions().get(0);

        // items in data conversion
        assertTrue(dataConversion.getDataConversionItems().size() == 1);

        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        TTypeName dataType = item.getDataType();
        assertTrue(dataType.getDataType() == EDataType.time_t);
    }


    public void test4 (){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select id + 1\n" +
                "from hr.TimeInfo\n" +
                "where id + 1 (integer) > 1";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getWhereClause().getCondition().getLeftOperand();

        // data conversion, only one conversion
        assertTrue(expr.getDataConversions().size() == 1);
        TDataConversion dataConversion = expr.getDataConversions().get(0);

        // items in data conversion
        assertTrue(dataConversion.getDataConversionItems().size() == 1);

        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        TTypeName dataType = item.getDataType();
        assertTrue(dataType.getDataType() == EDataType.integer_t);
    }


    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT a*5+3 (NAMED X), x*2 (NAMED Y)\n" +
                "    FROM T;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();

        // data conversion, only one conversion
        assertTrue(expr.getDataConversions().size() == 1);
        TDataConversion dataConversion = expr.getDataConversions().get(0);

        // items in data conversion
        assertTrue(dataConversion.getDataConversionItems().size() == 1);

        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        TDatatypeAttribute datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.named_t);
    }

    public void test0(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select col1 (DATE) from tab1";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();

        // data conversion, only one conversion
        assertTrue(expr.getDataConversions().size() == 1);
        TDataConversion dataConversion = expr.getDataConversions().get(0);

        // items in data conversion
        assertTrue(dataConversion.getDataConversionItems().size() == 1);

        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        TTypeName dataType = item.getDataType();
        assertTrue(dataType.getDataType() == EDataType.date_t);
    }

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT salary_amount (FORMAT '$,$$9,999.99') from t;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();

        // data conversion, only one conversion
        assertTrue(expr.getDataConversions().size() == 1);
        TDataConversion dataConversion = expr.getDataConversions().get(0);

        // items in data conversion
        assertTrue(dataConversion.getDataConversionItems().size() == 1);

        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        TDatatypeAttribute datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.format_t);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select id (FORMAT '$,$$9,999.99'  ) (Title 'abd')  , myName (varchar(1),uppercase, CASESPECIFIC, Title 'ssss'), offset (interval year) \n" +
                "from hr.TimeInfo;\n";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        // expr in the first column
        TExpression expr0 = select.getResultColumnList().getResultColumn(0).getExpr();

        // 2 data conversions
        assertTrue(expr0.getDataConversions().size() == 2);

        // first data conversion: (FORMAT '$,$$9,999.99'  )
        TDataConversion dataConversion0 = expr0.getDataConversions().get(0);
        // items in data conversion
        assertTrue(dataConversion0.getDataConversionItems().size() == 1);
        // the first item in data conversion, here is the FORMAT data attribute
        TDataConversionItem item = dataConversion0.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        TDatatypeAttribute datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.format_t);

        // the second conversion: (Title 'abd')
        TDataConversion dataConversion1 = expr0.getDataConversions().get(1);
        // items in data conversion
        assertTrue(dataConversion1.getDataConversionItems().size() == 1);
        // the first item in data conversion, here is the FORMAT data attribute
        item = dataConversion1.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.title_t);


        // expr in the second column
        TExpression expr1 = select.getResultColumnList().getResultColumn(1).getExpr();

        // 1 data conversion: (varchar(1),uppercase, CASESPECIFIC, Title 'ssss')
        assertTrue(expr1.getDataConversions().size() == 1);

        // first data conversion: (varchar(1),uppercase, CASESPECIFIC, Title 'ssss')
        dataConversion0 = expr1.getDataConversions().get(0);
        // items in data conversion
        assertTrue(dataConversion0.getDataConversionItems().size() == 4);
        // the first item in data conversion: varchar(1)
        item = dataConversion0.getDataConversionItems().get(0);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        TTypeName typeName = item.getDataType();
        assertTrue(typeName.getDataType() == EDataType.varchar_t);

        // the second item in data conversion: uppercase
        item = dataConversion0.getDataConversionItems().get(1);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.uppercase_t);

        // the third item in data conversion: CASESPECIFIC
        item = dataConversion0.getDataConversionItems().get(2);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.casespecific_t);

        // the fourth item in data conversion: Title 'ssss'
        item = dataConversion0.getDataConversionItems().get(3);
        assertTrue(item.getDataConversionType() == TDataConversionItem.EDataConversionype.dataAttribute);
        datatypeAttribute = item.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.title_t);
    }


}
