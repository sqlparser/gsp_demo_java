package demos.sqlrefactor;
/*
 * Date: 13-9-30
 */

import gudusoft.gsqlparser.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class rmdupParenthesis   {

    private static int removeDuplicatedParenthesis(TSourceTokenList sourceTokenList){
        int cnt = 0;
        TSourceToken st = null, prevEndToken = null;
        boolean inParenthesis = false;
        for(int i=0;i<sourceTokenList.size();i++){
            st = sourceTokenList.get(i);
            if (st.isnonsolidtoken()) continue;
            if ((st.tokencode == '(')&&(st.getLinkToken() != null)){
               if (inParenthesis){
                  if (st.getLinkToken() == prevEndToken.prevSolidToken()){
                      //this is duplicated token, remove this token
                      st.setString("");
                      st.getLinkToken().setString("");
                      cnt++;
                  }
                  prevEndToken = st.getLinkToken();
               }else {
                   inParenthesis = true;
                   prevEndToken = st.getLinkToken();
               }
            }else {
                inParenthesis = false;
                prevEndToken = null;
            }
        }
        return cnt;
    }


    public static void main(String args[])
    {
        long t;
        t = System.currentTimeMillis();

        List<String> argList = Arrays.asList(args);
        if (args.length < 1){
            System.out.println("Usage: java rmdupParenthesis <sqlfile.sql> [/t <database type>]");
            System.out.println("  /t <type>  - Specify database type (default: oracle)");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvoracle;
        int index = argList.indexOf("/t");
        if (index != -1 && args.length > index + 1){
            dbVendor = TGSqlParser.getDBVendorByName(args[index + 1]);
        }

        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);

        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            removeDuplicatedParenthesis(sqlparser.sourcetokenlist);
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                    System.out.println(sqlparser.sqlstatements.get(i).toString());
                }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}