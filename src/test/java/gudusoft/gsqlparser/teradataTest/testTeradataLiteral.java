package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 2010-9-25
 * Time: 9:37:10
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.teradata.TDataDefinition;
import junit.framework.TestCase;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

public class testTeradataLiteral extends TestCase {

    public void testDatetime(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select TIMESTAMP '1999-07-01 15:00:00-08:00',DATE '1998-06-01',TIME '10:35:00' from dual;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        assertTrue(column0.toString().equalsIgnoreCase("TIMESTAMP '1999-07-01 15:00:00-08:00'"));
        //System.out.println(column1.getExpr().getExpressionType());
        assertTrue(column1.toString().equalsIgnoreCase("DATE '1998-06-01'"));
        assertTrue(column2.toString().equalsIgnoreCase("TIME '10:35:00'"));
    }
    
    public void testInterval(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT INTERVAL - '2' YEAR + CURRENT_DATE;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        //TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        //TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        assertTrue(column0.getExpr().getLeftOperand().getExpressionType() == EExpressionType.simple_constant_t);
        assertTrue(column0.getExpr().getLeftOperand().getConstantOperand().toString().equalsIgnoreCase("INTERVAL - '2' YEAR"));
        //assertTrue(column1.toString().equalsIgnoreCase("DATE '1998-06-01'"));
        //assertTrue(column2.toString().equalsIgnoreCase("TIME '10:35:00'"));
    }

    public void testCharacter(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select 'He said ''yes'' to her question'" +
                ",''" +
                ",G'xxx'" +
                ",_Latin'abc'" +
                ",_Unicode'cde'" +
                ",X'0F'";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        //System.out.println(column0.toString());
        assertTrue(column0.toString().equalsIgnoreCase("'He said ''yes'' to her question'"));
        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        assertTrue(column1.toString().equalsIgnoreCase("''"));
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        assertTrue(column2.toString().equalsIgnoreCase("G'xxx'"));
        TResultColumn column3 = select.getResultColumnList().getResultColumn(3);
        assertTrue(column3.toString().equalsIgnoreCase("_Latin'abc'"));

        //System.out.println(column4.toString());

        TResultColumn column4 = select.getResultColumnList().getResultColumn(4);
        assertTrue(column4.toString().equalsIgnoreCase("_Unicode'cde'"));
      //  System.out.println(column4.toString());

        TResultColumn column5 = select.getResultColumnList().getResultColumn(5);
        assertTrue(column5.toString().equalsIgnoreCase("X'0F'"));

        sqlparser.sqltext = "select * from t where CodeVal = '7879'X"+
                " and codeVal= '55'XBF";
        assertTrue(sqlparser.parse() == 0);

        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TExpression whereCondition = select.getWhereClause().getCondition();

        //System.out.println(whereCondition.toString());

    }

    public void testDatatype(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "create table t(\n" +
                "\tSalary DECIMAL(8,2) FORMAT 'ZZZ,ZZ9.99'\n" +
                "\tCHECK (Salary BETWEEN 1.00 AND 999000.00),\n" +
                "\tid decimal(5,4)\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition column = createTable.getColumnList().getColumn(0);

        TTypeName datatype = column.getDatatype();

        assertTrue(datatype.getDataType() == EDataType.decimal_t);

        //System.out.println(column.getDataDefinitions().size());
        assertTrue(column.getDataDefinitions().size() == 2);
        TDataDefinition dataDefinition = column.getDataDefinitions().get(0);
        assertTrue(dataDefinition.getDataDefinitionType() == TDataDefinition.EDataDefinitionType.dataAttribute);


        TDatatypeAttribute datatypeAttribute = dataDefinition.getDatatypeAttribute();
        assertTrue(datatypeAttribute.getAttributeType() == EDataTypeAttribute.format_t);

        //System.out.println(datatypeAttribute.toString());
        assertTrue(datatypeAttribute.toString().equalsIgnoreCase("FORMAT 'ZZZ,ZZ9.99'"));

        assertTrue(column.getConstraints().getConstraint(0).getConstraint_type() == EConstraintType.check);
    }


}
