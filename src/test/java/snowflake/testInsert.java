package snowflake;



import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EInsertSource;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TInsertCondition;
import gudusoft.gsqlparser.nodes.TInsertIntoValue;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInsert extends TestCase {
    public void testUnconditionalInsert(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "insert overwrite all\n" +
                "  into t1\n" +
                "  into t2 (c1, c2, c3) values (n2, n1, default)\n" +
                "  into t3 (c1, c2, c3)\n" +
                "  into t4 values (n3, n2, n1)\n" +
                "select n1, n2, n3 from src;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertStmt = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertStmt.getInsertSource() == EInsertSource.subquery);
        assertTrue(insertStmt.getSubQuery().getTables().getTable(0).toString().equalsIgnoreCase("src"));
        assertTrue(insertStmt.getInsertIntoValues().size() == 4);
        TInsertIntoValue intoValue = insertStmt.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t1"));
        intoValue = insertStmt.getInsertIntoValues().getElement(1);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t2"));
        assertTrue(intoValue.getColumnList().size() == 3);
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        assertTrue(intoValue.getTargetList().size() == 1);
        TMultiTarget multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n2"));
        intoValue = insertStmt.getInsertIntoValues().getElement(3);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("t4"));
        assertTrue(intoValue.getTargetList().size() == 1);
        multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n3"));
    }

    public void testConditionalInsert(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "insert all\n" +
                "  when n1 > 100 then\n" +
                "    into tab1\n" +
                "  when n1 > 10 then\n" +
                "    into tab2 (c1, c2, c3)\n" +
                "    into tab3 (c1, c2, c3) values (n2, n1, default) \n" +
                "  else\n" +
                "    into tab4 values (n3, n2, n1)\n" +
                "select n1,n2,n3 from srcTab;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertStmt = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertStmt.getInsertSource() == EInsertSource.subquery);
        assertTrue(insertStmt.getSubQuery().getTables().getTable(0).toString().equalsIgnoreCase("srcTab"));
        assertTrue(insertStmt.getInsertConditions().size() == 2);
        TInsertCondition insertCondition = insertStmt.getInsertConditions().getElement(0);
        assertTrue(insertCondition.getCondition().toString().equalsIgnoreCase("n1 > 100"));
        TInsertIntoValue intoValue = insertCondition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab1"));

        insertCondition = insertStmt.getInsertConditions().getElement(1);
        assertTrue(insertCondition.getCondition().toString().equalsIgnoreCase("n1 > 10"));
        assertTrue(insertCondition.getInsertIntoValues().size() == 2);
        intoValue = insertCondition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab2"));
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        intoValue = insertCondition.getInsertIntoValues().getElement(1);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab3"));
        assertTrue(intoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("c1"));
        TMultiTarget multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("n2"));

        assertTrue(insertStmt.getElseIntoValues().size() == 1);
        intoValue = insertStmt.getElseIntoValues().getElement(0);
        assertTrue(intoValue.getTable().getTableName().toString().equalsIgnoreCase("tab4"));
        multiTarget = intoValue.getTargetList().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 3);
        assertTrue(multiTarget.getColumnList().getResultColumn(2).toString().equalsIgnoreCase("n1"));

    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "INSERT INTO COMMON.PREP.CHARACTER_CLASS_GAME_STATISTICS\n" +
                "(\n" +
                "    etl_load_log_id\n" +
                "    ,etl_update_log_id\n" +
                "    ,source\n" +
                "    ,player_id\n" +
                "    ,player_id_type\n" +
                "    ,platform_key\n" +
                "    ,nucleus_persona_key\n" +
                "    ,nucleus_user_key\n" +
                "    ,device_key\n" +
                "    ,taxonomy_version\n" +
                "    ,release_type\n" +
                "    ,build_version_key\n" +
                "    ,game_session_key\n" +
                "    ,blaze_game_session_key\n" +
                "    ,blaze_match_key\n" +
                "    ,game_mode_key\n" +
                "    ,game_type_key\n" +
                "    ,game_level_key\n" +
                "    ,mode_type_key\n" +
                "    ,game_difficulty_key\n" +
                "    ,game_end_time\n" +
                "    ,end_reason_key\n" +
                "    ,character_class_source_key\n" +
                "    ,character_class_key\n" +
                "    ,persona_score\n" +
                "    ,persona_kill_count\n" +
                "    ,total_kill_count\n" +
                "    ,spawn_count\n" +
                "    ,death_count\n" +
                "    ,persona_revived_count\n" +
                "    ,total_revived_count\n" +
                "    ,assist_count\n" +
                "    ,shot_fired_count\n" +
                "    ,shot_hit_count\n" +
                "    ,critical_hit_count\n" +
                "    ,critical_damage_dealt_count\n" +
                "    ,regular_damage_dealt_count\n" +
                "    ,character_gameplay_duration_seconds\n" +
                "    ,total_gameplay_duration_seconds\n" +
                "    ,total_duration_seconds\n" +
                "    ,title_code\n" +
                "    ,platform_code\n" +
                "    ,dt\n" +
                "    ,hour\n" +
                "    ,game_id\n" +
                ")\n" +
                "WITH GAME_SESSION_END AS\n" +
                "(\n" +
                "    SELECT\n" +
                "        source\n" +
                "        ,player_id\n" +
                "        ,player_id_type\n" +
                "        ,platform_key\n" +
                "        ,nucleus_persona_key\n" +
                "        ,nucleus_user_key\n" +
                "        ,device_key\n" +
                "        ,taxonomy_version\n" +
                "        ,release_type_key\n" +
                "        ,build_version_key\n" +
                "        ,game_session_key\n" +
                "        ,blaze_game_session_key\n" +
                "        ,blaze_match_key\n" +
                "        ,game_mode_key\n" +
                "        ,game_type_key\n" +
                "        ,game_level_key\n" +
                "        ,mode_type_key\n" +
                "        ,game_difficulty_key\n" +
                "        ,game_end_time\n" +
                "        ,end_reason_key\n" +
                "        ,character_stats\n" +
                "        ,gameplay_duration_seconds as total_gameplay_duration_seconds\n" +
                "        ,total_duration_seconds\n" +
                "        ,title_code\n" +
                "        ,platform_code\n" +
                "        ,dt\n" +
                "        ,hour\n" +
                "        ,game_id\n" +
                "    FROM\n" +
                "        COMMON.PREP.GAME_SESSION_END\n" +
                "    WHERE\n" +
                "        dt = '2021-01-31'\n" +
                "        AND game_id = 917002\n" +
                ")\n" +
                "\n" +
                "\n" +
                "SELECT\n" +
                "    0                        etl_load_log_id\n" +
                "    ,0                        etl_update_log_id\n" +
                "    ,g.source                   source\n" +
                "    ,g.player_id                player_id\n" +
                "    ,g.player_id_type           player_id_type\n" +
                "    ,g.platform_key             platform_key\n" +
                "    ,g.nucleus_persona_key      nucleus_persona_key\n" +
                "    ,g.nucleus_user_key         nucleus_user_key\n" +
                "    ,g.device_key               device_key\n" +
                "    ,g.taxonomy_version         taxonomy_version\n" +
                "    ,g.release_type_key         release_type\n" +
                "    ,g.build_version_key        build_version_key\n" +
                "    ,g.game_session_key         game_session_key\n" +
                "    ,g.blaze_game_session_key   blaze_game_session_key\n" +
                "    ,g.blaze_match_key          blaze_match_key\n" +
                "    ,g.game_mode_key            game_mode_key\n" +
                "    ,g.game_type_key            game_type_key\n" +
                "    ,g.game_level_key           game_level_key\n" +
                "    ,g.mode_type_key            mode_type_key\n" +
                "    ,g.game_difficulty_key      game_difficulty_key\n" +
                "    ,g.game_end_time            game_end_time\n" +
                "    ,g.end_reason_key           end_reason_key\n" +
                "    ,character_stat.value:character::string        character_class_source_key\n" +
                "    ,map_character_class.character_class_key       character_class_key\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:score::string)            persona_score\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:player_kills::string)     persona_kill_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:total_kills::string)      total_kill_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:spawn::string)            spawn_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:deaths::string)           death_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:players_revived::string)  persona_revived_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:revived::string)          total_revived_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:assists::string)          assist_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:shots_fired::string)      shot_fired_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:shots_hit::string)        shot_hit_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:critical_hits::string)    critical_hit_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:cdamage_dealt::string)    critical_damage_dealt_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:rdamage_dealt::string)    regular_damage_dealt_count\n" +
                "    ,TRY_TO_NUMBER(character_stat.value:gdur::string)             character_gameplay_duration_seconds\n" +
                "    ,g.total_gameplay_duration_seconds   total_gameplay_duration_seconds\n" +
                "    ,g.total_duration_seconds            total_duration_seconds\n" +
                "    ,g.title_code                      title_code\n" +
                "    ,g.platform_code                   platform_code\n" +
                "    ,g.dt                              dt\n" +
                "    ,g.hour                            hour\n" +
                "    ,g.game_id                         game_id\n" +
                "FROM\n" +
                "    GAME_SESSION_END g\n" +
                "JOIN\n" +
                "    lateral flatten(input => g.character_stats, outer => TRUE) character_stat\n" +
                "LEFT JOIN\n" +
                "    COMMON.MAP.character_class map_character_class\n" +
                "ON\n" +
                "    character_stat.value:character::string = map_character_class.character_class_source_key\n" +
                "    AND g.title_code = map_character_class.title_code\n";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertStmt = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = insertStmt.getSubQuery();
        TTable table1 = select.getTables().getTable(0);
        //System.out.println(select.getTables().size());
        //System.out.println(table1.getTableName().toString());


    }

    public void testColumnsInInsertAll() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "insert overwrite all\n" +
                "  into t1\n" +
                "  into t1 (c1, c2, c3) values (n2, n1, default)\n" +
                "  into t2 (c1, c2, c3)\n" +
                "  into t2 values (n3, n2, n1)\n" +
                "select n1, n2, n3 from src;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(sqlparser.sqlstatements.get(0).getSyntaxHints().size() == 0);
    }

}
