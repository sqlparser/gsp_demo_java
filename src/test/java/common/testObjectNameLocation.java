package common;



import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testObjectNameLocation extends TestCase {

    public void testSelect(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "select employee_id,last_name\n" +
                "from employees\n" +
                "where department_id = 90\n" +
                "group by employee_id\n" +
                "order by last_name;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);

        assertTrue(table.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.selectList);
        assertTrue(table.getLinkedColumns().getObjectName(1).getLocation() == ESqlClause.selectList);
        assertTrue(table.getLinkedColumns().getObjectName(2).getLocation() == ESqlClause.where);
        assertTrue(table.getLinkedColumns().getObjectName(3).getLocation() == ESqlClause.groupby);
        assertTrue(table.getLinkedColumns().getObjectName(4).getLocation() == ESqlClause.orderby);
    }

    public void testInsert(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "insert into emp e1 (e1.lastname,job) values('scott',10);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = insert.tables.getTable(0);

        assertTrue(table.getLinkedColumns().size() == 2);
        assertTrue(table.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.insertColumn);
        assertTrue(table.getLinkedColumns().getObjectName(1).getLocation() == ESqlClause.insertColumn);
    }

    public void testUpdate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "update employees\n" +
                "set department_ID = 70\n" +
                "where employee_id = 113;";
        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = update.tables.getTable(0);

        assertTrue(table.getLinkedColumns().size() == 2);
        assertTrue(table.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.set);
        assertTrue(table.getLinkedColumns().getObjectName(1).getLocation() == ESqlClause.where);
    }

    public void testDelete(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "delete from employees E\n" +
                "where employee_id = \n" +
                "(select employee_sal\n" +
                "from emp_history\n" +
                "where employee_id = e.employee_id);";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement delete = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = delete.tables.getTable(0);

        assertTrue(table.getLinkedColumns().size() == 2);
        assertTrue(table.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.where);
        assertTrue(table.getLinkedColumns().getObjectName(1).getLocation() == ESqlClause.where);

        // subquery in where clause
        TSelectSqlStatement select = (TSelectSqlStatement)delete.getStatements().get(0);
        TTable table1 = select.tables.getTable(0);
        assertTrue(table1.getLinkedColumns().size() == 2);
        assertTrue(table1.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.selectList);
        assertTrue(table1.getLinkedColumns().getObjectName(1).getLocation() == ESqlClause.where);

    }

}
