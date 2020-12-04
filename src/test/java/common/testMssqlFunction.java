package common;
/*
 * Date: 2010-8-30
 * Time: 16:08:03
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateFunction;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;

public class testMssqlFunction extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "CREATE FUNCTION dbo.ufn_FindReports (@InEmpID INTEGER)\n" +
                "RETURNS @retFindReports TABLE \n" +
                "(\n" +
                "    EmployeeID int primary key NOT NULL,\n" +
                "    FirstName nvarchar(255) NOT NULL,\n" +
                "    LastName nvarchar(255) NOT NULL,\n" +
                "    JobTitle nvarchar(50) NOT NULL,\n" +
                "    RecursionLevel int NOT NULL\n" +
                ")\n" +
                "--Returns a result set that lists all the employees who report to the \n" +
                "--specific employee directly or indirectly.*/\n" +
                "AS\n" +
                "BEGIN\n" +
                "WITH EMP_cte(EmployeeID, OrganizationNode, FirstName, LastName, JobTitle, RecursionLevel) -- CTE name and columns\n" +
                "    AS (\n" +
                "        SELECT e.BusinessEntityID, e.OrganizationNode, p.FirstName, p.LastName, e.JobTitle, 0 -- Get the initial list of Employees for Manager n\n" +
                "        FROM HumanResources.Employee e \n" +
                "\t\t\tINNER JOIN Person.Person p \n" +
                "\t\t\tON p.BusinessEntityID = e.BusinessEntityID\n" +
                "        WHERE e.BusinessEntityID = @InEmpID\n" +
                "        UNION ALL\n" +
                "        SELECT e.BusinessEntityID, e.OrganizationNode, p.FirstName, p.LastName, e.JobTitle, RecursionLevel + 1 -- Join recursive member to anchor\n" +
                "        FROM HumanResources.Employee e \n" +
                "            INNER JOIN EMP_cte\n" +
                "            ON e.OrganizationNode.GetAncestor(1) = EMP_cte.OrganizationNode\n" +
                "\t\t\tINNER JOIN Person.Person p \n" +
                "\t\t\tON p.BusinessEntityID = e.BusinessEntityID\n" +
                "        )\n" +
                "-- copy the required columns to the result of the function \n" +
                "   INSERT @retFindReports\n" +
                "   SELECT EmployeeID, FirstName, LastName, JobTitle, RecursionLevel\n" +
                "   FROM EMP_cte \n" +
                "   RETURN\n" +
                "END;";
        assertTrue((parser.parse() == 0));
        TMssqlCreateFunction createFunction = (TMssqlCreateFunction)parser.sqlstatements.get(0);
        //System.out.println(createFunction.getFunctionName().toString());
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("dbo.ufn_FindReports"));
        assertTrue(createFunction.getParameterDeclarations().size() == 1);
        TParameterDeclaration parameter1 = createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameter1.getParameterName().toString().equalsIgnoreCase("@InEmpID"));
        assertTrue(createFunction.getReturnMode() == TBaseType.function_return_table_variable);
        assertTrue(createFunction.getReturnTableVaraible().toString().equalsIgnoreCase("@retFindReports"));
        assertTrue(createFunction.getReturnTableDefinitions().size() == 5);
        TTableElement te = createFunction.getReturnTableDefinitions().getTableElement(4);
        TColumnDefinition cd = te.getColumnDefinition();
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("RecursionLevel"));
        assertTrue(cd.getDatatype().toString().equalsIgnoreCase("int"));

        TConstraintList clist = cd.getConstraints();
        TConstraint c = clist.getConstraint(0);
        assertTrue(c.getConstraint_type() == EConstraintType.notnull);

        TMssqlBlock b = createFunction.getBlock();
        assertTrue(b.getBodyStatements().size() == 2);
        assertTrue(b.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(b.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlreturn);
        //TCustomSqlStatement stmt1 = b.getBodyStatements().get(0);
        //TCustomSqlStatement stmt2 = b.getBodyStatements().get(1);



    }    
}
