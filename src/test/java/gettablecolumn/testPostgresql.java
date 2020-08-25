package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testPostgresql extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvpostgresql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testCopy1() {
        doTest("COPY (SELECT * FROM country WHERE country_name LIKE 'A%') TO '/usr1/proj/bray/sql/a_list_countries.copy';",
                "Tables:\n" +
                        "country\n" +
                        "\n" +
                        "Fields:\n" +
                        "country.*\n" +
                        "country.country_name");
    }

    public static void testCopy2() {
        doTest("COPY country TO STDOUT (DELIMITER '|');",
                "Tables:\n" +
                        "country\n" +
                        "\n" +
                        "Fields:");
    }

    public static void testCopy3() {
        doTest("COPY country(name,location,postid) FROM '/usr1/proj/bray/sql/country_data';",
                "Tables:\n" +
                        "country\n" +
                        "\n" +
                        "Fields:\n" +
                        "country.location\n" +
                        "country.name\n" +
                        "country.postid");
    }
}

