package gudusoft.gsqlparser.mssqlTest;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TJsonTable;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testOpenjson extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @json NVARCHAR(2048) = N'{\n" +
                "   \"String_value\": \"John\",\n" +
                "   \"DoublePrecisionFloatingPoint_value\": 45,\n" +
                "   \"DoublePrecisionFloatingPoint_value\": 2.3456,\n" +
                "   \"BooleanTrue_value\": true,\n" +
                "   \"BooleanFalse_value\": false,\n" +
                "   \"Null_value\": null,\n" +
                "   \"Array_value\": [\"a\",\"r\",\"r\",\"a\",\"y\"],\n" +
                "   \"Object_value\": {\"obj\":\"ect\"}\n" +
                "}';\n" +
                "\n" +
                "SELECT * FROM OpenJson(@json);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(1);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableType() == ETableSource.jsonTable);
        TJsonTable jsonTable = table.getJsonTable();
        assertTrue(jsonTable.getJsonExpression().toString().equalsIgnoreCase("@json"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @json NVARCHAR(4000) = N'{  \n" +
                "      \"path\": {  \n" +
                "            \"to\":{  \n" +
                "                 \"sub-object\":[\"en-GB\", \"en-UK\",\"de-AT\",\"es-AR\",\"sr-Cyrl\"]  \n" +
                "                 }  \n" +
                "              }  \n" +
                " }';\n" +
                "\n" +
                "SELECT [key], value\n" +
                "FROM OPENJSON(@json,'$.path.to.\"sub-object\"')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(1);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableType() == ETableSource.jsonTable);
        TJsonTable jsonTable = table.getJsonTable();
        assertTrue(jsonTable.getJsonExpression().toString().equalsIgnoreCase("@json"));
        assertTrue(jsonTable.getPath().equalsIgnoreCase("'$.path.to.\"sub-object\"'"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @json NVARCHAR(MAX) = N'[  \n" +
                "  {  \n" +
                "    \"Order\": {  \n" +
                "      \"Number\":\"SO43659\",  \n" +
                "      \"Date\":\"2011-05-31T00:00:00\"  \n" +
                "    },  \n" +
                "    \"AccountNumber\":\"AW29825\",  \n" +
                "    \"Item\": {  \n" +
                "      \"Price\":2024.9940,  \n" +
                "      \"Quantity\":1  \n" +
                "    }  \n" +
                "  },  \n" +
                "  {  \n" +
                "    \"Order\": {  \n" +
                "      \"Number\":\"SO43661\",  \n" +
                "      \"Date\":\"2011-06-01T00:00:00\"  \n" +
                "    },  \n" +
                "    \"AccountNumber\":\"AW73565\",  \n" +
                "    \"Item\": {  \n" +
                "      \"Price\":2024.9940,  \n" +
                "      \"Quantity\":3  \n" +
                "    }  \n" +
                "  }\n" +
                "]'  \n" +
                "   \n" +
                "SELECT *\n" +
                "FROM OPENJSON ( @json )  \n" +
                "WITH (   \n" +
                "              Number   VARCHAR(200)   '$.Order.Number',  \n" +
                "              Date     DATETIME       '$.Order.Date',  \n" +
                "              Customer VARCHAR(200)   '$.AccountNumber',  \n" +
                "              Quantity INT            '$.Item.Quantity',  \n" +
                "              [Order]  NVARCHAR(MAX)  AS JSON  \n" +
                " )";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(1);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableType() == ETableSource.jsonTable);
        TJsonTable jsonTable = table.getJsonTable();
        assertTrue(jsonTable.getJsonExpression().toString().equalsIgnoreCase("@json"));
        TColumnDefinitionList columnDefinitions = jsonTable.getColumnDefinitions();
        assertTrue(columnDefinitions.size() == 5);
        TColumnDefinition columnDefinition = columnDefinitions.getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("Number"));
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.varchar_t);
        assertTrue(columnDefinition.getColumnPath().equalsIgnoreCase("'$.Order.Number'"));
        assertTrue(!columnDefinition.isAsJson());

        columnDefinition = columnDefinitions.getColumn(4);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("[Order]"));
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.nvarchar_t);
        assertTrue(columnDefinition.isAsJson());
    }

}
