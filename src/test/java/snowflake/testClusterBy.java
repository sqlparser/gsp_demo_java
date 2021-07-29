package snowflake;

import demos.columnInClause;
import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testClusterBy extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE  TABLE sfdb.scenrdb.transactions_master2 \n" +
                " (   trans_mstr_sid   integer , trans_qty   integer ,trans_amount integer ,customer_sid  integer ,transaction_type_sid  integer ,addr_mstr_sid  integer ,trans_cid  integer )  \n" +
                " cluster by (  trans_mstr_sid , trans_qty );";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption tableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(tableOption.getCreateTableOptionType() == ECreateTableOption.etoClusterBy);
        TExpressionList expressionList = tableOption.getExpressionList();
        assertTrue(expressionList.getExpression(0).toString().equalsIgnoreCase("trans_mstr_sid"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE  TABLE sfdb.scenrdb.transactions_master2 \n" +
                " (   trans_mstr_sid   integer , trans_qty   integer ,trans_amount integer ,customer_sid  integer ,transaction_type_sid  integer ,addr_mstr_sid  integer ,trans_cid  integer )  \n" +
                " cluster by (  to_date(trans_mstr_sid ) , substring(trans_qty,0,10) )";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption tableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(tableOption.getCreateTableOptionType() == ECreateTableOption.etoClusterBy);
        TExpressionList expressionList = tableOption.getExpressionList();

//        new columnInClause().printColumns(expressionList.getExpression(0),createTableSqlStatement);
//        new columnInClause().printColumns(expressionList.getExpression(1),createTableSqlStatement);
    }

}
