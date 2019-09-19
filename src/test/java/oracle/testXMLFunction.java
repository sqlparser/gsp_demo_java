package test.oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.oracle.TXMLAttributesClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testXMLFunction extends TestCase {

    public static void testXMLElement(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select\n" +
                "  column_a,\n" +
                "  xmlelement(\"ns1:item\",\n" +
                "   xmlelement(\"ns2:item1\",\n" +
                "     xmlattributes(\n" +
                "      attr1 \"attribute\",\n" +
                "      nvl((select code from mapping where code = 'Default'),'0') \"code\"\n" +
                "     ),\n" +
                "   xmlelement(\"ns2:item2\",\n" +
                "     xmlattributes('type' \"type\"),\n" +
                "     xmlagg(factor order by name)))) sample_item\n" +
                "from TABLE_ABC";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(1);
        TExpression expr = column.getExpr();
        TFunctionCall f = expr.getFunctionCall();
        assertTrue(f.getFunctionType() == EFunctionType.xmlelement_t);
        assertTrue(f.getXMLElementNameExpr().toString().endsWith("\"ns1:item\""));
        assertTrue(f.getXMLElementValueExprList().size() == 1);
        TExpression expr1 = f.getXMLElementValueExprList().getResultColumn(0).getExpr();
        assertTrue(expr1.getExpressionType() == EExpressionType.function_t);
        f = expr1.getFunctionCall();
        assertTrue(f.getFunctionType() == EFunctionType.xmlelement_t);
        assertTrue(f.getXMLElementNameExpr().toString().endsWith("\"ns2:item1\""));
        TXMLAttributesClause xmlac = f.getXMLAttributesClause();
        assertTrue(xmlac.getValueExprList().size() == 2);
        assertTrue(xmlac.getValueExprList().getResultColumn(0).getExpr().toString().endsWith("attr1"));
        assertTrue(xmlac.getValueExprList().getResultColumn(0).getAliasClause().toString().endsWith("\"attribute\""));
        assertTrue(xmlac.getValueExprList().getResultColumn(1).getAliasClause().toString().endsWith("\"code\""));
        expr1 = xmlac.getValueExprList().getResultColumn(1).getExpr();
        assertTrue(expr1.getExpressionType() == EExpressionType.function_t);
        f = expr1.getFunctionCall();
        assertTrue(f.getFunctionName().toString().endsWith("nvl"));
        assertTrue(f.getArgs().getExpression(0).getExpressionType() == EExpressionType.subquery_t);
        TSelectSqlStatement subquery = f.getArgs().getExpression(0).getSubQuery();
        assertTrue(subquery.getWhereClause().toString().endsWith("code = 'Default'"));

    }

    public static void testXMLSERIALIZE(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT columna,\n" +
                "    XMLSERIALIZE(CONTENT DECODE(position_moniker, NULL, NULL, (SELECT DECODE(COUNT(*), 0, NULL, XMLELEMENT(\"Item\", XMLAGG(xml)))\n" +
                "                                                   from generic_item gi\n" +
                "                                                   WHERE gi.item_id = 10)) AS CLOB) items,\n" +
                "      columnc\n" +
                "  FROM TABLE_A";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(1);
        TExpression expr = column.getExpr();
        TFunctionCall f = expr.getFunctionCall();
        assertTrue(f.getFunctionType() == EFunctionType.xmlserialize_t);
        expr = f.getExpr1();
        assertTrue(expr.getExpressionType() == EExpressionType.function_t);
        f = expr.getFunctionCall();
        assertTrue(f.getFunctionName().toString().equalsIgnoreCase("decode"));
        assertTrue(f.getArgs().size() == 4);
        assertTrue(f.getArgs().getExpression(3).getExpressionType() == EExpressionType.subquery_t);
        TSelectSqlStatement subquery = f.getArgs().getExpression(3).getSubQuery();
        assertTrue(subquery.tables.getTable(0).toString().equalsIgnoreCase("generic_item"));

    }


}
