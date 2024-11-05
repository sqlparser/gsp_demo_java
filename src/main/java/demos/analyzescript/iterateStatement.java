package demos.analyzescript;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import demos.columnInClause;

/*
 * ref: http://www.dpriver.com/blog/list-of-demos-illustrate-how-to-use-general-sql-parser/visit-sql-statement-recursively/
 */

public class iterateStatement {
    public static void main(String args[])
     {

        if (args.length != 1){
            System.out.println("Usage: java getstatement sqlfile.sql");
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
        System.out.println(stmt.sqlstatementtype.toString());
        switch (stmt.sqlstatementtype){
            case sstselect:
                printSelect((TSelectSqlStatement)stmt);
                break;
            default:
        }
        for (int i=0;i<stmt.getStatements().size();i++){
           iterateStmt(stmt.getStatements().get(i));
        }
    }

    private static void printSelect(TSelectSqlStatement select){
        if (select.isCombinedQuery()){
            printSelect(select.getLeftStmt());
            printSelect(select.getRightStmt());
            return;
        }
        System.out.println("location:" + select.getLocation());

        System.out.println("Result Columns:");
        for(int i=0;i<select.getResultColumnList().size();i++){
            System.out.println(i+": "+select.getResultColumnList().getResultColumn(i).toString());
            if (select.getResultColumnList().getResultColumn(i).getExpr() != null){
                new columnInClause().printColumns(select.getResultColumnList().getResultColumn(i).getExpr(),select);
            }
        }

        if (select.tables.size() > 0){
            for(int i=0;i<select.tables.size();i++){
              if (select.tables.getTable(i).isBaseTable()){
                  System.out.println("table name: "+select.tables.getTable(i).getTableName().toString());
              }else{
                  String table_caption = select.tables.getTable(i).getTableType().toString();
                  if (select.tables.getTable(i).getAliasClause() != null){
                      table_caption += " "+ select.tables.getTable(i).getAliasClause().toString();
                  }
                  System.out.println("table source: "+table_caption);
              }
            }
        }

        if (select.getWhereClause() != null){
            System.out.println("where clause:");
            System.out.println(select.getWhereClause().toString());
            new columnInClause().printColumns(select.getWhereClause().getCondition(),select);
        }

        if (select.getGroupByClause() != null){
            System.out.println("group by clause:");
            System.out.println(select.getGroupByClause().getItems().toString());
            new columnInClause().printColumns(select.getGroupByClause().getItems(),select);
            if (select.getGroupByClause().getHavingClause() != null){
                System.out.println("having clause:");
                System.out.println(select.getGroupByClause().getHavingClause().toString());
                new columnInClause().printColumns(select.getGroupByClause().getHavingClause(),select);
            }
        }

        if (select.getOrderbyClause() != null){
            System.out.println("order by clause:");
            System.out.println(select.getOrderbyClause().toString());
            new columnInClause().printColumns(select.getOrderbyClause(), select);
        }
    }



}

