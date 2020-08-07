package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TCreateTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

import java.util.ArrayList;

import static gudusoft.gsqlparser.ECreateTableOption.*;

public class testCreateTableOption extends TestCase {

    public void testDateRetentionTime(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TABLE \"TestTable\"\n" +
                "(\n" +
                "\"Col1\" int NOT NULL\n" +
                ")\n" +
                "DATA_RETENTION_TIME_IN_DAYS = 12;";

        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TCreateTableOption> createTableOptions = createTableSqlStatement.getTableOptions();
        TCreateTableOption createTableOption = createTableOptions.get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoDateRetentionTimeInDays);
        assertTrue(createTableOptions.get(0).getDateRetentionInDays().toString().equalsIgnoreCase("12"));
    }


    public void testTableComment(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TABLE \"TestTable\"\n" +
                "(\n" +
                "\"Col1\" int NOT NULL COMMENT 'Test comment.',\n" +
                "\"Col2\" int NOT NULL COMMENT 'Test comment 2.'\n" +
                ")\n" +
                "COMMENT = 'Table comment';";

        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TCreateTableOption> createTableOptions = createTableSqlStatement.getTableOptions();
        TCreateTableOption createTableOption = createTableOptions.get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoComment);
        assertTrue(createTableOption.getCommentToken().toString().equalsIgnoreCase("'Table comment'"));
        assertTrue(createTableSqlStatement.getTableComment().toString().equalsIgnoreCase("'Table comment'"));
        TColumnDefinition cd = createTableSqlStatement.getColumnList().getColumn(0);
        assertTrue(cd.getComment().toString().equalsIgnoreCase("'Test comment.'"));
    }

    public void testCluster(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create table \"TestSchema\".\"TestTable\" (\n" +
                "col1 int,\n" +
                "\"col2\" int\n" +
                ")\n" +
                "cluster by\n" +
                "(\n" +
                "\"col2\"\n" +
                ",\n" +
                "col1\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TCreateTableOption> createTableOptions = createTableSqlStatement.getTableOptions();
        TCreateTableOption createTableOption = createTableOptions.get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoClusterBy);
        assertTrue(createTableOptions.get(0).getExpressionList().size() == 2);
    }

    public void testStageCopyOptions(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TABLE \"TestTable\"\n" +
                "(\n" +
                "\"Col1\" int NOT NULL\n" +
                ")\n" +
                "STAGE_COPY_OPTIONS =\n" +
                "(\n" +
                "ON_ERROR = CONTINUE\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TCreateTableOption> createTableOptions = createTableSqlStatement.getTableOptions();
        TCreateTableOption createTableOption = createTableOptions.get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoStageCopyOptions);
        assertTrue(createTableOptions.get(0).getCopyOptions().toString().trim().equalsIgnoreCase("ON_ERROR = CONTINUE"));
    }

    public void testStageFileFormat(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TABLE \"TestTable1\"\n" +
                "(\n" +
                "\"Col1\" int NOT NULL\n" +
                ")\n" +
                "STAGE_FILE_FORMAT =\n" +
                "(\n" +
                "RECORD_DELIMITER = ''''\n" +
                "ESCAPE_UNENCLOSED_FIELD = ''''\n" +
                "ESCAPE = ''\n" +
                "NULL_IF = ( 'a''a', '''bb' )\n" +
                "FIELD_DELIMITER = ')'\n" +
                "FIELD_OPTIONALLY_ENCLOSED_BY = '\\''\n" +
                ")\n" +
                "COMMENT='Test';";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TCreateTableOption> createTableOptions = createTableSqlStatement.getTableOptions();
        TCreateTableOption createTableOption = createTableOptions.get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoStageFileFormat);
        //System.out.println(createTableOptions.get(0).getStageFileFormat().toString());
        assertTrue(createTableOptions.get(0).getStageFileFormat().toString().trim().equalsIgnoreCase("RECORD_DELIMITER = ''''\n" +
                "ESCAPE_UNENCLOSED_FIELD = ''''\n" +
                "ESCAPE = ''\n" +
                "NULL_IF = ( 'a''a', '''bb' )\n" +
                "FIELD_DELIMITER = ')'\n" +
                "FIELD_OPTIONALLY_ENCLOSED_BY = '\\''"));
    }
}
