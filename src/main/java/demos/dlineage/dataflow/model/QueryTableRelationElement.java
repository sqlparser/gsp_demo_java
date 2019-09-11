
package demos.dlineage.dataflow.model;

public class QueryTableRelationElement implements RelationElement<QueryTable> {

    private QueryTable table;

    public QueryTableRelationElement(QueryTable table) {
        this.table = table;
    }

    @Override
    public QueryTable getElement() {
        return table;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryTableRelationElement other = (QueryTableRelationElement) obj;
        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.equals(other.table))
            return false;
        return true;
    }

}
