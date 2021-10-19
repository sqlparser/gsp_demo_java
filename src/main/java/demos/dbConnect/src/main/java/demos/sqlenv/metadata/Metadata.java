package demos.sqlenv.metadata;

import demos.sqlenv.metadata.model.DBLink;
import demos.sqlenv.metadata.model.Database;
import demos.sqlenv.metadata.model.Query;

import java.util.ArrayList;
import java.util.List;

public class Metadata {

    private String dialect;

    private String createdBy;

    private long exportTime;

    private String userAccountId = "";

    private String physicalInstance;

    private String exportId;

    private List<Database> databases;

    private List<Query> queries;

    private List<DBLink> dbLinks;

    private String customSqlSetName = "";

    private List<String> errorMessages;

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getCustomSqlSetName() {
        return customSqlSetName;
    }

    public void setCustomSqlSetName(String customSqlSetName) {
        this.customSqlSetName = customSqlSetName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getExportTime() {
        return exportTime;
    }

    public void setExportTime(long exportTime) {
        this.exportTime = exportTime;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public String getPhysicalInstance() {
        return physicalInstance;
    }

    public void setPhysicalInstance(String physicalInstance) {
        this.physicalInstance = physicalInstance;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void appendQueries(List<Query> queries) {
        if (queries != null) {
            if (this.queries == null) {
                this.queries = new ArrayList<Query>();
            }
            this.queries.addAll(queries);
        }
    }

    public List<DBLink> getDbLinks() {
        return dbLinks;
    }

    public void appendDbLinks(List<DBLink> dbLinks) {
        if (dbLinks != null) {
            if (this.dbLinks == null) {
                this.dbLinks = new ArrayList<DBLink>();
            }
            this.dbLinks.addAll(dbLinks);
        }
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void appendDatabases(List<Database> databases) {
        if (databases != null) {
            if (this.databases == null) {
                this.databases = new ArrayList<Database>();
            }
            this.databases.addAll(databases);
        }
    }

    public void appendErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            if (this.errorMessages == null) {
                this.errorMessages = new ArrayList<String>();
            }
            this.errorMessages.add(errorMessage);
        }
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

}
