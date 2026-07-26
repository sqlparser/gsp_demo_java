package demos.generateLineage;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.XML2Model;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class GenerateLineageExpected {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the path to the YAML file.");
            return;
        }

        String filePath = args[0];
        System.out.println("Processing file: " + filePath);

        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(filePath);
            List<Map<String, Map<String, String>>> testCases = yaml.load(inputStream);
            inputStream.close();

            for (Map<String, Map<String, String>> testCaseData : testCases) {
                Map<String, String> testCase = testCaseData.get("testCase");
                String dbVendorStr = testCase.get("dbVendor");
                String sql = testCase.get("sql");

                System.out.println("Generating lineage for SQL: " + sql.substring(0, Math.min(sql.length(), 50)) + "...");

                EDbVendor dbVendor = EDbVendor.valueOf(dbVendorStr);
                DataFlowAnalyzer analyzer = new DataFlowAnalyzer(sql, dbVendor, true);
                analyzer.setIgnoreRecordSet(false);
                analyzer.setSimpleShowTopSelectResultSet(true);
                analyzer.setIgnoreCoordinate(true);
                analyzer.generateDataFlow();
                dataflow dataflow = analyzer.getDataFlow();
                String actualXml = XML2Model.saveXML(dataflow);

                // Normalize line endings and trim the string to ensure clean YAML output
                String cleanXml = actualXml.trim().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
                testCase.put("expected", cleanXml);
            }

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            // Use LITERAL style for multiline strings, which is more readable for XML
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.LITERAL);
            options.setPrettyFlow(true);
            options.setIndicatorIndent(2);
            options.setIndent(4);
            // Ensure consistent line breaks in the output file
            options.setLineBreak(DumperOptions.LineBreak.UNIX);
            
            Yaml outputYaml = new Yaml(options);
            FileWriter writer = new FileWriter(filePath);
            outputYaml.dump(testCases, writer);
            writer.close();

            System.out.println("Successfully updated " + testCases.size() + " test cases in " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
