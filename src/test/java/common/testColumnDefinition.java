package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testColumnDefinition extends TestCase {

    public void testDefaultValue() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "CREATE TABLE \"DB2I1\".\"DEPOS\" (\n" +
                "          \"D_FILDT\" DATE NOT NULL , \n" +
                "          \"D_GL\" VARCHAR(10) NOT NULL WITH DEFAULT '-' , \n" +
                "          \"D_PRODUCT\" VARCHAR(20) ) \n" +
                "         ;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("\"DB2I1\".\"DEPOS\""));
        TColumnDefinition columnDefinition = createTable.getColumnList().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("\"D_GL\""));
        assertTrue(columnDefinition.getDefaultExpression().toString().equalsIgnoreCase("'-'"));
    }
}
