package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSyntaxError extends TestCase {

    public void testSnowflakePivot(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW ATLAN_SAMPLE_DATA.OTT_PLATFORMS.NETFLIX_CUSTOMER_PAYMENTS_BY_TYPE \n" +
                "AS  SELECT NC.*, NCP.CREDIT_CARD, NCP.DEBIT_CARD, NCP.VOUCHER \n" +
                "FROM ATLAN_SAMPLE_DATA.OTT_PLATFORMS.NETFLIX_CUSTOMER_MASTER AS NC \n" +
                "JOIN ( SELECT *   \n" +
                "\t\tFROM ATLAN_SAMPLE_DATA.OTT_PLATFORMS.NETFLIX_CUSTOMER_PAYMENTS_MASTER     PIVOT(sum(PAYMENT_VALUE) FOR PAYMENT_TYPE IN ('credit_card', 'debit_card', 'voucher')) AS P (CUSTOMER_ID, CREDIT_CARD, DEBIT_CARD, VOUCHER) ) AS NCP  \n" +
                "\t\t\t\tON NC.CUSTOMER_ID = NCP.CUSTOMER_ID;";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getSyntaxHints().size() == 0);
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "MERGE dataset.DetailedInventory T\n" +
                "USING dataset.Inventory S\n" +
                "ON T.product = S.product\n" +
                "WHEN NOT MATCHED AND quantity < 20 THEN\n" +
                "  INSERT(product, quantity, supply_constrained, comments)\n" +
                "  VALUES(product, quantity, true, ARRAY<STRUCT<created DATE, comment STRING>>[(DATE('2016-01-01'), 'comment1')])\n" +
                "WHEN NOT MATCHED THEN\n" +
                "  INSERT(product, quantity, supply_constrained)\n" +
                "  VALUES(product, quantity, false)\n" +
                ";";
       // System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(mergeSqlStatement.getErrorCount() == 0);
        assertTrue(mergeSqlStatement.getSyntaxErrors().size() == 0);
        assertTrue(mergeSqlStatement.getSyntaxHints().size() == 1);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "    SELECT\n" +
                "        c.[CustomerKey], \n" +
                "        x.[Region], \n" +
                "        x.[Age], \n" +
                "        CASE x.[Bikes] \n" +
                "            WHEN 0 THEN 0 \n" +
                "            ELSE 1 \n" +
                "        END AS [BikeBuyer]\n" +
                "    FROM\n" +
                "        [dbo].[DimCustomer] c INNER JOIN (\n" +
                "            SELECT\n" +
                "                [CustomerKey]\n" +
                "                ,[Region]\n" +
                "                ,[Age]\n" +
                "                ,Sum(\n" +
                "                    CASE [EnglishProductCategoryName] \n" +
                "                        WHEN 'Bikes' THEN 1 \n" +
                "                        ELSE 0 \n" +
                "                    END) AS [Bikes]\n" +
                "            FROM\n" +
                "                [dbo].[vDMPrep] \n" +
                "            GROUP BY\n" +
                "                [CustomerKey]\n" +
                "                ,[Region]\n" +
                "                ,[Age]\n" +
                "            ) AS [x]\n" +
                "        ON c.[CustomerKey] = x.[CustomerKey]"
                ;

       // System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement SqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(SqlStatement.getErrorCount() == 0);
        assertTrue(SqlStatement.getSyntaxErrors().size() == 0);
        assertTrue(SqlStatement.getSyntaxHints().size() == 0);
    }
}
