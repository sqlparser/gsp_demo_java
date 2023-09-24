package testSQLFiles;

import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TLog;
import junit.framework.TestCase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.File;
import common.SqlFileList;

public class parseTest extends TestCase {
    String xsdfile = "file:/C:/prg/gsp_java/library/doc/xml/sqlquery.xsd";
   // public static String gspCommon.BASE_SQL_DIR = "c:/prg/gsp_sqlfiles/TestCases/";

static void parsefiles(EDbVendor db,String dir){

    File parent = new File( dir );
    if (!( parent.exists( ) && parent.isDirectory( ))){
        System.out.println("Skip this testcase, directory not exists:"+dir);
        return;
    }

    boolean dumpResolverLog = TBaseType.DUMP_RESOLVER_LOG_TO_CONSOLE;
    int reportLevel = TLog.REPORT_LEVEL;

//    TLog.REPORT_LEVEL = TLog.ERROR;
//    TBaseType.DUMP_RESOLVER_LOG_TO_CONSOLE = false;

    TGSqlParser sqlparser = new TGSqlParser(db);
    //sqlparser.setSqlCharset("UTF-8");
    SqlFileList sqlfiles = new SqlFileList(dir,true);
    for(int k=0;k < sqlfiles.sqlfiles.size();k++){
        sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
        //System.out.printf("%s\n",sqlparser.sqlfilename);
        // boolean b = sqlparser.parse() == 0;

        // assertTrue(sqlparser.parse() == 0);

        try{
            boolean b = sqlparser.parse() == 0;
            assertTrue(sqlparser.sqlfilename+"\n"+sqlparser.getErrormessage(),b);
//            if (TBaseType.logSize() > 0){
//                TBaseType.log("Above logs is from sql file: "+sqlparser.sqlfilename,TLog.ERROR);
//                TBaseType.log("\n\n",TLog.ERROR);
//                TBaseType.dumpLogsToFile("c:\\prg\\tmp\\column_reslover_debug.log");
//                TBaseType.clearLogs();
//            }

        }catch (Exception e){
            System.out.println("parsefiles error:"+e.getMessage()+" "+ sqlparser.sqlfilename);
            e.printStackTrace();
        }

   }

//    TBaseType.DUMP_RESOLVER_LOG_TO_CONSOLE = dumpResolverLog;
//    TLog.REPORT_LEVEL = reportLevel;
}


    public  void testDax(){
        parsefiles(EDbVendor.dbvdax, common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"dax");
    }

//    public  void testSnowflake(){
//        parsefiles(EDbVendor.dbvsnowflake,common.gspCommon.BASE_SQL_DIR+"snowflake");
//    }



}
