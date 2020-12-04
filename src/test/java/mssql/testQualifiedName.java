package mssql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testQualifiedName extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE VIEW Customers\n" +
                "AS\n" +
                "--Select from local member table.\n" +
                "SELECT *\n" +
                "FROM CompanyData.dbo.Customers_33\n" +
                "UNION ALL\n" +
                "--Select from member table on Server2.\n" +
                "SELECT *\n" +
                "FROM Server2.CompanyData.dbo.Customers_66\n" +
                "UNION ALL\n" +
                "--Select from mmeber table on Server3.\n" +
                "SELECT *\n" +
                "FROM Server3.CompanyData.dbo.Customers_99";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateview);
        TCreateViewSqlStatement viewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = viewSqlStatement.getSubquery();
        TSelectSqlStatement select1 = select.getLeftStmt().getRightStmt();
        TObjectName tableName = select1.tables.getTable(0).getTableName();
       assertTrue(tableName.getServerToken().toString().equalsIgnoreCase("Server2"));
    }

}
