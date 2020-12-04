package postgresql;
/*
 * Date: 11-6-21
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import junit.framework.TestCase;

public class testDeleteStmt extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "DELETE FROM films USING producers\n" +
                "  WHERE producer_id = producers.id AND producers.name = 'foo';";
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement deleteSqlStatement = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(deleteSqlStatement.getTargetTable().toString().equalsIgnoreCase("films"));
        assertTrue(deleteSqlStatement.getReferenceJoins().size() == 1);
        assertTrue(deleteSqlStatement.getReferenceJoins().getJoin(0).getTable().toString().equalsIgnoreCase("producers"));
       // System.out.println(deleteSqlStatement.getWhereClause().getCondition().toString());
    }

}
