package oracle;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.TXMLPassingClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testXMLTable extends TestCase {

    public static void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT warehouse_name warehouse,\n" +
                "   warehouse2.\"Water\", warehouse2.\"Rail\"\n" +
                "   FROM warehouses,\n" +
                "   XMLTABLE('/Warehouse'\n" +
                "      PASSING warehouses.warehouse_spec\n" +
                "      COLUMNS\n" +
                "         \"Water\" varchar2(6) PATH '/Warehouse/WaterAccess',\n" +
                "         \"Rail\" varchar2(6) PATH '/Warehouse/RailAccess') warehouse2;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable Table1 = select.tables.getTable(1);
        assertTrue(Table1.getTableType() == ETableSource.xmltable);
        TXmlTable xmlTable = Table1.getXmlTable();
        TXmlTableParameter parameter = xmlTable.getArg();
        assertTrue(parameter.getXQueryString().toString().equalsIgnoreCase("'/Warehouse'"));
        TXMLPassingClause passingClause = parameter.getXmlPassingClause();
        assertTrue(passingClause.getPassingList().getResultColumn(0).toString().equalsIgnoreCase("warehouses.warehouse_spec"));
        assertTrue(parameter.getXmlTableColumns().size() == 2);
        TColumnDefinition columnDefinition = parameter.getXmlTableColumns().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("\"Water\""));
        assertTrue(columnDefinition.getDatatype().toString().equalsIgnoreCase("varchar2(6)"));
        assertTrue(columnDefinition.getXmlTableColumnPath().toString().equalsIgnoreCase("'/Warehouse/WaterAccess'"));
        columnDefinition = parameter.getXmlTableColumns().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("\"Rail\""));
        assertTrue(columnDefinition.getDatatype().toString().equalsIgnoreCase("varchar2(6)"));
        assertTrue(columnDefinition.getXmlTableColumnPath().toString().equalsIgnoreCase("'/Warehouse/RailAccess'"));

    }
}
