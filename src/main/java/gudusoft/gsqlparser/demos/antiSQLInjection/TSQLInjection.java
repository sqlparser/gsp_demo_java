package demos.antiSQLInjection;

/**
 * This class represents a sql injection.
 */
public class TSQLInjection  {

    private ESQLInjectionType type = ESQLInjectionType.syntax_error;

    public TSQLInjection(ESQLInjectionType pType){
        this.type = pType;
        this.description = pType.toString();
    }


    public ESQLInjectionType getType() {
        return type;
    }

    private  String description = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}