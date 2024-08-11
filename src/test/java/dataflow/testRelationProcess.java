package dataflow;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Schema;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testRelationProcess extends TestCase {

    public void testPackageIsParsedCorrectly() throws IOException {
        // Given an Oracle SQL metadata with a package containing a procedure emp_dump_by_location
        EDbVendor vendor = EDbVendor.dbvoracle;

        String sqlfile = gspCommon.BASE_SQL_DIR_PRIVATE +"dlineage/oracle/hrMetadataJson.json";
        // When the dataflow is generated
        DataFlowAnalyzer analyzer = analyseSqlFiles(sqlfile, vendor);
        analyzer.generateDataFlow();
        dataflow df = analyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, df, false);

        Relationship[] insertRels =
                Arrays.stream(dataFlow.getRelationships()).filter(r -> r.getEffectType().equals("insert")).collect(Collectors.toList()).toArray(new Relationship[0]);

        Relationship[] updateRels =
                Arrays.stream(dataFlow.getRelationships()).filter(r -> r.getEffectType().equals("update")).collect(Collectors.toList()).toArray(new Relationship[0]);

        // Then we receive 11 insert relationships linked with the insert process of the emp_dump_by_location procedure
        Schema schema = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(0);
        testEmpDumpByLocation(schema, insertRels);
        testRaiseSalary(schema, updateRels);
    }

    private void testRaiseSalary(Schema schema, Relationship[] updateRels) {
        gudusoft.gsqlparser.dlineage.metadata.Package empAdminPackage = schema.getPackages().get(0);
        List<gudusoft.gsqlparser.dlineage.metadata.Procedure> empAdminProcs = empAdminPackage.getProcedures();
        // Procedure called emp_dump_by_location
        gudusoft.gsqlparser.dlineage.metadata.Procedure empDumpByLocationProc =
                empAdminProcs.stream().filter(p -> p.getName().equals("raise_salary")).findFirst().get();

        List<gudusoft.gsqlparser.dlineage.metadata.Process> processes = schema.getProcesses();
        List<gudusoft.gsqlparser.dlineage.metadata.Process> empDumpByLocationProcProcess =
                processes.stream().filter(p -> p.getProcedureId() != null && p.getProcedureId().equals(empDumpByLocationProc.getId())).collect(Collectors.toList());
        // emp_dump_by_location Insert-1 process
        gudusoft.gsqlparser.dlineage.metadata.Process updateProcess =
                empDumpByLocationProcProcess.stream().filter(p -> p.getName().contains("Update")).findFirst().get();

        Relationship[] updateProcessRels =
                Arrays.stream(updateRels).filter(r -> r.getProcessId().equals(updateProcess.getId())).collect(Collectors.toList()).toArray(new Relationship[0]);

        assertTrue(updateProcessRels.length == 1);

        assertRelationshipSourceAndTargetExist(updateProcessRels, "SALARY", "SALARY");
    }

    private void testEmpDumpByLocation(Schema schema, Relationship[] insertRels) {
        gudusoft.gsqlparser.dlineage.metadata.Package empAdminPackage = schema.getPackages().get(0);
        List<gudusoft.gsqlparser.dlineage.metadata.Procedure> empAdminProcs = empAdminPackage.getProcedures();
        // Procedure called emp_dump_by_location
        gudusoft.gsqlparser.dlineage.metadata.Procedure empDumpByLocationProc =
                empAdminProcs.stream().filter(p -> p.getName().equals("emp_dump_by_location")).findFirst().get();

        List<gudusoft.gsqlparser.dlineage.metadata.Process> processes = schema.getProcesses();
        List<gudusoft.gsqlparser.dlineage.metadata.Process> empDumpByLocationProcProcess =
                processes.stream().filter(p -> p.getProcedureId() != null && p.getProcedureId().equals(empDumpByLocationProc.getId())).collect(Collectors.toList());
        // emp_dump_by_location Insert-1 process
        gudusoft.gsqlparser.dlineage.metadata.Process insertProcess =
                empDumpByLocationProcProcess.stream().filter(p -> p.getName().contains("Insert")).findFirst().get();

        Relationship[] insertProcessRels =
                Arrays.stream(insertRels).filter(r -> r.getProcessId().equals(insertProcess.getId())).collect(Collectors.toList()).toArray(new Relationship[0]);

        assertTrue(insertProcessRels.length == 11);

        assertRelationshipSourceAndTargetExist(insertProcessRels, "EMPLOYEE_ID", "EMPLOYEE_ID");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "FIRST_NAME", "FIRST_NAME");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "LAST_NAME", "LAST_NAME");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "EMAIL", "EMAIL");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "PHONE_NUMBER", "PHONE_NUMBER");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "HIRE_DATE", "HIRE_DATE");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "JOB_ID", "JOB_ID");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "SALARY", "SALARY");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "COMMISSION_PCT", "COMMISSION_PCT");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "MANAGER_ID", "MANAGER_ID");
        assertRelationshipSourceAndTargetExist(insertProcessRels, "DEPARTMENT_ID", "DEPARTMENT_ID");
    }

    void assertRelationshipSourceAndTargetExist(Relationship[] relationships, String sourceColumnName,
                                                String targetColumnName) {
        assertTrue(Arrays.stream(relationships).anyMatch(r -> r.getTarget().getColumn().equals(targetColumnName) && Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().equals(sourceColumnName))));
    }


    private DataFlowAnalyzer analyseSqlFiles(String path, EDbVendor vendor) throws IOException {
        List<File> file = Arrays.asList(new File(path));
        File[] files = file.toArray(new File[0]);


        Option option = new Option();
        option.setTransform(false);
        option.setShowConstantTable(true);
        option.setVendor(vendor);
        option.setSimpleOutput(true);

        option.setTextFormat(true);

        return new DataFlowAnalyzer(files, option);
    }
}
