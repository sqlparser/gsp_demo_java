package test.teradata;
/*
 * Date: 13-10-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataExecute;
import gudusoft.gsqlparser.stmt.teradata.TTeradataUsing;
import junit.framework.TestCase;

public class testUsing extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING (price DECIMAL(6,2))\n" +
                "UPDATE tab1\n" +
                "SET column1 = CAST(:price AS euro);";
         //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatausing);
        TTeradataUsing using = (TTeradataUsing)sqlparser.sqlstatements.get(0);
        assertTrue(using.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("price"));
        assertTrue(using.getSqlRequest().sqlstatementtype == ESqlStatementType.sstupdate);
        TUpdateSqlStatement update = (TUpdateSqlStatement)using.getSqlRequest();
        assertTrue(update.getTargetTable().toString().equalsIgnoreCase("tab1"));

    }

    public void testExec(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING(AA1 int)\n" +
                "EXEC insert1(:AA1);";
        //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatausing);
        TTeradataUsing using = (TTeradataUsing)sqlparser.sqlstatements.get(0);
        assertTrue(using.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("AA1"));
        assertTrue(using.getSqlRequest().sqlstatementtype == ESqlStatementType.sstteradataexecute);
        TTeradataExecute execute = (TTeradataExecute)using.getSqlRequest();
        assertTrue(execute.getMacroName().toString().equalsIgnoreCase("insert1"));

    }

}
