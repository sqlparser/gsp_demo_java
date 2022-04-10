package hive;

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TPartitionExtensionClause;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: tako
 * Date: 13-8-18
 * Time: 上午8:57
 * To change this template use File | Settings | File Templates.
 */
public class testAlterTable extends TestCase {

    public void testSerde(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "alter table decimal_1 set serde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe';";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("decimal_1"));

        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.serde);
        assertTrue(alterTableOption.getSerdeName().toString().equalsIgnoreCase("'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'"));
    }

    public void testArchive(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "ALTER TABLE srcpart UNARCHIVE PARTITION(ds='2008-04-08', hr='12');";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("srcpart"));

        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.unArchive);
        assertTrue(alterTableOption.getPartitionSpecList().size() == 1);
        TPartitionExtensionClause partitionSpec = alterTableOption.getPartitionSpecList().get(0);
        assertTrue(partitionSpec.getKeyValues().getExpression(0).toString().equalsIgnoreCase("ds='2008-04-08'"));

    }
}
