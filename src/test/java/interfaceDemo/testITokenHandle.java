package interfaceDemo;

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

class myTokenHandle implements ITokenHandle{
    public boolean processToken(TSourceToken st){

        if (st.toString().equalsIgnoreCase("limit")){
            st.tokencode = TBaseType.cmtslashstar;//treat this token as a comment
        }
           return true;
    }
}

public class testITokenHandle extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select * from dual limit";
        sqlparser.setTokenHandle(new myTokenHandle());

        assertTrue(sqlparser.parse() == 0);
    }

}
