package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 13-10-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataExecute;
import junit.framework.TestCase;

public class testUsing extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING (price DECIMAL(6,2))\n" +
                "UPDATE tab1\n" +
                "SET column1 = CAST(:price AS euro);";
         //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupdate);
        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(update.getUsingVariableList().getColumn(0).getColumnName().toString().equalsIgnoreCase("price"));
        assertTrue(update.getTargetTable().toString().equalsIgnoreCase("tab1"));

//        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatausing);
//        TTeradataUsing using = (TTeradataUsing)sqlparser.sqlstatements.get(0);
//        assertTrue(using.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("price"));
//        assertTrue(using.getSqlRequest().sqlstatementtype == ESqlStatementType.sstupdate);
//        TUpdateSqlStatement update = (TUpdateSqlStatement)using.getSqlRequest();
//        assertTrue(update.getTargetTable().toString().equalsIgnoreCase("tab1"));

    }

    public void testExec(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING(AA1 int)\n" +
                "EXEC insert1(:AA1);";
        //System.out.println(sqlparser.sqlstatements.size());
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradataexecute);
        TTeradataExecute execute = (TTeradataExecute)sqlparser.sqlstatements.get(0);
        assertTrue(execute.getUsingVariableList().getColumn(0).getColumnName().toString().equalsIgnoreCase("AA1"));
        assertTrue(execute.getMacroName().toString().equalsIgnoreCase("insert1"));

//        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatausing);
//        TTeradataUsing using = (TTeradataUsing)sqlparser.sqlstatements.get(0);
//        assertTrue(using.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("AA1"));
//        assertTrue(using.getSqlRequest().sqlstatementtype == ESqlStatementType.sstteradataexecute);
//        TTeradataExecute execute = (TTeradataExecute)using.getSqlRequest();
//        assertTrue(execute.getMacroName().toString().equalsIgnoreCase("insert1"));

    }

}
