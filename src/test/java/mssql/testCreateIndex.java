package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;


public class testCreateIndex extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE NONCLUSTERED INDEX IX_TransactionHistory_ReferenceOrderID\n" +
                "ON Production.TransactionHistory (ReferenceOrderID)\n" +
                "ON TransactionsPS1 (TransactionDate);";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement createIndexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createIndexSqlStatement.isNonClustered());
        assertTrue(createIndexSqlStatement.getIndexName().toString().equalsIgnoreCase("IX_TransactionHistory_ReferenceOrderID"));
        assertTrue(createIndexSqlStatement.getTableName().toString().equalsIgnoreCase("Production.TransactionHistory"));
        assertTrue(createIndexSqlStatement.getColumnNameList().getOrderByItem(0).toString().equalsIgnoreCase("ReferenceOrderID"));
        assertTrue(createIndexSqlStatement.getFilegroupOrPartitionSchemeName().toString().equalsIgnoreCase("TransactionsPS1"));
        assertTrue(createIndexSqlStatement.getPartitionSchemeColumns().getObjectName(0).toString().equalsIgnoreCase("TransactionDate"));

    }
}
