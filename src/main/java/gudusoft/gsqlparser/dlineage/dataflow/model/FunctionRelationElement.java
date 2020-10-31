
package gudusoft.gsqlparser.dlineage.dataflow.model;

public class FunctionRelationElement implements RelationElement<Function> {

    private Function function;

    public FunctionRelationElement(Function function) {
        this.function = function;
    }

    @Override
    public Function getElement() {
        return function;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
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
		FunctionRelationElement other = (FunctionRelationElement) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		return true;
	}


}
