package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import java.io.IOException;

public class searchColumnInResultColumn {

    public static void main(String args[]) throws IOException
    {
        long t = System.currentTimeMillis( );


        EDbVendor dbVendor = EDbVendor.dbvmssql;
        System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

        TGSqlParser sqlparser = new TGSqlParser( dbVendor );
        sqlparser.sqltext = "SELECT\n" +
                "    (e.first_name + 100) AS test,\n" +
                "     (e.last_name + 200) AS test2\n" +
                "FROM\n" +
                "    employees e;";


        int ret = sqlparser.parse();
        if (ret == 0){
            RCVisitor rcVisitor = new RCVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                for(int j=0;j<sqlStatement.getTables().size();j++){
                    TTable tbl = sqlStatement.getTables().getTable(j);
                    for(int k=0;k<tbl.getLinkedColumns().size();k++){
                        TObjectName objectName = tbl.getLinkedColumns().getObjectName(k);
                        System.out.println("Search: "+objectName.toString());
                        rcVisitor.setTargetColumn(objectName);
                        sqlStatement.acceptChildren(rcVisitor);
                    }
                }
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}

class RCVisitor extends TParseTreeVisitor {
    private TObjectName targetColumn;

    public void setTargetColumn(TObjectName targetColumn) {
        this.targetColumn = targetColumn;
    }

    public void preVisit(TResultColumn node) {

        if ((node.getStartToken().posinlist < targetColumn.getStartToken().posinlist)
                &&(node.getEndToken().posinlist > targetColumn.getEndToken().posinlist)){
            System.out.println("Found:" + targetColumn.toString() + " in "+node.toString());
        }
    }
}