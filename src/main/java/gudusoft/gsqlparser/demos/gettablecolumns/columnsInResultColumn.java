package demos.gettablecolumns;

/*
 * Date: 11-4-19
 * ref : http://www.dpriver.com/blog/list-of-demos-illustrate-how-to-use-general-sql-parser/get-referenced-table-column-in-a-select-list-item/
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import demos.columnInClause;

public class columnsInResultColumn {

    public static void main(String args[])
     {
         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

//         sqlparser.sqltext  = "select sal.income + sal.bonus * emp.age + 5 as real_sal,\n" +
//                 "       emp.name as title \n" +
//                 "from employee emp, salary sal \n" +
//                 "where emp.id=sal.eid";

//         sqlparser.sqltext = "SELECT CASE \n" +
//                 "         WHEN \"employees\".\"firstname1\" = ''THEN \"employees\".\"lastname1\" \n" +
//                 "         ELSE \"employees\".\"firstname2\" + ' ' \n" +
//                 "              + \"employees\".\"lastname2\" \n" +
//                 "       END AS \"Full Name\" \n" +
//                 "FROM   \"NORTHWIND\".\"DBO\".\"employees\" \"Employees\" ";

//         sqlparser.sqltext  = "SELECT e.ename as employeename,\n" +
//                 "       m.ename,\n" +
//                 "       d.name,\n" +
//                 "       e.first_name||d.dname,\n" +
//                 "       (select max(sal) from emp) as max_sal\n" +
//                 "FROM   employees e\n" +
//                 "       LEFT OUTER JOIN employees m ON ( e.mgr_id = m.id )\n" +
//                 "       LEFT OUTER JOIN department d ON ( e.department_id = d.department_id )";

//         sqlparser.sqltext  = "SELECT a.customerid, \n" +
//                 "       (SELECT CASE \n" +
//                 "                 WHEN b.city = 'Berlin' THEN b.country + ' ' + b.city \n" +
//                 "                 ELSE b.postalcode \n" +
//                 "               END \n" +
//                 "        FROM   customers b \n" +
//                 "        WHERE  a.customerid = b.customerid) AS Location \n" +
//                 "FROM   customers a ";
         sqlparser.sqltext = "SELECT   C.FirstName_NAME as FName,\n" +
                 "         C.JOB_ID ,\n" +
                 "         C.SALARY,\n" +
                 "C.FirstName + ' ' +D.LastName as Fullname\n" +
                 "\n" +
                 "from Emp C join EmpDetail D\n" +
                 "on C.ID = D.ID";
         int ret = sqlparser.parse();
         if (ret == 0){
             for(int i=0;i<sqlparser.sqlstatements.size();i++){
                 iterateStmt(sqlparser.sqlstatements.get(i));
             }

         }else{
             System.out.println(sqlparser.getErrormessage());
         }

     }

    static void iterateStmt(TCustomSqlStatement pStmt){

        if (pStmt instanceof TSelectSqlStatement){
            processSelect((TSelectSqlStatement)pStmt);
        }
        for (int i=0;i<pStmt.getStatements().size();i++){
            iterateStmt(pStmt.getStatements().get(i));
        }
    }

    static void processSelect(TSelectSqlStatement select){

        TResultColumnList columns = select.getResultColumnList();

        for(int i = 0; i < columns.size();i++){
            printColumns(columns.getResultColumn(i),select);
        }

    }

     static void printColumns(TResultColumn cl,TCustomSqlStatement sqlStatement){

         if(cl.getAliasClause() != null){
             System.out.println("\nResult column:" + cl.getAliasClause().toString());
         }else{
            System.out.println("\nResult column:" + cl.getExpr().toString());
         }

         new columnInClause().printColumns(cl.getExpr(),sqlStatement);
     }
}

