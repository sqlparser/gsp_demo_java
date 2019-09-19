package test.oracle;


import gudusoft.gsqlparser.EAlterTriggerOption;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterTriggerStmt;
import gudusoft.gsqlparser.stmt.oracle.TCompoundTriggerBody;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTrigger;
import junit.framework.TestCase;

public class testCompoundTrigger  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE TRIGGER aud_emp\n" +
                "FOR INSERT OR UPDATE\n" +
                "ON employees\n" +
                "COMPOUND TRIGGER\n" +
                "   \n" +
                "  TYPE t_emp_changes       IS TABLE OF aud_emp%ROWTYPE INDEX BY SIMPLE_INTEGER;\n" +
                "  v_emp_changes            t_emp_changes;\n" +
                "   \n" +
                "  v_index                  SIMPLE_INTEGER       := 0;\n" +
                "  v_threshhold    CONSTANT SIMPLE_INTEGER       := 1000; --maximum number of rows to write in one go.\n" +
                "  v_user          VARCHAR2(50); --logged in user\n" +
                "   \n" +
                "  PROCEDURE flush_logs\n" +
                "  IS\n" +
                "    v_updates       CONSTANT SIMPLE_INTEGER := v_emp_changes.count();\n" +
                "  BEGIN\n" +
                " \n" +
                "    FORALL v_count IN 1..v_updates\n" +
                "        INSERT INTO aud_emp\n" +
                "             VALUES v_emp_changes(v_count);\n" +
                " \n" +
                "    v_emp_changes.delete();\n" +
                "    v_index := 0; --resetting threshold for next bulk-insert.\n" +
                " \n" +
                "  END flush_logs;\n" +
                " \n" +
                "  AFTER EACH ROW\n" +
                "  IS\n" +
                "  BEGIN\n" +
                "         \n" +
                "    IF INSERTING THEN\n" +
                "        v_index := v_index + 1;\n" +
                "        v_emp_changes(v_index).upd_dt       := SYSDATE;\n" +
                "        v_emp_changes(v_index).upd_by       := SYS_CONTEXT ('USERENV', 'SESSION_USER');\n" +
                "        v_emp_changes(v_index).emp_id       := :NEW.emp_id;\n" +
                "        v_emp_changes(v_index).action       := 'Create';\n" +
                "        v_emp_changes(v_index).field        := '*';\n" +
                "        v_emp_changes(v_index).from_value   := 'NULL';\n" +
                "        v_emp_changes(v_index).to_value     := '*';\n" +
                " \n" +
                "    ELSIF UPDATING THEN\n" +
                "        IF (   (:OLD.EMP_ID <> :NEW.EMP_ID)\n" +
                "                OR (:OLD.EMP_ID IS     NULL AND :NEW.EMP_ID IS NOT NULL)\n" +
                "                OR (:OLD.EMP_ID IS NOT NULL AND :NEW.EMP_ID IS     NULL)\n" +
                "                  )\n" +
                "             THEN\n" +
                "                v_index := v_index + 1;\n" +
                "                v_emp_changes(v_index).upd_dt       := SYSDATE;\n" +
                "                v_emp_changes(v_index).upd_by       := SYS_CONTEXT ('USERENV', 'SESSION_USER');\n" +
                "                v_emp_changes(v_index).emp_id       := :NEW.emp_id;\n" +
                "                v_emp_changes(v_index).field        := 'EMP_ID';\n" +
                "                v_emp_changes(v_index).from_value   := to_char(:OLD.EMP_ID);\n" +
                "                v_emp_changes(v_index).to_value     := to_char(:NEW.EMP_ID);\n" +
                "                v_emp_changes(v_index).action       := 'Update';\n" +
                "          END IF;\n" +
                "         \n" +
                "        IF (   (:OLD.NAME <> :NEW.NAME)\n" +
                "                OR (:OLD.NAME IS     NULL AND :NEW.NAME IS NOT NULL)\n" +
                "                OR (:OLD.NAME IS NOT NULL AND :NEW.NAME IS     NULL)\n" +
                "                  )\n" +
                "             THEN\n" +
                "                v_index := v_index + 1;\n" +
                "                v_emp_changes(v_index).upd_dt       := SYSDATE;\n" +
                "                v_emp_changes(v_index).upd_by       := SYS_CONTEXT ('USERENV', 'SESSION_USER');\n" +
                "                v_emp_changes(v_index).emp_id       := :NEW.emp_id;\n" +
                "                v_emp_changes(v_index).field        := 'NAME';\n" +
                "                v_emp_changes(v_index).from_value   := to_char(:OLD.NAME);\n" +
                "                v_emp_changes(v_index).to_value     := to_char(:NEW.NAME);\n" +
                "                v_emp_changes(v_index).action       := 'Update';\n" +
                "          END IF;\n" +
                "                        \n" +
                "        IF (   (:OLD.SALARY <> :NEW.SALARY)\n" +
                "                OR (:OLD.SALARY IS     NULL AND :NEW.SALARY IS NOT NULL)\n" +
                "                OR (:OLD.SALARY IS NOT NULL AND :NEW.SALARY IS     NULL)\n" +
                "                  )\n" +
                "             THEN\n" +
                "                v_index := v_index + 1;\n" +
                "                v_emp_changes(v_index).upd_dt      := SYSDATE;\n" +
                "                v_emp_changes(v_index).upd_by      := SYS_CONTEXT ('USERENV', 'SESSION_USER');\n" +
                "                v_emp_changes(v_index).emp_id      := :NEW.emp_id;\n" +
                "                v_emp_changes(v_index).field       := 'SALARY';\n" +
                "                v_emp_changes(v_index).from_value  := to_char(:OLD.SALARY);\n" +
                "                v_emp_changes(v_index).to_value    := to_char(:NEW.SALARY);\n" +
                "                v_emp_changes(v_index).action      := 'Update';\n" +
                "          END IF;\n" +
                "                        \n" +
                "    END IF;\n" +
                " \n" +
                "    IF v_index >= v_threshhold THEN\n" +
                "      flush_logs();\n" +
                "    END IF;\n" +
                " \n" +
                "   END AFTER EACH ROW;\n" +
                " \n" +
                "  -- AFTER STATEMENT Section:\n" +
                "  AFTER STATEMENT IS\n" +
                "  BEGIN\n" +
                "     flush_logs();\n" +
                "  END AFTER STATEMENT;\n" +
                " \n" +
                "END aud_emp;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateTrigger createTrigger = (TPlsqlCreateTrigger)sqlparser.sqlstatements.get(0);
        assertTrue(createTrigger.getTriggerName().toString().equalsIgnoreCase("aud_emp"));
        TCompoundTriggerBody compoundTriggerBody = (TCompoundTriggerBody)createTrigger.getTriggerBody();
        //System.out.println(compoundTriggerBody.getDeclareStatements().size());
        //System.out.println(compoundTriggerBody.getTimingPointList().size());
        assertTrue(compoundTriggerBody.getDeclareStatements().size() == 6);
        assertTrue(compoundTriggerBody.getTimingPointList().size() == 2);
        //assertTrue();
    }
}
