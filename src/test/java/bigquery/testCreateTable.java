package bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EDeclareType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE TABLE `myproject.mydataset.mytable`\n" +
                "CLONE `myproject.mydataset.mytablesnapshot`\n" +
                "OPTIONS(\n" +
                "  expiration_timestamp=TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 365 DAY),\n" +
                "  friendly_name=\"my_table\",\n" +
                "  description=\"A table that expires in 1 year\",\n" +
                "  labels=[(\"org_unit\", \"development\")]\n" +
                ")";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement =  (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
       // System.out.println(createTableSqlStatement.getTableName().toString());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("`myproject`.`mydataset`.`mytable`"));
        assertTrue(createTableSqlStatement.getCloneSourceTable().toString().equalsIgnoreCase("`myproject`.`mydataset`.`mytablesnapshot`"));
    }
    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE TABLE mydataset.newtable\n" +
                "LIKE mydataset.sourcetable\n" +
                "AS SELECT * FROM mydataset.myothertable;";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement =  (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("mydataset.newtable"));
        assertTrue(createTableSqlStatement.getLikeTableName().toString().equalsIgnoreCase("mydataset.sourcetable"));
        TSelectSqlStatement subquery = createTableSqlStatement.getSubQuery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("mydataset.myothertable"));
    }

}

