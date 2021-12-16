package lineage;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.ProcessUtility;
import junit.framework.TestCase;

public class test1 extends TestCase {

    public static void test1(){
        String sqlText = "SELECT deptno, COUNT() num_emp, SUM(SAL) sal_sum\n" +
                "FROM scott.emp\n" +
                "GROUP BY deptno";
        boolean simple = false;
        String result = "";

        gudusoft.gsqlparser.dlineage.DataFlowAnalyzer dlineage = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
                sqlText, EDbVendor.dbvoracle, simple);

        dlineage.generateDataFlow();
        dataflow originDataflow = dlineage.getDataFlow();
        result = ProcessUtility.generateTableLevelLineageCsv(dlineage, originDataflow);
        System.out.println(result);

    }
}
