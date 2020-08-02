package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TTypeName;

import java.io.File;
import java.io.IOException;

public class searchNode {
    public static void main(String args[]) throws IOException
    {
        long t;

        t = System.currentTimeMillis();

        if (args.length != 1){
            System.out.println("Usage: java searchDatatype sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvpostgresql;
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
    public void preVisit(TAlterTableOption node){
        System.out.print("--> node pos"
                +"("+ node.getStartToken().lineNo+","+node.getStartToken().columnNo +"): "
                +node.toString()
                +", \ttype:"+node.getOptionType());

        System.out.println("");
    }
}
