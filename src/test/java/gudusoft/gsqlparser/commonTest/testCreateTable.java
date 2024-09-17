package gudusoft.gsqlparser.commonTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableKind;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void testCreateTableAsTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE a AS TABLE b";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("a"));
        assertTrue(createTable.getAsTable().toString().equalsIgnoreCase("b"));
    }

    public void testCreateTableClone(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create table orders_clone_restore clone orders at (timestamp => to_timestamp_tz('04/05/2013 01:02:03', 'mm/dd/yyyy hh24:mi:ss'));";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("orders_clone_restore"));
        assertTrue(createTable.getCloneSourceTable().toString().equalsIgnoreCase("orders"));
    }

    public void testCreateTableLike(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TABLE new_table LIKE old_table;";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("new_table"));
        assertTrue(createTable.getLikeTableName().toString().equalsIgnoreCase("old_table"));
    }

    public void testTableTransient(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE TRANSIENT TABLE \"TestTable4\" CLUSTER BY LINEAR(CR_RETURNED_DATE_SK, CR_ITEM_SK)\n" +
                "(\n" +
                "\"Col1\" int NOT NULL\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableKinds().contains(ETableKind.etkTransient));
    }

    public void testDuplicateColumn(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create table all_data_types ( \n" +
                " col1 GOOGLE,\n" +
                " col1 CRICBUZZ,\n" +
                " col1 GOIBIBO,\n" +
                " col1 AIRINDIA\n" +
                ");";
        assertTrue(sqlparser.parse() != 0);
    }

    public void testTemporaryTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create temporary table vt (id int, name varchar(1)) distribute on(id)";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTargetTable().toString().equalsIgnoreCase("vt"));
        assertTrue(createTable.getTableKinds().contains(ETableKind.etkTemporary));

        sqlparser.sqltext = "CREATE TEMP TABLE cows2 AS SELECT cname, cbreed FROM cows DISTRIBUTE ON RANDOM;";
        assertTrue(sqlparser.parse() == 0);

        createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTargetTable().toString().equalsIgnoreCase("cows2"));
       // System.out.println(createTable.getTableKinds());
        assertTrue(createTable.getTableKinds().contains(ETableKind.etkTemp));

    }

    public void testSAMEAS() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE demo_ext SAMEAS t1 USING (dataobject ('/tmp/demo.out' ) DELIMITER '|');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getAsTable().toString().equalsIgnoreCase("t1"));
    }

    public void testAsSubquery() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE TEMP TABLE PROD_ETL.PHDM.TEMP_FEP_CONTRACT_ENROLLMENT_COVERAGE AS\n" +
                "SELECT DISTINCT \n" +
                "   CAST('CONTRACTENROLLMENT' AS VARCHAR(25)) AS SUBSCR_CVRG_DATA_SRC_CD,\n" +
                "   CES.CONTRACT_R_NUMBER, \n" +
                "   CES.PRODUCT_CODE,\n" +
                "   CES.FAMILY_STATUS_CODE,\n" +
                "   GREATEST(CES.CONTRACT_ENROLLMENT_EFFECTIVE_DATE,CE.BLUE_CROSS_PLAN_CODE_EFFECTIVE_DATE) AS BEGIN_DATE, \n" +
                "   LEAST(CES.CONTRACT_ENROLLMENT_TERMINATION_DATE,CE.BLUE_CROSS_PLAN_CODE_TERMINATION_DATE) AS END_DATE\n" +
                "FROM PROD_STG.FEP_IDEA.CONTRACTENROLLMENTEXTRACT_STG01 CES\n" +
                "INNER JOIN PROD_STG.FEP_IDEA.CONTRACTPLANEXTRACT_STG01 CE\n" +
                "ON CES.CONTRACT_R_NUMBER = CE.CONTRACT_ID_R_NUMBER\n" +
                "WHERE (CES.CONTRACT_ENROLLMENT_EFFECTIVE_DATE, CES.CONTRACT_ENROLLMENT_TERMINATION_DATE)\n" +
                "      OVERLAPS\n" +
                "      (CE.BLUE_CROSS_PLAN_CODE_EFFECTIVE_DATE, CE.BLUE_CROSS_PLAN_CODE_TERMINATION_DATE)\n" +
                "AND CES.CONTRACT_ACTIVE_FLAG = 'Y'\n" +
                "AND CES.CONTRACT_DELETE_FLAG = 'N'\n" +
                "DISTRIBUTE ON (CONTRACT_R_NUMBER);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("PROD_ETL.PHDM.TEMP_FEP_CONTRACT_ENROLLMENT_COVERAGE"));
        TSelectSqlStatement subquery = createTable.getSubQuery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("PROD_STG.FEP_IDEA.CONTRACTENROLLMENTEXTRACT_STG01"));

    }

    public void testOnCommit() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE GLOBAL TEMPORARY TABLE \"TTABLE\" (\n" +
                "        \"id\" NUMBER(11 , 0)\n" +
                "    )\n" +
                "    ON COMMIT PRESERVE ROWS;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.toString().equalsIgnoreCase("CREATE GLOBAL TEMPORARY TABLE \"TTABLE\" (\n" +
                "        \"id\" NUMBER(11 , 0)\n" +
                "    )\n" +
                "    ON COMMIT PRESERVE ROWS;"));
    }
}
