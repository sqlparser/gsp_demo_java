package databricks;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import junit.framework.TestCase;

public class testUse extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "USE CATALOG hive_metastor";
        assertTrue(sqlparser.parse() == 0);
        TUseDatabase useDatabase = (TUseDatabase) sqlparser.sqlstatements.get (0);
        TObjectName databaseName = useDatabase.getDatabaseName();
        assertTrue(databaseName.getDatabaseString().equalsIgnoreCase("hive_metastor"));
        assertTrue(databaseName.getAnsiCatalogName().equalsIgnoreCase("hive_metastor"));
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("hive_metastor"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "USE DATABASE some_db;";
        assertTrue(sqlparser.parse() == 0);
        TUseDatabase useDatabase = (TUseDatabase) sqlparser.sqlstatements.get (0);
        TObjectName databaseName = useDatabase.getSchemaName();

        assertTrue(databaseName.getSchemaString().equalsIgnoreCase("some_db"));
        assertTrue(databaseName.getAnsiSchemaName().equalsIgnoreCase("some_db"));
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("some_db"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "USE schema some_db;";
        assertTrue(sqlparser.parse() == 0);
        TUseDatabase useDatabase = (TUseDatabase) sqlparser.sqlstatements.get (0);
        TObjectName databaseName = useDatabase.getSchemaName();

        assertTrue(databaseName.getSchemaString().equalsIgnoreCase("some_db"));
        assertTrue(databaseName.getAnsiSchemaName().equalsIgnoreCase("some_db"));
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("some_db"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "USE DATABASE main.my_db";
        assertTrue(sqlparser.parse() == 0);
        TUseDatabase useDatabase = (TUseDatabase) sqlparser.sqlstatements.get (0);
        TObjectName databaseName = useDatabase.getSchemaName();

        assertTrue(databaseName.getSchemaString().equalsIgnoreCase("my_db"));
        assertTrue(databaseName.getAnsiSchemaName().equalsIgnoreCase("my_db"));
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("my_db"));

        assertTrue(databaseName.getDatabaseString().equalsIgnoreCase("main"));
        assertTrue(databaseName.getAnsiCatalogName().equalsIgnoreCase("main"));
    }
}
