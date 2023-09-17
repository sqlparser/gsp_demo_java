package demos.visitors;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.THierarchical;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.mssql.*;
import gudusoft.gsqlparser.nodes.TExceptionClause;
import gudusoft.gsqlparser.nodes.TExceptionHandler;
import gudusoft.gsqlparser.nodes.TExceptionHandlerList;
import gudusoft.gsqlparser.stmt.TCommonStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TCaseStmt;
import gudusoft.gsqlparser.stmt.TCloseStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTrigger;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateType;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTypeBody;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateType_Placeholder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @deprecated
 */
public class toXmlOldVersion {

    public static void main(String args[]) throws IOException
    {
        long t;
        t = System.currentTimeMillis();

        if (true) {
            System.out.println("this demo was deprecated, please use toXML.java instead");
            return;
        }

        if (args.length != 1){
            System.out.println("Usage: java toXml sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvoracle;
        String msg = "Please select SQL dialect: 1: SQL Server, 2: Oralce, 3: MySQL, 4: DB2, 5: PostGRESQL, 6: Teradta, default is 2: Oracle";
        System.out.println(msg);

        BufferedReader br=new   BufferedReader(new InputStreamReader(System.in));
        try{
            int db = Integer.parseInt(br.readLine());
            if (db == 1){
                dbVendor = EDbVendor.dbvmssql;
            }else if(db == 2){
                dbVendor = EDbVendor.dbvoracle;
            }else if(db == 3){
                dbVendor = EDbVendor.dbvmysql;
            }else if(db == 4){
                dbVendor = EDbVendor.dbvdb2;
            }else if(db == 5){
                dbVendor = EDbVendor.dbvpostgresql;
            }else if(db == 6){
                dbVendor = EDbVendor.dbvteradata;
            }
        }catch(IOException i) {
        }catch (NumberFormatException numberFormatException){
        }

        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);

        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            simplXmlVisitor xv = new simplXmlVisitor();
            sqlparser.sqlstatements.accept(xv);
            String  testFile = args[0]+".xml";
            write(testFile,xv.getXml());
            System.out.println(testFile+" was generated!");
        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

  static public  void write(String fFileName, String FIXED_TEXT) throws IOException  {
      Writer out = new OutputStreamWriter(new FileOutputStream(fFileName));
      try {
        out.write(FIXED_TEXT);
      }
      finally {
        out.close();
      }
    }

}

class simplXmlVisitor extends TParseTreeVisitor {

    public final static String crlf = "\r\n";
    StringBuilder sb;
    public simplXmlVisitor(){
        sb = new StringBuilder(1024);
    }


    void appendEndTag(String tagName){
        sb.append(String.format("</%s>"+crlf,tagName));
    }

    void appendStartTag(String tagName){
        sb.append(String.format("<%s>"+crlf,tagName));
    }

    String getTagName(TParseTreeNode node){
        return node.getClass().getSimpleName();
    }


    void appendStartTag(TParseTreeNode node){

        if (node instanceof TStatementList){
          appendStartTagWithCount(node,( (TStatementList)node).size() );
        }
        else if (node instanceof TParseTreeNodeList){
          appendStartTagWithCount(node,( (TParseTreeNodeList)node).size() );  
        }
        else{
        sb.append(String.format("<%s>"+crlf,getTagName(node)));
        }
    }

    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, int propertyValue){
        sb.append(String.format("<%s "+propertyName+"='%d'>"+crlf,getTagName(node),propertyValue));
    }
    
    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, EExpressionType propertyValue){
        sb.append(String.format("<%s "+propertyName+"='%s'>"+crlf,getTagName(node),propertyValue.name( )));
    }

    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, String propertyValue){
        sb.append(String.format("<%s "+propertyName+"='%s'>"+crlf,getTagName(node),propertyValue));
    }

    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, int propertyValue,String propertyName2, String propertyValue2){
        sb.append(String.format("<%s "+propertyName+"='%d' "+propertyName2+"='%s'"+">"+crlf,getTagName(node),propertyValue,propertyValue2));
    }

    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, String propertyValue,String propertyName2, String propertyValue2){
        sb.append(String.format("<%s "+propertyName+"='%s' "+propertyName2+"='%s'"+">"+crlf,getTagName(node),propertyValue,propertyValue2));
    }

    void appendStartTagWithIntProperty(TParseTreeNode node,String propertyName, String propertyValue,String propertyName2, String propertyValue2,String propertyName3, String propertyValue3){
        sb.append(String.format("<%s "+propertyName+"='%s' "+propertyName2+"='%s' "+propertyName3+"='%s'"+">"+crlf,getTagName(node),propertyValue,propertyValue2,propertyValue3));
    }

    void appendStartTagWithProperties(TParseTreeNode node,String propertyName, String propertyValue,String propertyName2, String propertyValue2){
        sb.append(String.format("<%s "+propertyName+"='%s' "+propertyName2+"='%s'"+">"+crlf,getTagName(node),propertyValue,propertyValue2));
    }

    void appendEndTag(TParseTreeNode node){
        sb.append(String.format("</%s>"+crlf,getTagName(node)));   
    }

    void appendStartTagWithCount(TParseTreeNode node, int count){
        appendStartTagWithIntProperty(node,"size",count);
    }

    // process parse tree nodes

    public void preVisit(TAlterTableOption node){
        //appendStartTag(node);
        appendStartTagWithIntProperty(node,"alterOption",node.getOptionType().toString());
        switch (node.getOptionType()){
            case AddColumn:
                node.getColumnDefinitionList().accept(this);
                break;
            case AlterColumn:
                node.getColumnName().accept(this);
                break;
            case ChangeColumn:
                node.getColumnName().accept(this);
                break;
            case DropColumn:
                node.getColumnNameList().accept(this);
                break;
            case ModifyColumn:
                node.getColumnDefinitionList().accept(this);
                break;
            case RenameColumn:
                node.getColumnName().accept(this);
                node.getNewColumnName().accept(this);
                break;
            case AddConstraint:
                node.getConstraintList().accept(this);
                break;
            default:
                sb.append(node.toString());
        }
    }
    public void postVisit(TAlterTableOption node){
        appendEndTag(node);
    }

    public void preVisit(TConstant node){
        appendStartTag(node);
        sb.append(node.toString());
    }
    public void postVisit(TConstant node){
        appendEndTag(node);
    }

    public void preVisit(TTopClause node){
        appendStartTagWithProperties(node, "percent", String.valueOf(node.isPercent())
                , "withties", String.valueOf(node.isWithties()));
        if (node.getExpr() != null){
            node.getExpr().accept(this);
        }
        if (node.getSubquery() != null){
            node.getSubquery().accept(this);
        }
    }
    
    public void postVisit(TTopClause node){
        appendEndTag(node);
    }


    public void preVisit(TSelectSqlStatement node){
//        sb.append(String.format("<%s setOperator='%d'>"+crlf,getTagName(node),node.getSetOperator()) );
        appendStartTagWithProperties(node, "setOperator",node.getSetOperatorType().toString()
                , "isAll", String.valueOf(node.isAll())) ;

        if(node.isCombinedQuery()){

            if (node.getCteList() != null){
                node.getCteList().accept(this);
            }

            node.getLeftStmt().accept(this);

            node.getRightStmt().accept(this);

            if (node.getOrderbyClause() != null){
                node.getOrderbyClause().accept(this);
            }

            if (node.getLimitClause() != null){
                node.getLimitClause().accept(this);
            }

            if (node.getForUpdateClause() != null){
                node.getForUpdateClause().accept(this);
            }

            if (node.getComputeClause() != null){
                node.getComputeClause().accept(this);
            }

            this.postVisit(node);

            return ;
        }

        if (node.getCteList() != null){
            node.getCteList().accept(this);
        }

        if (node.getTopClause() != null){
            node.getTopClause().accept(this);
        }

        node.getResultColumnList().accept(this);

        if (node.getIntoClause() != null){
            node.getIntoClause().accept(this);
        }

        node.joins.accept(this);

        if (node.getWhereClause() != null){
            node.getWhereClause().accept(this);
        }

        if (node.getHierarchicalClause() != null){
            node.getHierarchicalClause().accept(this);
        }

        if (node.getGroupByClause() != null){
            node.getGroupByClause().accept(this);
        }

        if (node.getQualifyClause() != null){
            node.getQualifyClause().accept(this);
        }

        if (node.getOrderbyClause() != null){
            node.getOrderbyClause().accept(this);
        }

        if (node.getLimitClause() != null){
            node.getLimitClause().accept(this);
        }

        if (node.getForUpdateClause() != null){
            node.getForUpdateClause().accept(this);
        }

        if (node.getComputeClause() != null){
            node.getComputeClause().accept(this);
        }

    }

    public void postVisit(TSelectSqlStatement node){
        appendEndTag(node);
    }

    public void preVisit(TResultColumnList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getResultColumn(i).accept(this);
        }
    }

    public void postVisit(TResultColumnList node){
        appendEndTag(node);
    }

    public void preVisit(TResultColumn node){
        appendStartTag(node);
        node.getExpr().accept(this);
        if (node.getAliasClause() != null){
            node.getAliasClause().accept(this);
        }
    }

    public void postVisit(TResultColumn node){
        appendEndTag(node);
    }

    public void preVisit(TExpression node){
        appendStartTagWithIntProperty(node,"type",node.getExpressionType());
        switch(node.getExpressionType()){
            case simple_object_name_t:
                node.getObjectOperand().accept(this);
                break;
            case simple_constant_t:
                node.getConstantOperand().accept(this);
                break;
            case function_t:
                node.getFunctionCall().accept(this);
                break;
            case cursor_t:
                node.getSubQuery().accept(this);
                break;
            case subquery_t:
                node.getSubQuery().accept(this);
                break;
            case exists_t:
                node.getSubQuery().accept(this);
                break;
            case case_t:
                node.getCaseExpression().accept(this);
                break;
            case simple_comparison_t:
                appendStartTag("comparisonOperator");
                sb.append(node.getComparisonOperator().toString().replace(">","&#62;").replace("<","&#60;"));
                appendEndTag("comparisonOperator");

                if (node.getSubQuery() != null){
                    node.getExprList().accept(this);
                    node.getSubQuery().accept(this);
                }else{
                    node.getLeftOperand().accept(this);
                    node.getRightOperand().accept(this);
                }
                break;
            case group_comparison_t:
                appendStartTag("comparisonOperator");
                sb.append(node.getComparisonOperator().toString().replace(">","&#62;").replace("<","&#60;"));
                appendEndTag("comparisonOperator");

                if (node.getQuantifier() != null){
                    appendStartTag("comparsionSomeAnyAll");
                    sb.append(node.getQuantifier().toString().replace(">","&#62;").replace("<","&#60;"));
                    appendEndTag("comparsionSomeAnyAll");
                }

                appendStartTag("left_expr");
                if (node.getExprList() != null){
                  node.getExprList().accept(this);
                }else{
                    node.getLeftOperand().accept(this);
                }
                appendEndTag("left_expr");

                appendStartTag("right_expr");
                node.getRightOperand().accept(this);
                appendEndTag("right_expr");
                break;
            case in_t:
                if (node.getExprList() != null){
                  node.getExprList().accept(this);
                }else{
                    node.getLeftOperand().accept(this);
                }

                node.getRightOperand( ).accept(this);
                break;
            case list_t:
                if (node.getExprList() != null){
                  node.getExprList().accept(this);
                }
                break;
            case pattern_matching_t:
                node.getLeftOperand().accept(this);
                node.getRightOperand().accept(this);
                if (node.getLikeEscapeOperand() != null){
                    node.getLikeEscapeOperand().accept(this);
                }
                break;
            case between_t:
                node.getBetweenOperand().accept(this);
                node.getLeftOperand().accept(this);
                node.getRightOperand().accept(this);
                break;
            default:
                if (node.getLeftOperand() != null){
                node.getLeftOperand().accept(this);
                }

                if (node.getRightOperand() != null){
                node.getRightOperand().accept(this);
                }
                
                //sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
                break;
        }
    }

    public void postVisit(TExpression node){
        appendEndTag(node);
    }

    public void preVisit(TAliasClause node){
        appendStartTag(node);
        sb.append(node.toString());
    }


    public void preVisit(TInExpr node){
        appendStartTag(node);
        if (node.getSubQuery() != null){
            node.getSubQuery().accept(this);
        }else if(node.getGroupingExpressionItemList() != null){
            node.getGroupingExpressionItemList().accept(this);
        }else{
        sb.append(node.toString());
        }
    }

    public void postVisit(TInExpr node){
        appendEndTag(node);
    }

    public void preVisit(TExpressionList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getExpression(i).accept(this);
        }
    }

    public void postVisit(TExpressionList node){
        appendEndTag(node);
    }

    public void preVisit(TGroupingExpressionItem node){
        appendStartTag(node);
        if (node.getExpr() != null){
            node.getExpr().accept(this);
        }else if (node.getExprList() != null){
            node.getExprList().accept(this);
        }
    }

    public void postVisit(TGroupingExpressionItem node){
        appendEndTag(node);
    }

    public void preVisit(TGroupingExpressionItemList node){
        appendStartTag(node);
    }

    public void postVisit(TGroupingExpressionItemList node){
        appendEndTag(node);
    }

    public void postVisit(TAliasClause node){
        appendEndTag(node);
    }

    public void preVisit(TJoin node){
        appendStartTagWithIntProperty(node,"type",node.getKind());
        if (node.getAliasClause() != null){
            node.getAliasClause().accept(this);
        }

        if(node.getKind() == TBaseType.join_source_fake){
            node.getTable().accept(this);
        }else if(node.getKind() == TBaseType.join_source_table){
            node.getTable().accept(this);
        }else if(node.getKind() == TBaseType.join_source_join){
            node.getJoin().accept(this);
        }

        node.getJoinItems().accept(this);

    }

    public void postVisit(TJoin node){
        appendEndTag(node);
    }
    
    public void preVisit(TJoinList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getJoin(i).accept(this);
        }
    }
    public void postVisit(TJoinList node){
        appendEndTag(node);
    }
    public void preVisit(TJoinItem node){
        appendStartTagWithIntProperty(node,"jointype",node.getJoinType().toString());
        if (node.getKind() == TBaseType.join_source_table){
            node.getTable().accept(this);
        }else if (node.getKind() == TBaseType.join_source_join){
            node.getJoin().accept(this);
        }

        if (node.getOnCondition() != null){
            node.getOnCondition().accept(this);
        }

        if (node.getUsingColumns() != null){
            node.getUsingColumns().accept(this);
        }
    }
    public void postVisit(TJoinItem node){
        appendEndTag(node);
    }
    public void preVisit(TJoinItemList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getJoinItem(i).accept(this);
        }
    }
    public void postVisit(TJoinItemList node){
        appendEndTag(node);
    }

    public void preVisit(TUnpivotInClauseItem node){
        appendStartTag(node);
        outputNodeData(node);

    }
    public void postVisit(TUnpivotInClauseItem node){
        appendEndTag(node);
    }

    public void preVisit(TUnpivotInClause node){
        appendStartTag(node);
        for(int i=0;i<node.getItems().size();i++){
            node.getItems().getElement(i).accept(this);
        }

    }
    public void postVisit(TUnpivotInClause node){
        appendEndTag(node);
    }

    public void preVisit(TPivotInClause node){
        appendStartTag(node);
        if (node.getItems() != null) node.getItems().accept(this);
        if (node.getSubQuery() != null) node.getSubQuery().accept(this);

    }
    public void postVisit(TPivotInClause node){
        appendEndTag(node);
    }

    public void preVisit(TPivotedTable node){
        appendStartTag(node);
        node.getJoins().accept(this);
        for(int i=0;i<node.getPivotClauseList().size();i++){
            node.getPivotClauseList().getElement(i).accept(this);
        }
    }
    public void postVisit(TPivotedTable node){
        appendEndTag(node);
    }

    public void preVisit(TPivotClause node){
        appendStartTag(node);

        if (node.getAggregation_function() != null){
            node.getAggregation_function().accept(this);
        }
        if (node.getValueColumn() != null){
            node.getValueColumn().accept(this);
        }
        if (node.getValueColumnList() != null){
            for(int i=0;i<node.getValueColumnList().size();i++){
                node.getValueColumnList().getObjectName(i).accept(this);
            }
        }
        if (node.getPivotColumn() != null){
            node.getPivotColumn().accept(this);
        }
        if (node.getPivotColumnList() != null){
            for(int i=0;i<node.getPivotColumnList().size();i++){
                node.getPivotColumnList().getObjectName(i).accept(this);
            }
        }

        if (node.getAggregation_function_list() != null){
            node.getAggregation_function_list().accept(this);
        }

        if (node.getIn_result_list() != null){
            node.getIn_result_list().accept(this);
        }

        if (node.getPivotInClause() != null){
            node.getPivotInClause().accept(this);
        }

        if (node.getUnpivotInClause() != null){
            node.getUnpivotInClause().accept(this);
        }

        if (node.getAliasClause() != null){
            node.getAliasClause().accept(this);
        }

    }
    public void postVisit(TPivotClause node){
        appendEndTag(node);
    }

    public void preVisit(TTable node){
        appendStartTagWithIntProperty(node,"type",node.getTableType().toString());
        //sb.append(node.toString());

        switch(node.getTableType()){
            case objectname:{
                sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
                break;
            }
            case tableExpr:{
                sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
                break;
            }
            case subquery:{
                node.getSubquery().accept(this);
                break;
            }
            case function:{
                node.getFuncCall().accept(this);
                break;
            }
            case containsTable:{
                node.getContainsTable().accept(this);
                break;
            }

            case openrowset:{
                node.getOpenRowSet().accept(this);
                break;
            }

            case openxml:{
                node.getOpenXML().accept(this);
                break;
            }

            case opendatasource:{
                node.getOpenDatasource().accept(this);
                break;
            }

            case openquery:{
                node.getOpenquery().accept(this);
                break;
            }

            case datachangeTable:{
                node.getDatachangeTable().accept(this);
                break;
            }
            case rowList:{
                node.getValueClause().accept(this);
                break;
            }
            case pivoted_table:{
                node.getPivotedTable().accept(this);
                break;
            }
            case xmltable:{
                node.getXmlTable().accept(this);
                break;
            }

            case informixOuter:{
                node.getOuterClause().accept(this);
                break;
            }

            case table_ref_list:{
                node.getFromTableList().accept(this);
                break;
            }
            case hiveFromQuery:{
                node.getHiveFromQuery().accept(this);
                break;
            }
            case output_merge:{
                node.getOutputMerge().accept(this);
                break;
            }
            default:
                sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
                break;

        }


        if (node.getAliasClause() != null){
            node.getAliasClause().accept(this);
        }

        if (node.getTableHintList() != null){
            appendStartTag("tablehints");
            for(int i=0;i<node.getTableHintList().size();i++){
                TTableHint tableHint = node.getTableHintList().getElement(i);
                tableHint.accept(this);
            }
            appendEndTag("tablehints");
        }
    }
    public void postVisit(TTable node){
        appendEndTag(node);
    }

    public void preVisit(TTableHint node){
        appendStartTag(node);
        sb.append(node.toString());
    }
    public void postVisit(TTableHint node){
        appendEndTag(node);
    }

    public void preVisit(TObjectName node){
        appendStartTagWithIntProperty(node,"type",node.getObjectType());
        sb.append(node.toString());
    }
    public void postVisit(TObjectName node){
        appendEndTag(node);
    }
    public void preVisit(TObjectNameList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getObjectName(i).accept(this);
        }
    }
    public void postVisit(TObjectNameList node){
        appendEndTag(node);
    }

    public void preVisit(TWhereClause node){
        appendStartTag(node);
        node.getCondition().accept(this);
    }
    public void postVisit(TWhereClause node){
        appendEndTag(node);
    }

    public void preVisit(THierarchical node){
        appendStartTag(node);
        if (node.getConnectByClause() != null){
            appendStartTag("connect_by_clause");
            node.getConnectByClause().accept(this);
            appendEndTag("connect_by_clause");
        }
        
        if (node.getStartWithClause() != null){
            appendStartTag("start_with_clause");
            node.getStartWithClause().accept(this);
            appendEndTag("start_with_clause");
        }

    }
    public void postVisit(THierarchical node){
        appendEndTag(node);
    }

    public void preVisit(TGroupBy node){
        appendStartTag(node);
        if (node.getItems() != null){
            node.getItems().accept(this);
        }
        if (node.getHavingClause() != null){
            appendStartTag("haveing_clause");
            node.getHavingClause().accept(this);
            appendEndTag("haveing_clause");
        }
    }
    public void postVisit(TGroupBy node){
        appendEndTag(node);
    }
    public void preVisit(TGroupByItem node){
        appendStartTag(node);
        //sb.append(node.toString());
        if (node.getExpr() != null){
            node.getExpr().accept(this);
        }
    }
    public void postVisit(TGroupByItem node){
        appendEndTag(node);
    }
    public void preVisit(TGroupByItemList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getGroupByItem(i).accept(this);
        }
    }
    public void postVisit(TGroupByItemList node){
        appendEndTag(node);
    }

    public void preVisit(TOrderBy node){
        appendStartTag(node);
        node.getItems().accept(this);
    }
    public void postVisit(TOrderBy node){
        appendEndTag(node);
    }
    public void preVisit(TOrderByItem node){
        appendStartTag(node);
        //sb.append(node.toString()) ;
        if (node.getSortKey() != null){
            node.getSortKey().accept(this);
        }
    }
    public void postVisit(TOrderByItem node){
        appendEndTag(node);
    }
    public void preVisit(TOrderByItemList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getOrderByItem(i).accept(this);
        }
    }
    public void postVisit(TOrderByItemList node){
        appendEndTag(node);
    }

    public void preVisit(TForUpdate node){
        appendStartTag(node);
        node.getColumnRefs().accept(this);
    }
    public void postVisit(TForUpdate node){
        appendEndTag(node);
    }

    public void preVisit(TStatementList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.get(i).accept(this);
        }
        //appendStartTagWithIntProperty(node,"size",node.size());
    }
    public void postVisit(TStatementList node){
        appendEndTag(node);
    }

    void doDeclare_Body_Exception(TCommonStoredProcedureSqlStatement node){
       
        if (node.getDeclareStatements() != null){
            appendStartTag("declare");
            node.getDeclareStatements().accept(this);
            appendEndTag("declare");
        }

        if (node.getBodyStatements() != null){
            appendStartTag("body");
            node.getBodyStatements().accept(this);
            appendEndTag("body");
        }

        if (node.getExceptionClause() != null){
            node.getExceptionClause().accept(this);
        }

    }
    public void preVisit(TPlsqlCreatePackage node){
        appendStartTag(node);
        //doDeclare_Body_Exception(node);
        if (node.getParameterDeclarations() != null) node.getParameterDeclarations().accept(this);
        if ( node.getBodyStatements().size() > 0) node.getBodyStatements().accept(this);
        if (node.getExceptionClause() != null) node.getExceptionClause().accept(this);

    }
    public void postVisit(TPlsqlCreatePackage node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateProcedure node){
        appendStartTag(node);
        //doDeclare_Body_Exception(node);
        if (node.getParameterDeclarations() != null) node.getParameterDeclarations().accept(this);
        if (node.getInnerStatements().size() > 0) node.getInnerStatements().accept(this);
        if (node.getDeclareStatements().size() > 0) node.getDeclareStatements().accept(this);
        if ( node.getBodyStatements().size() > 0) node.getBodyStatements().accept(this);
        if (node.getExceptionClause() != null) node.getExceptionClause().accept(this);
    }
    public void postVisit(TPlsqlCreateProcedure node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateFunction node){
        appendStartTag(node);
        //doDeclare_Body_Exception(node);
        if (node.getParameterDeclarations() != null) node.getParameterDeclarations().accept(this);
        if (node.getDeclareStatements().size() > 0) node.getDeclareStatements().accept(this);
        if ( node.getBodyStatements().size() > 0) node.getBodyStatements().accept(this);
        if (node.getExceptionClause() != null) node.getExceptionClause().accept(this);

    }
    public void postVisit(TPlsqlCreateFunction node){
        appendEndTag(node);
    }
    public void preVisit(TCommonBlock node){
        appendStartTag(node);
       // doDeclare_Body_Exception(node);
        if (node.getDeclareStatements().size() > 0) node.getDeclareStatements().accept(this);
        if (node.getBodyStatements().size() > 0 ) node.getBodyStatements().accept(this);

    }
    public void postVisit(TCommonBlock node){
        appendEndTag(node);
    }

    public void preVisit(TExceptionClause node){
        appendStartTag(node);
        node.getHandlers().accept(this);
    }
    public void postVisit(TExceptionClause node){
        appendEndTag(node);
    }
    public void preVisit(TExceptionHandler node){
        appendStartTag(node);
        if (node.getExceptionNames() != null){
            node.getExceptionNames().accept(this);
        }
        node.getStatements().accept(this);
    }
    public void postVisit(TExceptionHandler node){
        appendEndTag(node);
    }
    public void preVisit(TExceptionHandlerList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getExceptionHandler(i).accept(this);
        }
    }
    public void postVisit(TExceptionHandlerList node){
        appendEndTag(node);
    }
    public void preVisit(TAlterTableStatement stmt){
        appendStartTagWithIntProperty(stmt,"name",stmt.getTableName().toString());
        if (stmt.getAlterTableOptionList() != null){
            for(int i=0;i<stmt.getAlterTableOptionList().size();i++){
                stmt.getAlterTableOptionList().getAlterTableOption(i).accept(this);
            }
        }

        if (stmt.getMySQLTableOptionList() != null){
            stmt.getMySQLTableOptionList().accept(this);
        }

    }
    public void postVisit(TAlterTableStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TTypeName node){
       appendStartTagWithIntProperty(node,"type",node.getType());
       sb.append(node.toString());
    }
    public void postVisit(TTypeName node){
        appendEndTag(node);
    }

    public void preVisit(TColumnDefinition node){
        appendStartTagWithIntProperty(node,"null",(node.isNull())? 1:0,"name",node.getColumnName().toString());
        if (node.getDatatype() != null){
            node.getDatatype().accept(this);
        }
        if (node.getDefaultExpression() != null){
            node.getDefaultExpression().accept(this);
        }
        if (node.getConstraints() != null){
            node.getConstraints().accept(this);
        }
    }
    public void postVisit(TColumnDefinition node){
        appendEndTag(node);
    }

    public void preVisit(TColumnDefinitionList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getColumn(i).accept(this);
        }
    }
    public void postVisit(TColumnDefinitionList node){
        appendEndTag(node);
    }

    public void preVisit(TMergeWhenClause node){
        appendStartTag(node);
        if (node.getCondition() != null){
            node.getCondition().accept(this);
        }

        if (node.getUpdateClause() != null){
            node.getUpdateClause().accept(this);
        }

        if (node.getInsertClause() != null){
            node.getInsertClause().accept(this);
        }

        if (node.getDeleteClause() != null){
            node.getDeleteClause().accept(this);
        }

    }
    public void postVisit(TMergeWhenClause node){
        appendEndTag(node);
    }

    public void preVisit(TMergeUpdateClause node){
        appendStartTag(node);
        if (node.getUpdateColumnList() != null){
            node.getUpdateColumnList().accept(this);
        }

        if (node.getUpdateWhereClause() != null){
            node.getUpdateWhereClause().accept(this);
        }

        if (node.getDeleteWhereClause() != null){
            node.getDeleteWhereClause() .accept(this);
        }

    }
    public void postVisit(TMergeUpdateClause node){
        appendEndTag(node);
    }

    public void preVisit(TMergeInsertClause node){
        appendStartTag(node);
        if (node.getColumnList() != null){
             node.getColumnList().accept(this);
        }

        if (node.getValuelist() != null){
            node.getValuelist().accept(this);
        }

        if (node.getInsertWhereClause() != null){
            node.getInsertWhereClause().accept(this);
        }

    }
    public void postVisit(TMergeInsertClause node){
        appendEndTag(node);
    }

    public void preVisit(TConstraint node){
        appendStartTagWithIntProperty(node,"type",node.getConstraint_type().toString(),"name",(node.getConstraintName() != null) ? node.getConstraintName().toString():"");
        switch(node.getConstraint_type()){
            case notnull:
                break;
            case unique:
                if (node.getColumnList() != null){
                    node.getColumnList().accept(this);
                }
                break;
            case check:
                node.getCheckCondition().accept(this);
                break;
            case primary_key:
                if (node.getColumnList() != null){
                    node.getColumnList().accept(this);
                }
                break;
            case foreign_key:
                if (node.getColumnList() != null){
                    node.getColumnList().accept(this);
                }
                if (node.getReferencedObject() != null){
                    node.getReferencedObject().accept(this);
                }
                if (node.getReferencedColumnList() != null){
                    node.getReferencedColumnList().accept(this);
                }
                break;
            case reference:
                node.getReferencedObject().accept(this);
                node.getReferencedColumnList().accept(this);
                break;
            default:
                break;
        }
    }
    public void postVisit(TConstraint node){
        appendEndTag(node);
    }
    public void preVisit(TConstraintList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getConstraint(i).accept(this);
        }

    }
    public void postVisit(TConstraintList node){
        appendEndTag(node);
    }

    public void preVisit(TCreateViewSqlStatement stmt){
        appendStartTagWithIntProperty(stmt, "name", stmt.getViewName().toString());
        if (stmt.getViewAliasClause() != null) stmt.getViewAliasClause().accept(this);
        stmt.getSubquery().accept(this);
    }
    public void postVisit(TCreateViewSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TMssqlDeclare stmt){
        appendStartTagWithIntProperty(stmt,"type",stmt.getDeclareType().toString());
        if (stmt.getDeclareType() == EDeclareType.variable){
            stmt.getVariables().accept(this);
        }
        if (stmt.getSubquery() != null) stmt.getSubquery().accept(this);
    }
    public void postVisit(TMssqlDeclare stmt){
        appendEndTag(stmt);
    }


    public void preVisit(TMssqlSet stmt){
        appendStartTagWithIntProperty(stmt,"type",stmt.getSetType());
        if (stmt.getSetType() == TBaseType.mstLocalVar){

            appendStartTagWithIntProperty(stmt,
                    "variableName",
                    stmt.getVarName().toString(),
                    "value",
                    stmt.getVarExpr().toString());

        }
    }
    public void postVisit(TMssqlSet stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TMergeSqlStatement stmt){
        appendStartTagWithIntProperty(stmt,"tableName",stmt.getTargetTable().toString());
        stmt.getUsingTable().accept(this);
        stmt.getCondition().accept(this);
        if (stmt.getColumnList() != null) stmt.getColumnList().accept(this);
        if (stmt.getWhenClauses() != null) stmt.getWhenClauses().accept(this);
        if (stmt.getOutputClause() != null) stmt.getOutputClause().accept(this);
        if (stmt.getErrorLoggingClause() != null) stmt.getErrorLoggingClause().accept(this);

    }
    public void postVisit(TMergeSqlStatement stmt){
        appendEndTag(stmt);
    }


    public void preVisit(TCreateIndexSqlStatement stmt){
        appendStartTagWithIntProperty(stmt,"name",stmt.getIndexName().toString());
    }
    public void postVisit(TCreateIndexSqlStatement stmt){
        appendEndTag(stmt);
    }
    public void preVisit(TCreateTableSqlStatement stmt){
       appendStartTagWithIntProperty(stmt,"name",stmt.getTargetTable().toString());
        stmt.getColumnList().accept(this);
        if ((stmt.getTableConstraints() != null)&&(stmt.getTableConstraints().size()>0)){
            stmt.getTableConstraints().accept(this);
        }
        if (stmt.getSubQuery() != null){
            stmt.getSubQuery().accept(this);
        }

    }
    public void postVisit(TCreateTableSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TDropIndexSqlStatement stmt){
        appendStartTagWithIntProperty(stmt,"name",stmt.getIndexName().toString());
    }
    public void postVisit(TDropIndexSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TDropTableSqlStatement stmt){
        appendStartTagWithIntProperty(stmt,"name",stmt.getTableName().toString());
    }
    public void postVisit(TDropTableSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TDropViewSqlStatement stmt){
        appendStartTagWithIntProperty(stmt,"name",stmt.getViewName().toString());
    }
    public void postVisit(TDropViewSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TDeleteSqlStatement stmt){
        appendStartTag(stmt);

        if (stmt.getCteList() != null){
            stmt.getCteList().accept(this);
        }

        if (stmt.getTopClause() != null){
            stmt.getTopClause().accept(this);
        }

        stmt.getTargetTable().accept(this);
        if (stmt.joins.size() > 0){
            stmt.joins.accept(this);
        }

        if (stmt.getOutputClause() != null){
            stmt.getOutputClause().accept(this);
        }

        if (stmt.getWhereClause() != null){
            stmt.getWhereClause().accept(this);
        }

        if (stmt.getReturningClause() != null){
            stmt.getReturningClause().accept(this);
        }

    }
    public void postVisit(TDeleteSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TUpdateSqlStatement stmt){
        appendStartTag(stmt);
        if (stmt.getCteList() != null){
            stmt.getCteList().accept(this);
        }

        if (stmt.getTopClause() != null){
            stmt.getTopClause().accept(this);
        }

        stmt.getTargetTable().accept(this);
        if (stmt.getOutputClause() != null){
            stmt.getOutputClause().accept(this);
        }

        if (stmt.joins.size() > 0){
            stmt.joins.accept(this);
        }

        stmt.getResultColumnList().accept(this);

        if (stmt.getWhereClause() != null){
            stmt.getWhereClause().accept(this);
        }

        if (stmt.getOrderByClause() != null){
            stmt.getOrderByClause().accept(this);
        }

        if (stmt.getLimitClause() != null){
            stmt.getLimitClause().accept(this);
        }

        if (stmt.getReturningClause() != null){
            stmt.getReturningClause().accept(this);
        }

    }
    public void postVisit(TUpdateSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TFunctionCall node){
        appendStartTagWithIntProperty(node,"type",node.getFunctionType().toString()
                ,"name",node.getFunctionName().toString()
                ,"aggregateType",node.getAggregateType().toString());
//        appendStartTagWithIntProperty(node,"aggregateType",node.getAggregateType().toString());

//        if (node.getArgs() != null){
//            appendStartTag("args");
//            node.getArgs().accept(this);
//            appendEndTag("args");

            switch(node.getFunctionType()){
                case unknown_t:
                    if (node.getArgs() != null){
                        node.getArgs().accept(this);
                    }
                    break;
                case udf_t:
                    if (node.getArgs() != null){
                        node.getArgs().accept(this);
                    }
                    if (node.getAnalyticFunction() != null){
                        node.getAnalyticFunction().accept(this);
                    }
                    break;
                case trim_t:
                    if (node.getTrimArgument() != null){
                        node.getTrimArgument().accept(this);
                    }

                    break;
                case cast_t:
                    node.getExpr1().accept(this);
                    break;
                case convert_t:
                    node.getExpr1().accept(this);
                    if (node.getExpr2() != null)
                    { //sql server
                        node.getExpr2().accept(this);
                    }
                    break;
                case extract_t:
                    if (node.getExpr1() != null){
                        node.getExpr1().accept(this);
                    }
                    break;
                case treat_t:
                    node.getExpr1().accept(this);
                    break;
                case contains_t:
                    node.getInExpr().accept(this);
                    node.getExpr1().accept(this);
                    break;
                case freetext_t:
                    node.getInExpr().accept(this);
                    node.getExpr1().accept(this);
                    break;
                case case_n_t:
                    node.getExprList().accept(this);
                    break;
//                case range_n_t:
//                    node.getBetweenExpr().accept(this);
//                    if (node.getExprList() != null){
//                        node.getExprList().accept(this);
//                    }
//                    if ( node.getRangeSize() != null){
//                        node.getRangeSize().accept(this);
//                    }
//                    break;
                case position_t:
                    node.getExpr1().accept(this);
                    node.getExpr2().accept(this);
                    break;
                case substring_t:
                    if (node.getArgs() != null){
                        node.getArgs().accept(this);
                    }
                    if (node.getExpr1() != null) node.getExpr1().accept(this);
                    if (node.getExpr2() != null) node.getExpr2().accept(this);
                    if (node.getExpr3() != null) node.getExpr3().accept(this);
                    break;
                case xmlquery_t:
                case xmlcast_t:
                    break;
                case match_against_t:
                    node.getAgainstExpr().accept(this);
                    break;
                case adddate_t:
                case date_add_t:
                case subdate_t:
                case date_sub_t:
                case timestampadd_t:
                case timestampdiff_t:
                    node.getExpr1().accept(this);
                    node.getExpr2().accept(this);
                    break;
                default:;
            }

    }
    public void postVisit(TFunctionCall node){
        appendEndTag(node);
    }

    public void preVisit(TInsertSqlStatement stmt){
        appendStartTag(stmt);
        if (stmt.getCteList() != null){
            stmt.getCteList().accept(this);
        }

        if (stmt.getTopClause() != null){
            stmt.getTopClause().accept(this);
        }

        if (stmt.getTargetTable() != null){
            stmt.getTargetTable().accept(this);
        }

        if (stmt.getColumnList() != null){
            stmt.getColumnList().accept(this);
        }

        if (stmt.getOutputClause() != null){
            stmt.getOutputClause().accept(this);
        }

        switch(stmt.getValueType()){
            case TBaseType.vt_values:
                stmt.getValues().accept(this);
                break;
            case TBaseType.vt_values_empty:
                break;
            case TBaseType.vt_query:
                stmt.getSubQuery().accept(this);
                break;
            case TBaseType.vt_values_function:
                stmt.getFunctionCall().accept(this);
                break;
            case TBaseType.vt_values_oracle_record:
                stmt.getRecordName().accept(this);
                break;
            case TBaseType.vt_set_column_value:
                stmt.getSetColumnValues().accept(this);
                break;
            default:
                break;
        }

        if (stmt.getReturningClause() != null){
            stmt.getReturningClause().accept(this);
        }

    }
    public void postVisit(TInsertSqlStatement stmt){
        appendEndTag(stmt);
    }

    public void preVisit(TMultiTarget node){
        appendStartTag(node);
        if (node.getColumnList() != null){
            node.getColumnList().accept(this);
        }

        if (node.getSubQuery() != null){
            node.getSubQuery().accept(this);
        }
    }
    public void postVisit(TMultiTarget node){
        appendEndTag(node);
    }
    
    public void preVisit(TMultiTargetList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getMultiTarget(i).accept(this);
        }
    }

    public void postVisit(TMultiTargetList node){
        appendEndTag(node);
    }

    public void preVisit(TCTE node){
        appendStartTagWithIntProperty(node,"name",node.getTableName().toString());
        if (node.getColumnList() != null){
            node.getColumnList().accept(this);
        }
        node.getSubquery().accept(this);
    }
    public void postVisit(TCTE node){
        appendEndTag(node);
    }

    public void preVisit(TCTEList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getCTE(i).accept(this);
        }
    }
    public void postVisit(TCTEList node){
        appendEndTag(node);
    }

    public void preVisit(TAssignStmt node){
        appendStartTag(node);
        outputNodeData(node);
    }
    public void postVisit(TAssignStmt node){
        appendEndTag(node);
    }

    public void preVisit(TIfStmt node){
        appendStartTag(node);
        node.getCondition().accept(this);
        node.getThenStatements().accept(this);
        if (node.getElseifStatements() != null) node.getElseifStatements().accept(this);
        if (node.getElseStatements() != null)  node.getElseStatements().accept(this);

    }
    public void postVisit(TIfStmt node){
        appendEndTag(node);
    }


    public void preVisit(TBasicStmt node){
        appendStartTag(node);
        outputNodeData(node);
    }
    public void postVisit(TBasicStmt node){
        appendEndTag(node);
    }

    public void preVisit(TCaseStmt node){
        appendStartTag(node);
        node.getCaseExpr().accept(this);
    }
    public void postVisit(TCaseStmt node){
        appendEndTag(node);
    }

    public void preVisit(TCaseExpression node){
        appendStartTag(node);
        if (node.getInput_expr() != null){
            appendStartTag("input_expr");
            node.getInput_expr().accept(this);
            appendEndTag("input_expr");
        }

        node.getWhenClauseItemList().accept(this);

        if (node.getElse_expr() != null){
            appendStartTag("else_expr");
            node.getElse_expr().accept(this);
            appendEndTag("else_expr");
        }

        if (node.getElse_statement_list().size() > 0){
            node.getElse_statement_list().accept(this);
        }
    }
    public void postVisit(TCaseExpression node){
        appendEndTag(node);
    }

    public void preVisit(TWhenClauseItemList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getWhenClauseItem(i).accept(this);
        }

    }
    public void postVisit(TWhenClauseItemList node){
        appendEndTag(node);
    }

    public void preVisit(TWhenClauseItem node){
        appendStartTag(node);
        node.getComparison_expr().accept(this);
        if (node.getReturn_expr() != null){
            node.getReturn_expr().accept(this);
        }else if (node.getStatement_list().size() >0){
            node.getStatement_list().accept(this);
        }
    }
    public void postVisit(TWhenClauseItem node){
        appendEndTag(node);
    }


    public void preVisit(TCloseStmt node){
        appendStartTag(node);
    }

    public void postVisit(TCloseStmt node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateTrigger node){
        appendStartTagWithIntProperty(node,"name",node.getTriggerName().toString());
        node.getEventClause().accept(this);
        if (node.getFollowsTriggerList() != null)  node.getFollowsTriggerList().accept(this);
        if (node.getWhenCondition() != null) node.getWhenCondition().accept(this);
        node.getTriggerBody().accept(this);

//        appendStartTag("event_clause");
//        sb.append(node.getEventClause().toString());
//        appendEndTag("event_clause");
//        if (node.getWhenCondition() != null){
//            node.getWhenCondition().accept(this);
//        }
//        appendStartTag("body");
//        node.getTriggerBody().accept(this);
//        appendEndTag("body");
    }

    public void postVisit(TPlsqlCreateTrigger node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateType node){
        appendStartTagWithIntProperty(node,"type",node.getKind(),"name",node.getTypeName().toString());
        if (node.getTypeAttributes() != null){
            node.getTypeAttributes().accept(this);
        }
    }

    public void postVisit(TPlsqlCreateType node){
        appendEndTag(node);
    }


    public void preVisit(TTypeAttribute node){
        appendStartTag(node);
        node.getAttributeName().accept(this);
        node.getDatatype().accept(this);
    }

    public void postVisit(TTypeAttribute node){
        appendEndTag(node);
    }

    public void preVisit(TTypeAttributeList node){
        appendStartTag(node);
        for(int i=0;i<node.size();i++){
            node.getAttributeItem(i).accept(this);
        }
    }

    public void postVisit(TTypeAttributeList node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateType_Placeholder node){
        appendStartTagWithIntProperty(node,"type",node.getKind());
        switch(node.getKind()){
            case TBaseType.kind_create_varray:
                node.getVarrayStatement().accept(this);
                break;
            case TBaseType.kind_create_nested_table:
                node.getNestedTableStatement().accept(this);
                break;
            default:
                node.getObjectStatement().accept(this);
                break;
        }
    }

    public void postVisit(TPlsqlCreateType_Placeholder node){
        appendEndTag(node);
    }

    public void preVisit(TPlsqlCreateTypeBody node){
        appendStartTagWithIntProperty(node,"name",node.getTypeName().toString());
        node.getBodyStatements().accept(this);
    }

    public void postVisit(TPlsqlCreateTypeBody node){
        appendEndTag(node);
    }

    void outputNodeData(TParseTreeNode node){
        sb.append(node.toString());
    }
    
    public void preVisit(TMssqlCommit node){
        if (node.getTransactionName() != null){
         appendStartTagWithIntProperty(node,"transactionName",node.getTransactionName().toString());   
        }else{
        appendStartTag(node);
        }
        sb.append(node.toString());
    }

    public void postVisit(TMssqlCommit node){
        appendEndTag(node);
    }


    public void preVisit(TMssqlRollback node){
        if (node.getTransactionName() != null){
         appendStartTagWithIntProperty(node,"transactionName",node.getTransactionName().toString());
        }else{
        appendStartTag(node);
        }
        sb.append(node.toString());
    }

    public void postVisit(TMssqlRollback node){
        appendEndTag(node);
    }

    public void preVisit(TMssqlSaveTran node){
        if (node.getTransactionName() != null){
         appendStartTagWithIntProperty(node,"transactionName",node.getTransactionName().toString());
        }else{
        appendStartTag(node);
        }
        sb.append(node.toString());
    }

    public void postVisit(TMssqlSaveTran node){
        appendEndTag(node);
    }


    public void preVisit(TMssqlGo node){
        appendStartTag(node);
    }

    public void postVisit(TMssqlGo node){
        appendEndTag(node);
    }

    public void preVisit(TMssqlCreateProcedure node){
        //appendStartTag(node);
        appendStartTagWithIntProperty(node, "procedureName", node.getProcedureName().toString());
        if (node.getParameterDeclarations() != null) node.getParameterDeclarations().accept(this);
        if (node.getBodyStatements().size() > 0) node.getBodyStatements().accept(this);

//        node.getParameterDeclarations().accept(this);
//        appendStartTag("bodyList");
//        node.getBodyStatements().accept(this);
//        appendEndTag("bodyList");
    }

    public void postVisit(TMssqlCreateProcedure node){
        appendEndTag(node);
    }

    public void preVisit(TParameterDeclaration node){
        //appendStartTag(node);
        appendStartTagWithIntProperty(node,
                "parameterName",
                (node.getParameterName() != null)?node.getParameterName().toString():"",
                "parameterType",
                (node.getDataType() != null)?node.getDataType().toString():"");

    }

    public void postVisit(TParameterDeclaration node){
        appendEndTag(node);
    }

    public void preVisit(TDeclareVariable node){
        //appendStartTag(node);
        appendStartTagWithIntProperty(node,
                "variableName",
                node.getVariableName().toString(),
                "dataType",
                (node.getDatatype() != null)?node.getDatatype().toString():"");
    }

    public void postVisit(TDeclareVariable node){
        appendEndTag(node);
    }

    public void preVisit(TDeclareVariableList node){
        //appendStartTag(node);
        appendStartTagWithIntProperty(node,
                "variables",
                node.size());

        for(int i=0;i<node.size();i++){
            node.getDeclareVariable(i) .accept(this);
        }

    }

    public void postVisit(TDeclareVariableList node){
        appendEndTag(node);
    }

    public String getXml(){
        return "<?xml-stylesheet type=\"text/xsl\" href=\"tree-view.xsl\"?>"+crlf
                +"<sqlscript>"+crlf
                +sb.toString()
                +"</sqlscript>";
    }
}
