package gettablecolumn;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testStarColumn extends TestCase {

    public static void test1(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "select emp.* from emp,dept";
        sqlParser.setSqlEnv(new TOracleEnv1());
        sqlParser.parse();
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.get(0).equalsIgnoreCase("EMP.NO"));
        assertTrue(columns.get(1).equalsIgnoreCase("EMP.NAME"));
        assertTrue(columns.get(2).equalsIgnoreCase("EMP.DEPTNO"));
    }

    public static void test11(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "select dept.* from emp,dept";
        sqlParser.setSqlEnv(new TOracleEnv1());
        sqlParser.parse();
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.get(0).equalsIgnoreCase("DEPT.NO"));
        assertTrue(columns.get(1).equalsIgnoreCase("DEPT.NAME"));
        assertTrue(columns.get(2).equalsIgnoreCase("DEPT.LOCATION"));
    }

    public static void test2(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "select * from emp,dept";
        sqlParser.setSqlEnv(new TOracleEnv1());
        sqlParser.parse();
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.get(0).equalsIgnoreCase("EMP.NO"));
        assertTrue(columns.get(1).equalsIgnoreCase("EMP.NAME"));
        assertTrue(columns.get(2).equalsIgnoreCase("EMP.DEPTNO"));
        assertTrue(columns.get(3).equalsIgnoreCase("DEPT.NO"));
        assertTrue(columns.get(4).equalsIgnoreCase("DEPT.NAME"));
        assertTrue(columns.get(5).equalsIgnoreCase("DEPT.LOCATION"));
    }

    public static void test3(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "select * from emp";
        sqlParser.setSqlEnv(new TOracleEnv1());
        sqlParser.parse();
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.get(0).equalsIgnoreCase("EMP.NO"));
        assertTrue(columns.get(1).equalsIgnoreCase("EMP.NAME"));
        assertTrue(columns.get(2).equalsIgnoreCase("EMP.DEPTNO"));
    }

    public static void test4(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "select o1.*, o2.c1 from (select c123, c345 from some_table) o1, other_table o2";
        sqlParser.setSqlEnv(new TOracleEnv1());
        assertTrue(sqlParser.parse()==0);
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.get(0).equalsIgnoreCase("c123"));
        assertTrue(columns.get(1).equalsIgnoreCase("c345"));
    }

    public static void testCTE(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlParser.sqltext = "CREATE VIEW [dwh_sws_reports].[DimGroupCorporation] AS WITH src AS (\n" +
                "    SELECT \n" +
                "      [PackageExecutionID] = tb1.[PackageExecutionID2], \n" +
                "      [SourceTK] = tb1.[SourceTK], \n" +
                "      [ValidFromTC] = tb1.[ValidFromTC], \n" +
                "      [ChecksumTC] = tb1.[ChecksumTC], \n" +
                "      [IsActiveTC] = tb1.[IsActiveTC], \n" +
                "      [DimGroupCorporationSK] = tb1.[DimGroupCorporationSK], \n" +
                "      [GroupCorporationNo] = tb1.[GroupCorporationNo], \n" +
                "      [GroupCorporationName] = tb1.[GroupCorporationName], \n" +
                "      [GroupCorporationName_ERP] = tb1.[GroupCorporationName_ERP], \n" +
                "      [GroupCorporationNameAnnex_ERP] = tb1.[GroupCorporationNameAnnex_ERP], \n" +
                "      [GroupCorporationNameComplete_ERP] = tb1.[GroupCorporationNameComplete_ERP], \n" +
                "      [FinalAssemblyActivityCode] = tb1.[FinalAssemblyActivityCode], \n" +
                "      [ShipmentActivityCode] = tb1.[ShipmentActivityCode], \n" +
                "      [IsUsedInBAAN] = tb1.[IsUsedInBAAN], \n" +
                "      [IsActiveInBAAN] = tb1.[IsActiveInBAAN], \n" +
                "      [IsActive] = tb1.[IsActive] \n" +
                "    FROM \n" +
                "      [star_software_solutions].[DimGroupCorporation] AS tb1 \n" +
                "    WHERE \n" +
                "      1 = 1\n" +
                "  ) \n" +
                "SELECT \n" +
                "  * \n" +
                "FROM \n" +
                "  [src] ";

        assertTrue(sqlParser.parse()==0);
        nodeVisitor columnVisitor = new nodeVisitor();
        sqlParser.getSqlstatements().acceptChildren(columnVisitor);
        ArrayList<String> columns = columnVisitor.columns;
        assertTrue(columns.size() == 16);
        assertTrue(columns.get(0).equalsIgnoreCase("[PackageExecutionID]"));
//        int i = 0;
//        for(String s:columns){
//            i++;
//            System.out.println(i+":\t"+s);
//        }
    }
}

class nodeVisitor extends TParseTreeVisitor {
    ArrayList<String> columns  = new ArrayList<>();
    public void preVisit(TObjectName node) {
        if (node.toString().endsWith("*")){
            if (node.getColumnsLinkedToStarColumn().size() > 0){
                for(String column : node.getColumnsLinkedToStarColumn()){
                   // System.out.println(column);
                }
                columns.addAll(node.getColumnsLinkedToStarColumn());
            }else{
                System.out.println("No linked column found for:"+node.toString());
            }
        }

    }
}

class TOracleEnv1 extends TSQLEnv {

    public TOracleEnv1(){
        super(EDbVendor.dbvoracle);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {
        TSQLCatalog sqlCatalog = createSQLCatalog("default");
        TSQLSchema sqlSchema = sqlCatalog.createSchema("scott");
        TSQLTable aTab = sqlSchema.createTable("emp");
        aTab.addColumn("no");
        aTab.addColumn("name");
        aTab.addColumn("deptNo");
        TSQLTable bTab = sqlSchema.createTable("dept");
        bTab.addColumn("no");
        bTab.addColumn("name");
        bTab.addColumn("location");
    }
}
