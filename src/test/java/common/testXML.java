package common;


import demos.visitors.xmlVisitor;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testXML extends TestCase  {
    String xsdfile = "file:/C:/prg/gsp_java/library/doc/xml/sqlquery.xsd";

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
        toXmlFiles(EDbVendor.dbvmssql, "c:/prg/gsqlparser/Test/TestCases/mssql");
        toXmlFiles(EDbVendor.dbvmssql, "c:/prg/gsqlparser/Test/TestCases/java/mssql");
    }

    public void testOracle() throws Exception {
        toXmlFiles(EDbVendor.dbvoracle, "c:/prg/gsqlparser/Test/TestCases/oracle");
        toXmlFiles(EDbVendor.dbvoracle, "c:/prg/gsqlparser/Test/TestCases/java/oracle");
    }
}
