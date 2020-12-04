package mysql;
/*
 * Date: 12-3-30
 */

import gudusoft.gsqlparser.EConstraintType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testTimestampOnUpdate extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE test_table (pk_id bigint(10) unsigned NOT NULL auto_increment, \n" +
                "last_update timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP, \n" +
                "PRIMARY KEY (pk_id)) ENGINE=InnoDB";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition columnDefinition = createTableSqlStatement.getColumnList().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("last_update"));
        assertTrue(columnDefinition.getConstraints().getConstraint(0).toString().equalsIgnoreCase("NOT NULL"));
        TConstraint constraint = columnDefinition.getConstraints().getConstraint(1);
        assertTrue(constraint.getConstraint_type() == EConstraintType.default_value);
        assertTrue(constraint.getDefaultExpression().toString().equalsIgnoreCase("CURRENT_TIMESTAMP"));
        TAutomaticProperty automaticProperty0 = constraint.getAutomaticProperties().getElement(0);
//        assertTrue(automaticProperty0.toString().equalsIgnoreCase("CURRENT_TIMESTAMP"));
//        TAutomaticProperty automaticProperty1 = constraint.getAutomaticProperties().getElement(1);
        assertTrue(automaticProperty0.toString().equalsIgnoreCase("on update CURRENT_TIMESTAMP"));


    }
}
