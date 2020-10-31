
package gudusoft.gsqlparser.dlineage.dataflow.model;

public class IndirectImpactRelation extends AbstractRelation {

    @Override
    public RelationType getRelationType() {
        return RelationType.fddi;
    }
}
