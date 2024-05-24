package dlineage;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.ProcessUtility;
import gudusoft.gsqlparser.util.SQLUtil;
import junit.framework.TestCase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class testColumnLevelLineageFile extends TestCase {

    public void test1() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/group-by.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/group-by.csv", EDbVendor.dbvoracle));
    }

    public void test2() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/from.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/from.csv", EDbVendor.dbvoracle));
    }

    public void test3() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/left-join.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/left-join.csv", EDbVendor.dbvoracle));
    }

    public void test4() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count.csv", EDbVendor.dbvoracle));
    }

    public void test5() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/alter-table.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/alter-table.csv", EDbVendor.dbvoracle));
    }

    public void test6() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-view.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-view.csv", EDbVendor.dbvoracle));
    }

    public void test7() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/from-where.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/from-where.csv", EDbVendor.dbvoracle));
    }

    public void test8() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/ROUND.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/ROUND.csv", EDbVendor.dbvoracle));
    }

    public void test9() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-sum.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-sum.csv", EDbVendor.dbvoracle));
    }

    public void test10() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-view-alter.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-view-alter.csv", EDbVendor.dbvoracle));
    }

    public void test11() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-where.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-where.csv", EDbVendor.dbvoracle));
    }

    public void test12() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-where-group.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-where-group.csv", EDbVendor.dbvoracle));
    }

    public void test13() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-where-group.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-where-group.csv", EDbVendor.dbvoracle));
    }

    public void test14() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-where.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-where.csv", EDbVendor.dbvoracle));
    }

    public void test15() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-column.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/count-column.csv", EDbVendor.dbvoracle));
    }

    public void test16() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-group.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/sum-group.csv", EDbVendor.dbvoracle));
    }

    public void test17() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/with.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/with.csv", EDbVendor.dbvoracle));
    }
    
    public void test18() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/select.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/select.csv", EDbVendor.dbvoracle));
    }
    
    public void test19() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-create.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-create.csv", EDbVendor.dbvoracle));
    }

    public void test20() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-select.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-select.csv", EDbVendor.dbvoracle));
    }
    
    public void test21() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-procedure.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-procedure.csv", EDbVendor.dbvmssql));
    }
    
    public void test22() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-all.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/insert-all.csv", EDbVendor.dbvpostgresql));
    }
    
    public void test23() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/with-select.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/with-select.csv", EDbVendor.dbvmssql));
    }

    public void test24() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-function.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/create-function.csv", EDbVendor.dbvmssql));
    }
    
    public void test25() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/plsql-array.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/plsql-array.csv", EDbVendor.dbvoracle));
    }

    public void test26() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-1.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-1.csv", EDbVendor.dbvpresto));
    }

    public void test27() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-2.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-2.csv", EDbVendor.dbvpresto));
    }

    public void test28() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-3.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-3.csv", EDbVendor.dbvpresto));
    }

    public void test29() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-4.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/presto-unnest-4.csv", EDbVendor.dbvpresto));
    }
    
    public void test30() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/star-column.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/star-column.csv", EDbVendor.dbvoracle));
    }
    
    public void test31() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/course.sql", common.gspCommon.BASE_SQL_DIR_PUBLIC + "lineage/course.csv", EDbVendor.dbvoracle));
    }
    
    public void test32() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/update-set-clause.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/update-set-clause.csv", EDbVendor.dbvmssql));
    }

    public void test33() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/column-alias.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/column-alias.csv", EDbVendor.dbvmssql));
    }

//    public void test34() {
//        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/subquery-alais.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/subquery-alais.csv", EDbVendor.dbvmssql));
//    }
    
    public void test35() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external1.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external1.csv", EDbVendor.dbvbigquery));
    }

    public void test36() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external2.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external2.csv", EDbVendor.dbvbigquery));
    }
    
    public void test37() {
        assertTrue(compare(common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external3.sql", common.gspCommon.BASE_SQL_DIR_PRIVATE + "lineage/create-external3.csv", EDbVendor.dbvredshift));
    }


    private boolean compare(String sourceFile, String resultFile, EDbVendor vendor) {
        if ((null == sourceFile || "".equals(sourceFile)) ||
                null == resultFile || "".equals(resultFile)) {
            return false;
        }
        File file = new File(sourceFile);
        if (!file.exists()) {
            return false;
        }

        File rf = new File(resultFile);
        if (!rf.exists()) {
            return false;
        }

        StringBuilder v1 = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(rf));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                v1.append(new String(tempStr.getBytes(), StandardCharsets.UTF_8)).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        gudusoft.gsqlparser.dlineage.DataFlowAnalyzer dlineage = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
                file, vendor, false);
        dlineage.generateDataFlow();
        dataflow originDataflow = dlineage.getDataFlow();
        String result = ProcessUtility.generateColumnLevelLineageCsv(dlineage, originDataflow);
        // compare
        List<String[]> c1 = resolver(v1.toString());
        List<String[]> c2 = resolver(result);

        if (c1.isEmpty() || c2.isEmpty() || c1.size() != c2.size()) {
//        	try {
//				SQLUtil.writeToFile(rf, result);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//          return true;
        	return false;
        }
        boolean r = true;
        for (int i = 0; i < c1.size(); i++) {
            if (!TBaseType.comparyStringArray(c1.get(i), c2.get(i))) {
                r = false;
                System.out.println("The line number that failed to compare is : " + (i + 1));
                System.out.println();
            }
        }

        return r;
    }

    private List<String[]> resolver(String str) {
    	List<String[]> r = new ArrayList<>();
        String[] vars = str.replaceAll("\\d+", "").replace(";", ",").split("\n");
        List<String> lines = new ArrayList(Arrays.asList(vars));
        Collections.sort(lines);
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) {
                r.add(lines.get(i).split(","));
                continue;
            }
            r.add(lines.get(i).split(","));
        }
        return r;
    }

}
