package demos.antiSQLInjection;

import java.util.Map;

public interface GContext {

    void setVars(Map vars);
    Map getVars();

}
