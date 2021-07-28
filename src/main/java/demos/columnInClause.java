package demos;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.*;

public class columnInClause  {

    public columnInClause(){}

    public void printColumns(TExpression expression,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnVisitor cv = new columnVisitor(statement);
        expression.postOrderTraverse(cv);
    }

    public void printColumns(TGroupByItemList list,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnExprVisitor gbv = new columnExprVisitor(statement);
        list.acceptChildren(gbv);
    }

    public void printColumns(TOrderBy orderBy,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnExprVisitor obv = new columnExprVisitor(statement);
        orderBy.acceptChildren(obv);
    }

}

class columnVisitor implements IExpressionVisitor {

    TCustomSqlStatement statement = null;

    public columnVisitor(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    String getColumnWithBaseTable(TObjectName objectName){
        String ret = "";
        TTable table = null;
        boolean  find = false;
        TCustomSqlStatement lcStmt = statement;

        while ((lcStmt != null) && (!find)){
            for(int i=0;i<lcStmt.tables.size();i++){
                table = lcStmt.tables.getTable(i);
                for(int j=0;j<table.getLinkedColumns().size();j++){
                    //if (objectName == table.getLinkedColumns().getObjectName(j)){
                     if (objectName.toString().equalsIgnoreCase(table.getLinkedColumns().getObjectName(j).toString())){
                        if(table.isBaseTable()){
                            ret =  table.getTableName()+"."+objectName.getColumnNameOnly();
                        }else{
                            //derived table
                            if (table.getAliasClause() != null){
                               ret =  table.getAliasClause().toString()+"."+objectName.getColumnNameOnly();
                            }else {
                                ret =  objectName.getColumnNameOnly();
                            }

                            ret += "(column in derived table)";
                        }
                        find = true;
                        break;
                    }
                }
            }
            if(!find){
                lcStmt = lcStmt.getParentStmt();
            }
        }

        return  ret;
    }

    public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
         TExpression expr = (TExpression)pNode;
         switch ((expr.getExpressionType())){
             case simple_object_name_t:
                 TObjectName obj = expr.getObjectOperand();
                 if (obj.getObjectType() != TObjectName.ttobjNotAObject){
                    System.out.println(getColumnWithBaseTable(obj));
                    if (statement.dbvendor == EDbVendor.dbvteradata){
                        // this maybe a column alias
                        //for(int i=0;i<statement.)
                    }
                    // System.out.print(statement.dbvendor);
                 }
                 break;
             case function_t:
                 columnExprVisitor fcv = new columnExprVisitor(statement);
                 expr.getFunctionCall().acceptChildren(fcv);
                 break;
             case case_t:
                 TCaseExpression caseExpression = expr.getCaseExpression();
                 columnExprVisitor cv = new columnExprVisitor(statement);
                 caseExpression.acceptChildren(cv);

                 break;
         }
         return  true;
     }

}

class columnExprVisitor extends TParseTreeVisitor{

    TCustomSqlStatement statement = null;

    public columnExprVisitor(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    public void preVisit(TExpression expression){
        columnVisitor cv = new columnVisitor(statement);
        expression.postOrderTraverse(cv);
    }
}

