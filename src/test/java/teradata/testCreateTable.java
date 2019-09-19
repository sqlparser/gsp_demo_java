package test.teradata;
/*
 * Date: 13-8-28
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.teradata.TIndexDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void testIndexDefinition(){

    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
    sqlparser.sqltext = "CREATE TABLE Sales\n" +
            "(storeid INTEGER NOT NULL,\n" +
            "productid INTEGER NOT NULL,\n" +
            "salesdate DATE FORMAT 'yyyy-mm-dd' NOT NULL,\n" +
            "totalrevenue DECIMAL(13,2),\n" +
            "totalsold INTEGER,\n" +
            "note VARCHAR(256))\n" +
            "UNIQUE PRIMARY INDEX (storeid, productid, salesdate)\n" +
            "PARTITION BY (\n" +
            "RANGE_N(salesdate BETWEEN\n" +
            "DATE '2002-01-01' AND DATE '2008-12-31'\n" +
            "EACH INTERVAL '1' YEAR),\n" +
            "RANGE_N(storeid BETWEEN 1 AND 300 EACH 100),\n" +
            "RANGE_N(productid BETWEEN 1 AND 400 EACH 100));";
    assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("Sales"));
        assertTrue(createTable.getColumnList().size() == 6 );

        TIndexDefinition indexDefinition = createTable.getIndexDefinitions().getElement(0);
        assertTrue(indexDefinition.isPrimary());
        assertTrue(indexDefinition.isUnique());
        assertTrue(indexDefinition.getIndexColumns().size() == 3);
        assertTrue(indexDefinition.getIndexColumns().getObjectName(0).toString().equalsIgnoreCase("storeid"));
        assertTrue(indexDefinition.getPartitionExprList().size() == 3);
        TExpression partitionExpr = indexDefinition.getPartitionExprList().getExpression(0);
        assertTrue(partitionExpr.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall = partitionExpr.getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.range_n_t);
        TExpression betweenExpr = functionCall.getBetweenExpr();
        assertTrue(betweenExpr.getBetweenOperand().toString().equalsIgnoreCase("salesdate"));
        assertTrue(betweenExpr.getLeftOperand().toString().equalsIgnoreCase("DATE '2002-01-01'"));
        assertTrue(betweenExpr.getRightOperand().toString().equalsIgnoreCase("DATE '2008-12-31'"));
        TExpression rangSize = functionCall.getRangeSize();
        //System.out.println(rangSize.toString());
        assertTrue(rangSize.toString().equalsIgnoreCase("INTERVAL '1' YEAR"));
    }


    public void testSubquery(){

    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
    sqlparser.sqltext = "CREATE VOLATILE TABLE MYREC_V AS (\n" +
            "       WITH RECURSIVE MYREC( COL1) AS\n" +
            "       ( SELECT XX.COL1\n" +
            "              FROM (\n" +
            "                                          SELECT COL1,\n" +
            "                                                               TRIM(MIN(CAST(CAST(L1_ID AS BIGINT) AS VARCHAR(100)))) AS COL2\n" +
            "                                          FROM DB2.TABLE2\n" +
            "                                          GROUP BY 1\n" +
            "                                   ) XX\n" +
            "              JOIN DB1.TABLE1 C\n" +
            "                     ON XX.COL2 = C.COL2\n" +
            " )\n" +
            "       SELECT COL1\n" +
            "       FROM MYREC\n" +
            ")WITH DATA PRIMARY INDEX(COL1) ON COMMIT PRESERVE ROWS;";
    assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("MYREC_V"));

        assertTrue(createTable.getSubQuery().getCteList().size() == 1);
        TCTE cte =   createTable.getSubQuery().getCteList().getCTE(0);
        assertTrue(cte.getSubquery().toString().equalsIgnoreCase("( SELECT XX.COL1\n" +
                "              FROM (\n" +
                "                                          SELECT COL1,\n" +
                "                                                               TRIM(MIN(CAST(CAST(L1_ID AS BIGINT) AS VARCHAR(100)))) AS COL2\n" +
                "                                          FROM DB2.TABLE2\n" +
                "                                          GROUP BY 1\n" +
                "                                   ) XX\n" +
                "              JOIN DB1.TABLE1 C\n" +
                "                     ON XX.COL2 = C.COL2\n" +
                " )"));

    }

    public void testTableKind() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);

        sqlparser.sqltext = "create volatile table schema.vt (id int, name varchar(1))";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("schema.vt"));
        assertTrue(createTable.getTableKinds().toString().equalsIgnoreCase("[etkVolatile]"));

        sqlparser.sqltext = "create table schema.vt (id int, name varchar(1));";
        assertTrue(sqlparser.parse() == 0);
        createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("schema.vt"));
        assertTrue(createTable.getTableKinds().size() == 0);

        sqlparser.sqltext = "create multiset volatile table vt (id int, name varchar(1));";
        assertTrue(sqlparser.parse() == 0);
        createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("vt"));
        assertTrue(createTable.getTableKinds().toString().equalsIgnoreCase("[etkVolatile, etkMultiset]"));

    }

}

