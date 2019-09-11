package demos.gettablecolumns;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class columnTableStmt {

    public static void main(String args[])
     {

        if (args.length != 1){
            System.out.println("Usage: java columnTableStmt sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

         //1: dbvmssql,2: dbvoracle, 3: dbvmysql,  dbvaccess, dbvgeneric, 4: dbvdb2,
         // dbvsybase,dbvinformix, 5: dbvpostgresql,dbvfirebird,dbvmdx, 6: dbvteradata

        EDbVendor dbVendor = EDbVendor.dbvmssql;
        String msg = "Please select SQL dialect: 1: SQL Server, 2: Oralce, 3: MySQL, 4: DB2, 5: PostGRESQL, 6: Teradta, default is 1: SQL Server";
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

        int ret = sqlparser.parse();
        if (ret == 0){
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                iterateStmt(sqlparser.sqlstatements.get(i));
            }
        }else{
            System.out.println(sqlparser.getErrormessage());
        }
     }

    protected static void iterateStmt(TCustomSqlStatement stmt){
       // System.out.println(stmt.sqlstatementtype.toString());

        for(int i=0;i<stmt.tables.size();i++){
            TTable table = stmt.tables.getTable(i);
            String table_name = table.getName();
            System.out.println("Analyzing: "+ table_name +" <- "+ stmt.sqlstatementtype);
            for (int j=0; j < table.getLinkedColumns().size(); j++) {
              TObjectName objectName = table.getLinkedColumns().getObjectName(j);
              String column_name = table_name +"."+ objectName.getColumnNameOnly().toLowerCase();
              if (!objectName.isTableDetermined()) {
                 column_name = "?."+ objectName.getColumnNameOnly().toLowerCase();
              }
              System.out.println("Analyzing: "+ column_name +" in "+ stmt.sqlstatementtype +" "+ objectName.getLocation());
            }
        }

        for (int i=0;i<stmt.getStatements().size();i++){
           iterateStmt(stmt.getStatements().get(i));
        }

    }


}
