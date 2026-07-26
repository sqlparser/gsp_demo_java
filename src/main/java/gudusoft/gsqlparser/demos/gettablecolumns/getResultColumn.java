package demos.gettablecolumns;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class getResultColumn  {

    public static void main(String args[])
     {
         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
         sqlparser.sqltext  = "select a,b,c,DATETOSTRING(date_attr_name,'mm/dd/yyyy') from tbl_a";
         int ret = sqlparser.parse();
         if (ret == 0){
             TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
             TResultColumnList columns = select.getResultColumnList();
             for(int i = 0; i < columns.size();i++){
                 System.out.println(columns.getResultColumn(i).toString());
             }

         }else{
             System.out.println(sqlparser.getErrormessage());
         }
         
     }

}