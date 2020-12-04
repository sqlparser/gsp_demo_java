package sybase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testFunctionTable extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "  select res_ok,\n" +
                "    res_message\n" +
                "  from soap_pe.pe_insert_update_pc_hor_disp_det_web(li_id_employe, \n" +
                "    li_langue,\n" +
                "    arg_id_pc_hor_disp, \n" +
                "    arg_heure_debut,\n" +
                "    arg_heure_fin,\n" +
                "    arg_jour1, \n" +
                "    arg_jour2,\n" +
                "    arg_jour3,\n" +
                "    arg_jour4, \n" +
                "    arg_jour5,\n" +
                "    arg_jour6,\n" +
                "    arg_jour7,\n" +
                "    arg_heure_debut_old,\n" +
                "    arg_heure_fin_old);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.function);
        TObjectName functionName = table.getFuncCall().getFunctionName();
        assertTrue(functionName.toString().equalsIgnoreCase("soap_pe.pe_insert_update_pc_hor_disp_det_web"));
        assertTrue(functionName.getSchemaString().equalsIgnoreCase("soap_pe"));
        assertTrue(functionName.getObjectString().equalsIgnoreCase("pe_insert_update_pc_hor_disp_det_web"));
    }
}
