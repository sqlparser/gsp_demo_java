package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCreateDatabaseLinkStmt;
import gudusoft.gsqlparser.stmt.TDropDatabaseLinkStmt;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import gudusoft.gsqlparser.stmt.sybase.TSybaseDeleteStatistics;
import junit.framework.TestCase;


public class testDatabaseLink extends TestCase {

    public void testDrop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DROP PUBLIC DATABASE LINK remote";
        assertTrue(sqlparser.parse() == 0);

        TDropDatabaseLinkStmt databaseLinkStmt = (TDropDatabaseLinkStmt)sqlparser.sqlstatements.get(0);
        assertTrue(databaseLinkStmt.getDatabaseLinkName().toString().equalsIgnoreCase("remote"));
    }

    public void testCreate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE DATABASE LINK chicago\n" +
                "  CONNECT TO admin IDENTIFIED BY 'mypassword'\n" +
                "  USING oci '//127.0.0.1/acctg';";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseLinkStmt databaseLinkStmt = (TCreateDatabaseLinkStmt)sqlparser.sqlstatements.get(0);
        assertTrue(databaseLinkStmt.getDatabaseLinkName().toString().equalsIgnoreCase("chicago"));
    }

    public void testDatabase(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select REGION_ID, COUNTRY_NAME from\n" +
                "        \"ORCLPDB2.LOCALDOMAIN\".\"PUBLIC\".\"HR_COUNTRIES\"@LD_PDB1_SOL.LOCALDOMAIN;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.getSqlstatements().get(0);
        TObjectName table = selectSqlStatement.getTables().getTable(0).getTableName();
        assertTrue(table.getDatabaseString().equalsIgnoreCase("\"ORCLPDB2.LOCALDOMAIN\""));
        assertTrue(table.getSchemaString().equalsIgnoreCase("\"PUBLIC\""));
        assertTrue(table.getTableString().equalsIgnoreCase("\"HR_COUNTRIES\""));

        assertTrue(table.getDblink().toString().equalsIgnoreCase("LD_PDB1_SOL.LOCALDOMAIN"));
    }

    public void testDatabaseInProc(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create procedure CARS_HR_COUNTRIES\n" +
                "    is\n" +
                "begin\n" +
                "select REGION_ID, COUNTRY_NAME from\n" +
                "\t\t\"ORCLPDB2.LOCALDOMAIN\".\"PUBLIC\".\"HR_COUNTRIES\"@LD_PDB1_SOL.LOCALDOMAIN;\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure =  (TPlsqlCreateProcedure)sqlparser.getSqlstatements().get(0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createProcedure.getBodyStatements().get(0);
        TObjectName table = selectSqlStatement.getTables().getTable(0).getTableName();
        assertTrue(table.getDatabaseString().equalsIgnoreCase("\"ORCLPDB2.LOCALDOMAIN\""));
        assertTrue(table.getSchemaString().equalsIgnoreCase("\"PUBLIC\""));
        assertTrue(table.getTableString().equalsIgnoreCase("\"HR_COUNTRIES\""));
        assertTrue(table.getDblink().toString().equalsIgnoreCase("LD_PDB1_SOL.LOCALDOMAIN"));
    }

    public void testDatabaseInProc2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create procedure CARS_HR_COUNTRIES\n" +
                "    is\n" +
                "begin\n" +
                "    insert into CARS (id, name)\n" +
                "    select REGION_ID, COUNTRY_NAME from\n" +
                "        PUBLIC.HR_COUNTRIES@LD_PDB1_SOL.LOCALDOMAIN;\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure =  (TPlsqlCreateProcedure)sqlparser.getSqlstatements().get(0);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)createProcedure.getBodyStatements().get(0);

        TSelectSqlStatement selectSqlStatement = insertSqlStatement.getSubQuery();
        TObjectName table = selectSqlStatement.getTables().getTable(0).getTableName();
        assertTrue(table.getSchemaString().equalsIgnoreCase("PUBLIC"));
        assertTrue(table.getTableString().equalsIgnoreCase("HR_COUNTRIES"));
        assertTrue(table.getDblink().toString().equalsIgnoreCase("LD_PDB1_SOL.LOCALDOMAIN"));

    }

}
