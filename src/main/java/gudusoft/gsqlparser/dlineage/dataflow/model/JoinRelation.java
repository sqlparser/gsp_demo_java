
package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.EJoinType;

public class JoinRelation extends AbstractRelation {

    public static enum JoinClauseType {
        on, where
    }

    private EJoinType joinType;

    private String joinCondition;

    private JoinClauseType joinClauseType;

    @Override
    public RelationType getRelationType() {
        return RelationType.join;
    }

    public EJoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(EJoinType joinType) {
        this.joinType = joinType;
    }

    public String getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(String joinCondition) {
        this.joinCondition = joinCondition;
    }

    public JoinClauseType getJoinClauseType() {
        return joinClauseType;
    }

    public void setJoinClauseType(JoinClauseType joinClauseType) {
        this.joinClauseType = joinClauseType;
    }

}
