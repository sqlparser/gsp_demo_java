package teradata;
/*
 * Date: 14-6-16
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.teradata.TDataConversion;
import gudusoft.gsqlparser.nodes.teradata.TDataConversionItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testFunctionINResultColumn extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT a.acc_co_no (SMALLINT)\n" +
                "FROM   a";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn rc = select.getResultColumnList().getResultColumn(0);
        assertTrue(rc.toString().equalsIgnoreCase("a.acc_co_no (SMALLINT)"));
        TExpression e = rc.getExpr();
        //System.out.println(e.toString());
        assertTrue(e.toString().equalsIgnoreCase("a.acc_co_no (SMALLINT)"));
        assertTrue(e.getObjectOperand().toString().equalsIgnoreCase("a.acc_co_no"));
        TDataConversion dataConversion = e.getDataConversions().get(0);
        TDataConversionItem dataConversionItem = dataConversion.getDataConversionItems().get(0);
        assertTrue(dataConversionItem.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        assertTrue(dataConversionItem.getDataType().getDataType() == EDataType.smallint_t);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select col1 (DATE), col2 from tab1";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn rc = select.getResultColumnList().getResultColumn(0);
        assertTrue(rc.toString().equalsIgnoreCase("col1 (DATE)"));
        TExpression e = rc.getExpr();
       // System.out.println(e.getExpressionType()+e.toString());
        assertTrue(e.toString().equalsIgnoreCase("col1 (DATE)"));
        assertTrue(e.getObjectOperand().toString().equalsIgnoreCase("col1"));

        TDataConversion dataConversion = e.getDataConversions().get(0);
        TDataConversionItem dataConversionItem = dataConversion.getDataConversionItems().get(0);
        assertTrue(dataConversionItem.getDataConversionType() == TDataConversionItem.EDataConversionype.dataType);
        assertTrue(dataConversionItem.getDataType().getDataType() == EDataType.date_t);

    }

}
