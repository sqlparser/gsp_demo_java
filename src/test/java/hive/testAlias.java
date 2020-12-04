package hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.hive.THiveTransformClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAlias extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT TRANSFORM(stuff) \n" +
                "  USING 'script'\n" +
                "  AS (thing1 INT, thing2 INT) from b;";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        THiveTransformClause transformClause = select.getTransformClause();
        assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettSelect);
        assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'script'"));

        TAliasClause aliasClause = transformClause.getAliasClause();
        assertTrue(aliasClause.getColumnNameTypeList().size() == 2);
        TColumnDefinition cd1  = aliasClause.getColumnNameTypeList().getColumn(0);
        assertTrue(cd1.getColumnName().toString().equalsIgnoreCase("thing1"));
        assertTrue(cd1.getDatatype().getDataType() == EDataType.int_t);
        TColumnDefinition cd2  = aliasClause.getColumnNameTypeList().getColumn(1);
        assertTrue(cd2.getColumnName().toString().equalsIgnoreCase("thing2"));
        assertTrue(cd2.getDatatype().getDataType() == EDataType.int_t);

    }

}
