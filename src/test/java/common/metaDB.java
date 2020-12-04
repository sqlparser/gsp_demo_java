package common;
/*
 * Date: 12-3-31
 */

import gudusoft.gsqlparser.IMetaDatabase;


public class metaDB implements IMetaDatabase {

    String columns[][] = {
        {"server","db","dbo","aTab","Quantity3"},
        {"server","db","dbo","bTab","Quantity2"},
        {"server","db","dbo","cTab","Quantity"}
    };

public boolean checkColumn(String server, String database,String schema, String table, String column){
       boolean bServer,bDatabase,bSchema,bTable,bColumn,bRet = false;
        for (int i=0; i<columns.length;i++){
            if ((server == null)||(server.length() == 0)){
                bServer = true;
            }else{
                bServer = columns[i][0].equalsIgnoreCase(server);
            }
            if (!bServer) continue;

            if ((database == null)||(database.length() == 0)){
                bDatabase = true;
            }else{
                bDatabase = columns[i][1].equalsIgnoreCase(database);
            }
            if (!bDatabase) continue;

            if ((schema == null)||(schema.length() == 0)){
                bSchema = true;
            }else{
                bSchema = columns[i][2].equalsIgnoreCase(schema);
            }

            if (!bSchema) continue;

            bTable = columns[i][3].equalsIgnoreCase(table);
            if (!bTable) continue;

            bColumn = columns[i][4].equalsIgnoreCase(column);
            if (!bColumn) continue;

            bRet =true;
            break;

        }

        return bRet;
    }

}