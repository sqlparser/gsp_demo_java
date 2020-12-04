package gettablecolumn;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import junit.framework.TestCase;

public class testDBObjectOfSourceToken extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "Select unPop.policy.policynum\n" +
                "From unPop.policy, unEmp.deal where unpop.policy.Covn=unemp.deal.Covn";
        assertTrue(sqlparser.parse() == 0);

        TSourceToken st ;
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            st = sqlparser.sourcetokenlist.get(i);
            if((st.tokentype == ETokenType.ttidentifier)
                    ){
                if (st.toString().equalsIgnoreCase("unPop")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjSchemaName);
                }else if (st.toString().equalsIgnoreCase("unEmp")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjSchemaName);
                }else if (st.toString().equalsIgnoreCase("policy")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjTable);
                }else if (st.toString().equalsIgnoreCase("deal")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjTable);
                }else if (st.toString().equalsIgnoreCase("policynum")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjColumn);
                }else if (st.toString().equalsIgnoreCase("Covn")){
                    assertTrue(st.getDbObjType() == TObjectName.ttobjColumn);
                }
           }
        }
    }

}
