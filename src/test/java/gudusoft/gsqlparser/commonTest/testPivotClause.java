package gudusoft.gsqlparser.commonTest;


import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TPivotClause;
import junit.framework.TestCase;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class testPivotClause  extends TestCase {

    public void test1(){
        //String sqlfile = "c:/prg/tmp/demo.sql";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        //sqlparser.sqlfilename = sqlfile;
        sqlparser.sqltext = "SELECT dim_patient_bk\n" +
                "\t\t,FALNR\n" +
                "\t\t,tage_imc\t\t\t\t= [IMC]\n" +
                "\t\t,tage_ips\t\t\t\t= [IPS]\n" +
                "\t\t,tage_isolierstation\t= [Isolierstation]\n" +
                "\t\t,tage_bettenstation\t\t= [Bettenstation]\n" +
                "FROM (\n" +
                "\tSELECT dim_patient_bk\n" +
                "\t\t\t,FALNR\n" +
                "\t\t\t,stat_typ\n" +
                "\t\t\t,sum_days = SUM(day_diff)\n" +
                "\tFROM (\n" +
                "\t\tSELECT\tdim_patient_bk\n" +
                "\t\t\t\t,FALNR\n" +
                "\t\t\t\t,day_diff = DATEDIFF(DAY, BWIDT\n" +
                "\t\t\t\t\t, CASE BEWTY WHEN 2 THEN BWIDT ELSE ISNULL(LEAD(BWIDT)\n" +
                "\t\t\t\t\t\t\tOVER (PARTITION BY dim_patient_bk, FALNR ORDER BY BWIDT),CAST(GETDATE() AS DATE)) END)\n" +
                "\t\t\t\t,stat_typ = stellplatz_typ\n" +
                "\t\tFROM\tatl.covid_patient_bewegung\n" +
                "\t\tWHERE BEWTY <> 4\n" +
                "\t) AS b\n" +
                "\tGROUP BY b.dim_patient_bk\n" +
                "\t\t\t,b.FALNR\n" +
                "\t\t\t,b.stat_typ\n" +
                ") AS src\n" +
                "PIVOT\n" +
                "(\n" +
                "MAX(sum_days)\n" +
                "FOR stat_typ IN ([IMC], [IPS], [Isolierstation], [Bettenstation])\n" +
                ") AS bPivot";
        assertTrue(sqlparser.parse() == 0);
        sqlparser.getSqlstatements().get(0).acceptChildren(new nodeVisitor());
    }
}

class nodeVisitor extends TParseTreeVisitor {

    public void preVisit(TPivotClause node) {
        System.out.print(
                 "name:" + node.getPivotTable().getTableName().toString()
                + ", \tsource table:" + node.getPivotTable().getSourceTableOfPivot().getTableName().toString()
        );

        if (node.getType() == TPivotClause.pivot){
            //System.out.println("\n\nColumns in pivot table:");
        }else{
            //System.out.println("\n\nColumns in unpivot table:");
        }

        for(TObjectName objectName:node.getPivotTable().getLinkedColumns()){
            System.out.println(objectName.toString());
            if (objectName.getSourceColumn() != null){
              //  System.out.println("\tSource column:"+objectName.getSourceColumn().toString());
            }
        }

        System.out.println("\nColumns in pivot source table:");
        for(TObjectName objectName:node.getPivotTable().getSourceTableOfPivot().getLinkedColumns()){
            //System.out.println(objectName.toString());
        }

        if (node.getAliasClause() != null){
            if (node.getAliasClause().getAliasName() != null){
                //System.out.println("pivot alias:\t"+node.getAliasClause().getAliasName().toString());
            }
            if (node.getAliasClause().getColumns() != null){
                System.out.println("\tcolumns:\t");
                for(TObjectName objectName:node.getAliasClause().getColumns()){
                  //  System.out.println("\t\t"+objectName.toString());
                }
            }
        }

    }
}
