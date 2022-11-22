package netezza;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCTEInInsert extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "INSERT INTO NZ_FOODMART.ADMIN.Test5(id, name, bool_data)\n" +
                " WITH NewCTE as (SELECT 1 as employee_id, 'dm' as first_name, true as bool_data)\n" +
                " select employee_id, first_name, bool_data from NewCTE;";
        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement tInsertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = tInsertSqlStatement.getSubQuery();
        assertTrue(select.getCteList().size() == 1);
        TCTE cte = select.getCteList().getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("NewCTE"));
        assertTrue(cte.getSubquery().getStartToken().toString().equalsIgnoreCase("SELECT"));
        assertTrue(cte.getSubquery().getEndToken().toString().equalsIgnoreCase("bool_data"));
    }

}
