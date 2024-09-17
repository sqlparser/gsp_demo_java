package gudusoft.gsqlparser.mysqlTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testNullEncoded extends TestCase {

    public void testIdentifierStartWithNumber(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "INSERT INTO pet VALUES ('Fluffy','Harold','cat','f',\\N,'1993-02-04',NULL);";
        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertSqlStatement.getValues().getMultiTarget(0).getColumnList().size() == 7);
        assertTrue(insertSqlStatement.getValues().getMultiTarget(0).getColumnList().getResultColumn(4).getExpr().getExpressionType()== EExpressionType.simple_constant_t );
        assertTrue(insertSqlStatement.getValues().getMultiTarget(0).getColumnList().getResultColumn(6).getExpr().getExpressionType() == EExpressionType.simple_constant_t);
        assertTrue(insertSqlStatement.getValues().getMultiTarget(0).getColumnList().getResultColumn(4).getExpr().getConstantOperand().toString().equalsIgnoreCase("\\N"));
        assertTrue(insertSqlStatement.getValues().getMultiTarget(0).getColumnList().getResultColumn(6).getExpr().getConstantOperand().toString().equalsIgnoreCase("NULL"));
    }

}
