package oracle;
/*
 * Date: 13-1-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testMergeInPlsql extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "PROCEDURE load_bank_file ()\n" +
                "IS\n" +
                "BEGIN\n" +
                "\n" +
                "MERGE INTO ap_bank_branches apb\n" +
                "USING (SELECT xbl.bank_num\n" +
                "FROM xxuom_bank_load xbl\n" +
                "WHERE\t  xbl.status = cn_status_bank_num_match) bnk_load\n" +
                "ON (apb.bank_num = bnk_load.bank_num)\n" +
                "WHEN MATCHED\n" +
                "THEN\n" +
                "UPDATE SET\n" +
                "apb.bank_name = SUBSTR (bnk_load.bank_name, 1, 60)\n" +
                "WHEN NOT MATCHED\n" +
                "THEN\n" +
                "INSERT\t  (address_line1)\n" +
                "VALUES (NULL);\n" +
                "COMMIT;\n" +
                "\n" +
                "END load_bank_file";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure = (TPlsqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TMergeSqlStatement merge = (TMergeSqlStatement)createProcedure.getBodyStatements().get(0);
        assertTrue(merge.getTargetTable().toString().equalsIgnoreCase("ap_bank_branches"));

        TSelectSqlStatement select  = merge.getUsingTable().getSubquery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("xxuom_bank_load"));

    }

}
