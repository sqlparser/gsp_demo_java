package testSQLFiles;

import junit.framework.TestCase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.File;
import common.SqlFileList;

public class parseTest extends TestCase {
    String xsdfile = "file:/C:/prg/gsp_java/library/doc/xml/sqlquery.xsd";
   // public static String gspCommon.BASE_SQL_DIR = "c:/prg/gsp_sqlfiles/TestCases/";

static void parsefiles(EDbVendor db,String dir)  {

    File parent = new File( dir );
    if (!( parent.exists( ) && parent.isDirectory( ))){
        System.out.println("Skip this testcase, directory not exists:"+dir);
        return;
    }

    TGSqlParser sqlparser = new TGSqlParser(db);
    SqlFileList sqlfiles = new SqlFileList(dir,true);
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
            e.printStackTrace();
        }
    }

}


    public  void testDax(){
        parsefiles(EDbVendor.dbvdax, common.gspCommon.BASE_SQL_DIR+"dax");
    }

//    public  void testSnowflake(){
//        parsefiles(EDbVendor.dbvsnowflake,common.gspCommon.BASE_SQL_DIR+"snowflake");
//    }



}
