package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testQualifyClause extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select department_id, COUNT(department_id) as department_id_number, ROW_NUMBER() OVER (ORDER BY department_id ASC), SUM(department_id_number) OVER (ORDER BY department_id ASC) sum_dept_num\n" +
                "from foodmart.trimmed_employee GROUP by department_id qualify sum_dept_num > 0;";
        //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getQualifyClause().getSearchConditoin().toString().equalsIgnoreCase("sum_dept_num > 0"));
    }


    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT col1,col2  qualify ROW_NUMBER () OVER (PARTITION BY col1 ORDER BY  col3 DESC , col4 DESC ) =1\n" +
                "FROM database_sample.T_sample_table";
        //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getQualifyClause().getSearchConditoin().toString().equalsIgnoreCase("ROW_NUMBER () OVER (PARTITION BY col1 ORDER BY  col3 DESC , col4 DESC ) =1"));
        //System.out.println(select.getQualifyClause().getSearchConditoin().toString());
    }

}
