package demos.dlineage.dataflow.metadata;

import demos.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.EDbVendor;

public interface MetadataAnalyzer {
	dataflow analyzeMetadata(EDbVendor vendor, String metadata);
}
