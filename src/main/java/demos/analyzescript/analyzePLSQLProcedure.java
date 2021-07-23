package demos.analyzescript;
/*
 * Date: 2010-12-10
 * Time: 16:24:30
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.TIfStmt;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TVarDeclStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;

public class analyzePLSQLProcedure {

    public static void main(String args[])
     {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR replace PROCEDURE Db_mdw_trt_elt_prel_cond ( p_param_call IN pkg_mdw_def_general.param_call, p_trt IN VARCHAR2 ) IS\n" +
                "\n" +
                "w_nom_ekapi VARCHAR2(255);\n" +
                "w_msg pkg_mdw_def_general.param_msg;\n" +
                "trt_error EXCEPTION;\n" +
                "\n" +
                "BEGIN\n" +
                "\n" +
                "w_nom_ekapi := 'PKG_EKI_ELT_PREL_COND';\n" +
                "w_code_retour := p_code_retour;\n" +
                "w_nom_proc := 'PKG_MDW_ELT_PREL_COND_ALIM.INIT_TCONDPREL';\n" +
                "\n" +
                "IF w_existe = 'N' THEN\n" +
                "w_nom_proc := 'INSERT:POOLPREL';\n" +
                "END IF;\n" +
                "END db_mdw_trt_elt_prel_cond;";

        int ret = sqlparser.parse();
         if (ret != 0){
             System.out.println(sqlparser.getErrormessage());
             return;
         }

         TCustomSqlStatement sql = sqlparser.sqlstatements.get(0);
         System.out.println("SQL Statement: " + sql.sqlstatementtype);

         TPlsqlCreateProcedure procedure  = (TPlsqlCreateProcedure)sql;
         System.out.println("Procedure name: "+procedure.getProcedureName().toString());
         System.out.println("Parameters:");

         TParameterDeclaration param = null;
         for(int i=0;i<procedure.getParameterDeclarations().size();i++){
              param = procedure.getParameterDeclarations().getParameterDeclarationItem(i);
             System.out.println("\tName:"+param.getParameterName().toString());
             System.out.println("\tDatatype:"+param.getDataType().toString());
             System.out.println("\tIN/OUT:"+param.getMode());
         }

         TStatementList declareStatements = procedure.getDeclareStatements();
         System.out.println("declare statements: " + declareStatements.size());
         TCustomSqlStatement declareStatement = null;
         TVarDeclStmt variableDelcare = null;
         for(int i = 0; i< declareStatements.size();i++){
             declareStatement = declareStatements.get(i);
             System.out.println("SQL Statement: " + declareStatement.sqlstatementtype);
             variableDelcare = (TVarDeclStmt)declareStatement;

             switch(variableDelcare.getWhatDeclared()){
                 case  TVarDeclStmt.whatDeclared_variable:
                     System.out.println("\tVariable Name:"+variableDelcare.getElementName().toString());
                     System.out.println("\tVariable Datatype:"+variableDelcare.getDataType().toString());
                     break;
                 case TVarDeclStmt.whatDeclared_constant:
                     break;
                 case TVarDeclStmt.whatDeclared_exception:
                     System.out.println("\tException:"+variableDelcare.getElementName().toString());
                     break;
                 case TVarDeclStmt.whatDeclared_subtype:
                     break;
                 case TVarDeclStmt.whatDeclared_pragma_autonomous_transaction:
                     break;
                 case TVarDeclStmt.whatDeclared_pragma_exception_init:
                     break;
                 case TVarDeclStmt.whatDeclared_pragma_serially_reusable:
                     break;
                 case TVarDeclStmt.whatDeclared_pragma_restrict_references:
                     break;
                 case TVarDeclStmt.whatDeclared_pragma_timestamp:
                     break;
             }
         }

         TStatementList bodyStatements = procedure.getBodyStatements();
         System.out.println("body statements: " + bodyStatements.size());
         TCustomSqlStatement bodyStatement = null;
         for(int i=0;i<bodyStatements.size();i++){
           bodyStatement = bodyStatements.get(i);
           System.out.println("SQL Statement: " + bodyStatement.sqlstatementtype);
           switch(bodyStatement.sqlstatementtype){
               case sst_assignstmt:
                   TAssignStmt assign = (TAssignStmt)bodyStatement;
                   System.out.println("left: "+assign.getLeft().toString());
                   System.out.println("right: "+assign.getExpression().toString());
                   break;
               case sst_ifstmt:
                   TIfStmt ifstmt = (TIfStmt)bodyStatement;
                   System.out.println("condition: "+ ifstmt.getCondition().toString());

                   if (ifstmt.getThenStatements().size() > 0){
                    System.out.println("then statement:");
                    for(int j=0;j<ifstmt.getThenStatements().size();j++){
                        System.out.println("\tStatement type: "+ifstmt.getThenStatements().get(j).sqlstatementtype);
                        System.out.println("\tStatement text: "+ifstmt.getThenStatements().get(j).toString());
                    }
                   }

                   if (ifstmt.getElseifStatements().size() > 0){
                    System.out.println("else if statement:");
                    for(int j=0;j<ifstmt.getElseifStatements().size();j++){
                        System.out.println("\tStatement type: "+ifstmt.getElseifStatements().get(j).sqlstatementtype);
                        System.out.println("\tStatement text: "+ifstmt.getElseifStatements().get(j).toString());
                    }
                   }

                   if (ifstmt.getElseStatements().size() > 0){
                    System.out.println("else statement:");
                    for(int j=0;j<ifstmt.getElseStatements().size();j++){
                        System.out.println("\tStatement type: "+ifstmt.getElseStatements().get(j).sqlstatementtype);
                        System.out.println("\tStatement text: "+ifstmt.getElseStatements().get(j).toString());
                    }
                   }

                   break;
               default:
                   break;
           }
         }

     }

}