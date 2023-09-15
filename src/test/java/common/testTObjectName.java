package common;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDropDbObject;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.*;

public class testTObjectName extends TestCase {

    public void testAttribute(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select college.school().school_name(),high_school.GPA()";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);

        TObjectNameList attributes = column.getExpr().getObjectOperand().getColumnAttributes();
        TObjectName attribute1 = attributes.getObjectName(0);
        TObjectName attribute2 = attributes.getObjectName(1);

        assertTrue(attribute1.getObjectType() == TObjectName.ttobjAttribute);
        assertTrue(attribute1.toString().equalsIgnoreCase("school()"));
        assertTrue(attribute1.getPartToken().toString().equalsIgnoreCase("school"));

        assertTrue(attribute2.getObjectType() == TObjectName.ttobjAttribute);
        assertTrue(attribute2.toString().equalsIgnoreCase("school_name()"));
        assertTrue(attribute2.getPartToken().toString().equalsIgnoreCase("school_name"));

        // high_school.GPA() was treated as a function but not attribute
        assertTrue(column1.toString().equalsIgnoreCase("high_school.GPA()"));
        assertTrue(column1.getExpr().getExpressionType() == EExpressionType.function_t);
        assertTrue(column1.getExpr().getFunctionCall().getFunctionName().toString().equalsIgnoreCase("high_school.GPA"));

//        System.out.println(attribute1.getPartToken().toString());
//        System.out.println(column.toString());
//        System.out.println(column1.toString());
    }

    public void testColumnToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT t.f1, f2 from t where t.f3 = f4";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column1 = select.getResultColumnList().getResultColumn(0);
        TResultColumn column2 = select.getResultColumnList().getResultColumn(1);

        TWhereClause where = select.getWhereClause();
        TExpression expr1 = where.getCondition().getLeftOperand();
        TExpression expr2 = where.getCondition().getRightOperand();

        assertTrue(column1.getExpr().getObjectOperand().getColumnToken().toString().equalsIgnoreCase("f1"));
        assertTrue(column2.getExpr().getObjectOperand().getColumnToken().toString().equalsIgnoreCase("f2"));
        assertTrue(expr1.getObjectOperand().getColumnToken().toString().equalsIgnoreCase("f3"));
        assertTrue(expr2.getObjectOperand().getColumnToken().toString().equalsIgnoreCase("f4"));

        TObjectName columnName1 = column2.getExpr().getObjectOperand();
        assertTrue(columnName1.getDbObjectType() == EDbObjectType.column);
//        System.out.println(columnName1.getObjectToken().toString());
//        System.out.println(columnName1.getTableToken().toString());
//        System.out.println(columnName1.getColumnToken().toString());
//        System.out.println(columnName1.getPartToken().toString());

        //System.out.println(column1.getExpr().getObjectOperand().getColumnToken().toString());
        //System.out.println(column2.getExpr().getObjectOperand().getColumnToken().toString());
        //System.out.println(expr1.getObjectOperand().getColumnToken().toString());
        //System.out.println(expr2.getObjectOperand().getColumnToken().toString());

    }

    public void testTableObjectInTTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test1 as select account_name, account_number from  AP10_BANK_ACCOUNTS t;";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0); 
        TSelectSqlStatement select = createView.getSubquery();
        TTable table = select.tables.getTable(0);
        TObjectName oObjName = table.getTableName();
        assertTrue(oObjName.getObjectType() == TObjectName.ttobjTable);
        // System.out.println(oObjName.getObjectType());
    }

    public void testTableName(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "CREATE INDEX index_name ON cat1.sch1.tab1 ( col1 )";
        assertTrue(sqlparser.parse() == 0);
        TObjectName tableName = ((TCreateIndexSqlStatement) sqlparser.getSqlstatements().get(0)).getTableName();
        assertTrue(tableName.getDatabaseString().equalsIgnoreCase("cat1"));
        assertTrue(tableName.getSchemaString().equalsIgnoreCase("sch1"));
        assertTrue(tableName.getObjectString().equalsIgnoreCase("tab1"));

        sqlparser.sqltext = "CREATE TABLE table_name ( col1 int REFERENCES cat2.sch2.tab2 ( col1 ) )";
        assertTrue(sqlparser.parse() == 0);
        tableName = ((TCreateTableSqlStatement) sqlparser.getSqlstatements().get(0))
                .getColumnList().getColumn(0).getConstraints().getConstraint(0).getReferencedObject();
        assertTrue(tableName.getDatabaseString().equalsIgnoreCase("cat2"));
        assertTrue(tableName.getSchemaString().equalsIgnoreCase("sch2"));
        assertTrue(tableName.getObjectString().equalsIgnoreCase("tab2"));

    }

    public void testDropProcedure() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "DROP PROCEDURE sch1.proc1";
        assertTrue(sqlparser.parse() == 0);
        TObjectName tableName = ((TMssqlDropDbObject) sqlparser.getSqlstatements().get(0))
                .getObjectNameList().getObjectName(0);
        assertTrue(tableName.getSchemaString().equalsIgnoreCase("sch1"));
        assertTrue(tableName.getObjectString().equalsIgnoreCase("proc1"));
    }


    public void testFunctionCallArgument() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "select * from t where column = _expr(\"abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def 123456789\")";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        TExpression e  = select.getWhereClause().getCondition();
        assertTrue(e.getRightOperand().toString().equalsIgnoreCase("_expr(\"abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def ghi jkl mno pqr stu vwx yz abc def 123456789\")"));

    }

    public void testDropMaterializedView() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DROP MATERIALIZED VIEW sample1.view1";
        assertTrue(sqlparser.parse() == 0);
        TObjectName view = ((TDropMaterializedViewStmt)sqlparser.sqlstatements.get(0)).getViewName();
        assertTrue(view.getSchemaString().equalsIgnoreCase("sample1"));
        assertTrue(view.getObjectString().equalsIgnoreCase("view1"));
    }

    public void testListTableNameOnly() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT * FROM `Test`.`geom`";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectStmt = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectStmt.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("`Test`.`geom`"));
        assertTrue(table.getTableName().getObjectToken().toString().equalsIgnoreCase("`geom`"));
        assertTrue(table.getTableName().getTableString().toString().equalsIgnoreCase("`geom`"));
        assertTrue(table.getTableName().getDatabaseString().toString().equalsIgnoreCase("`Test`"));
    }

}
