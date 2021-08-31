package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTableResultCache extends TestCase {

    public void testCreateTableLike(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE TABLE \"BIDU_E1\".\"TIPO_COMUNICACAO\"\n" +
                "( \"TICO_CD_TIPO_COMUNICACAO\" NUMBER(*,0) NOT NULL ENABLE,\n" +
                "\"TICO_DS_TIPO_COMUNICACAO\" VARCHAR2(30),\n" +
                " SUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS,\n" +
                " SUPPLEMENTAL LOG DATA (UNIQUE INDEX) COLUMNS,\n" +
                " SUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS,\n" +
                " SUPPLEMENTAL LOG GROUP \"GGS_538033\" (\"TICO_CD_TIPO_COMUNICACAO\") ALWAYS\n" +
                ")\n" +
                "RESULT_CACHE(MODE FORCE) ;";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("\"BIDU_E1\".\"TIPO_COMUNICACAO\""));
        assertTrue(createTable.getColumnList().size() == 2);
        assertTrue(createTable.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("\"TICO_CD_TIPO_COMUNICACAO\""));
    }
}
