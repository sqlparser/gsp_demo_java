package redshift;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;



public class testCreateTable extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create table tickit.public.test (c1 int);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("tickit.public.test"));
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("c1"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.int_t);
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create table sales(\n" +
                "salesid integer not null,\n" +
                "listid integer not null,\n" +
                "sellerid integer not null,\n" +
                "buyerid integer not null,\n" +
                "eventid integer not null encode mostly16,\n" +
                "dateid smallint not null,\n" +
                "qtysold smallint not null encode mostly8,\n" +
                "pricepaid decimal(8,2) encode delta32k,\n" +
                "commission decimal(8,2) encode delta32k,\n" +
                "saletime timestamp,\n" +
                "primary key(salesid),\n" +
                "foreign key(listid) references listing(listid),\n" +
                "foreign key(sellerid) references users(userid),\n" +
                "foreign key(buyerid) references users(userid),\n" +
                "foreign key(dateid) references date(dateid))\n" +
                "distkey(listid)\n" +
                "compound sortkey(listid,sellerid);";
       // System.out.print(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("sales"));
        assertTrue(createTable.getColumnList().size() == 10);
        assertTrue(createTable.getTableConstraints().size() == 5);

        TColumnDefinition cd = createTable.getColumnList().getColumn(4);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("eventid"));
        //System.out.print(cd.getDatatype().getDataType());
        assertTrue(cd.getDatatype().getDataType() == EDataType.int_t);
       // assertTrue(cd.getColumnAttributes().getEncoding().equalsIgnoreCase("mostly16"));
        assertTrue(cd.getColumnAttributes().getColumnAttribute(0).getColumnAttributeType() == EColumnAttributeType.encode);
        assertTrue(cd.getColumnAttributes().getColumnAttribute(0).getEncoding().equalsIgnoreCase("mostly16"));

        TConstraint constraint = createTable.getTableConstraints().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.primary_key);
        assertTrue(constraint.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("salesid"));

        constraint = createTable.getTableConstraints().getConstraint(1);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);
        assertTrue(constraint.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("listid"));
        assertTrue(constraint.getReferencedObject().toString().equalsIgnoreCase("listing"));
        assertTrue(constraint.getReferencedColumnList().getObjectName(0).toString().equalsIgnoreCase("listid"));
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create table eventdistevensort diststyle even sortkey (venueid)\n" +
                "as select eventid, venueid, dateid, eventname from event;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("eventdistevensort"));

        TSelectSqlStatement select = createTable.getSubQuery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("event"));
        assertTrue(select.getResultColumnList().size() == 4);
        assertTrue(select.getResultColumnList().getResultColumn(0).getExpr().toString().equalsIgnoreCase("eventid"));
    }

    public void test4() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "CREATE TABLE customer_v1 (\n" +
                "c_custkey int8 NOT NULL DISTKEY SORTKEY PRIMARY KEY ,\n" +
                "c_name varchar(25) NOT NULL ,\n" +
                "c_address varchar(40) NOT NULL ,\n" +
                "c_nationkey int4 NOT NULL REFERENCES nation(n_nationkey) ,\n" +
                "c_phone char(15) NOT NULL ,\n" +
                "c_acctbal numeric(12,2) NOT NULL ,\n" +
                "c_mktsegment char(10) NOT NULL ,\n" +
                "c_comment varchar(117) NOT NULL\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("customer_v1"));

        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("c_custkey"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.bigint_t);
        assertTrue(cd.getColumnAttributes().getColumnAttribute(0).getColumnAttributeType() == EColumnAttributeType.distkey);
        assertTrue(cd.getColumnAttributes().getColumnAttribute(1).getColumnAttributeType() == EColumnAttributeType.sortkey);
        assertTrue(cd.getConstraints().getConstraint(0).getConstraint_type() == EConstraintType.notnull);
        assertTrue(cd.getConstraints().getConstraint(1).getConstraint_type() == EConstraintType.primary_key);
    }
}