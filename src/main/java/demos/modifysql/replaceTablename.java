package demos.modifysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

/**
 * This demo shows how to replace a specified table name with new one.
 * <p>Steps to modify a table name:
 * <p>1. find the table name that need to be replaced.
 * <p>2. use setString method to set a new string representation of that table.
 *
 * <p>In this demo, input sql is:
 *
 * <p>select table1.col1, table2.col2
 * <p>from table1, table2
 * <p>where table1.foo > table2.foo
 *
 * <p>we want to replace table2 with "(tableX join tableY using (id)) as table3"
 * <p>and change table2.col2 to table3.col2, table2.foo to table3.foo accordingly.
 *  
 */
public class replaceTablename{
    public static void main(String args[])
     {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

         sqlparser.sqltext = "select table1.col1, table2.col2\n" +
                 "from table1, table2\n" +
                 "where table1.foo > table2.foo";

        System.out.println("input sql:");
        System.out.println(sqlparser.sqltext);

        int ret = sqlparser.parse();
        if (ret == 0){

            TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

           TTable t ;
           for(int i=0;i<select.tables.size();i++){
             t = select.tables.getTable(i);
             if (t.toString().compareToIgnoreCase("table2") == 0){
                 for(int j=0;j<t.getLinkedColumns().size();j++){
                    if(t.getLinkedColumns().getObjectName(j).getObjectToken().toString().equalsIgnoreCase("table2")){
                        t.getLinkedColumns().getObjectName(j).getObjectToken().astext = "table3";
                    }
                 }
               t.setString("(tableX join tableY using (id)) as table3");
             }
           }

            System.out.println("\noutput sql:");
            System.out.println(select.toString());

        }else{
            System.out.println(sqlparser.getErrormessage());
        }
     }

}
