package demos.getstatement;


import gudusoft.gsqlparser.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static gudusoft.gsqlparser.util.SQLUtil.listFiles;

public class getstatement {
   public static void main(String args[])
    {
    long t = System.currentTimeMillis();
        String sqlFile= "";

        List<String> argList = Arrays.asList(args);
        if ( argList.indexOf( "/f" ) != -1
                && argList.size( ) > argList.indexOf( "/f" ) + 1 )
        {
            sqlFile = args[argList.indexOf( "/f" ) + 1] ;
        }

    EDbVendor dbVendor = EDbVendor.dbvoracle;


    int index = argList.indexOf( "/t" );

    if ( index != -1 && args.length > index + 1 )
    {
        dbVendor = TGSqlParser.getDBVendorByName(args[index + 1]);
    }


    System.out.println("Selected SQL dialect: "+dbVendor.toString());

    TGSqlParser sqlparser = new TGSqlParser(dbVendor);

    sqlparser.sqlfilename  = sqlFile;

    int ret = sqlparser.getrawsqlstatements();
    if (ret == 0){
        TSourceToken endToken,nextToken;

        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            System.out.println(sqlparser.sqlstatements.get(i).sqlstatementtype.toString());
            System.out.print(sqlparser.sqlstatements.get(i).toString());
            endToken = sqlparser.sqlstatements.get(i).getEndToken();
            for(int j=endToken.posinlist + 1; j< sqlparser.sourcetokenlist.size();j++){
                nextToken = sqlparser.sourcetokenlist.get(j);
                if ((nextToken.tokencode == TBaseType.cmtslashstar)
                        ||(nextToken.tokencode == TBaseType.cmtdoublehyphen)
                        ||(nextToken.tokencode == TBaseType.lexspace)
                        ||(nextToken.tokencode == TBaseType.lexnewline)
                    ){
                        System.out.print(nextToken.toString());
                }else break;
            }
            System.out.println();
        }
    }else{
        System.out.println(sqlparser.getErrormessage());
    }

    System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}