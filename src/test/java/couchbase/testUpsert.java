package test.couchbase;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.couchbase.TObjectConstruct;
import gudusoft.gsqlparser.nodes.couchbase.TPair;
import gudusoft.gsqlparser.stmt.couchbase.TUpsertStmt;
import junit.framework.TestCase;

public class testUpsert extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "UPSERT INTO product (KEY, VALUE) VALUES (\"odwalla-juice1\", { \"productId\": \"odwalla-juice1\", \n" +
                "      \"unitPrice\": 5.40, \"type\": \"product\", \"color\":\"red\"}) RETURNING * ;";

        assertTrue(sqlparser.parse() == 0);
        TUpsertStmt upsertStmt = (TUpsertStmt)sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("product"));
        TMultiTarget multiTarget = upsertStmt.getValues().getMultiTarget(0);
        TResultColumn resultColumn0 = multiTarget.getColumnList().getResultColumn(0);
        TExpression expression0 = resultColumn0.getExpr();
        assertTrue(expression0.toString().equalsIgnoreCase("\"odwalla-juice1\""));

        TResultColumn resultColumn1 = multiTarget.getColumnList().getResultColumn(1);
        TExpression expression1 = resultColumn1.getExpr();
        assertTrue(expression1.getExpressionType() == EExpressionType.objectConstruct_t);
        TObjectConstruct objectConstruct = expression1.getObjectConstruct();
        assertTrue(objectConstruct.getPairs().size() == 4);
        TPair pair = objectConstruct.getPairs().getElement(0);
        assertTrue(pair.getKeyName().toString().equalsIgnoreCase("\"productId\""));
        assertTrue(pair.getKeyValue().toString().equalsIgnoreCase("\"odwalla-juice1\""));

    }
}
