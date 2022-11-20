package demos.traceColumn;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateMacro;


public class TTraceColumn {
    private EDbVendor dbVendor;
    private String queryStr;
    private TGSqlParser sqlparser;
    private StringBuffer infos;
    private String newline  = "\n";

    public StringBuffer getInfos() {
        return infos;
    }

    public  TTraceColumn(EDbVendor pDBVendor) {
        dbVendor = pDBVendor;
        sqlparser = new TGSqlParser(dbVendor);
        infos = new StringBuffer();
    }

    public void runText(String pQuery){
        run(pQuery,false);
    }

    public void runFile(String filename){
        run(filename,true);
    }


    private void run(String pQuery, boolean isFile) {
        queryStr = pQuery;
        if (isFile) sqlparser.sqlfilename = pQuery;
        else sqlparser.sqltext = pQuery;
        int iRet = sqlparser.parse();
        if (iRet != 0) {
            System.out.println(sqlparser.getErrormessage());
            return;
        }

        infos.setLength(0);
        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            processStmt(sqlparser.sqlstatements.get(i));
        }

    }

    void processStmt(TCustomSqlStatement pStmt){

        switch (pStmt.sqlstatementtype){
            case sstselect:
                processSelect((TSelectSqlStatement)pStmt,0);
                break;
            case sstcreateview:
                processCreateView((TCreateViewSqlStatement)pStmt);
                break;
            case sstteradatacreatemacro:
                processCreateMacro((TCreateMacro)pStmt);
                break;
            default:
                break;
        }
    }

    void processCreateMacro(TCreateMacro createMacro){

        for (int i=0;i<createMacro.getBodyStatements().size();i++){
            processStmt(createMacro.getBodyStatements().get(i));
        }
    }

    void processCreateView(TCreateViewSqlStatement pCreateView){
        if (pCreateView.getViewAliasClause() != null){
            for(int i=0;i<pCreateView.getViewAliasClause().getViewAliasItemList().size();i++){
                TViewAliasItem viewAliasItem = pCreateView.getViewAliasClause().getViewAliasItemList().getViewAliasItem(i);
                infos.append(viewAliasItem.toString()+newline);
            }
        }
        processSelect(pCreateView.getSubquery(),1);
    }

    void processSelect(TSelectSqlStatement pSelect, int pLevel){

        if (pSelect.isCombinedQuery()){
            processSelect(pSelect.getLeftStmt(),pLevel);
            processSelect(pSelect.getRightStmt(),pLevel);
            return;
        }

        int level = pLevel;

        for(int i=0;i<pSelect.getResultColumnList().size();i++){
            TResultColumn resultColumn = pSelect.getResultColumnList().getResultColumn(i);
            if (resultColumn.getAliasClause() != null){
                //System.out.println(resultColumn.getAliasClause().toString());
                infos.append(numberOfSpace(pLevel)+resultColumn.getAliasClause().toString()+newline);
                level++;
            }

            //System.out.println(numberOfSpace(level)+resultColumn.getExpr().toString() + "(expr)");
            infos.append(numberOfSpace(level)+resultColumn.getExpr().toString() + "(expr)"+newline);

            if (resultColumn.getExpr().getExpressionType() == EExpressionType.subquery_t){
                processSelect(resultColumn.getExpr().getSubQuery(),level+1);
            }else{
                TColumnVisitor columnVisitor = new TColumnVisitor();
                resultColumn.getExpr().postOrderTraverse(columnVisitor);
                traceColumns(columnVisitor.getColumnList(),level+1);
            }
        }
    }

    public  void traceColumns(TObjectNameList columnList, int pLevel){
        for( int i=0;i<columnList.size();i++){
            TObjectName columnName = columnList.getObjectName(i);

            if (columnName.getSourceTable() == null) {
                //System.out.println(numberOfSpace(pLevel)+"*"+columnName.toString());
                infos.append(numberOfSpace(pLevel)+"*"+columnName.toString()+newline);
                continue;
            }

            TTable table = columnName.getSourceTable();
            if(table.isBaseTable()){
                //System.out.println(numberOfSpace(pLevel)+table.getTableName()+"."+columnName.getColumnNameOnly());
                infos.append(numberOfSpace(pLevel)+table.getTableName()+"."+columnName.getColumnNameOnly()+newline);
            }else{
                //derived table
                if (table.getAliasClause() != null){
                    //System.out.println( numberOfSpace(pLevel)+table.getAliasClause().toString()+"."+columnName.getColumnNameOnly());
                    infos.append(numberOfSpace(pLevel)+table.getAliasClause().toString()+"."+columnName.getColumnNameOnly()+newline);
                }else {
                    //System.out.println(numberOfSpace(pLevel)+ columnName.getColumnNameOnly());
                    infos.append(numberOfSpace(pLevel)+ columnName.getColumnNameOnly()+newline);
                }
            }

            if (columnName.getSourceColumn() != null){
                //System.out.println(numberOfSpace(pLevel + 1) + columnName.getSourceColumn().getExpr().toString() +"(expr)");
                infos.append(numberOfSpace(pLevel + 1) + columnName.getSourceColumn().getExpr().toString() +"(expr)"+newline);
                if (columnName.getSourceColumn().getExpr().getExpressionType() == EExpressionType.subquery_t){
                    processSelect(columnName.getSourceColumn().getExpr().getSubQuery(),pLevel + 2);
                }else {
                    TColumnVisitor columnVisitor = new TColumnVisitor();
                    columnName.getSourceColumn().getExpr().postOrderTraverse(columnVisitor);
                    traceColumns(columnVisitor.getColumnList(), pLevel + 2);
                }
            }
        }
    }

    private String numberOfSpace(int pNum){
        if (pNum == 0) return "";
        String ret="-->";
        for(int i=0;i<pNum;i++){
            ret = " "+ret;
        }
        return ret;
    }
}

class nodeVisitor extends TParseTreeVisitor{

    private TObjectNameList columnList;

    public nodeVisitor() {
        columnList = new TObjectNameList();
    }

    public TObjectNameList getColumnList() {
        return columnList;
    }

    public void preVisit(TExpression expression){
        TColumnVisitor cv = new TColumnVisitor();
        expression.postOrderTraverse(cv);
        for(int i=0;i<cv.getColumnList().size();i++){
            columnList.addObjectName(cv.getColumnList().getObjectName(i));
        }
    }
}

class TColumnVisitor implements IExpressionVisitor {

    private TObjectNameList columnList;

    public TObjectNameList getColumnList() {
        return columnList;
    }

    public TColumnVisitor()
    {
        columnList = new TObjectNameList();
    }

    public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
        TExpression expr = (TExpression)pNode;
        nodeVisitor cv;
        switch ((expr.getExpressionType())){
            case simple_object_name_t:
                TObjectName obj = expr.getObjectOperand();
                if (obj.getObjectType() != TObjectName.ttobjNotAObject){
                    columnList.addObjectName(obj);
                }
                break;
            case function_t:
//                cv = new nodeVisitor();
//                expr.getFunctionCall().acceptChildren(cv);
//                for(int i=0;i<cv.getColumnList().size();i++){
//                    columnList.addObjectName(cv.getColumnList().getObjectName(i));
//                }
                for(int i=0;i<expr.getFunctionCall().getArgs().size();i++){
                    expr.getFunctionCall().getArgs().getExpression(i).postOrderTraverse(this);
                }
                break;
            case case_t:
                cv = new nodeVisitor();
                try {
                    expr.getCaseExpression().acceptChildren(cv);
                    for (int i = 0; i < cv.getColumnList().size(); i++) {
                        columnList.addObjectName(cv.getColumnList().getObjectName(i));
                    }
                }
                catch(Exception e){

                }
                break;
        }
        return  true;
    }

}

