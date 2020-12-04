package interfaceDemo;


import gudusoft.gsqlparser.*;
import junit.framework.TestCase;


class ignorePrompt implements ITokenHandle {
     boolean commentToken = false;
     int nested = 0;
    TSourceToken prevSt = null;

    public boolean processToken(TSourceToken st){

        if (commentToken){
            if (st.tokencode == '(') nested++;
            if (st.tokencode == ')'){
                nested--;
                if (nested == 0){
                    commentToken = false;
                }
            }
            st.tokencode = TBaseType.lexspace;
        }

//        if (st.tokentype == ETokenType.ttsqlvar){
//            if (st.toString().equalsIgnoreCase("@Prompt")){
//                commentToken = true;
//            }
//        }

        if (st.toString().equalsIgnoreCase("prompt")){
            if ((prevSt != null)&&(prevSt.tokencode == '@')){
                prevSt.tokencode = TBaseType.lexspace;
                commentToken = true;
            }
        }

        prevSt = st;
        return true;
    }
}

public class testIgnorePrompt extends TestCase {


    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT Count (Distinct PREMIUM_RECEIPT.MEMBER_NUMBER) \n" +
                "FROM PREMIUM_RECEIPT \n" +
                "WHERE EDW_PREMIUM_RECEIPT.PREM_PAID_DATE BETWEEN @Prompt('Enter Start Date:','D',,Mono,Free,,'01/01/2000') \n" +
                "AND @Prompt('Enter End Date:','D',,Mono,Free,,'01/01/2003'); ";
        sqlparser.setTokenHandle(new ignorePrompt());

        assertTrue(sqlparser.parse() == 0);
    }

}