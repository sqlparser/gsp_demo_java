package gudusoft.gsqlparser.postgresqlTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testCreateFunction2 extends TestCase {

    public void testStmtToString(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PRIVATE +"java/postgresql/create_function_big_proc.sql";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("intf_crm.p_crm_cust_lbl_smy"));
        assertTrue(createFunction.getDeclareStatements().size() == 36);
        assertTrue(createFunction.getBodyStatements().size() == 119);
//        int i=0,j=0;
//        for(TCustomSqlStatement sql:createFunction.getBodyStatements()){
//            i++;
//            if (sql.toString() == null){
//                j++;
//              System.out.println(j+":"+i+sql.sqlstatementtype+"\t"+sql.getStartToken().lineNo);
//            }
//            //System.out.println(sql.toString());
//        }
    }
}
