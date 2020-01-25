package common;

import gudusoft.gsqlparser.nodes.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TObjectName;

/*
* Date: 2010-5-27
* Time: 17:30:20
*/
public class testQualifiedVariable extends TestCase {
    private String sqlfile = null;
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    String variablesInBlock(TCommonBlock pblock){
        String ret="";
        TVarDeclStmt stmt = null;
        TObjectName var = null;
        for(int i=0;i<pblock.getDeclareStatements().size();i++){
            if (pblock.getDeclareStatements().get(i) instanceof TVarDeclStmt){
             stmt = (TVarDeclStmt)pblock.getDeclareStatements().get(i);
                if(stmt.getWhatDeclared() == TVarDeclStmt.whatDeclared_variable){
                    var = stmt.getElementName();
                    ret = var.toString();//System.out.println(var.toString());
                    for(int j=0;j<var.getReferencedObjects().size();j++){
                        //System.out.println(var.getReferencedObjects().getColumnReference(j).toString());
                        ret = ret+","+var.getReferencedObjects().getObjectName(j).toString();
                    }

                }
            }
        }

        return ret;
    }

    public void testDo1(){
        sqlfile =test.gspCommon.BASE_SQL_DIR+ "java/oracle/scope_visibility_plsql_identifier/block_label.sql";
        parser.sqlfilename = sqlfile;
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0) instanceof TCommonBlock);
        TCommonBlock block = (TCommonBlock)parser.sqlstatements.get(0);
        //System.out.println(variablesInBlock(block));
        //System.out.println(variablesInBlock((TCommonBlock)block.getBodyStatements().get(0)));
        assertTrue("birthdate,outer.birthdate".compareToIgnoreCase(variablesInBlock(block)) == 0);
        assertTrue("birthdate,birthdate,birthdate".compareToIgnoreCase(variablesInBlock((TCommonBlock)block.getBodyStatements().get(0))) == 0);
    }
    
}
