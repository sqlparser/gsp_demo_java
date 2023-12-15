package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TAlterConstraintStmt;
import gudusoft.gsqlparser.util.THelp;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testCheckSourceColumn extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING (empno INTEGER,\n" +
                "name VARCHAR(50),\n" +
                "salary INTEGER)\n" +
                "MERGE INTO employee AS t\n" +
                "USING VALUES (:empno, :name, :salary) AS s(empno, name, salary)\n" +
                "ON t.empno=s.empno\n" +
                "WHEN MATCHED THEN UPDATE\n" +
                "SET salary=s.salary\n" +
                "WHEN NOT MATCHED THEN INSERT (empno, name, salary)\n" +
                "VALUES (s.empno, s.name, s.salary);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmerge);
        TMergeSqlStatement sqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TObjectName> columns = THelp.getObjectNames(sqlStatement);
        for(TObjectName objectName:columns){
           // System.out.println(objectName.toString());
            if (objectName.toString().equalsIgnoreCase("s.empno") && (objectName.getStartToken().lineNo == 10)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase(":empno"));
            }
            if (objectName.toString().equalsIgnoreCase("s.name") && (objectName.getStartToken().lineNo == 10)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase(":name"));
            }
            if (objectName.toString().equalsIgnoreCase("s.salary") && (objectName.getStartToken().lineNo == 10)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase(":salary"));
            }
        }

    }
}

