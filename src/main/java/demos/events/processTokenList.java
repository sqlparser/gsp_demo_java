package demos.events;

import gudusoft.gsqlparser.*;

public class processTokenList {
    public static void main(String args[]) {
        long t = System.currentTimeMillis();
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "delete from dwrdata.dwc_cm_dps_cust_asset_gain_loss_cny_mid_add where data_mth=${tx_date_yyyymm};";
        sqlparser.setTokenListHandle(new myTokenListHandle());
        int ret = sqlparser.parse();
        if (ret != 0){
            System.out.println(sqlparser.getErrormessage());
            return;
        }else {
            System.out.println(sqlparser.getSqlstatements().get(0).toString());
        }


        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t)  );
    }
}

class myTokenListHandle implements ITokenListHandle {
    // 把 ${tx_date_yyyymm} 合并为一个token，token code为 TBasetype.ident
    public boolean processTokenList(TSourceTokenList sourceTokenList){
        int startIndex = -1;
        int endIndex = -1;

        for(int i=0;i< sourceTokenList.size();i++) {
            TSourceToken token = sourceTokenList.get(i);

            // Check for '$' followed immediately by '{'
            if (token.tokencode == 36) { // Check for '$'
                if (i + 1 < sourceTokenList.size() && sourceTokenList.get(i + 1).tokencode == 123) { // Check for '{' immediately after '$'
                    startIndex = i;
                }
            } else if (token.tokencode == 125 && startIndex != -1) { // Check for '}'
                endIndex = i;

            }


            if (startIndex != -1 && endIndex != -1) {
                TSourceToken firstToken = sourceTokenList.get(startIndex);
                firstToken.tokencode = TBaseType.ident;
                for (int j = startIndex + 1; j <= endIndex; j++) {
                    TSourceToken st = sourceTokenList.get(j);
                    st.tokenstatus = ETokenStatus.tsdeleted;
                    firstToken.setString(firstToken.astext + st.astext);
                }

                //System.out.println("Found variable token: " + firstToken.toStringDebug());

                startIndex = -1;
                endIndex = -1;
            }
        }
        return true;
    }
}
