
package demos.dlineageBasic.model.xml;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "procedureImpactResult")
public class procedureImpactResult
{

	@ElementList(entry = "targetProcedure", inline = true, required = false)
	private List<targetProcedure> targetProcedures;

	@ElementList(entry = "procedure", inline = true, required = false)
	private List<procedure> procedures;

	public List<targetProcedure> getTargetProcedures( )
	{
		return targetProcedures;
	}

	public void setTargetProcedures( List<targetProcedure> targetProcedures )
	{
		this.targetProcedures = targetProcedures;
	}

	public List<procedure> getProcedures( )
	{
		return procedures;
	}

	public void setProcedures( List<procedure> procedures )
	{
		this.procedures = procedures;
	}

}
