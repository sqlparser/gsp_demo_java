package formatsql;
/*
 * Date: 2010-11-9
 * Time: 9:57:07
 */

import gudusoft.gsqlparser.pp.para.GOutputFmt;
import junit.framework.TestCase;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import common.SqlFileList;

public class formatsqlTest extends TestCase {

static int formatFiles(EDbVendor db,String dir)  {

    TGSqlParser sqlparser = new TGSqlParser(db);
    SqlFileList sqlfiles = new SqlFileList(dir,true);
    int i = 0,j = 0;
    for(int k=0;k < sqlfiles.sqlfiles.size();k++){
        sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
        //System.out.println(sqlparser.sqlfilename);
        boolean b = sqlparser.parse() == 0;
        assertTrue(sqlparser.sqlfilename+"\n"+sqlparser.getErrormessage(),b);
        if (b){
            GFmtOpt option = GFmtOptFactory.newInstance();
            //String result = FormattorFactory.pp(sqlparser, option);
            //option.outputFmt =  GOutputFmt.ofhtml;
            try{
            String result = FormatterFactory.pp(sqlparser, option);
            }catch(Exception e){
                System.out.println(sqlparser.sqlfilename+" formatFiles error: "+e.getMessage());
                j++;
            }

        }
    }

    if (j > 0){
        System.out.println(db.toString()+ ", total files formatted;"+sqlfiles.sqlfiles.size()+", exception:"+j);
    }
    return j;
}

    public static void testSQLServer(){
         assertTrue(formatFiles(EDbVendor.dbvmssql,"c:/prg/gsqlparser/Test/TestCases/mssql") == 0);
    }

    
    public static void testTeradata(){
        assertTrue(formatFiles(EDbVendor.dbvteradata,"c:/prg/gsqlparser/Test/TestCases/teradata/verified") == 0);
    }

    public static void testPostGreSQL(){
        assertTrue(formatFiles(EDbVendor.dbvpostgresql,"c:/prg/gsqlparser/Test/TestCases/postgresql/verified") == 0);
    }

    public static void testMySQL(){
        assertTrue(formatFiles(EDbVendor.dbvmysql,"c:/prg/gsqlparser/Test/TestCases/mysql") == 0);
    }

    public static void testDB2(){
        assertTrue(formatFiles(EDbVendor.dbvdb2,"c:/prg/gsqlparser/Test/TestCases/db2") == 0);
    }

    public static void testOracle(){
           assertTrue(formatFiles(EDbVendor.dbvoracle,"c:/prg/gsqlparser/Test/TestCases/oracle") == 0);
    }

    public static void testNetezza(){
           assertTrue(formatFiles(EDbVendor.dbvnetezza,"c:/prg/gsqlparser/Test/TestCases/netezza") == 0);
    }

}
