package mssql;
/*
 * Date: 11-8-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import junit.framework.TestCase;

public class testDeclareBlock extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE \n" +
                "\t@n_Count integer\n" +
                "BEGIN\t\n" +
                "\tSELECT @n_Count = count(JobTypeCode) FROM Validbatchjobtype WHERE JobTypeCode = 7\n" +
                "\tIF @n_Count = 1 \n" +
                "\t\tBEGIN\n" +
                "\t\t\tUPDATE  ValidBatchJobType SET JobTypeDesc = 'Merge Document Report',  JobTypeClass = 'WordMergeJob' WHERE JobTypeCode = 7\n" +
                "\t\tEND\n" +
                "\tELSE\n" +
                "\t\tBEGIN\n" +
                "\t\t\tINSERT INTO ValidBatchJobType(JobTypeCode, JobTypeDesc, JobTypeClass, StampUser, StampDate) \n" +
                "\t             VALUES(7, 'Merge Document Report', 'WordMergeJob', SYSTEM_USER, GETDATE())\n" +
                "\t\tEND\n" +
                "END";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.size() == 2);

        TMssqlDeclare declare = (TMssqlDeclare)sqlparser.sqlstatements.get(0);
        assertTrue(declare.getVariables().getDeclareVariable(0).getDatatype().toString().equalsIgnoreCase("integer"));
        assertTrue(declare.getVariables().getDeclareVariable(0).getVariableName().toString().equalsIgnoreCase("@n_Count"));

        TMssqlBlock block = (TMssqlBlock)sqlparser.sqlstatements.get(1);
        assertTrue(block.getBodyStatements().size() == 2);
    }

}
