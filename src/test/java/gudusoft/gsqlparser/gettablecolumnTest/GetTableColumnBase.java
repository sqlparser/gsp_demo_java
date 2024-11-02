package gudusoft.gsqlparser.gettablecolumnTest;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;

public class GetTableColumnBase {

    static boolean doTest(EDbVendor dbVendor, String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(dbVendor);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());

        return TBaseType.compareStringsLineByLine(getTableColumn.outList.toString().trim(), desireResult);
    }

}
