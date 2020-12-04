package dag;
/*
 * Date: 11-4-6
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectAccess;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testBuiltInFucntion extends TestCase {

    public static void testExtractXML(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
          sqlparser.sqltext = "SELECT warehouse_name,\n" +
                  "   EXTRACT(warehouse_spec, '/Warehouse/Docks')\n" +
                  "   \"Number of Docks\"\n" +
                  "   FROM warehouses\n" +
                  "   WHERE warehouse_name = 'San Francisco';";
          assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(1);
        TExpression expr = column.getExpr();
        TFunctionCall f = expr.getFunctionCall();
       assertTrue(f.getFunctionType() == EFunctionType.extractxml_t);
        assertTrue(f.getXMLType_Instance().toString().equalsIgnoreCase("warehouse_spec"));
       assertTrue(f.getXPath_String().toString().equalsIgnoreCase("'/Warehouse/Docks'"));
    }

    public static void testTreat(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
          sqlparser.sqltext = "SELECT name, TREAT(VALUE(p) AS employee_t).salary salary \n" +
                  "   FROM persons p;";
          assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(1);
        TExpression expr = column.getExpr();
        TFunctionCall f = expr.getFunctionCall();
        assertTrue(expr.getExpressionType() == EExpressionType.object_access_t);
       //assertTrue(f.getFuncType() == TFunctionCall.fntTreat);

    }

    public static void testTranslate(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
          sqlparser.sqltext = "create view wm$all_locks_view as select t.table_owner, t.table_name,\n" +
                  "       decode(sys.lt_ctx_pkg.getltlockinfo(translate(t.info using char_cs),'row_lockmode'), 'e', 'exclusive', 's', 'shared') lock_mode,\n" +
                  "       sys.lt_ctx_pkg.getltlockinfo(translate(t.info using char_cs),'row_lockuser') lock_owner,\n" +
                  "       sys.lt_ctx_pkg.getltlockinfo(translate(t.info using char_cs),'row_lockstate') locking_state\n" +
                  "from (select table_owner, table_name, info from\n" +
                  "      table( cast(sys.ltadm.get_lock_table() as wm$lock_table_type))) t\n" +
                  "with read only";
         assertTrue(sqlparser.parse()==0);
    }

    public static void testXmlAgg(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
          sqlparser.sqltext = "SELECT\n" +
                  "   O.OBJECT_ID,\n" +
                  "   '|' || RTRIM (XMLAGG (XMLELEMENT (K, O.KEY_1 || '|')).EXTRACT ('//text()'),\n" +
                  "'|') || '|' AS TEXT_KEY\n" +
                  "FROM DAG_OBJECT_FACT O";
         assertTrue(sqlparser.parse()==0);

        TResultColumn rc = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(1);
        TExpression expression = rc.getExpr().getLeftOperand().getRightOperand();
        TFunctionCall functionCall = expression.getFunctionCall();
        TExpression xmlaggExpr = functionCall.getArgs().getExpression(0);
        assertTrue(xmlaggExpr.getExpressionType() == EExpressionType.object_access_t);
        TObjectAccess objectAccess = xmlaggExpr.getObjectAccess();
        TFunctionCall xmlelement = objectAccess.getObjectExpr().getFunctionCall().getArgs().getExpression(0).getFunctionCall();
        //assertTrue(xmlelement.getArgs().size() == 1);
        assertTrue(xmlelement.getXMLElementNameExpr().toString().equalsIgnoreCase("K"));
        TResultColumn resultColumn = xmlelement.getXMLElementValueExprList().getResultColumn(0);
        TExpression expression1 = resultColumn.getExpr();
        assertTrue(expression1.toString().equalsIgnoreCase("O.KEY_1 || '|'"));

    }

}
