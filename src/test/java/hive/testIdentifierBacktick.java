package hive;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testIdentifierBacktick extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE TABLE Hive.`default`.ct622_date5 as SELECT to_date(hive.read_date) as `to_date(hive.default.``read_date``)` from ct622";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("Hive.`default`.ct622_date5"));

        TSelectSqlStatement select = createTableSqlStatement.getSubQuery();
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("to_date(hive.read_date)"));
        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("`to_date(hive`.`default`.```read_date``)`"));
        //System.out.println(resultColumn.getAliasClause().toString());
    }

}

