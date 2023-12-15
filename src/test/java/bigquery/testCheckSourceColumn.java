package bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.util.THelp;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testCheckSourceColumn extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "MERGE INTO EMP1 D USING (\n" +
                "  SELECT E2.* FROM EMP2 E2, EMP3 E3 WHERE E3.dept = 'prod'\n" +
                ") S\n" +
                "ON (D.id = S.id)\n" +
                "WHEN MATCHED THEN\n" +
                "  UPDATE SET joining_date = current_date\n" +
                "WHEN NOT MATCHED THEN\n" +
                "  INSERT (id, name, joining_date) VALUES (id2, TRIM(name2), joining_date2);";
        System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmerge);
        TMergeSqlStatement sqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        ArrayList<TObjectName> columns = THelp.getObjectNames(sqlStatement);
        for(TObjectName objectName:columns){
            // System.out.println(objectName.toString());
            if (objectName.toString().equalsIgnoreCase("id2") && (objectName.getStartToken().lineNo == 8)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase("E2.*"));
            }
            if (objectName.toString().equalsIgnoreCase("name2") && (objectName.getStartToken().lineNo == 8)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase("E2.*"));
            }
            if (objectName.toString().equalsIgnoreCase("joining_date2") && (objectName.getStartToken().lineNo == 8)){
                //System.out.println(String.format("Source column for %s is %s",objectName.toString(),objectName.getSourceColumn()==null?"N/A":objectName.getSourceColumn().toString()));
                assertTrue(objectName.getSourceColumn().toString().equalsIgnoreCase("E2.*"));
            }
        }

    }
}