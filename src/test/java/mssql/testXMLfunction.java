package test.mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testXMLfunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT    p.Demographics.value('declare namespace awns=\"http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/IndividualSurvey\"; (awns:IndividualSurvey/awns:NumberCarsOwned) [1]',\n" +
                "                       'int') AS NumberCarsOwned,db1.schema1.func1(arg1)\n" +
                "FROM         Sales.Customer AS c INNER JOIN\n" +
                "                      Person.Person AS p ON p.BusinessEntityID = c.PersonID INNER JOIN\n" +
                "                      Person.BusinessEntityAddress AS a ON a.BusinessEntityID = p.BusinessEntityID INNER JOIN\n" +
                "                      Person.AddressType AS t ON a.AddressTypeID = t.AddressTypeID INNER JOIN\n" +
                "                      Person.Address AS ad ON ad.AddressID = a.AddressID INNER JOIN\n" +
                "                      Person.EmailAddress AS ea ON ea.BusinessEntityID = p.BusinessEntityID INNER JOIN\n" +
                "                      Person.StateProvince AS sp ON sp.StateProvinceID = ad.StateProvinceID\n" +
                "WHERE     (c.StoreID IS NULL) AND (t.Name = N'Home') AND (sp.CountryRegionCode = N'US')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = selectSqlStatement.getResultColumnList().getResultColumn(0);
        TExpression expression = column.getExpr();
        TFunctionCall functionCall = expression.getFunctionCall();
        //System.out.println(functionCall.getFunctionName().getObjectToken().toString());
        assertTrue(functionCall.getFunctionName().getObjectToken().getDbObjType() == TObjectName.ttobjFunctionName);

        TExpressionCallTarget   callTarget = functionCall.getCallTarget();
        assertTrue(callTarget.getExpr().toString().equalsIgnoreCase("p.Demographics"));

//        //System.out.println(functionCall.getFunctionName().getSchemaToken().toString());
//        assertTrue(functionCall.getFunctionName().getSchemaToken().getDbObjType() == TObjectName.ttobjColumn);
//        //System.out.println(functionCall.getFunctionName().getDatabaseToken().toString());
//        assertTrue((functionCall.getFunctionName().getDatabaseToken().getDbObjType() == TObjectName.ttobjTable)
//        ||(functionCall.getFunctionName().getDatabaseToken().getDbObjType() == TObjectName.ttObjTableAlias));


        TResultColumn column1 = selectSqlStatement.getResultColumnList().getResultColumn(1);
        TExpression expression1 = column1.getExpr();
        TFunctionCall functionCall1 = expression1.getFunctionCall();
        //System.out.println(functionCall1.getFunctionName().getObjectToken().toString());
        assertTrue(functionCall1.getFunctionName().getObjectToken().getDbObjType() == TObjectName.ttobjFunctionName);
       //System.out.println(functionCall1.getFunctionName().getSchemaString().toString());
        assertTrue(functionCall1.getFunctionName().getSchemaToken().getDbObjType() == TObjectName.ttobjSchemaName);
       // System.out.println(functionCall1.getFunctionName().getDatabaseToken().toString());
        assertTrue((functionCall1.getFunctionName().getDatabaseToken().getDbObjType() == TObjectName.ttobjDatabaseName));

    }

}
