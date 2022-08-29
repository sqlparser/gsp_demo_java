package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testHive extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvhive);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testColumnInSelectListInCTAS() {
        doTest("create table a(a int, b int);\n" +
                        "create table a as select b from c",
                "Tables:\n" +
                        "a\n" +
                        "c\n" +
                        "\n" +
                        "Fields:\n" +
                        "a.a:int\n" +
                        "a.b\n" +
                        "a.b:int\n" +
                        "c.b");
    }

    public static void testDate() {
        doTest("create table table1(date date, col1 varchar(2));\n" +
                        "\n" +
                        "insert into table2\n" +
                        "select date, col1 from table1",
                "Tables:\n" +
                        "table1\n" +
                        "table2\n" +
                        "\n" +
                        "Fields:\n" +
                        "table1.col1\n" +
                        "table1.col1:varchar:2\n" +
                        "table1.date\n" +
                        "table1.date:date");
    }


    public static void testCreateFunction() {
        doTest("SELECT\n" +
                        "    ssa.h_sammelanlage_hk,\n" +
                        "    ssa.id_sammelanlage,\n" +
                        "    ssa.hash_diff,\n" +
                        "    -- ab hier alphabetische Reihenfolge (aus GO-Modell Innovator importiert)\n" +
                        "    ssafx.acc_ahk_gesamt_aktiviert,\n" +
                        "    ssafx.acc_ahk_kumuliert,\n" +
                        "    ssafx.acc_drohverlust_rueckstellung,\n" +
                        "    ssafx.acc_gel_anzahl_gesamt,\n" +
                        "    ssafx.acc_gel_anzahl_jahr,\n" +
                        "    ssafx.acc_gel_anzahl_vorjahr,\n" +
                        "    ssafx.acc_plan_ahk_gesamt,\n" +
                        "    ssafx.acc_plan_ahk_nachtraeglich,\n" +
                        "    ssafx.acc_plan_ahk_vertraglich,\n" +
                        "    ssa.afa_ab,\n" +
                        "    ssa.afa_bis,\n" +
                        "    ssa.afa_stichtag,\n" +
                        "    ssafx.ahk_gesamt,\n" +
                        "    ssabhw.ahk_sammelanlage_hergeleitet.betrag AS betrag_sammelanlage_bestof,\n" +
                        "    ssabhw.ahk_sammelanlage_hergeleitet.herkunft as betrag_sammelanlage_herkunft,\n" +
                        "    ssafx.ahk_vertraglich,\n" +
                        "    ssa.aktivierung_datum,\n" +
                        "    ssafx.anteil_fremdwaehrung,\n" +
                        "    ssafx.anteil_hauswaehrung,\n" +
                        "    ssabhw.beginn_sammelanlage_hergeleitet.datum AS datum_sammelanlage_bestof,\n" +
                        "    ssabhw.beginn_sammelanlage_hergeleitet.herkunft AS datum_sammelanlage_herkunft,\n" +
                        "    ssa.bemerkung,\n" +
                        "    ssafx.differenz_fremdwaehrung,\n" +
                        "    ssafx.differenz_hauswaehrung,\n" +
                        "    ssa.gepl_einsatz_bewertung,\n" +
                        "    ssa.haupttitel_analyse,\n" +
                        "    ssafx.hgb_ahk_gesamt,\n" +
                        "    ssafx.hgb_ahk_vertraglich,\n" +
                        "    ssa.is_aktiviert,\n" +
                        "    ssa.is_catchup,\n" +
                        "    ssa.is_erstsendung,\n" +
                        "    ssa.is_highlight,\n" +
                        "    ssa.is_inaktiv,\n" +
                        "    ssa.is_plan_ausstrahlung,\n" +
                        "    ssax.laufzeit_bis,\n" +
                        "    ssax.laufzeit_von,\n" +
                        "    ssa.library,\n" +
                        "    ssa.lizenz_beginn,\n" +
                        "    ssa.lizenz_ende,\n" +
                        "    ssa.objekt_typ,\n" +
                        "    ssafx.plan_ahk_gesamt,\n" +
                        "    ssa.plan_datum,\n" +
                        "    ssa.programmvorschau_zeit,\n" +
                        "    ssa.sendedatum,\n" +
                        "    ssa.sendelaenge,\n" +
                        "    ssa.sendezeit,\n" +
                        "    ssa.serientitel_analyse,\n" +
                        "    ssa.verteilungsgitter_id,\n" +
                        "    ssa.wiederholung,\n" +
                        "    ssa.wievielte,\n" +
                        "    -- ab hier technische Attribute\n" +
                        "    ssa.delivery_timestamp,\n" +
                        "    ssa.delivery_end_timestamp,\n" +
                        "    current_timestamp() as load_timestamp,\n" +
                        "    ssa.last_op,\n" +
                        "    ssa.error_code,\n" +
                        "    1 AS hash_partition_column\n" +
                        "FROM\n" +
                        "    pharos_raw_vault.h_sammelanlage hsa\n" +
                        "    INNER JOIN pharos_raw_vault.s_sammelanlage ssa\n" +
                        "        ON hsa.h_sammelanlage_hk = ssa.h_sammelanlage_hk\n" +
                        "        AND ssa.is_deleted = FALSE\n" +
                        "        AND ssa.partition_dt = '9999-12-31'\n" +
                        "    INNER JOIN pharos_business_vault.s_sammelanlage_bv_hergeleitete_werte ssabhw\n" +
                        "        ON hsa.h_sammelanlage_hk = ssabhw.h_sammelanlage_hk\n" +
                        "    INNER JOIN pharos_raw_vault.s_sammelanlage_promamsxml ssax\n" +
                        "        ON hsa.h_sammelanlage_hk = ssax.h_sammelanlage_hk\n" +
                        "        AND ssax.is_deleted = FALSE\n" +
                        "        AND ssax.partition_dt = '9999-12-31'\n" +
                        "    INNER JOIN pharos_raw_vault.s_sammelanlage_fact_promamsxml ssafx\n" +
                        "        ON hsa.h_sammelanlage_hk = ssafx.h_sammelanlage_hk\n" +
                        "        AND ssafx.is_deleted = FALSE\n" +
                        "        AND ssafx.partition_dt = '9999-12-31'",
                "Tables:\n" +
                        "pharos_business_vault.s_sammelanlage_bv_hergeleitete_werte\n" +
                        "pharos_raw_vault.h_sammelanlage\n" +
                        "pharos_raw_vault.s_sammelanlage\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml\n" +
                        "\n" +
                        "Fields:\n" +
                        "pharos_business_vault.s_sammelanlage_bv_hergeleitete_werte.ahk_sammelanlage_hergeleitet\n" +
                        "pharos_business_vault.s_sammelanlage_bv_hergeleitete_werte.beginn_sammelanlage_hergeleitet\n" +
                        "pharos_business_vault.s_sammelanlage_bv_hergeleitete_werte.h_sammelanlage_hk\n" +
                        "pharos_raw_vault.h_sammelanlage.h_sammelanlage_hk\n" +
                        "pharos_raw_vault.s_sammelanlage.afa_ab\n" +
                        "pharos_raw_vault.s_sammelanlage.afa_bis\n" +
                        "pharos_raw_vault.s_sammelanlage.afa_stichtag\n" +
                        "pharos_raw_vault.s_sammelanlage.aktivierung_datum\n" +
                        "pharos_raw_vault.s_sammelanlage.bemerkung\n" +
                        "pharos_raw_vault.s_sammelanlage.delivery_end_timestamp\n" +
                        "pharos_raw_vault.s_sammelanlage.delivery_timestamp\n" +
                        "pharos_raw_vault.s_sammelanlage.error_code\n" +
                        "pharos_raw_vault.s_sammelanlage.gepl_einsatz_bewertung\n" +
                        "pharos_raw_vault.s_sammelanlage.h_sammelanlage_hk\n" +
                        "pharos_raw_vault.s_sammelanlage.hash_diff\n" +
                        "pharos_raw_vault.s_sammelanlage.haupttitel_analyse\n" +
                        "pharos_raw_vault.s_sammelanlage.id_sammelanlage\n" +
                        "pharos_raw_vault.s_sammelanlage.is_aktiviert\n" +
                        "pharos_raw_vault.s_sammelanlage.is_catchup\n" +
                        "pharos_raw_vault.s_sammelanlage.is_deleted\n" +
                        "pharos_raw_vault.s_sammelanlage.is_erstsendung\n" +
                        "pharos_raw_vault.s_sammelanlage.is_highlight\n" +
                        "pharos_raw_vault.s_sammelanlage.is_inaktiv\n" +
                        "pharos_raw_vault.s_sammelanlage.is_plan_ausstrahlung\n" +
                        "pharos_raw_vault.s_sammelanlage.last_op\n" +
                        "pharos_raw_vault.s_sammelanlage.library\n" +
                        "pharos_raw_vault.s_sammelanlage.lizenz_beginn\n" +
                        "pharos_raw_vault.s_sammelanlage.lizenz_ende\n" +
                        "pharos_raw_vault.s_sammelanlage.objekt_typ\n" +
                        "pharos_raw_vault.s_sammelanlage.partition_dt\n" +
                        "pharos_raw_vault.s_sammelanlage.plan_datum\n" +
                        "pharos_raw_vault.s_sammelanlage.programmvorschau_zeit\n" +
                        "pharos_raw_vault.s_sammelanlage.sendedatum\n" +
                        "pharos_raw_vault.s_sammelanlage.sendelaenge\n" +
                        "pharos_raw_vault.s_sammelanlage.sendezeit\n" +
                        "pharos_raw_vault.s_sammelanlage.serientitel_analyse\n" +
                        "pharos_raw_vault.s_sammelanlage.verteilungsgitter_id\n" +
                        "pharos_raw_vault.s_sammelanlage.wiederholung\n" +
                        "pharos_raw_vault.s_sammelanlage.wievielte\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_ahk_gesamt_aktiviert\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_ahk_kumuliert\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_drohverlust_rueckstellung\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_gel_anzahl_gesamt\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_gel_anzahl_jahr\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_gel_anzahl_vorjahr\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_plan_ahk_gesamt\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_plan_ahk_nachtraeglich\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.acc_plan_ahk_vertraglich\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.ahk_gesamt\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.ahk_vertraglich\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.anteil_fremdwaehrung\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.anteil_hauswaehrung\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.differenz_fremdwaehrung\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.differenz_hauswaehrung\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.h_sammelanlage_hk\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.hgb_ahk_gesamt\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.hgb_ahk_vertraglich\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.is_deleted\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.partition_dt\n" +
                        "pharos_raw_vault.s_sammelanlage_fact_promamsxml.plan_ahk_gesamt\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml.h_sammelanlage_hk\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml.is_deleted\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml.laufzeit_bis\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml.laufzeit_von\n" +
                        "pharos_raw_vault.s_sammelanlage_promamsxml.partition_dt");
    }
}
