package demos.benchmark;
/*
 * Date: 12-6-29
 */

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.File;

class parseJob implements Runnable{

    String name;
    Thread t;
    EDbVendor dbVendor;
    int filesize;
    String filename;
    int repeatTimes;

    parseJob(String threadname,EDbVendor dbVendor, int filesize, String filename, int repeatTimes){
        name = threadname;
        this.dbVendor = dbVendor;
        this.filesize = filesize;
        this.filename = filename;
        this.repeatTimes = repeatTimes;
        t = new Thread(this,name);
        t.start();
    }

    public void run(){

        double  t = System.currentTimeMillis();
        TGSqlParser sqlparser = new TGSqlParser(this.dbVendor);

        for (int i=0;i<repeatTimes;i++){
            sqlparser.sqlfilename = filename;
             sqlparser.parse();
        }

        System.out.printf("Thread %s, parse query of size %dk, takes %.3f seconds, repeated in %d times, takes %.3f seconds\n",
                name, filesize, (System.currentTimeMillis() - t)/1000/repeatTimes, repeatTimes, (System.currentTimeMillis() - t)/1000 );

    }
}

public class benchmark  {
    static String testfile1K = "c:\\prg\\gsp_sqlfiles\\TestCases\\private\\benchmark\\oracle\\1k.sql";
    static String testfile10K = "c:\\prg\\gsp_sqlfiles\\TestCases\\private\\benchmark\\oracle\\10k.sql";
    static String testfile100K = "c:\\prg\\gsp_sqlfiles\\TestCases\\private\\benchmark\\oracle\\100k.sql";

    static void doParse(EDbVendor dbVendor, int filesize, String filename, int repeatTimes){
        double  t = System.currentTimeMillis();

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);

        System.out.printf("Main thread: load sql parser %.3f seconds\n",(System.currentTimeMillis() - t)/1000 );

        System.out.printf("Parsing query of size %dk, repeated %d times,...\n",filesize, repeatTimes);

        for (int i=0;i<repeatTimes;i++){
            sqlparser.sqlfilename = filename;
            sqlparser.parse();
        }

        System.out.printf("Main thread: Takes %.3f seconds to parse query of %dk, Total times: %.3f seconds for query of %dk, repeated %d times,.\n"
                ,((System.currentTimeMillis() - t)/1000)/repeatTimes, filesize
                ,(System.currentTimeMillis() - t)/1000, filesize, repeatTimes

        );

        t = System.currentTimeMillis();
        System.out.printf("\nMulti thread mode:\n");

        parseJob j1 = new parseJob("1",dbVendor,filesize,filename,repeatTimes);
        parseJob j2 = new parseJob("2",dbVendor,filesize,filename,repeatTimes);
        parseJob j3 = new parseJob("3",dbVendor,filesize,filename,repeatTimes);
        parseJob j4 = new parseJob("4",dbVendor,filesize,filename,repeatTimes);
        parseJob j5 = new parseJob("5",dbVendor,filesize,filename,repeatTimes);

        try{
            j1.t.join();
            j2.t.join();
            j3.t.join();
            j4.t.join();
            j5.t.join();
        }catch (InterruptedException e){

        }

//        System.out.printf("Main thread: parsing %d sql in %.3f seconds, %.3f sql/second, parse 100K sql in %.3f.\n",
//                iterates,
//                (System.currentTimeMillis() - t)/1000,
//                iterates/((System.currentTimeMillis() - t)/1000),
//                (System.currentTimeMillis() - t)/iterates
//        );

    }

    public static void main(String args[])
   {
       File file1k = new File(testfile1K);
       System.out.println("\n=======================Benchmark 1k size file=============================================");
       doParse(EDbVendor.dbvoracle,(int)file1k.length()/1024,testfile1K,100);

       File file10k = new File(testfile10K);
       System.out.println("\n=======================Benchmark 10k size file=============================================");
       doParse(EDbVendor.dbvoracle,(int)file10k.length()/1024,testfile10K,100);

       File file100k = new File(testfile100K);
       System.out.println("\n=======================Benchmark 100k size file=============================================");
       doParse(EDbVendor.dbvoracle,(int)file100k.length()/1024,testfile100K,100);

   }

}