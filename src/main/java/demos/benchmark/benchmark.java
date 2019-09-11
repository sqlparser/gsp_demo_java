package demos.benchmark;
/*
 * Date: 12-6-29
 */

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;

class parseJob implements Runnable{

    String name;
    Thread t;
    parseJob(String threadname){
        name = threadname;
        t = new Thread(this,name);
        t.start();
    }

    public void run(){

        double  t = System.currentTimeMillis();
        int sqls = 10000;

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        for (int i=0;i<sqls;i++){
            sqlparser.sqltext = "select last_name,job_id\n" +
                    "                      from employee\n" +
                    "                    order by hire_date";
             sqlparser.parse();
        }

        System.out.printf("Parse %d sqls in thread %s: %.3f seconds\n",sqls,name,(System.currentTimeMillis() - t)/1000 );

    }
}

public class benchmark  {

    public static void main(String args[])
   {
       double  t = System.currentTimeMillis();
       int sqls = 50000;

       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

       System.out.printf("Main thread: load sql parser %.3f second\n",(System.currentTimeMillis() - t)/1000 );

       System.out.printf("Parsing %d sqls,...\n",sqls);

       for (int i=0;i<sqls;i++){
           sqlparser.sqltext = "select last_name,job_id\n" +
                   "                      from employee\n" +
                   "                    order by hire_date";
            sqlparser.parse();
       }

       System.out.printf("Main thread: In %.3f seconds, %.3f sql/second.\n",(System.currentTimeMillis() - t)/1000,sqls/((System.currentTimeMillis() - t)/1000));

       t = System.currentTimeMillis();
       System.out.printf("\nMulti thread mode:\n");

       parseJob j1 = new parseJob("1");
       parseJob j2 = new parseJob("2");
       parseJob j3 = new parseJob("3");
       parseJob j4 = new parseJob("4");
       parseJob j5 = new parseJob("5");

       try{
       j1.t.join();
       j2.t.join();
       j3.t.join();
       j4.t.join();
       j5.t.join();
       }catch (InterruptedException e){

       }

       System.out.printf("Main thread: parsing %d sql in %.3f seconds, %.3f sql/second.\n",
               sqls,
               (System.currentTimeMillis() - t)/1000,
               sqls/((System.currentTimeMillis() - t)/1000));
   }

}