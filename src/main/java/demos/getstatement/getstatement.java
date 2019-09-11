package demos.getstatement;


import gudusoft.gsqlparser.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class getstatement {
   public static void main(String args[])
    {
    long t = System.currentTimeMillis();

        if (args.length != 1){
            System.out.println("Usage: java getstatement sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

    EDbVendor dbVendor = EDbVendor.dbvoracle;
    String msg = "Please select SQL dialect: 1: SQL Server, 2: Oralce, 3: MySQL, 4: DB2, 5: PostGRESQL, 6: Teradta, default is 2: Oracle";
    System.out.println(msg);

    BufferedReader br=new   BufferedReader(new InputStreamReader(System.in));
    try{
        int db = Integer.parseInt(br.readLine());
        if (db == 1){
            dbVendor = EDbVendor.dbvmssql;
        }else if(db == 2){
            dbVendor = EDbVendor.dbvoracle;
        }else if(db == 3){
            dbVendor = EDbVendor.dbvmysql;
        }else if(db == 4){
            dbVendor = EDbVendor.dbvdb2;
        }else if(db == 5){
            dbVendor = EDbVendor.dbvpostgresql;
        }else if(db == 6){
            dbVendor = EDbVendor.dbvteradata;
        }
    }catch(IOException i) {
    }catch (NumberFormatException numberFormatException){
    }

    System.out.println("Selected SQL dialect: "+dbVendor.toString());

    TGSqlParser sqlparser = new TGSqlParser(dbVendor);

    sqlparser.sqlfilename  = args[0];

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