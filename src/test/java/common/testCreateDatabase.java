package common;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import junit.framework.TestCase;

public class testCreateDatabase extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE DATABASE menagerie";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement stmt = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getDatabaseName().toString().equalsIgnoreCase("menagerie"));
        TObjectName databaseName = stmt.getDatabaseName();
        assertTrue(databaseName.getDbObjectType() == EDbObjectType.database);
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("menagerie"));
    }

    public void testCollation(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE DATABASE db1 DEFAULT COLLATE latin1_german1_ci";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement stmt = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);

        TObjectName databaseName = stmt.getDatabaseName();
        assertTrue(databaseName.getDbObjectType() == EDbObjectType.database);
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("db1"));
        assertTrue(stmt.getCollationName().toString().equalsIgnoreCase("latin1_german1_ci"));
    }

    public void testCollation2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE SCHEMA db1 DEFAULT COLLATE latin1_german1_ci";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement stmt = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);

        TObjectName databaseName = stmt.getDatabaseName();
        assertTrue(databaseName.getDbObjectType() == EDbObjectType.database);
        assertTrue(databaseName.getObjectToken().toString().equalsIgnoreCase("db1"));
        assertTrue(stmt.getCollationName().toString().equalsIgnoreCase("latin1_german1_ci"));
    }

    public void testNetezza(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE DATABASE DEFINITION_SCHEMA.ECM_SABER2;";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement stmt = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);

        TObjectName databaseName = stmt.getDatabaseName();
        assertTrue(databaseName.getDbObjectType() == EDbObjectType.database);
        assertTrue(databaseName.toString().equalsIgnoreCase("DEFINITION_SCHEMA.ECM_SABER2"));
    }
}
