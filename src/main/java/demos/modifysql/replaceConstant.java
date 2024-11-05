package demos.modifysql;
/*
 * Date: 13-4-18
 */

import gudusoft.gsqlparser.*;

public class replaceConstant {

    public static void main(String args[])
     {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

         sqlparser.sqltext = "INSERT INTO employee(employeename,lastname) VALUES ('arun','deep')";
        System.out.println("input sql:");
        System.out.println(sqlparser.sqltext);

        int ret = sqlparser.parse();
        if (ret == 0){


            for (int i=0;i<sqlparser.sourcetokenlist.size();i++){
                TSourceToken st = sqlparser.sourcetokenlist.get(i);
                if ((st.tokencode == TBaseType.sconst)
                    ||(st.tokencode == TBaseType.iconst)
                     ||(st.tokencode == TBaseType.fconst)
                        ){
                    st.astext = "?";
                }
            }
            System.out.println("\noutput sql:");
            System.out.println(sqlparser.sqlstatements.get(0).toString());
        }else{
            System.out.println(sqlparser.getErrormessage());
        }
     }


}