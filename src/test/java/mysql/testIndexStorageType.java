package mysql;
/*
 * Date: 12-2-6
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TMySQLIndexStorageType;
import gudusoft.gsqlparser.nodes.mysql.TMySQLIndexOption;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testIndexStorageType extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story ADD INDEX INK01_jr_story (story_no) using BTREE";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        TMySQLIndexOption  indexOption = alterTableOption.getIndexOptionList().getElement(0);
        TMySQLIndexStorageType indexStorageType = indexOption.getIndexStorageType();
        assertTrue(indexStorageType.getTypeToken().toString().equalsIgnoreCase("BTREE"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story ADD INDEX INK02_jr_story (story_no) using HASH";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        TMySQLIndexOption  indexOption = alterTableOption.getIndexOptionList().getElement(0);
        TMySQLIndexStorageType indexStorageType = indexOption.getIndexStorageType();
        assertTrue(indexStorageType.getTypeToken().toString().equalsIgnoreCase("HASH"));
    }

}
