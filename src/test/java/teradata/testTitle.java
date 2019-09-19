package test.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;



public class testTitle extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT (1000/salary)*100 (FORMAT 'zz9%')\n" +
                "(TITLE 'Percent Incr')\n" +
                "FROM employee\n" +
                "WHERE empno = 10019 ;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("(1000/salary)*100"));
//        assertTrue(resultColumn.getDataTypeConversion().getDataTypeAttributeList1().size() == 2);
//        TDatatypeAttribute datatypeAttribute0 =  resultColumn.getDataTypeConversion().getDataTypeAttributeList1().getElement(0);
//        assertTrue(datatypeAttribute0.getAttributeType() == EDataTypeAttribute.format_t);
//        assertTrue(datatypeAttribute0.getAttributeValue().equalsIgnoreCase("'zz9%'"));
//
//        TDatatypeAttribute datatypeAttribute1 =  resultColumn.getDataTypeConversion().getDataTypeAttributeList1().getElement(1);
//        assertTrue(datatypeAttribute1.getAttributeType() == EDataTypeAttribute.title_t);
//        assertTrue(datatypeAttribute1.getAttributeValue().equalsIgnoreCase("'Percent Incr'"));

    }

}