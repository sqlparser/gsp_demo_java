package test;

import junit.framework.TestCase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.File;


public class parseTest extends TestCase {
    String xsdfile = "file:/C:/prg/gsp_java/library/doc/xml/sqlquery.xsd";
   // public static String test.gspCommon.BASE_SQL_DIR = "c:/prg/gsp_sqlfiles/TestCases/";

void parsefiles(EDbVendor db,String dir)  {

    File parent = new File( dir );
    if (!( parent.exists( ) && parent.isDirectory( ))){
        System.out.println("Skip this testcase, directory not exists:"+dir);
        return;
    }

    TGSqlParser sqlparser = new TGSqlParser(db);
    test.SqlFileList sqlfiles = new test.SqlFileList(dir,true);
    for(int k=0;k < sqlfiles.sqlfiles.size();k++){
        sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
        // System.out.printf("%s\n",sqlparser.sqlfilename);
        // boolean b = sqlparser.parse() == 0;

        try{
            boolean b = sqlparser.parse() == 0;
            assertTrue(sqlparser.sqlfilename+"\n"+sqlparser.getErrormessage(),b);

            if (b){
//                xmlVisitor xv2 = new xmlVisitor(xsdfile);
//                xv2.run(sqlparser);

                //xv2.validXml();
            }
        }catch (Exception e){
            System.out.println("parsefiles error:"+e.getMessage()+" "+ sqlparser.sqlfilename);
        }
    }

}


    public  void testOracle(){
        parsefiles(EDbVendor.dbvoracle,test.gspCommon.BASE_SQL_DIR+"oracle");
        parsefiles(EDbVendor.dbvoracle,test.gspCommon.BASE_SQL_DIR+"java/oracle/");
    }

    public  void testSQLServer(){
        parsefiles(EDbVendor.dbvmssql,test.gspCommon.BASE_SQL_DIR+"mssql");
        parsefiles(EDbVendor.dbvmssql,test.gspCommon.BASE_SQL_DIR+"java/mssql");
    }

    public  void testSybase(){
        parsefiles(EDbVendor.dbvsybase,test.gspCommon.BASE_SQL_DIR+"sybase");
        parsefiles(EDbVendor.dbvsybase,test.gspCommon.BASE_SQL_DIR+"java/sybase");
    }

    public  void testTeradata(){
        parsefiles(EDbVendor.dbvteradata,test.gspCommon.BASE_SQL_DIR+"teradata/verified");
        parsefiles(EDbVendor.dbvteradata,test.gspCommon.BASE_SQL_DIR+"java/teradata");
    }


    public  void testDB2(){
        parsefiles(EDbVendor.dbvdb2,test.gspCommon.BASE_SQL_DIR+"db2");
        parsefiles(EDbVendor.dbvdb2,test.gspCommon.BASE_SQL_DIR+"java/db2/");
    }

    public  void testMySQL(){
        parsefiles(EDbVendor.dbvmysql,test.gspCommon.BASE_SQL_DIR+"mysql");
        parsefiles(EDbVendor.dbvmysql,test.gspCommon.BASE_SQL_DIR+"java/mysql");

//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\testing");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\training");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\hotelrs-x");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\taskfreak-b");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\wordpress-s");
//          parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\theorganizer-s");
    }

    public  void testMdx(){
        parsefiles(EDbVendor.dbvmdx,test.gspCommon.BASE_SQL_DIR+"mdx");
    }

    public  void testNetezza(){
        parsefiles(EDbVendor.dbvnetezza,test.gspCommon.BASE_SQL_DIR+"netezza");
        parsefiles(EDbVendor.dbvnetezza,test.gspCommon.BASE_SQL_DIR+"java/netezza");
    }

    public  void testInformix(){
        parsefiles(EDbVendor.dbvinformix,test.gspCommon.BASE_SQL_DIR+"informix");
    }

    public  void testPostgresql(){
        parsefiles(EDbVendor.dbvpostgresql,test.gspCommon.BASE_SQL_DIR+"postgresql/verified");
        parsefiles(EDbVendor.dbvpostgresql,test.gspCommon.BASE_SQL_DIR+"java/postgresql");
    }

    public  void testGreenplum(){
        parsefiles(EDbVendor.dbvgreenplum,test.gspCommon.BASE_SQL_DIR+"greenplum");
    }

    public  void testRedshift(){
        parsefiles(EDbVendor.dbvredshift,test.gspCommon.BASE_SQL_DIR+"java/redshift");
    }

    public  void testHive(){
        parsefiles(EDbVendor.dbvhive,test.gspCommon.BASE_SQL_DIR+"hive");
    }

    public  void testImpala(){
        parsefiles(EDbVendor.dbvimpala,test.gspCommon.BASE_SQL_DIR+"impala");
        parsefiles(EDbVendor.dbvimpala,test.gspCommon.BASE_SQL_DIR+"java/impala");
    }

    public  void testHana(){
        parsefiles(EDbVendor.dbvhana,test.gspCommon.BASE_SQL_DIR+"hana");
    }
    public  void testDax(){
        parsefiles(EDbVendor.dbvdax,test.gspCommon.BASE_SQL_DIR+"dax");
    }

    public  void testODBC(){
        parsefiles(EDbVendor.dbvodbc,test.gspCommon.BASE_SQL_DIR+"odbc");
    }

    public  void testVertica(){
        parsefiles(EDbVendor.dbvvertica,test.gspCommon.BASE_SQL_DIR+"vertica");
    }

    public  void testOpenedge(){
        parsefiles(EDbVendor.dbvopenedge,test.gspCommon.BASE_SQL_DIR+"openedge");
        parsefiles(EDbVendor.dbvopenedge,test.gspCommon.BASE_SQL_DIR+"java/openedge");
    }

    public  void testCouchbase(){
        parsefiles(EDbVendor.dbvcouchbase,test.gspCommon.BASE_SQL_DIR+"couchbase");
    }

    public  void testSnowflake(){
        parsefiles(EDbVendor.dbvsnowflake,test.gspCommon.BASE_SQL_DIR+"snowflake");
    }

    public  void testBigquery(){
        parsefiles(EDbVendor.dbvbigquery,test.gspCommon.BASE_SQL_DIR+"bigquery");
    }

}
