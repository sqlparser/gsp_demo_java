package mysql;
/*
 * Date: 12-2-6
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testIndexType extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story ADD UNIQUE INDEX INK03_jr_story (story_no)";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getMySQLIndexTypeToken().toString().equalsIgnoreCase("UNIQUE"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story ADD FULLTEXT INDEX INK04_jr_story (story_no);";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getMySQLIndexTypeToken().toString().equalsIgnoreCase("FULLTEXT"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story ADD SPATIAL INDEX INK05_jr_story (story_no);";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getMySQLIndexTypeToken().toString().equalsIgnoreCase("SPATIAL"));
    }

}
