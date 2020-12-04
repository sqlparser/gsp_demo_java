package netezza;
/*
 * Date: 14-2-17
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSelectFromExternalTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "insert into \"$hist_column_access_1\"(npsid, npsinstanceid, opid, logentryid, seqid, sessionid, dbid, dbname, schemaid, schemaname, tableid, tablename, columnid, columnname, usage) select 1, * from external '/nz/data.1.0/hist/loading/alc_20140215_151309.473439/alc_co_20140215_151309.473439' (npsinstanceid int, opid bigint, logentryid bigint, seqid integer, sessionid bigint, dbid int, dbname nvarchar (128), schemaid int , schemaname nvarchar (128), tableid int, tablename nvarchar (128), columnid int, columnname nvarchar (128), usage int) using( logdir '/nz/data.1.0/hist/loading/alc_20140215_151309.473439' encoding 'internal' escapechar '\\' fillrecord true ctrlchars true crinstring true)";
        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = insertSqlStatement.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("\"$hist_column_access_1\""));
        TSelectSqlStatement select = (TSelectSqlStatement)insertSqlStatement.getSubQuery();
        table = select.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("'/nz/data.1.0/hist/loading/alc_20140215_151309.473439/alc_co_20140215_151309.473439'"));
    }


}
