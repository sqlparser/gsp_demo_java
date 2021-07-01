package bigquery;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EDeclareType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import junit.framework.TestCase;

public class testDeclare extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "DECLARE prev_execution_date DATE DEFAULT DATE_SUB(CURRENT_DATE(),\n" +
                "INTERVAL 1 DAY);\n" +
                "\n" +
                "DECLARE prev_execution_time TIMESTAMP DEFAULT CAST(CONCAT(\n" +
                "DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),' 23:59:59.999 UTC') as\n" +
                "TIMESTAMP);";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqldeclare);
        TMssqlDeclare declare =  (TMssqlDeclare)sqlparser.sqlstatements.get(0);

        assertTrue(declare.getVariables().getDeclareVariable(0).toString().equalsIgnoreCase("prev_execution_date"));
        assertTrue(declare.getDeclareType() == EDeclareType.variable);
        assertTrue(declare.getVariables().getDeclareVariable(0).getDefaultValue().toString().equalsIgnoreCase("DATE_SUB(CURRENT_DATE(),\n" +
                "INTERVAL 1 DAY)"));

    }
}

