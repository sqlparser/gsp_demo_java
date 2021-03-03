package greenplum;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;

public class testDeclareBlock extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.setSqlfilename(common.gspCommon.BASE_SQL_DIR+"java/greenplum/timewalking/declare_block.sql");
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        assertTrue(block.getBodyStatements().size()==127);
        assertTrue(block.getDeclareStatements().size() == 37);
//        int i = 0;
//        for(TCustomSqlStatement sql:block.getBodyStatements()){
//            i++;
//            System.out.println(i+":\t"+sql.sqlstatementtype);
//            if ((sql.getStartToken()==null)||(sql.getEndToken()==null)){
//                System.out.println("\t"+i);
//            }
//        }
    }

}
