package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCopyIntoStmt;
import junit.framework.TestCase;

public class testCopyIntoStmt  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "COPY INTO test_1 (Col_one default 'myStringDefault' 1, Col_two default 1 3)\n" +
                "FROM 'https://myaccount.blob.core.windows.net/myblobcontainer/folder1/'\n" +
                "WITH (\n" +
                "FILE_TYPE = 'CSV',\n" +
                "CREDENTIAL=(IDENTITY= 'Storage Account Key', SECRET='<Your_Account_Key>'),\n" +
                "--CREDENTIAL should look something like this:\n" +
                "--CREDENTIAL=(IDENTITY= 'Storage Account Key', SECRET='x6RWv4It5F2msnjelv3H4DA80n0PQW0daPdw43jM0nyetx4c6CpDkdj3986DX5AHFMIf/YN4y6kkCnU8lb+Wx0Pj+6MDw=='),\n" +
                "FIELDQUOTE = '\"',\n" +
                "FIELDTERMINATOR=',',\n" +
                "ROWTERMINATOR='0x0A',\n" +
                "ENCODING = 'UTF8',\n" +
                "FIRSTROW = 2\n" +
                ")";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCopyIntoStmt copyIntoStmt = (TMssqlCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getTablename().toString().equalsIgnoreCase("test_1"));
        assertTrue(copyIntoStmt.getColumnList().size() == 2);
        assertTrue(copyIntoStmt.getFromList().size() == 1);
        assertTrue(copyIntoStmt.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("Col_one"));
        assertTrue(copyIntoStmt.getFromList().get(0).toString().equalsIgnoreCase("'https://myaccount.blob.core.windows.net/myblobcontainer/folder1/'"));
        assertTrue(copyIntoStmt.getOptionNames().size() == 7);

//        for(int i=0;i<copyIntoStmt.getOptionNames().size();i++){
//            System.out.println(copyIntoStmt.getOptionNames().get(i)+"="+copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(i)));
//        }

        assertTrue(copyIntoStmt.getOptionNames().get(0).equalsIgnoreCase("FILE_TYPE"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(0)).equalsIgnoreCase("'CSV'"));

        assertTrue(copyIntoStmt.getOptionNames().get(1).equalsIgnoreCase("CREDENTIAL"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(1)).equalsIgnoreCase("(IDENTITY='Storage Account Key',SECRET='<Your_Account_Key>')"));

        assertTrue(copyIntoStmt.getOptionNames().get(2).equalsIgnoreCase("FIELDQUOTE"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(2)).equalsIgnoreCase("'\"'"));

        assertTrue(copyIntoStmt.getOptionNames().get(3).equalsIgnoreCase("FIELDTERMINATOR"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(3)).equalsIgnoreCase("','"));

        assertTrue(copyIntoStmt.getOptionNames().get(4).equalsIgnoreCase("ROWTERMINATOR"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(4)).equalsIgnoreCase("'0x0A'"));

        assertTrue(copyIntoStmt.getOptionNames().get(5).equalsIgnoreCase("ENCODING"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(5)).equalsIgnoreCase("'UTF8'"));

        assertTrue(copyIntoStmt.getOptionNames().get(6).equalsIgnoreCase("FIRSTROW"));
        assertTrue(copyIntoStmt.getOption(copyIntoStmt.getOptionNames().get(6)).equalsIgnoreCase("2"));

    }
}
