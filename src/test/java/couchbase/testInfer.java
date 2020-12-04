package couchbase;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.couchbase.TObjectConstruct;
import gudusoft.gsqlparser.stmt.couchbase.TInferKeyspaceStmt;
import junit.framework.TestCase;


public class testInfer extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "INFER `beer-sample` WITH {\"sample_size\":10000,\"num_sample_values\":1,\"similarity_metric\":0.0};";

        assertTrue(sqlparser.parse() == 0);
        TInferKeyspaceStmt inferKeyspaceStmt = (TInferKeyspaceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(inferKeyspaceStmt.getTargetTable().getTableName().toString().equalsIgnoreCase("`beer-sample`"));
        TExpression inferWith = inferKeyspaceStmt.getInferWith();
        assertTrue(inferWith.getExpressionType() == EExpressionType.objectConstruct_t);
        TObjectConstruct objectConstruct = inferWith.getObjectConstruct();
        assertTrue(objectConstruct.getPairs().size() == 3);
        assertTrue(objectConstruct.getPairs().getElement(0).getKeyName().toString().equalsIgnoreCase("\"sample_size\""));
        assertTrue(objectConstruct.getPairs().getElement(0).getKeyValue().toString().equalsIgnoreCase("10000"));

    }
}

