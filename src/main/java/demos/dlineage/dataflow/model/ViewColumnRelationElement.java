
package demos.dlineage.dataflow.model;

public class ViewColumnRelationElement implements RelationElement<TableColumn> {

    private TableColumn column;

    public ViewColumnRelationElement(TableColumn column) {
        this.column = column;
    }

    @Override
    public TableColumn getElement() {
        return column;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((column == null) ? 0 : column.hashCode());
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
        ViewColumnRelationElement other = (ViewColumnRelationElement) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        return true;
    }

}
