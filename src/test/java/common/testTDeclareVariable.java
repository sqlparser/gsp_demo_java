package common;
/*
 * Date: 2010-11-16
 * Time: 16:16:51
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.nodes.TTableElementList;
import gudusoft.gsqlparser.nodes.TTableElement;

public class testTDeclareVariable extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @MyTableVar table(\n" +
                "    EmpID int NOT NULL,\n" +
                "    OldVacationHours int,\n" +
                "    NewVacationHours int,\n" +
                "    ModifiedDate datetime);";
        assertTrue(sqlparser.parse() == 0);

        TMssqlDeclare declare = (TMssqlDeclare)sqlparser.sqlstatements.get(0);
        
        TDeclareVariable node = declare.getVariables().getDeclareVariable(0);
        assertTrue(node.getVariableType() == TBaseType.declare_varaible_table);

        TTableElementList elements = node.getTableTypeDefinitions();
        assertTrue(elements.size() == 4);
        TTableElement element0 = elements.getTableElement(0);
        assert(element0.getType() == TTableElement.type_column_def);
        assert(element0.getColumnDefinition().getColumnName().toString().equalsIgnoreCase("EmpID"));

        TTableElement element3 = elements.getTableElement(3);
        assert(element3.getType() == TTableElement.type_column_def);
        assert(element3.getColumnDefinition().getDatatype().toString().equalsIgnoreCase("datetime"));
    }

}
