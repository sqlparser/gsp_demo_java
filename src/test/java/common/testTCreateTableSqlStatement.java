package test;
/*
 * Date: 2010-11-11
 * Time: 14:20:43
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TConstraintList;

public class testTCreateTableSqlStatement extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "create table new_employees\n" +
                "(employee_id  number not null,\n" +
                " first_name  char2(15) null,\n" +
                " first_name2  char2(15) primary key,\n" +
                "last_name char2(15) unique,\n" +
                "last_name2 char2(15) check(last_name>10),\n" +
                "hire_date date default 5,\n" +
                "start_date timestamp(7) references scott.dept(start_date),\n" +
                "end_date timestamp(7) references dept.end_date on delete cascade,\n" +
                "end_date2 timestamp(7) references dept.end_date on update set null,\n" +
                "check (start_date>end_date),\n" +
                "constraint abc unique(a,b),\n" +
                "primary key(a),\n" +
                "foreign key(a,b) references dept(c,d) on delete set null\n" +
                ")";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);

        TColumnDefinitionList columns = createTable.getColumnList();
        assertTrue(columns.size() == 9);
        for(int i=0;i<columns.size();i++){
            assertTrue(columns.getColumn(i).getStartToken() != null);
            assertTrue(columns.getColumn(i).getEndToken() != null);
        }

        TConstraintList constraints = createTable.getTableConstraints();
        assertTrue(constraints.size() == 4);
        for(int i=0;i<constraints.size();i++){
            assertTrue(constraints.getConstraint(i).getStartToken() != null);
            assertTrue(constraints.getConstraint(i).getEndToken() != null);
        }

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "create table dept(employee_id,last_name,annsal)\n" +
                "as \n" +
                "select employee_id,last_name,salary*12 annsal\n" +
                "from employees\n" +
                "where department_id = 80";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);


        TColumnDefinitionList columns = createTable.getColumnList();
        assertTrue(columns.size() == 3);
        assertTrue(columns.getColumn(0).getStartToken().toString().equalsIgnoreCase("employee_id"));
        assertTrue(columns.getColumn(0).getEndToken().toString().equalsIgnoreCase("employee_id"));
        assertTrue(columns.getColumn(1).getStartToken().toString().equalsIgnoreCase("last_name"));
        assertTrue(columns.getColumn(1).getEndToken().toString().equalsIgnoreCase("last_name"));
        assertTrue(columns.getColumn(2).getStartToken().toString().equalsIgnoreCase("annsal"));
        assertTrue(columns.getColumn(2).getEndToken().toString().equalsIgnoreCase("annsal"));
    }

    public void testMySQL(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE t (i INT, INDEX /*!40100 USING BTREE */ (i)) ENGINE = MEMORY;";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);

        TColumnDefinitionList columns = createTable.getColumnList();
        assertTrue(columns.size() == 1);
        assertTrue(columns.getStartToken().toString().equalsIgnoreCase("i"));
        assertTrue(columns.getEndToken().toString().equalsIgnoreCase("INT"));

        TConstraintList constraints = createTable.getTableConstraints();
        assertTrue(constraints.size() == 1);
        assertTrue(constraints.getStartToken().toString().equalsIgnoreCase("INDEX"));
        assertTrue(constraints.getEndToken().toString().equalsIgnoreCase(")"));

    }

}
