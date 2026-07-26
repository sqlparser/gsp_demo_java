package demos.modifysql;
/*
 * Date: 2010-12-10
 * Time: 11:18:02
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class add2SQL {

    public static void main(String args[])
     {
        TGSqlParser sqlparser1 = new TGSqlParser(EDbVendor.dbvmssql);
        TGSqlParser sqlparser2 = new TGSqlParser(EDbVendor.dbvmssql);

        String sqlstr1 = "SELECT product_id, product_name, product_type FROM products WHERE product_type = 'widget'";
        String sqlstr2 = "SELECT product_id, product_name, product_price FROM products WHERE product_price < 100";

       sqlparser1.sqltext = sqlstr1;
       int ret = sqlparser1.parse();
         if (ret != 0) {
             System.out.println(sqlparser1.getErrormessage());
             return;
         }

         sqlparser2.sqltext = sqlstr2;
         ret = sqlparser2.parse();
           if (ret != 0) {
               System.out.println(sqlparser2.getErrormessage());
               return;
           }

         TSelectSqlStatement sql1 = (TSelectSqlStatement)sqlparser1.sqlstatements.get(0);
         TSelectSqlStatement sql2 = (TSelectSqlStatement)sqlparser2.sqlstatements.get(0);

         //get column name
         StringBuffer flds = new StringBuffer();
         for(int i=0;i<sql1.getResultColumnList().size();i++){
             if (i>0) { flds.append(","); }
             flds.append(sql1.getResultColumnList().getResultColumn(i).toString());
            }

         //get table name
         StringBuffer tbls = new StringBuffer();
         for(int i=0;i<sql1.tables.size();i++){
             if (i>0) { tbls.append(","); }
             tbls.append(sql1.tables.getTable(i).toString());
         }

         String wherestr = "";
         if (sql1.getWhereClause() != null){
             wherestr = sql1.getWhereClause().getCondition().toString();
         }

         //get column, table and where clause from the second sql statement

         Boolean found;
         for(int i=0;i<sql2.getResultColumnList().size();i++){
             found = false;
             for (int j=0; j<sql1.getResultColumnList().size();j++){
                if (sql2.getResultColumnList().getResultColumn(i).toString().equalsIgnoreCase(sql1.getResultColumnList().getResultColumn(j).toString())){
                    found = true;
                    break;
                }
             }
             if (!found){
             flds.append(","+sql2.getResultColumnList().getResultColumn(i).toString());
            }
         }

         for(int i=0;i<sql2.tables.size();i++){
             found = false;
             for(int j=0;j<sql1.tables.size();j++){
                 if ( sql2.tables.getTable(i).toString().equalsIgnoreCase(sql1.tables.getTable(j).toString()) ){
                     found = true;
                     break;
                 }
             }
             if (!found){
                tbls.append(","+sql2.tables.getTable(i).toString());
             }
         }

         if (sql2.getWhereClause() != null){
             wherestr = wherestr+" and "+ sql2.getWhereClause().getCondition().toString();
         }

         System.out.println("select "+flds.toString()+" from "+ tbls.toString()+" where "+wherestr);
         }
         
 }