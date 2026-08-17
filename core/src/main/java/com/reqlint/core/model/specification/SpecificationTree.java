package com.reqlint.core.model.specification;

import com.reqlint.core.model.specification.items.EquipmentRequirement;
import com.reqlint.core.model.specification.items.SoftwareRequirement;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.model.specification.warnings.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification tree contains all {@link SpecificationItem} found in the specification documentation.
 * It can them perform the {@link #verify()} operation, which populates the tree with additional
 * {@link SpecificationTreeWarning}, describing problems found during verification.
 */
public class SpecificationTree {
    private final List<EquipmentRequirement> equipmentRequirements = new ArrayList<>();
    private final List<SoftwareRequirement> softwareRequirements = new ArrayList<>();
    private final List<TestCase> testCases = new ArrayList<>();
    private final List<SpecificationTreeWarning> warnings = new ArrayList<>();

    /**
     * @return All equipment requirements.
     */
    public List<EquipmentRequirement> equipmentRequirements() {
        return equipmentRequirements.stream().sorted().toList();
    }

    /**
     * The list of equipment requirements linked to the specified software requirement.
     * @param softwareRequirement The software requirement.
     * @return The related equipment requirements.
     */
    public List<EquipmentRequirement> equipmentRequirements(SoftwareRequirement softwareRequirement) {
        return equipmentRequirements.stream()
                .filter(er -> er.softwareRequirements().contains(softwareRequirement))
                .toList();
    }

    /**
     * @return All software requirements.
     */
    public List<SoftwareRequirement> softwareRequirements() {
        return softwareRequirements.stream().sorted().toList();
    }

    /**
     * The list of software requirements linked to the specified test case.
     * @param testCase The test case.
     * @return The related software requirements.
     */
    public List<SoftwareRequirement> softwareRequirements(TestCase testCase) {
        return softwareRequirements.stream()
                .filter(sr -> sr.testCases().contains(testCase))
                .toList();
    }

    /**
     * @return All test cases.
     */
    public List<TestCase> testCases() {
        return testCases.stream().sorted().toList();
    }

    /**
     * @return All warnings.
     */
    public List<SpecificationTreeWarning> warnings() {
        return warnings.stream().toList();
    }

    public void attach(SpecificationItem specificationItem) {
        switch (specificationItem) {
            case EquipmentRequirement equipmentRequirement -> equipmentRequirements.add(equipmentRequirement);
            case SoftwareRequirement softwareRequirement -> softwareRequirements.add(softwareRequirement);
            case TestCase testCase -> testCases.add(testCase);
            default -> {}
        }
    }

    /**
     * Verifies the specification tree.
     */
    public void verify() {
        warnings.clear();
        linkSoftwareRequirementsToEquipmentRequirements();
        linkTestCasesToSoftwareRequirements();
        verifyDuplicatedEquipmentRequirements();
        verifyDuplicatedSoftwareRequirements();
        verifyDuplicatedTestCases();
        verifyChildlessEquipmentRequirements();
        verifyChildlessSoftwareRequirements();
    }

    private void verifyDuplicatedEquipmentRequirements() {
        for (var e1 : equipmentRequirements()) {
            for (var e2 : equipmentRequirements()) {
                if (e1 != e2 && e1.identifier().equals(e2.identifier())) {
                    warnings.add(new DuplicatedEquipmentRequirement(e1));
                }
            }
        }
    }

    private void verifyDuplicatedSoftwareRequirements() {
        for (var e1 : softwareRequirements()) {
            for (var e2 : softwareRequirements()) {
                if (e1 != e2 && e1.identifier().equals(e2.identifier())) {
                    warnings.add(new DuplicatedSoftwareRequirement(e1));
                }
            }
        }
    }

    private void verifyDuplicatedTestCases() {
        for (var e1 : testCases()) {
            for (var e2 : testCases()) {
                if (e1 != e2 && e1.identifier().equals(e2.identifier())) {
                    warnings.add(new DuplicatedTestCase(e1));
                }
            }
        }
    }

    private void linkSoftwareRequirementsToEquipmentRequirements() {
        for (SoftwareRequirement softwareRequirement : softwareRequirements()) {
            if (softwareRequirement.childOf().isEmpty()) {
                warnings.add(new OrphanSoftwareRequirement(softwareRequirement));
            }
            for (String equipmentRequirementId : softwareRequirement.childOf()) {
                boolean found = false;
                for (EquipmentRequirement equipmentRequirement : equipmentRequirements()) {
                    if (equipmentRequirement.identifier().equals(equipmentRequirementId)) {
                        equipmentRequirement.attach(softwareRequirement);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    warnings.add(new UnknownEquipmentRequirement(softwareRequirement, equipmentRequirementId));
                }
            }
        }
    }

    private void linkTestCasesToSoftwareRequirements() {
        for (TestCase testCase : testCases()) {
            if (testCase.childOf().isEmpty()) {
                warnings.add(new OrphanTestCase(testCase));
            }
            for (String identifier : testCase.childOf()) {
                boolean found = false;
                for (SoftwareRequirement softwareRequirement : softwareRequirements()) {
                    if (softwareRequirement.identifier().equals(identifier)) {
                        softwareRequirement.attach(testCase);
                        found = true;
                    }
                }
                if (!found) {
                    warnings.add(new UnknownSoftwareRequirement(testCase, identifier));
                }
            }
        }
    }
    private void verifyChildlessEquipmentRequirements() {
        for (EquipmentRequirement equipmentRequirement : equipmentRequirements()) {
            if (equipmentRequirement.softwareRequirements().isEmpty()) {
                warnings.add(new ChildlessEquipmentRequirement(equipmentRequirement));
            }
        }
    }

    private void verifyChildlessSoftwareRequirements() {
        for (SoftwareRequirement softwareRequirement : softwareRequirements()) {
            if (softwareRequirement.testCases().isEmpty()) {
                warnings.add(new ChildlessSoftwareRequirement(softwareRequirement));
            }
        }
    }

    /**
     * @return The traceability matrix.
     */
    public List<TraceabilityMatrixItem> buildTraceabilityMatrix() {
        List<TraceabilityMatrixItem> traceabilityMatrix = new ArrayList<>();
        for (EquipmentRequirement equipmentRequirement : equipmentRequirements()) {
            for (SoftwareRequirement softwareRequirement : equipmentRequirement.softwareRequirements()) {
                for (TestCase testCase : softwareRequirement.testCases()) {
                    traceabilityMatrix.add(new TraceabilityMatrixItem(
                            equipmentRequirement,
                            softwareRequirement,
                            testCase));
                }
            }
        }
        return traceabilityMatrix;
    }
}
