package gettablecolumn;
/*
 * Date: 15-4-30
 */

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import junit.framework.TestCase;

/**
 * @deprecated As of v2.0.3.1, please use {@link #testSQLEnv} instead
 */
class myMetaDB2 implements IMetaDatabase {

    String columns[][] = {
        {"server","db","DW","AcctInfo_PT","ACCT_ID"},
        {"server","db","DW","ImSysInfo_BC","ACCT_ID"},
        {"server","db","DW","AcctInfo_PT","SystemOfRec"},
        {"server","db","DW","ImSysInfo_BC","SystemOfRec"},
        {"server","db","DW","AcctInfo_PT","OfficerCode"},
        {"server","db","DW","ImSysInfo_BC","OpeningDate"},
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

public class testTC1 extends TestCase {

  public static void test1(){
//      TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvteradata);
//      getTableColumn.isConsole = false;
//      getTableColumn.listStarColumn = true;
//      getTableColumn.setMetaDatabase(new myMetaDB2());
//      getTableColumn.runText("SELECT officercode, \n" +
//              "       openingdate, \n" +
//              "       Count(*) AS NUM_OPEN \n" +
//              "FROM   (SELECT * \n" +
//              "        FROM   (SELECT * \n" +
//              "                FROM   dw.acctinfo_pt b \n" +
//              "                WHERE  snap_dt = DATE'2014-12-31') a) Acct \n" +
//              "       inner join (SELECT * \n" +
//              "                   FROM   (SELECT * \n" +
//              "                           FROM   dw.imsysinfo_bc c \n" +
//              "                           WHERE  snap_dt = DATE'2014-12-31' \n" +
//              "                                  AND acct_id IN (SELECT acct_id \n" +
//              "                                                  FROM   dw.acctinfo_pt \n" +
//              "                                                  WHERE \n" +
//              "                                      snap_dt = DATE'2014-12-31' \n" +
//              "                                      AND acctstatus = '04')) b) \n" +
//              "                                                   ImSys \n" +
//              "               ON ImSys.systemofrec = Acct.systemofrec \n" +
//              "GROUP  BY 1, \n" +
//              "          2;");
//       String strActual = getTableColumn.outList.toString();
//      // System.out.println(strActual);
//      assertTrue(strActual.trim().equalsIgnoreCase("Tables:\n" +
//              "dw.acctinfo_pt\n" +
//              "dw.imsysinfo_bc\n" +
//              "\nFields:\n" +
//              "dw.acctinfo_pt.*\n" +
//              "dw.acctinfo_pt.acct_id\n" +
//              "dw.acctinfo_pt.acctstatus\n" +
//              "dw.acctinfo_pt.officercode\n" +
//              "dw.acctinfo_pt.snap_dt\n" +
//              "dw.acctinfo_pt.systemofrec\n" +
//              "dw.imsysinfo_bc.*\n" +
//              "dw.imsysinfo_bc.acct_id\n" +
//              "dw.imsysinfo_bc.openingdate\n" +
//              "dw.imsysinfo_bc.snap_dt\n" +
//              "dw.imsysinfo_bc.systemofrec"));
  }

}
