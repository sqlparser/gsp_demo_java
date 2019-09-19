package test.teradata;
/*
 * Date: 2010-9-17
 * Time: 10:36:52
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;

public class testTeradataGetRawStatements extends TestCase {

    public void test1(){
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
    sqlparser.sqltext = "USING (var_1 CHARACTER, var_2 CHARACTER, var_3 CHARACTER)\n" +
            "INSERT INTO TestTabU (c1) VALUES (:var_1)\n" +
            "; INSERT INTO TestTabU (c1) VALUES (:var_2)\n" +
            "; INSERT INTO TestTabU (c1) VALUES (:var_3)\n" +
            "; UPDATE TestTabU\n" +
            "SET c2 = c1 + 1\n" +
            "WHERE c1 = :var_1\n" +
            "; UPDATE TestTabU\n" +
            "SET c2 = c1 + 1\n" +
            "WHERE c1 = :var_2\n" +
            "; UPDATE TestTabU\n" +
            "SET c2 = c1 + 1\n" +
            "WHERE c1 = :var_3 ;\n" +
            "\n" +
            "USING (zonedec CHARACTER(4))\n" +
            "BEGIN TRANSACTION\n" +
            "; INSERT INTO Dectest (Colz = :zonedec (DECIMAL(4), FORMAT\n" +
            "��9999S��)) ;\n" +
            "\n" +
            "USING\n" +
            "cunicode(CHAR(10))\n" +
            ",clatin(CHAR(10))\n" +
            ",csjis(CHAR(10))\n" +
            ",cgraphic(GRAPHIC(10))\n" +
            ",cgraphic2(CHAR(10) CHARACTER SET GRAPHIC))\n" +
            "INSERT INTO table_1(:cunicode, :clatin, :csjis, :cgraphic,\n" +
            ":cgraphic2);";
    sqlparser.getrawsqlstatements();
    assertTrue(sqlparser.sqlstatements.size() == 9);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatausing);
        assertTrue(sqlparser.sqlstatements.get(1).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(sqlparser.sqlstatements.get(2).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(sqlparser.sqlstatements.get(3).sqlstatementtype == ESqlStatementType.sstupdate);
        assertTrue(sqlparser.sqlstatements.get(4).sqlstatementtype == ESqlStatementType.sstupdate);
        assertTrue(sqlparser.sqlstatements.get(5).sqlstatementtype == ESqlStatementType.sstupdate);
        assertTrue(sqlparser.sqlstatements.get(6).sqlstatementtype == ESqlStatementType.sstteradatausing);
        assertTrue(sqlparser.sqlstatements.get(7).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(sqlparser.sqlstatements.get(8).sqlstatementtype == ESqlStatementType.sstteradatausing);
    }

    public void testnullstatement(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "/* This example shows a comment followed by\n" +
                "a semicolon used as a null statement */\n" +
                "; UPDATE Pay_Test SET a = c;\n" +
                "\n" +
                "\n" +
                "/* This example shows a semicolon used as a null\n" +
                "statement and as a statement separator */\n" +
                "; UPDATE Payroll_Test SET Name = 'Wedgewood A'\n" +
                "WHERE Name = 'Wedgewood A'\n" +
                "; SELECT a from b\n" +
                "-- This example shows the use of an ANSI component\n" +
                "-- used as a null statement and statement separator ;\n" +
                "\n" +
                "\n" +
                ";DROP TABLE temp_payroll;";
        sqlparser.getrawsqlstatements();
        //System.out.println(sqlparser.sqlstatements.size());
        //System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        //System.out.println(sqlparser.sqlstatements.get(1).sqlstatementtype);
        //System.out.println(sqlparser.sqlstatements.get(2).sqlstatementtype);
        //System.out.println(sqlparser.sqlstatements.get(3).sqlstatementtype);
        assertTrue(sqlparser.sqlstatements.size() == 4);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "/*\n" +
                "Valued case expression\n" +
                "Searched case expression\n" +
                "The two special shorthand variants of case expression:\n" +
                "coalesce\n" +
                "nullif\n" +
                "*/\n" +
                "\n" +
                "-- valued\n" +
                "\n" +
                "SELECT SUM(CASE part\n" +
                "WHEN '1'\n" +
                "THEN cost\n" +
                "ELSE 0\n" +
                "END\n" +
                ")/SUM(cost)\n" +
                "FROM t;\n" +
                "\n" +
                "SELECT prodID, (CASE prodSTATUS\n" +
                "WHEN 1\n" +
                "THEN 'SENT'\n" +
                "ELSE 'BACK ORDER'\n" +
                "END) || ' STATUS'\n" +
                "FROM t1;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE x = CASE y\n" +
                "WHEN 2\n" +
                "THEN 1001\n" +
                "WHEN 5\n" +
                "THEN 1002\n" +
                "END;\n" +
                "\n" +
                "-- searched\n" +
                "SELECT SUM(CASE\n" +
                "WHEN part=��1��\n" +
                "THEN cost\n" +
                "ELSE 0\n" +
                "END\n" +
                ") / SUM(cost)\n" +
                "FROM t;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE x = CASE\n" +
                "WHEN y=2\n" +
                "THEN 1\n" +
                "WHEN (z=3 AND y=5)\n" +
                "THEN 2\n" +
                "END;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE x = CASE\n" +
                "WHEN y=2\n" +
                "THEN 1\n" +
                "ELSE 2\n" +
                "END;\n" +
                "\n" +
                "SELECT MONTH, SUM(CASE\n" +
                "WHEN Region=��NE��\n" +
                "THEN Revenue\n" +
                "ELSE 0\n" +
                "END),\n" +
                "SUM(CASE\n" +
                "WHEN Region=��NW��\n" +
                "THEN Revenue\n" +
                "ELSE 0\n" +
                "END),\n" +
                "SUM(CASE\n" +
                "WHEN Region LIKE ��N%��\n" +
                "THEN Revenue\n" +
                "ELSE 0\n" +
                "END)\n" +
                "AS NorthernExposure, NorthernExposure/SUM(Revenue),\n" +
                "SUM(Revenue)\n" +
                "FROM SALES\n" +
                "GROUP BY MONTH;\n" +
                "\n" +
                "SELECT CAST(last_name AS CHARACTER(15)),salary_amount (FORMAT\n" +
                "'$,$$9,999.99'),(date - hire_date)/365.25 (FORMAT 'Z9.99') AS\n" +
                "On_The_Job, CASE\n" +
                "WHEN salary_amount < 30000 AND On_The_Job > 8\n" +
                "THEN '15% Increase'\n" +
                "WHEN salary_amount < 35000 AND On_The_Job > 10\n" +
                "THEN '10% Increase'\n" +
                "WHEN salary_amount < 40000 AND On_The_Job > 10\n" +
                "THEN '05% Increase'\n" +
                "ELSE 'Under 8 Years'\n" +
                "END AS Plan\n" +
                "WHERE salary_amount < 40000\n" +
                "FROM employee\n" +
                "ORDER BY 4;\n" +
                "\n" +
                "SELECT prodID, (CASE\n" +
                "WHEN prodSTATUS = 1\n" +
                "THEN 'SENT'\n" +
                "ELSE 'BACK ORDER'\n" +
                "END) || ' STATUS'\n" +
                "FROM t1;\n" +
                "\n" +
                "/*\n" +
                "In the following query, the CASE expression returns a FLOAT result because its\n" +
                "THEN and ELSE clauses contain both INTEGER and DECIMAL values. The\n" +
                "result is then cast to DECIMAL using teradata conversion syntax:\n" +
                "*/\n" +
                "SELECT SUM (CASE\n" +
                "WHEN column_2=1\n" +
                "THEN column_3 * 6\n" +
                "ELSE column_3\n" +
                "END (DECIMAL(15,2)))\n" +
                "FROM dec15;\n" +
                "\n" +
                "\n" +
                "SELECT SUM (CASE\n" +
                "WHEN column_2=100\n" +
                "THEN (column_1 (DECIMAL(15,2)))\n" +
                "ELSE column_3\n" +
                "END )\n" +
                "FROM dec15;\n" +
                "\n" +
                "SELECT i, ((CASE\n" +
                "WHEN i=1\n" +
                "THEN start_date\n" +
                "WHEN i=2\n" +
                "THEN end_date\n" +
                "END) (FORMAT 'DDBM4BYYYY'))\n" +
                "FROM duration\n" +
                "ORDER BY 1;\n" +
                "\n" +
                "SELECT CASE NULL\n" +
                "WHEN 10\n" +
                "THEN ��TEN��\n" +
                "END;\n" +
                "\n" +
                "SELECT CASE NULL + 1\n" +
                "WHEN 10\n" +
                "THEN ��TEN��\n" +
                "END;\n" +
                "\n" +
                "SELECT CASE\n" +
                "WHEN column_1 IS NULL\n" +
                "THEN ��NULL��\n" +
                "END\n" +
                "FROM table_1;\n" +
                "\n" +
                "--COALESCE \n" +
                "SELECT Name, COALESCE (HomePhone, OfficePhone, MessageService)\n" +
                "FROM PhoneDir;\n" +
                "\n" +
                "SELECT Name\n" +
                "FROM Directory\n" +
                "WHEN Organization <> COALESCE (Level1, Level2, Level3);\n" +
                "";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.size() == 17);
        //System.out.print(sqlparser.sqlstatements.size());
    }

    public void testcreateprocedure(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE spAccount(OUT p1 CHARACTER(30))\n" +
                "L1: BEGIN\n" +
                "DECLARE i INTEGER;\n" +
                "DECLARE DeptCursor CURSOR FOR\n" +
                "SELECT DeptName from Department;\n" +
                "DECLARE CONTINUE HANDLER FOR SQLSTATE VALUE '23505'\n" +
                "L2: BEGIN\n" +
                "SET p1='Failed To Insert Row';\n" +
                "END L2;\n" +
                "L3: BEGIN\n" +
                "INSERT INTO table_1 VALUES(1,10);\n" +
                "IF SQLCODE <> 0 THEN LEAVE L1;\n" +
                "END L3;\n" +
                "INSERT INTO table_2 VALUES(2,20);\n" +
                "END L1;\n" +
                "/\n" +
                "\n" +
                "CREATE PROCEDURE spSample1(INOUT IOParam1 INTEGER,\n" +
                "OUT OParam2 INTEGER)\n" +
                "L1: BEGIN\n" +
                "DECLARE K INTEGER DEFAULT 10;\n" +
                "L2: BEGIN\n" +
                "DECLARE K INTEGER DEFAULT 20;\n" +
                "SET OParam2 = K;\n" +
                "SET IOParam1 = L1.K;\n" +
                "END L2;\n" +
                "\n" +
                "END L1;\n" +
                "/\n" +
                "\n" +
                "CREATE PROCEDURE spSample3(OUT p1 CHARACTER(80))\n" +
                "BEGIN\n" +
                "DECLARE i INTEGER DEFAULT 20;\n" +
                "DECLARE EXIT HANDLER\n" +
                "FOR SQLSTATE '42000'\n" +
                "BEGIN\n" +
                "DECLARE i INTEGER DEFAULT 10;\n" +
                "DECLARE CONTINUE HANDLER\n" +
                "FOR SQLEXCEPTION\n" +
                "SET p1 = 'Table does not exist';\n" +
                "DROP TABLE table1;\n" +
                "CREATE TABLE table1 (c1 INTEGER);\n" +
                "INSERT INTO table1 (i);\n" +
                "END;\n" +
                "INSERT INTO table1 VALUES(1000,'aaa');\n" +
                "/* table1 does not exist */\n" +
                "END;\n" +
                "/\n" +
                "\n" +
                "CREATE PROCEDURE spSample (OUT po1 VARCHAR(50),\n" +
                "OUT po2 VARCHAR(50))\n" +
                "BEGIN\n" +
                "DECLARE i INTEGER DEFAULT 0;\n" +
                "L1: BEGIN\n" +
                "DECLARE var1 VARCHAR(25) DEFAULT 'ABCD';\n" +
                "DECLARE CONTINUE HANDLER FOR SQLSTATE '42000'\n" +
                "SET po1 = 'Table does not exist in L1';\n" +
                "INSERT INTO tDummy (10, var1);\n" +
                "-- Table Does not exist\n" +
                "END L1;\n" +
                "L2: BEGIN\n" +
                "DECLARE var1 VARCHAR(25) DEFAULT 'XYZ';\n" +
                "DECLARE CONTINUE HANDLER FOR SQLSTATE '42000'\n" +
                "SET po2 = 'Table does not exist in L2';\n" +
                "INSERT INTO tDummy (i, var1);\n" +
                "-- Table Does not exist\n" +
                "END L2;\n" +
                "END;"; 
        sqlparser.getrawsqlstatements();
        //System.out.print(sqlparser.sqlstatements.size());
        //System.out.println(sqlparser.sqlstatements.get(3).toString());
        //System.out.println(sqlparser.sqlstatements.get(1).sqlstatementtype);
        //System.out.println(sqlparser.sqlstatements.get(2).sqlstatementtype);
        //System.out.println(sqlparser.sqlstatements.get(3).sqlstatementtype);

        assertTrue(sqlparser.sqlstatements.size() == 4);
    }

}
