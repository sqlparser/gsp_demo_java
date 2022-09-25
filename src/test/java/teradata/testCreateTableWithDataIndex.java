package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.teradata.TIndexDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTableWithDataIndex extends TestCase {

        public void test1() {
            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
            sqlparser.sqltext = "CREATE MULTISET TABLE test_schema.test_table_table_1\n" +
                    "AS\n" +
                    "(\n" +
                    "  SELECT COL1, COL2, COL3\n" +
                    "  FROM test_table2\n" +
                    ") WITH DATA INDEX(COL3) ON COMMIT PRESERVE ROWS;";


            assertTrue(sqlparser.parse() == 0);

            TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
            assertTrue(createTable.getTableName().toString().equalsIgnoreCase("test_schema.test_table_table_1"));
            assertTrue(createTable.getIndexDefinitions().size() == 1);
            TIndexDefinition indexDefinition = createTable.getIndexDefinitions().get(0);
            assertTrue(indexDefinition.getIndexColumns().getObjectName(0).toString().equalsIgnoreCase("COL3"));

        }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "\tCREATE MULTISET TABLE test_schema.test_table_table_1\n" +
                "AS\n" +
                "(\n" +
                "  SELECT COL1, COL2, COL3\n" +
                "  FROM test_table2\n" +
                ") WITH DATA UNIQUE PRIMARY INDEX my_index (COL3)\n" +
                "ON COMMIT PRESERVE ROWS;";


        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("test_schema.test_table_table_1"));
        assertTrue(createTable.getIndexDefinitions().size() == 1);
        TIndexDefinition indexDefinition = createTable.getIndexDefinitions().get(0);
        assertTrue(indexDefinition.getIndexColumns().getObjectName(0).toString().equalsIgnoreCase("COL3"));

    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE MULTISET TABLE test_schema.test_table_table_1\n" +
                "AS\n" +
                "(\n" +
                "  SELECT COL1, COL2, COL3\n" +
                "  FROM test_table2\n" +
                ") WITH DATA PRIMARY INDEX\n" +
                "(COL3, COL2)\n" +
                "ON COMMIT PRESERVE ROWS;";


        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("test_schema.test_table_table_1"));
        assertTrue(createTable.getIndexDefinitions().size() == 1);
        TIndexDefinition indexDefinition = createTable.getIndexDefinitions().get(0);
        assertTrue(indexDefinition.getIndexColumns().getObjectName(0).toString().equalsIgnoreCase("COL3"));
        assertTrue(indexDefinition.getIndexColumns().getObjectName(1).toString().equalsIgnoreCase("COL2"));

    }
}
