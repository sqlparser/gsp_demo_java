
package gudusoft.gsqlparser.dlineage.dataflow.model;

public class RecordSetRelation extends AbstractRelation {

    private String aggregateFunction;

    public String getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(String aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

    @Override
    public RelationType getRelationType() {
        return RelationType.frd;
    }

}
