package users;
/*
 * Date: 11-11-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import junit.framework.TestCase;

import java.util.ArrayList;

 class SQLParser extends java.lang.Object{
    /**
     * This method gets select clause,where clause and from clauses of the query and adds into dw.
     */

     public String buildMyQuery(String argQuery ,boolean argIsOracle){

       TGSqlParser sqlparser = new TGSqlParser(argIsOracle ? EDbVendor.dbvoracle:EDbVendor.dbvmssql);
       String result = "";
       TCustomSqlStatement stmt   = null;
        ArrayList FromClauseTables = new ArrayList();
        TTable table     = null;
       sqlparser.sqltext      = argQuery;
       int ret = sqlparser.parse();
        if (ret == 0){
            stmt   = (TCustomSqlStatement) sqlparser.sqlstatements.get(0);
            for(int k=0;k<stmt.tables.size();k++){
               table = stmt.tables.getTable(k);
               if(table != null){
                 FromClauseTables.add(new String[]{table.toString(),""});
              }
            }
        }else{
           System.out.println("Error occured in SQLParser.java : SQLParser.GetAllClausesOfQuery(): "+sqlparser.getErrormessage());
        }
        return result;
    }
}

public class testBuindQuery extends TestCase {

    public void test0(){
//        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
//        sqlparser.sqltext      = "SELECT 'Test' FROM dual b";
//        int ret = sqlparser.parse();
//         if (ret == 0){
//             TCustomSqlStatement stmt   = null;
//              TTable table     = null;
//             stmt   = (TCustomSqlStatement) sqlparser.sqlstatements.get(0);
//             for(int k=0;k<stmt.tables.size();k++){
//                table = stmt.tables.getTable(k);
//                 System.out.println(table.toString());
//                 System.out.println(table.getAliasClause().toString());
//             }
//         }else {
//             System.out.println("error:"+sqlparser.getErrormessage());
//         }
    }

     public void test1(){
        SQLParser sqlParser = new SQLParser();
        String sqlStatement = "SELECT 'Test' FROM dual";
        sqlParser.buildMyQuery(sqlStatement ,true);
//         System.out.println("Done 2");
     }

}
