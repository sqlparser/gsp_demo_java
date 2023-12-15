package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.util.keywordChecker;
import junit.framework.TestCase;

public class testODBC  extends TestCase {

    public void testKeyword() {
        String keywordStr = keywordChecker.getKeywordList(EDbVendor.dbvodbc, "CUSTOMIZED1", true);
        String[] keywords = keywordStr.split( "," );
        assertTrue(keywords.length == 71);
    }

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvodbc);
        String keywordStr = keywordChecker.getKeywordList(EDbVendor.dbvodbc, "CUSTOMIZED1", true);
        String[] keywords = keywordStr.split( "," );
        for(int i=0;i<keywords.length;i++){
            sqlparser.sqltext = "select * from t1 where c1 = "+keywords[i];
            if (sqlparser.getrawsqlstatements() == 0){
                TSourceToken st = sqlparser.sourcetokenlist.get(sqlparser.sourcetokenlist.size()-1);
                assertTrue(st.tokentype == ETokenType.ttkeyword);


                // GSP Java version 2.8.3.4(2023-11-30)
                //  - [General] change tokentype to ETokenType.ttidentifier if this is a column.
                // 2023/11/30 做了上面调整后， sqlparser.parse() 后， 下面的 assertTrue() 不再成立
//                assertTrue(st.tokentype == ETokenType.ttkeyword);
            }
        }

    }

//    public void test2() {
//        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvodbc);
//            sqlparser.sqltext = "select * from t1 where c1 = ADD";
////        sqlparser.tokenizeSqltext();
////        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
////            TSourceToken st = sqlparser.sourcetokenlist.get(i);
////            System.out.println(st.toString() +":"+ st.tokentype);
////        }
//
////            //assertTrue(sqlparser.parse() == 0);
//            if (sqlparser.parse() == 0){
//                TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//                TExpression e = select.getWhereClause().getCondition();//.getRightOperand();
//                TSourceToken st = e.getEndToken();
//
//                //assertTrue(st.tokentype == ETokenType.ttkeyword);
//                if ((st.tokentype != ETokenType.ttkeyword)){
//                    System.out.println(st.toString() +":"+ st.tokentype);
//                }
//                //TSourceToken st = e.getObjectOperand().getStartToken();
//            }
//    }
}
