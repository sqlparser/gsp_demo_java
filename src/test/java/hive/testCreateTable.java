package test.hive;
/*
 * Date: 13-8-15
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.nodes.hive.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "create table Addresses (\n" +
                "  name string,\n" +
                "  street string,\n" +
                "  city string,\n" +
                "  state string,\n" +
                "  zip int\n" +
                ") stored as orc tblproperties (\"orc.compress\"=\"NONE\");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("Addresses"));
        assertTrue(createTable.getColumnList().size() == 5 );
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("name"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.string_t);
        cd = createTable.getColumnList().getColumn(4);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("zip"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.int_t);

        THiveTableFileFormat tff = createTable.getHiveTableFileFormat();
        assertTrue(tff.getFileFormat() == EHiveStoredFileFormat.sffFILEFORMAT_GENERIC);
        assertTrue(tff.getGenericSpec().toString().equalsIgnoreCase("orc"));

        THiveTableProperties tp = createTable.getHiveTableProperties();
        assertTrue(tp.getTableProperties().size() == 1);
        THiveKeyValueProperty kv = tp.getTableProperties().getElement(0);
        assertTrue(kv.getKeyString().toString().equalsIgnoreCase("\"orc.compress\""));
        assertTrue(kv.getValueString().toString().equalsIgnoreCase("\"NONE\""));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE TABLE union_test(foo UNIONTYPE<int, double, array<string>, struct<a:int,b:string>>);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("union_test"));
        assertTrue(createTable.getColumnList().size() == 1 );
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("foo"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.unionType_t);

        assertTrue(cd.getDatatype().getColTypeList().size() == 4);
        TTypeName dataType = cd.getDatatype().getColTypeList().getElement(0);
        assertTrue(dataType.getDataType() == EDataType.int_t);

        dataType = cd.getDatatype().getColTypeList().getElement(1);
        assertTrue(dataType.getDataType() == EDataType.double_t);

        dataType = cd.getDatatype().getColTypeList().getElement(2);
        assertTrue(dataType.getDataType() == EDataType.listType_t);
        assertTrue(dataType.getTypeOfList().getDataType() == EDataType.string_t);

        dataType = cd.getDatatype().getColTypeList().getElement(3);
        assertTrue(dataType.getDataType() == EDataType.structType_t);
        assertTrue(dataType.getColumnDefList().size() == 2);
        TColumnDefinition cd1 = dataType.getColumnDefList().getColumn(0);
        assertTrue(cd1.getColumnName().toString().equalsIgnoreCase("a"));
        assertTrue(cd1.getDatatype().getDataType() == EDataType.int_t);
        TColumnDefinition cd2 = dataType.getColumnDefList().getColumn(1);
        assertTrue(cd2.getColumnName().toString().equalsIgnoreCase("b"));
        assertTrue(cd2.getDatatype().getDataType() == EDataType.string_t);

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE TABLE complex_json (\n" +
                "  DocId string,\n" +
                "  User struct<Id:int,\n" +
                "              Username:string,\n" +
                "              Name: string,\n" +
                "              ShippingAddress:struct<Address1:string,\n" +
                "                                     Address2:string,\n" +
                "                                     City:string,\n" +
                "                                     State:string>,\n" +
                "              Orders:array<struct<ItemId:int,\n" +
                "                                  OrderDate:string>>>\n" +
                ")\n" +
                "ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("complex_json"));
        assertTrue(createTable.getColumnList().size() == 2 );
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("DocId"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.string_t);

        cd = createTable.getColumnList().getColumn(1);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("User"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.structType_t);
        assertTrue(cd.getDatatype().getColumnDefList().size() == 5);

        THiveRowFormat rowFormat = createTable.getHiveRowFormat();
        assertTrue(rowFormat.getRowFormatType() == THiveRowFormat.ERowFormatType.serde);
        assertTrue(rowFormat.getRowFormatName().toString().equalsIgnoreCase("'org.openx.data.jsonserde.JsonSerDe'"));

    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE TABLE page_view(viewTime INT, userid BIGINT,\n" +
                "page_url STRING, referrer_url STRING,\n" +
                "ip STRING COMMENT 'IP Address of the User')\n" +
                "COMMENT 'This is the page view table'\n" +
                "PARTITIONED BY(dt STRING, country STRING)\n" +
                "ROW FORMAT DELIMITED\n" +
                "FIELDS TERMINATED BY '1'\n" +
                "STORED AS SEQUENCEFILE;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("page_view"));
        TColumnDefinition cd = createTable.getColumnList().getColumn(4);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("ip"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.string_t);
        assertTrue(cd.getComment().toString().equalsIgnoreCase("'IP Address of the User'"));

        assertTrue(createTable.getTableComment().toString().equalsIgnoreCase("'This is the page view table'"));
        THiveTablePartition tp =  createTable.getHiveTablePartition();
        assertTrue(tp.getColumnDefList().size() == 2);
        assertTrue(tp.getColumnDefList().getColumn(0).getColumnName().toString().equalsIgnoreCase("dt"));
        assertTrue(tp.getColumnDefList().getColumn(1).getDatatype().getDataType() == EDataType.string_t);

        THiveRowFormat rowFormat = createTable.getHiveRowFormat();
        assertTrue(rowFormat.getRowFormatType() == THiveRowFormat.ERowFormatType.delimited);
        assertTrue(rowFormat.getTableRowFormatFieldIdentifier().getTerminateString().toString().equalsIgnoreCase("'1'"));

        THiveTableFileFormat tff = createTable.getHiveTableFileFormat();
        assertTrue(tff.getFileFormat() == EHiveStoredFileFormat.sffTBLSEQUENCEFILE);


    }

}
