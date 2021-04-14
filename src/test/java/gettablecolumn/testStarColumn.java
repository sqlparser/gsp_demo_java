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
                System.out.println("Not linked column found for:"+node.toString());
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
