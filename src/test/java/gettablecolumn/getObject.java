package gettablecolumn;
/*
 * Date: 15-4-23
 */

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import common.gspCommon;
import common.metaDB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class getObject{
    public final static int compareMode = 1;
    public final static int showMode = 2;

    String sqlfile;
    String[] desiredTables = new String[100];
    String[] desiredColumns = new String[100];
    int desiredTableCount = 0, desiredColumnCount = 0;

    String oracle_sqldir = gspCommon.BASE_SQL_DIR_PRIVATE + "java/oracle/dbobject/";
    String[] oracle_sqlfiles = {
        "berger_example_01","berger_sqltest_01","berger_sqltest_02",
        "berger_sqltest_03","berger_sqltest_04","berger_sqltest_05",
            "createtrigger","plsql_block_correlated_subquery","createfunction"  ,
            "createpackagebody","merge","no_qualified_subquery"
    };

    String sqlserver_sqldir = gspCommon.BASE_SQL_DIR_PRIVATE +  "java/mssql/dbobject/";
    String[] sqlserver_sqlfiles = {
        // "bigjoin1","shurleyjoin",
//            "delete1","delete2","delete4","delete5",
//            "update1","update2","update3","update4",
            "createfunction1"
//            ,"createprocedure1","createtrigger1",
//            "createview1",
//            "while1","keyword_not_column_name",
//            "ogcmethod",
//           "funcitonOnXMLColumn"

    };

    String sqldir ;
    String[] sqlfiles ;
    int files_count = 0;

    public String showModeFile;


    String[] foundTables = new String[1000];
    String[] foundColumns = new String[1000];
    int foundTableCount = 0;
    int foundColumnsCount = 0;

    EDbVendor dbvendor;
    getObject(EDbVendor db){
        this.dbvendor = db;
        if (db == EDbVendor.dbvoracle){
            sqldir = oracle_sqldir;
            sqlfiles = oracle_sqlfiles;
            files_count = sqlfiles.length;
        }else{
            sqldir = sqlserver_sqldir;
            sqlfiles = sqlserver_sqlfiles;
            files_count = sqlfiles.length;
        }
      }

    void getDesiredTablesColumns(String sqlfile, String[] pTables,String[] pColumns){
        String line;
        desiredTableCount = 0;
        desiredColumnCount = 0;
        boolean isTable = false,isColumn = false;
        try{
            BufferedReader br = new BufferedReader( new FileReader(sqlfile) );

            try{
                while( (line = br.readLine()) != null){

                    if (line.toLowerCase().indexOf("tables:") >= 0 ) {
                       isTable = true;
                        isColumn =false;
                        continue;
                    }

                    if (line.toLowerCase().indexOf("fields:") >= 0 ) {
                       isTable = false;
                        isColumn =true;
                       continue;
                    }

                    if (line.toLowerCase().indexOf("functions:") >= 0 ) {
                       isTable = false;
                        isColumn =false;
                       continue;
                    }

                    if (line.toLowerCase().indexOf("schema:") >= 0 ) {
                       isTable = false;
                        isColumn =false;
                       continue;
                    }


                  if (isTable){
                      pTables[desiredTableCount] = line.trim();
                      desiredTableCount++;
                  }

                    if (isColumn){
                        pColumns[desiredColumnCount] = line.trim();
                        desiredColumnCount++;
                    }
                }
                br.close();
                }catch(IOException e){
                  System.out.println(e.toString());
                }

        }catch(FileNotFoundException e){
            System.out.println(e.toString());
        }

    }

    public void setSqlEnv(TSQLEnv sqlEnv) {
        this.sqlEnv = sqlEnv;
    }

    private TSQLEnv sqlEnv = null;

    boolean run(int pmode){
        // 1: compare, compare found table/column with desired results in a file
        // 2: show result, don't compare
        boolean retb = true;

        TGSqlParser sqlparser = new TGSqlParser(this.dbvendor);
        if (sqlEnv != null){
            sqlparser.setSqlEnv(sqlEnv);
        }
        if (pmode == showMode){
            files_count = 1;
            sqlparser.setMetaDatabase(new metaDB());
        }
        for (int k=0;k<files_count;k++){
            foundColumnsCount = 0;
            foundTableCount = 0;
            if (pmode == showMode){
                //sqlparser.sqlfilename = showModeFile;
                sqlparser.sqlfilename = sqldir+"berger_sqltest_01.sql";
            }else{
                sqlparser.sqlfilename = sqldir+sqlfiles[k]+".sql";
            }

            int ret = sqlparser.parse();
            if (ret == 0){
                if (pmode == compareMode){
                getDesiredTablesColumns(sqldir+sqlfiles[k]+".out", desiredTables, desiredColumns);
                }

               TCustomSqlStatement stmt = null;
               for (int i=0;i<sqlparser.sqlstatements.size();i++){
                   analyzeStmt(sqlparser.sqlstatements.get(i));
               }

                String[] foundTables2 = new String[foundTableCount];
                for(int k1=0;k1<foundTableCount;k1++){
                        foundTables[k1] = foundTables[k1].toLowerCase();
                }
                System.arraycopy(foundTables,0,foundTables2,0,foundTableCount);
                Set set= new HashSet(Arrays.asList(foundTables2));
                Object[] foundTables3 = set.toArray();
                Arrays.sort(foundTables3);

                String[] foundColumns2 = new String[foundColumnsCount];
                for(int k1=0;k1<foundColumnsCount;k1++){
                        foundColumns[k1] = foundColumns[k1].toLowerCase();
                }
                System.arraycopy(foundColumns,0,foundColumns2,0,foundColumnsCount);
                Set set2= new HashSet(Arrays.asList(foundColumns2));
                Object[] foundColumns3 = set2.toArray();
                Arrays.sort(foundColumns3);

                if (pmode == showMode){
                    for (int i=0;i<foundColumns3.length;i++){
                         System.out.println(foundColumns3[i].toString());
                    }
                  return true;
                }


                boolean isfound = false;
                for (int i=0;i<desiredColumnCount;i++){
                    isfound = false;
                    for(int j=0;j<foundColumns3.length;j++){
                        if (desiredColumns[i].toString().equalsIgnoreCase(foundColumns3[j].toString())){
                            isfound = true;
                           break;
                        }
                    }

                    if (!isfound){
                       retb = isfound;
                        System.out.println(sqlparser.sqlfilename+ " desired column not found:"+desiredColumns[i]);
                    }
                }


                for (int i=0;i<foundColumns3.length;i++){
                    isfound = false;
                    for(int j=0;j<desiredColumnCount;j++){
                        if (foundColumns3[i].toString().equalsIgnoreCase(desiredColumns[j].toString())){
                            isfound = true;
                           break;
                        }
                    }

                    if (!isfound){
                        retb = isfound;
                        //System.out.println("desiredColumns:"+Arrays.toString(desiredColumns));
                        //System.out.println("foundColumns:"+Arrays.toString(foundColumns3));
                        System.out.println(sqlparser.sqlfilename+ " not in desired column:"+foundColumns3[i]);
                    }
                }


            }else{
                System.out.println(sqlparser.getErrormessage());
            }
        }

        return retb;
    }

    protected void analyzeStmt(TCustomSqlStatement stmt){
        for(int i=0;i<stmt.tables.size();i++){
            if  (stmt.tables.getTable(i).isBaseTable())
            {
                if ( (stmt.dbvendor == EDbVendor.dbvmssql)
                        &&( (stmt.tables.getTable(i).getFullName().equalsIgnoreCase("deleted"))
                            ||(stmt.tables.getTable(i).getFullName().equalsIgnoreCase("inserted"))
                           )
                  ){
                    continue;
                }

              foundTables[foundTableCount] = stmt.tables.getTable(i).getFullName();
              foundTableCount++;
              for (int j=0;j<stmt.tables.getTable(i).getLinkedColumns().size();j++){
                if (stmt.tables.getTable(i).getLinkedColumns().getObjectName(j).getColumnNameOnly().startsWith("*")){
                    continue; //ignore *
                }
                foundColumns[foundColumnsCount] = stmt.tables.getTable(i).getFullName()+"."+stmt.tables.getTable(i).getLinkedColumns().getObjectName(j).getColumnNameOnly();
               // System.out.println("Found column:"+foundColumns[foundColumnsCount]);
                foundColumnsCount++;

              }
            }
            //System.out.println(stmt.tables.getTable(i).getFullName());
        }

        for (int i=0;i<stmt.getStatements().size();i++){
           analyzeStmt(stmt.getStatements().get(i));
        }
    }

}