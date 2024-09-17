package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TViewAliasItem;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testCreateView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW\n" +
                "   AS\n" +
                "   SELECT \n" +
                "   UNCOLLATED_PHRASE,\n" +
                "   SUBSTRING(UNCOLLATED_PHRASE,1,2) AS substr_val,\n" +
                "   HASH(UTF8_PHRASE) AS hash_val,\n" +
                "   ENGLISH_PHRASE,\n" +
                "   SPANISH_PHRASE AS sp_phrase\n" +
                "   FROM COLLATION_DEMO;\n" +
                "\n" +
                "select\n" +
                "  t.table_catalog as dbName,\n" +
                "  t.table_schema as schemaName,\n" +
                "  t.table_name as tableName,\n" +
                "  case when t.table_type = 'VIEW' then 'true'\n" +
                "       when t.table_type = 'BASE TABLE' then 'false'\n" +
                "       else 'false'\n" +
                "  end as isView,\n" +
                "  c.column_name,\n" +
                "  c.data_type,\n" +
                "  null as comments\n" +
                "from\n" +
                "  \"DEMO_DB\".information_schema.tables t,\n" +
                "  \"DEMO_DB\".information_schema.columns c\n" +
                "where\n" +
                "  t.table_catalog = c.table_catalog\n" +
                "  and t.table_schema = c.table_schema\n" +
                "  and t.table_name = c.table_name\n" +
                "  and upper(t.table_schema) not in ('INFORMATION_SCHEMA')\n" +
                "order by t.table_catalog, t.table_schema, t.table_name, c.ordinal_position;\n" +
                "\n" +
                "use database \"DEMO_DB\" ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "USE DATABASE DEMO_DB ;\n" +
                "SHOW SCHEMAS ;\n" +
                "SHOW VIEWS IN DEMO_DB.PUBLIC ;\n" +
                "SELECT GET_DDL('VIEW', 'DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW') ;\n" +
                "SHOW PROCEDURES IN DEMO_DB.PUBLIC ;\n" +
                "SHOW FUNCTIONS IN DEMO_DB.PUBLIC ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "select *\n" +
                "from table(information_schema.query_history())\n" +
                "order by start_time;\n" +
                "create table collation_demo_qh (\n" +
                "  uncollated_phrase varchar, \n" +
                "  utf8_phrase varchar collate 'utf8',\n" +
                "  english_phrase varchar collate 'en',\n" +
                "  spanish_phrase varchar collate 'sp'\n" +
                "  );\n" +
                "SELECT * FROM identifier('\"DEMO_DB\".\"PUBLIC\".\"COLLATION_DEMO_QH\"') LIMIT 100;\n" +
                "INSERT INTO collation_demo_qh(uncollated_phrase,utf8_phrase,english_phrase,spanish_phrase)\n" +
                "SELECT uncollated_phrase,substr_val,english_phrase,sp_phrase from collation_demo_view ;\n" +
                "select\n" +
                "  t.table_catalog as dbName,\n" +
                "  t.table_schema as schemaName,\n" +
                "  t.table_name as tableName,\n" +
                "  case when t.table_type = 'VIEW' then 'true'\n" +
                "       when t.table_type = 'BASE TABLE' then 'false'\n" +
                "       else 'false'\n" +
                "  end as isView,\n" +
                "  c.column_name,\n" +
                "  c.data_type,\n" +
                "  null as comments\n" +
                "from\n" +
                "  \"DEMO_DB\".information_schema.tables t,\n" +
                "  \"DEMO_DB\".information_schema.columns c\n" +
                "where\n" +
                "  t.table_catalog = c.table_catalog\n" +
                "  and t.table_schema = c.table_schema\n" +
                "  and t.table_name = c.table_name\n" +
                "  and upper(t.table_schema) not in ('INFORMATION_SCHEMA')\n" +
                "order by t.table_catalog, t.table_schema, t.table_name, c.ordinal_position;\n" +
                "\n" +
                "use database \"DEMO_DB\" ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "USE DATABASE DEMO_DB ;\n" +
                "SHOW SCHEMAS ;\n" +
                "SHOW VIEWS IN DEMO_DB.PUBLIC ;\n" +
                "SELECT GET_DDL('VIEW', 'DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW') ;\n" +
                "SHOW PROCEDURES IN DEMO_DB.PUBLIC ;\n" +
                "SHOW FUNCTIONS IN DEMO_DB.PUBLIC ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "select\n" +
                "  t.table_catalog as dbName,\n" +
                "  t.table_schema as schemaName,\n" +
                "  t.table_name as tableName,\n" +
                "  case when t.table_type = 'VIEW' then 'true'\n" +
                "       when t.table_type = 'BASE TABLE' then 'false'\n" +
                "       else 'false'\n" +
                "  end as isView,\n" +
                "  c.column_name,\n" +
                "  c.data_type,\n" +
                "  null as comments\n" +
                "from\n" +
                "  \"DEMO_DB\".information_schema.tables t,\n" +
                "  \"DEMO_DB\".information_schema.columns c\n" +
                "where\n" +
                "  t.table_catalog = c.table_catalog\n" +
                "  and t.table_schema = c.table_schema\n" +
                "  and t.table_name = c.table_name\n" +
                "  and upper(t.table_schema) not in ('INFORMATION_SCHEMA')\n" +
                "order by t.table_catalog, t.table_schema, t.table_name, c.ordinal_position;\n" +
                "\n" +
                "use database \"DEMO_DB\" ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "USE DATABASE DEMO_DB ;\n" +
                "SHOW SCHEMAS ;\n" +
                "SHOW VIEWS IN DEMO_DB.PUBLIC ;\n" +
                "SELECT GET_DDL('VIEW', 'DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW') ;\n" +
                "SHOW PROCEDURESd IN DEMO_DB.PUBLIC ;\n" +
                "SHOW FUNCTIONS IN DEMO_DB.PUBLIC ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "select *\n" +
                "from table(information_schema.query_history())\n" +
                "order by start_time;\n" +
                "select *\n" +
                "from table(information_schema.query_history())\n" +
                "order by start_time desc;\n" +
                "INSERT INTO collation_demo_qh(uncollated_phrase,utf8_phrase,english_phrase,spanish_phrase)\n" +
                "SELECT uncollated_phrase,substr_val,english_phrase,sp_phrase from collation_demo_view ;\n" +
                "select *\n" +
                "from table(information_schema.query_history())\n" +
                "order by start_time desc;\n" +
                "select\n" +
                "  t.table_catalog as dbName,\n" +
                "  t.table_schema as schemaName,\n" +
                "  t.table_name as tableName,\n" +
                "  case when t.table_type = 'VIEW' then 'true'\n" +
                "       when t.table_type = 'BASE TABLE' then 'false'\n" +
                "       else 'false'\n" +
                "  end as isView,\n" +
                "  c.column_name,\n" +
                "  c.data_type,\n" +
                "  null as comments\n" +
                "from\n" +
                "  \"DEMO_DB\".information_schema.tables t,\n" +
                "  \"DEMO_DB\".information_schema.columns c\n" +
                "where\n" +
                "  t.table_catalog = c.table_catalog\n" +
                "  and t.table_schema = c.table_schema\n" +
                "  and t.table_name = c.table_name\n" +
                "  and upper(t.table_schema) not in ('INFORMATION_SCHEMA')\n" +
                "order by t.table_catalog, t.table_schema, t.table_name, c.ordinal_position;\n" +
                "\n" +
                "use database \"DEMO_DB\" ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "USE DATABASE DEMO_DB ;\n" +
                "SHOW SCHEMAS ;\n" +
                "SHOW VIEWS IN DEMO_DB.PUBLIC ;\n" +
                "SELECT GET_DDL('VIEW', 'DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW') ;\n" +
                "SHOW PROCEDURES IN DEMO_DB.PUBLIC ;\n" +
                "SHOW FUNCTIONS IN DEMO_DB.PUBLIC ;\n" +
                "use database \"DEMO_DB\" ;\n" +
                "select *\n" +
                "from table(information_schema.query_history())\n" +
                "order by start_time desc;\n" +
                "USE DATABASE DEMO_DB ;\n" +
                "SHOW SCHEMAS ;\n" +
                "SHOW PROCEDURES IN DEMO_DB.PUBLIC ;\n" +
                "SHOW FUNCTIONS IN DEMO_DB.PUBLIC ;\n" +
                "use database \"DEMO_DB\" ;";
        //assertTrue(sqlparser.parse() == 0);
        sqlparser.parse();
        assertTrue(sqlparser.sqlstatements.size() == 55);
        //System.out.println(sqlparser.sqlstatements.size());
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getViewName().toString().equalsIgnoreCase("DEMO_DB.PUBLIC.COLLATION_DEMO_VIEW"));
//        assertTrue(createStreamStmt.getStreamName().toString().equalsIgnoreCase("mystream"));
//        assertTrue(createStreamStmt.getTableName().toString().equalsIgnoreCase("mytable"));
    }


    public void test2Comment() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE VIEW defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V comment = 'COMMENT'\n" +
                "AS ((select EMPLOYEE_ID,FULL_NAME,FIRST_NAME from defaultdatabase.SF_DML.trimmed_employee_SF_Testing))";
        sqlparser.parse();
        assertTrue(sqlparser.sqlstatements.size() == 1);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getViewName().toString().equalsIgnoreCase("defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V"));
        assertTrue(createViewSqlStatement.getComment().toString().equalsIgnoreCase("'COMMENT'"));
    }

    public void test3Comment() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE VIEW defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V\n" +
                "AS (select EMPLOYEE_ID,FULL_NAME,FIRST_NAME from defaultdatabase.SF_DML.trimmed_employee_SF_Testing) comment = 'COMMENT'";
        sqlparser.parse();
        assertTrue(sqlparser.sqlstatements.size() == 1);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getViewName().toString().equalsIgnoreCase("defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V"));
        assertTrue(createViewSqlStatement.getComment().toString().equalsIgnoreCase("'COMMENT'"));
    }

    public void test4Comment() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace view defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V\n" +
                "( ADMIN1 COMMENT 'COLUMN 1',\n" +
                "ADMIN2 COMMENT 'COLUMN 2',\n" +
                "ADMIN3 COMMENT 'COLUMN 3') COMMENT='COMMENT'\n" +
                "as (select EMPLOYEE_ID,FULL_NAME,FIRST_NAME from defaultdatabase.SF_DML.trimmed_employee_SF_Testing);";
        sqlparser.parse();
        assertTrue(sqlparser.sqlstatements.size() == 1);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getViewName().toString().equalsIgnoreCase("defaultdatabase.SF_DML.trimmed_employee_SF_Testing_V"));
        assertTrue(createViewSqlStatement.getComment().toString().equalsIgnoreCase("'COMMENT'"));

        TViewAliasItem viewAliasItem = createViewSqlStatement.getViewAliasClause()
                .getViewAliasItemList()
                .getViewAliasItem(0);
        assertTrue(viewAliasItem.getAlias().toString().equalsIgnoreCase("ADMIN1"));
        assertTrue(viewAliasItem.getComment().toString().equalsIgnoreCase("'COLUMN 1'"));
    }

}


