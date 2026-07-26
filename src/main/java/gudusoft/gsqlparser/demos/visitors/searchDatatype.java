package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TTypeName;

import java.io.File;
import java.io.IOException;

public class searchDatatype {
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

        EDbVendor dbVendor = EDbVendor.dbvmysql;
        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            datatypeVisitor datatypeVisitor = new datatypeVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
                sqlStatement.acceptChildren(datatypeVisitor);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class datatypeVisitor extends TParseTreeVisitor {
    public void preVisit(TTypeName node){
        System.out.print("--> full name"
                +"("+ node.getStartToken().lineNo+","+node.getStartToken().columnNo +"): "
                +node.toString()+", \tshort name:"+node.getDataTypeName()
                +", \ttype:"+node.getDataType());
        if (node.getLength() != null){
            System.out.print(",\tlength:"+node.getLength().toString());
        }
        if (node.getPrecision() != null){
            System.out.print(",\tprecision:"+node.getPrecision().toString());
        }
        if (node.getScale() != null){
            System.out.print(",\tscale:"+node.getScale().toString());
        }
        System.out.println("");
    }
}
