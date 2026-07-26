package demos.getcrud;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

enum crudAction  {
    select(0,"select"),
    createtable(1,"createtable"),
    delete(2,"delete") ,
    insert(3,"insert"),
    update(4,"update");

    private final int id;

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    private final String label;

    crudAction(final int id, final String label) {
        this.id = id;
        this.label = label;
    }

};

class tableEffected{
//    public final static int select = 0;
//    public final static int createtable = 1;
//    public final static int delete = 2;
//    public final static int insert = 3;
//    public final static int update = 4;
    public final static String[] stmtstr = {"s","c","d","i","u"};

    TObjectName tablename;
    int[] statetments;
    public tableEffected(){
        statetments = new int[5];
    }
}

class crud{
   public final static int 	max_tables = 100;
   tableEffected[] tables ;
   int total_tables;
   EDbVendor dbvendor;
   String sqlfile;


    public crud(EDbVendor db, String sqlfile){
        this.dbvendor = db;
        this.sqlfile = sqlfile;
        tables = new tableEffected[max_tables];
        total_tables = 0;
    }

    public int run(){
        TGSqlParser sqlparser = new TGSqlParser(this.dbvendor);
        sqlparser.sqlfilename = sqlfile;
        int ret = sqlparser.parse();
        if (ret != 0){
            System.out.println(sqlparser.getErrormessage());
            return ret;
        }

        for (int i=0;i<sqlparser.sqlstatements.size();i++){
            analyzeStmt(sqlparser.sqlstatements.get(i));
        }

        showResult();
        return ret;
    }

    void showResult(){
        String str="";

        System.out.println("Summary");
        for(int i=0;i<total_tables;i++){
            for(int j=0;j<5;j++){
              str = str+"\t\t\t\t"+tables[i].statetments[j]+"("+tableEffected.stmtstr[j]+")";
            }
            System.out.println(tables[i].tablename.toString()+str);
            str = "";
        }

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

              switch(stmt.sqlstatementtype){
                  case sstselect:
                      addTable(stmt.tables.getTable(i),crudAction.select);
                      break;
                  case sstcreatetable:
                      addTable(stmt.tables.getTable(i),crudAction.createtable);
                      break;
                  case sstdelete:
                      if (i == 0){
                        addTable(stmt.tables.getTable(i),crudAction.delete);
                      }else{
                          addTable(stmt.tables.getTable(i),crudAction.select);
                      }
                      break;
                  case sstinsert:
                      addTable(stmt.tables.getTable(i),crudAction.insert);
                      break;
                  case sstupdate:
                      if (i == 0){
                        addTable(stmt.tables.getTable(i),crudAction.update);
                      }else{
                        addTable(stmt.tables.getTable(i),crudAction.select);
                      }
                      break;
                  default:
                      System.out.println(stmt.sqlstatementtype.toString()+" was not supported, you can extend this demo to support this kind SQL statement.");
                      break;
              }

            }
            //System.out.println(stmt.tables.getTable(i).getFullName());
        }

        for (int i=0;i<stmt.getStatements().size();i++){
           analyzeStmt(stmt.getStatements().get(i));
        }
    }


    void addTable(TTable pTable,crudAction pkind){

        int kind = pkind.getId();

        TObjectName tablename;
        tablename = pTable.getTableName();

        System.out.println("CRUD: "+pkind.getLabel()+", Table:"+ tablename);

        String columnName;
        if (pTable.getLinkedColumns().size() > 0){
            System.out.println("\tColumns:");
        }
        for (int j=0;j<pTable.getLinkedColumns().size();j++){
          columnName = pTable.getLinkedColumns().getObjectName(j).getColumnNameOnly();
          if (!pTable.getLinkedColumns().getObjectName(j).isTableDetermined()){
              columnName = columnName + "(not determined)";
          }
          System.out.println("\t\t"+columnName);
        }

       //check is this table already in tables
        boolean isFound = false;

        for(int i=0;i<total_tables;i++){
            if (tables[i].tablename.toString().compareToIgnoreCase(tablename.toString()) == 0){
               tables[i].statetments[kind]++;
                isFound = true;
                break;
            }
        }

         if (!isFound){
          tables[total_tables] = new  tableEffected();
          tables[total_tables].tablename = tablename;
          tables[total_tables].statetments[kind]++;
          total_tables++;
         }

    }
}

public class getcrud {

    public static void main(String args[])
     {
       long t = System.currentTimeMillis();

       if (args.length != 1){
           System.out.println("Usage: java getcrud sqlfile.sql");
           return;
       }
       File file=new File(args[0]);
       if (!file.exists()){
           System.out.println("File not exists:"+args[0]);
           return;
       }

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

     crud go = new crud(dbVendor,file.getPath());
     go.run();

     System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
     }

}