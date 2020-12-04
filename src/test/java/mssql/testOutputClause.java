package mssql;
/*
 * Date: 14-10-17
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TOutputClause;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import junit.framework.TestCase;

public class testOutputClause extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "MERGE INTO [data].[TABLEDEST] dest\n" +
                "USING\n" +
                "   (\n" +
                "   SELECT src.[Column1],src.[Column2],src.[Column3]\n" +
                "   FROM [sys].[TABLESRC] src\n" +
                "   ) src1\n" +
                "   ON dest.[Column1] = src1.[Column1]\n" +
                "WHEN NOT MATCHED BY TARGET THEN\n" +
                "   INSERT ([Column1],[Column2],[Column3])\n" +
                "      VALUES([Column1],[Column2],[Column3])\n" +
                "WHEN MATCHED THEN\n" +
                "   UPDATE SET dest.[Column1] = dest.[Column1]\n" +
                "OUTPUT src1.[Column1],src1.[Column2],src1.[Column3]\n" +
                "   INTO @TABLEOUTPUT\n" +
                "   (\n" +
                "      [Column1]\n" +
                "      ,[Column2]\n" +
                "      ,[Column3]\n" +
                "   );";
        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        TOutputClause outputClause  = mergeSqlStatement.getOutputClause();
        assertTrue(outputClause.getSelectItemList().size() == 3);
        assertTrue(outputClause.getSelectItemList().getResultColumn(0).toString().equalsIgnoreCase("src1.[Column1]"));
        assertTrue(outputClause.getIntoTable().toString().equalsIgnoreCase("@TABLEOUTPUT"));
        assertTrue(outputClause.getIntoColumnList().size() == 3);
        assertTrue(outputClause.getIntoColumnList().getObjectName(2).toString().equalsIgnoreCase("[Column3]"));

    }

}
