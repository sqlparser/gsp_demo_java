package gudusoft.gsqlparser.commonTest;


import demos.visitors.xmlVisitor;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testXML extends TestCase  {
    String xsdfile = "file:/"+gspCommon.BASE_SQL_DIR_PUBLIC+ "xml/sqlschema.xsd";

     void toXmlFiles(EDbVendor db, String dir) throws Exception {
        TGSqlParser sqlparser = new TGSqlParser(db);
        SqlFileList sqlfiles = new SqlFileList(dir,true);
        for(int k=0;k < sqlfiles.sqlfiles.size();k++){
            sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
            //System.out.println(sqlparser.sqlfilename);
            try{
                boolean b = sqlparser.parse() == 0;
                assertTrue(sqlparser.sqlfilename+"\n"+sqlparser.getErrormessage(),b);
                if (b){
                    xmlVisitor xv2 = new xmlVisitor(xsdfile);
                    xv2.run(sqlparser);
                    //xv2.validXml();
                }

            }catch (Exception e){
                System.out.println("testXML error:"+e.getMessage()+" "+ sqlparser.sqlfilename);
                throw e;
            }
        }
    }

    public void testSQLServer() throws Exception {
        toXmlFiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS+"mssql");
        toXmlFiles(EDbVendor.dbvmssql,  gspCommon.BASE_SQL_DIR_PUBLIC_JAVA+"mssql");
    }

    public void testOracle() throws Exception {
        toXmlFiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS+"oracle");
        toXmlFiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA+"oracle");
    }
}
