package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.TLateralView;
import gudusoft.gsqlparser.nodes.hive.THiveTablePartition;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TExecImmeStmt;

import java.io.File;
import java.io.IOException;

public class searchNode {
    public static void main(String args[]) throws IOException
    {
        long t;

        t = System.currentTimeMillis();

        if (args.length != 1){
            System.out.println("Usage: java searchNode sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvhive;
        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            nodeVisitor nodeVisitor = new nodeVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
                sqlStatement.acceptChildren(nodeVisitor);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class nodeVisitor extends TParseTreeVisitor {
    private int stmtCount = 0;

    public void preVisit(TAlterTableOption node){
        System.out.print("--> node pos"
                +"("+ node.getStartToken().lineNo+","+node.getStartToken().columnNo +"): "
                +node.toString()
                +", \ttype:"+node.getOptionType());

        System.out.println("");
    }

    public void preVisit(TColumnDefinition cd){
        System.out.println("column:"+cd.getColumnName().toString());
    }

    public void preVisit(TIdentityClause identityClause){
        System.out.println("\tidentityClause:"+identityClause.toString());
    }

    public void preVisit(TSequenceOption sequenceOption){
        System.out.println("\t\tsequenceOption:"+sequenceOption.getSequenceOptionType().toString()+" ,value:"+sequenceOption.getOptionValue().toString());
    }

    public void preVisit(TLateralView node){

        if (node.getUdtf() != null){
            System.out.println(node.getUdtf().toString());
        }

        if (node.getTableAlias() != null){
            System.out.println(node.getTableAlias().toString());
        }

        if (node.getColumnAliasList() != null){
            for(TObjectName columnAlais:node.getColumnAliasList()){
                System.out.println(columnAlais.toString());
            }
        }
    }

    public void preVisit(THiveTablePartition node) {
        for(TColumnDefinition columnDefinition : node.getColumnDefList()){
            System.out.println("Columns in partition:");
            System.out.print("\tname:"+columnDefinition.getColumnName().toString());
            System.out.println(",type:"+columnDefinition.getDatatype().toString());
        }
    }

    public void preVisit(TExpression node) {
        if (node.getExpressionType() == EExpressionType.in_t){
            System.out.println(node.toString()+"\t"+node.getLocation());
            if (node.getNotToken() != null){
                System.out.println(node.getNotToken());
            }
        }
    }

    public void preVisit(TExecImmeStmt node) {
        //System.out.println("\n"+(++stmtCount)+" Statement:\t"+node.sqlstatementtype);
        System.out.println(node.getDynamicSQL()+";\n");
    }

    public void preVisit(TSelectSqlStatement node) {
//        System.out.println("\n"+(++stmtCount)+" Statement:\t"+node.sqlstatementtype);
//        System.out.println(node.getTables().getTable(0).toString());
    }


}
