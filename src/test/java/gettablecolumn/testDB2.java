package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testDB2 extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvdb2);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testCreateFunction() {
        doTest("CREATE FUNCTION \"F_GetRFECustomerName\" ( p_date DATE,p_officecode CHAR(7),p_cifcode CHAR(8) )\n" +
                        "  RETURNS VARCHAR(1024)\n" +
                        "\n" +
                        "LANGUAGE SQL\n" +
                        "  NOT DETERMINISTIC\n" +
                        "  EXTERNAL ACTION\n" +
                        "  READS SQL DATA\n" +
                        "  CALLED ON NULL INPUT\n" +
                        "  INHERIT ISOLATION LEVEL WITHOUT LOCK REQUEST\n" +
                        "  INHERIT SPECIAL REGISTERS\n" +
                        "\n" +
                        "BEGIN ATOMIC\n" +
                        "\n" +
                        "  DECLARE p_name VARCHAR(1024);\n" +
                        "\n" +
                        "  SELECT\n" +
                        "    CUSTNAMEENG INTO p_name\n" +
                        "  FROM\n" +
                        "    TBD_CUSLISTD_DCH\n" +
                        "  WHERE\n" +
                        "    AS_OF_DATE = p_date;\n" +
                        "\n" +
                        "  RETURN COALESCE(p_name, '');\n" +
                        "\n" +
                        "END;\n" +
                        "\n" +
                        "@\n" +
                        "\n" +
                        "CREATE VIEW VBD_RFE2101OCA_DCH AS\n" +
                        "WITH MAIN_DATA AS\n" +
                        "(\n" +
                        "\tSELECT\n" +
                        "\t\tACAV01.BAD0ZO,\n" +
                        "\t\tACAV01.CMFOFF,\n" +
                        "\t\tACAV01.CDCSTZ\n" +
                        "\tFROM TBD_RFEACAV01BASE_DCH AS ACAV01\n" +
                        ")\n" +
                        "\n" +
                        "SELECT\n" +
                        "\tF_GetRFECustomerName(MAIN_DATA.BAD0ZO, MAIN_DATA.CMFOFF, MAIN_DATA.CDCSTZ) AS SPAYERNAME\n" +
                        "FROM\n" +
                        "\tMAIN_DATA;",
                "Tables:\n" +
                        "TBD_CUSLISTD_DCH\n" +
                        "TBD_RFEACAV01BASE_DCH\n" +
                        "\n" +
                        "Fields:\n" +
                        "TBD_CUSLISTD_DCH.AS_OF_DATE\n" +
                        "TBD_CUSLISTD_DCH.CUSTNAMEENG\n" +
                        "TBD_RFEACAV01BASE_DCH.BAD0ZO\n" +
                        "TBD_RFEACAV01BASE_DCH.CDCSTZ\n" +
                        "TBD_RFEACAV01BASE_DCH.CMFOFF");
    }

}
