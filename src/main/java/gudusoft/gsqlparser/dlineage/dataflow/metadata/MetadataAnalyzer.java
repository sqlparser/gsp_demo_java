package gudusoft.gsqlparser.dlineage.dataflow.metadata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;

public interface MetadataAnalyzer {
	dataflow analyzeMetadata(EDbVendor vendor, String metadata);
}
