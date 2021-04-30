package common;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testComment extends TestCase {

    public void test0(){
//        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
//        sqlparser.sqltext = "select * from t1;\n" +
//                "--this is the query used for analysis\n" +
//                "select * from t2;";

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT last_name,                    -- select the name\n" +
                "    salary + NVL(commission_pct, 0),-- total compensation\n" +
                "    job_id,                         -- job\n" +
                "    e.department_id                 -- and department\n" +
                "  FROM employees e,                 -- of all employees\n" +
                "       departments d\n" +
                "  WHERE e.department_id = d.department_id\n" +
                "    AND salary + NVL(commission_pct, 0) >  -- whose compensation \n" +
                "                                           -- is greater than\n" +
                "      (SELECT salary + NVL(commission_pct,0)  -- the compensation\n" +
                "    FROM employees \n" +
                "    WHERE last_name = 'Pataballa')        -- of Pataballa.";


        assertTrue(sqlparser.parse() == 0);

        // fetch all comments
        for(int i=0;i<sqlparser.getSourcetokenlist().size();i++){
            TSourceToken st = sqlparser.getSourcetokenlist().get(i);
            if ((st.tokentype == ETokenType.ttsimplecomment)||(st.tokentype == ETokenType.ttbracketedcomment)){
               // System.out.println(st.toString());
            }
        }


        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
            analyzeStmt(sqlStatement);
        }

    }

    void analyzeStmt( TCustomSqlStatement stmt ){
       // System.out.println(stmt.sqlstatementtype);
        selectItemVisitor itemVisitor = new selectItemVisitor();
        stmt.acceptChildren(itemVisitor);

        for ( int i = 0; i < stmt.getStatements( ).size( ); i++ )
        {
            analyzeStmt( stmt.getStatements( ).get( i ) );
        }
    }
}

class selectItemVisitor extends TParseTreeVisitor {

    public void preVisit(TResultColumn node){
       // System.out.println("--> select item: " + node.toString());
        TSourceToken endToken = node.getEndToken();
        TSourceToken commentToken =  searchComment(endToken);
        if (commentToken != null){
            // System.out.println("comment: "+commentToken.toString());
        }
    }

    public void preVisit(TSelectSqlStatement node){
        // System.out.println("--> select item: " + node.toString());
        TSourceToken endToken = node.getEndToken();
        TSourceToken commentToken =  searchComment(endToken);
        if (commentToken != null){
            // System.out.println("comment: "+commentToken.toString());
        }
    }

    TSourceToken searchComment(TSourceToken currentToken){
        // check next solid token to see whether it is a comment
        TSourceToken resultToken = null;
        TSourceToken st = currentToken.nextSolidToken(true);
        if (st != null){
            if ((st.tokentype == ETokenType.ttsimplecomment)||(st.tokentype == ETokenType.ttbracketedcomment)){
                resultToken = st;
            }else if (st.tokencode == ','){
                resultToken = searchComment(st);
            }
        }

        return resultToken;
    }
}
