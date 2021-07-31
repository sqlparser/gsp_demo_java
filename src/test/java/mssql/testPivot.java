package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPivotClause;
import gudusoft.gsqlparser.nodes.TPivotedTable;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TUnpivotInClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testPivot extends TestCase {

    public void testSqlServer1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT 'AverageCost' AS Cost_Sorted_By_Production_Days, \n" +
                "[0], [1], [2], [3], [4]\n" +
                "FROM\n" +
                "(SELECT DaysToManufacture, StandardCost \n" +
                "    FROM Production.Product) AS SourceTable\n" +
                "PIVOT\n" +
                "(\n" +
                "AVG(StandardCost)\n" +
                "FOR DaysToManufacture IN ([0], [1], [2], [3], [4])\n" +
                ") AS PivotTable;";
        assertTrue(sqlparser.parse() == 0);

      //  System.out.print(sqlparser.sqltext);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TTable table = select.tables.getTable(0);
        //assertTrue(table.getTableType() == ETableSource.pivoted_table);
        assertTrue(table.getTableType() == ETableSource.subquery);
        TSelectSqlStatement subquery = table.getSubquery();
        assertTrue(subquery.tables.getTable(0).toString().equalsIgnoreCase("Production.Product"));
        assertTrue(table.getAliasClause().getAliasName().toString().equalsIgnoreCase("SourceTable"));

        assertTrue(select.getTargetTable().getTableType() == ETableSource.pivoted_table);
        TPivotedTable pivotedTable = select.getTargetTable().getPivotedTable();

        TPivotClause pivotClause = pivotedTable.getPivotClause();
        assertTrue(pivotClause.getType() == TPivotClause.pivot);

        assertTrue(pivotClause.getAggregation_function().toString().equalsIgnoreCase("AVG(StandardCost)"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("DaysToManufacture"));
        //assertTrue(pivotClause.getPivotColumnList().size() == 5);
        assertTrue( pivotClause.getPivotInClause().getItems().size() == 5);
        //assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("[0]"));
        assertTrue( pivotClause.getPivotInClause().getItems().getResultColumn(0).toString().equalsIgnoreCase("[0]"));
        assertTrue(pivotClause.getAliasClause().toString().equalsIgnoreCase("PivotTable"));
    }

    public void testSqlServer2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT ShipName, ShipAddress, [1],[2],[3], France, Germany, Brazil\n" +
                "from [northwind].[dbo].[Orders]\n" +
                "pivot ( sum(freight) for shipvia in ([1],[2],[3])) sum_freight\n" +
                "pivot ( count(shipcountry) for Shipcountry in ([France], [Germany], [Brazil])) cntry";
        assertTrue(sqlparser.parse() == 0);
        //System.out.print(sqlparser.sqltext);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TTable table = select.tables.getTable(0);
        //assertTrue(table.getTableType() == ETableSource.pivoted_table);
        assertTrue(table.getTableType() == ETableSource.objectname);
        assertTrue(table.toString().equalsIgnoreCase("[northwind].[dbo].[Orders]"));

        TPivotedTable pivotedTable =  select.getTargetTable().getPivotedTable();
        assertTrue(pivotedTable.getPivotClauseList().size() == 2);

        TPivotClause pivotClause = pivotedTable.getPivotClauseList().getElement(0);
        assertTrue(pivotClause.getType() == TPivotClause.pivot);
        assertTrue(pivotClause.getAggregation_function().toString().equalsIgnoreCase("sum(freight)"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("shipvia"));

//        assertTrue(pivotClause.getPivotColumnList().size() == 3);
        assertTrue(pivotClause.getPivotInClause().getItems().size() == 3);
//        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("[1]"));
        assertTrue(pivotClause.getPivotInClause().getItems().getResultColumn(0).toString().equalsIgnoreCase("[1]"));
        assertTrue(pivotClause.getAliasClause().toString().equalsIgnoreCase("sum_freight"));

//        table = pivotedTable.getTableSource();
//        assertTrue(table.getTableType() == ETableSource.pivoted_table);
//        pivotedTable = table.getPivotedTable();

        pivotClause = pivotedTable.getPivotClauseList().getElement(1);
        assertTrue(pivotClause.getType() == TPivotClause.pivot);
        assertTrue(pivotClause.getAggregation_function().toString().equalsIgnoreCase("count(shipcountry)"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("Shipcountry"));
        //assertTrue(pivotClause.getPivotColumnList().size() == 3);
        assertTrue(pivotClause.getPivotInClause().getItems().size() == 3);
        //assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("[France]"));
        assertTrue(pivotClause.getPivotInClause().getItems().getResultColumn(0).toString().equalsIgnoreCase("[France]"));
        assertTrue(pivotClause.getAliasClause().toString().equalsIgnoreCase("cntry"));

    }

    public void testUnpivot1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT CustomerID, Phone\n" +
                "FROM\n" +
                "(\n" +
                "  SELECT CustomerID, Phone1, Phone2, Phone3 \n" +
                "  FROM dbo.CustomerPhones\n" +
                ") AS cp\n" +
                "UNPIVOT \n" +
                "(\n" +
                "  Phone FOR Phones IN (Phone1, Phone2, Phone3)\n" +
                ") AS up;\n";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.subquery);
        TSelectSqlStatement subquery = table.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("dbo.CustomerPhones"));

        TPivotedTable pivotedTable =  select.getTargetTable().getPivotedTable();
        assertTrue(pivotedTable.getPivotClauseList().size() == 1);

        TPivotClause pivotClause = pivotedTable.getPivotClauseList().getElement(0);
        assertTrue(pivotClause.getType() == TPivotClause.unpivot);
        assertTrue(pivotClause.getPivotTable().toString().equalsIgnoreCase("up"));
        assertTrue(pivotClause.getValueColumnList().getObjectName(0).toString().equalsIgnoreCase("Phone"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("Phones"));
        TUnpivotInClause unpivotInClause = pivotClause.getUnpivotInClause();
        assertTrue(unpivotInClause.getItems().getElement(0).getColumn().toString().equalsIgnoreCase("Phone1"));
        assertTrue(unpivotInClause.getItems().getElement(1).getColumn().toString().equalsIgnoreCase("Phone2"));
        assertTrue(unpivotInClause.getItems().getElement(2).getColumn().toString().equalsIgnoreCase("Phone3"));
    }

    public void testUnpivot2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT CustomerID, Phone, PhoneType\n" +
                "FROM \n" +
                "(\n" +
                "  SELECT CustomerID, Phone, PhoneType,\n" +
                "    idp = SUBSTRING(Phones, LEN(Phones) - PATINDEX('%[^0-9]%', REVERSE(Phones)) + 2, 32),\n" +
                "    idpt = SUBSTRING(PhoneTypes, LEN(PhoneTypes) - PATINDEX('%[^0-9]%', REVERSE(PhoneTypes)) + 2, 32)\n" +
                "  FROM\n" +
                "  (\n" +
                "    SELECT CustomerID, Phone1, Phone2, Phone3,\n" +
                "           PhoneType1, PhoneType2, PhoneType3\n" +
                "    FROM dbo.CustomerPhones2\n" +
                "  ) AS cp\n" +
                "  UNPIVOT \n" +
                "  (\n" +
                "    Phone FOR Phones IN (Phone1, Phone2, Phone3)\n" +
                "  ) AS p\n" +
                "  UNPIVOT\n" +
                "  (\n" +
                "    PhoneType FOR PhoneTypes IN (PhoneType1, PhoneType2, PhoneType3)\n" +
                "  ) AS pt\n" +
                ") AS x\n" +
                "WHERE idp = idpt;\n";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement subquery0 = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table0 = subquery0.tables.getTable(0);

        TSelectSqlStatement  select = table0.getSubquery();

        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.subquery);
        TSelectSqlStatement subquery = table.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("dbo.CustomerPhones2"));

        TPivotedTable pivotedTable =  select.getTargetTable().getPivotedTable();
        assertTrue(pivotedTable.getPivotClauseList().size() == 2);

        TPivotClause pivotClause = pivotedTable.getPivotClauseList().getElement(0);
        assertTrue(pivotClause.getType() == TPivotClause.unpivot);
        assertTrue(pivotClause.getPivotTable().toString().equalsIgnoreCase("p"));
        assertTrue(pivotClause.getValueColumnList().getObjectName(0).toString().equalsIgnoreCase("Phone"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("Phones"));
        TUnpivotInClause unpivotInClause = pivotClause.getUnpivotInClause();
        assertTrue(unpivotInClause.getItems().getElement(0).getColumn().toString().equalsIgnoreCase("Phone1"));
        assertTrue(unpivotInClause.getItems().getElement(1).getColumn().toString().equalsIgnoreCase("Phone2"));
        assertTrue(unpivotInClause.getItems().getElement(2).getColumn().toString().equalsIgnoreCase("Phone3"));

        pivotClause = pivotedTable.getPivotClauseList().getElement(1);
        assertTrue(pivotClause.getType() == TPivotClause.unpivot);
        assertTrue(pivotClause.getPivotTable().toString().equalsIgnoreCase("pt"));
        assertTrue(pivotClause.getValueColumnList().getObjectName(0).toString().equalsIgnoreCase("PhoneType"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("PhoneTypes"));
        unpivotInClause = pivotClause.getUnpivotInClause();
        assertTrue(unpivotInClause.getItems().getElement(0).getColumn().toString().equalsIgnoreCase("PhoneType1"));
        assertTrue(unpivotInClause.getItems().getElement(1).getColumn().toString().equalsIgnoreCase("PhoneType2"));
        assertTrue(unpivotInClause.getItems().getElement(2).getColumn().toString().equalsIgnoreCase("PhoneType3"));
    }

}