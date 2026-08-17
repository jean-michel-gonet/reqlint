package com.reqlint.core.model.specification;

import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.model.specification.TraceabilityMatrixItem;
import com.reqlint.core.model.specification.items.EquipmentRequirement;
import com.reqlint.core.model.specification.items.SoftwareRequirement;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.model.specification.warnings.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SpecificationTreeTest {
    private static final String ER_1 = "ER-1";
    private static final String ER_2 = "ER-2";
    private static final String ER_3 = "ER-3";
    private static final String SR_1 = "SR-11";
    private static final String SR_2 = "SR-21";
    private static final String SR_3 = "SR-31";
    private static final String TC_1 = "TC-101";
    private static final String TC_2 = "TC-201";
    private static final String TC_3 = "TC-301";

    private static final String I_AM_UNKNOWN = "I_AM_UNKNOWN";
    private static final String TITLE = "TITLE";

    private SpecificationTree underTest;

    private EquipmentRequirement er1, er2, er3;
    private SoftwareRequirement sr1, sr2, sr3;
    private TestCase tc1, tc2, tc3;

    @BeforeEach
    public void setUp() {
        underTest = new SpecificationTree();

        er1 = new EquipmentRequirement(ER_1, TITLE + ER_1);
        er2 = new EquipmentRequirement(ER_2, TITLE + ER_2);
        er3 = new EquipmentRequirement(ER_3, TITLE + ER_3);
        underTest.attach(er3);
        underTest.attach(er1);
        underTest.attach(er2);

        sr1 = new SoftwareRequirement(SR_1, TITLE + SR_1);
        sr1.childOf(ER_1);
        sr2 = new SoftwareRequirement(SR_2, TITLE + SR_2);
        sr2.childOf(ER_2);
        sr3 = new SoftwareRequirement(SR_3, TITLE + SR_3);
        sr3.childOf(ER_3);
        underTest.attach(sr3);
        underTest.attach(sr1);
        underTest.attach(sr2);

        tc1 = new TestCase(TC_1, TITLE + TC_1);
        tc1.childOf(SR_1);
        tc2 = new TestCase(TC_2, TITLE + TC_2);
        tc2.childOf(SR_2);
        tc3 = new TestCase(TC_3, TITLE + TC_3);
        tc3.childOf(SR_3);
        underTest.attach(tc3);
        underTest.attach(tc1);
        underTest.attach(tc2);
    }

    @Test
    public void can_build_the_traceability_matrix() {
        underTest.verify();

        List<TraceabilityMatrixItem> traceabilityMatrix = underTest.buildTraceabilityMatrix();

        Assertions.assertThat(traceabilityMatrix).containsExactly(
                new TraceabilityMatrixItem(er1, sr1, tc1),
                new TraceabilityMatrixItem(er2, sr2, tc2),
                new TraceabilityMatrixItem(er3, sr3, tc3));

        Assertions.assertThat(underTest.warnings()).isEmpty();
    }

    @Test
    public void can_build_the_traceability_matrix_with_n_n_relationships() {

        sr1.childOf(ER_2);
        tc3.childOf(SR_1);

        underTest.verify();

        List<TraceabilityMatrixItem> traceabilityMatrix = underTest.buildTraceabilityMatrix();

        Assertions.assertThat(traceabilityMatrix).containsExactly(
                new TraceabilityMatrixItem(er1, sr1, tc1),
                new TraceabilityMatrixItem(er1, sr1, tc3),
                new TraceabilityMatrixItem(er2, sr1, tc1),
                new TraceabilityMatrixItem(er2, sr1, tc3),
                new TraceabilityMatrixItem(er2, sr2, tc2),
                new TraceabilityMatrixItem(er3, sr3, tc3));

        Assertions.assertThat(underTest.warnings()).isEmpty();
    }

    @Test
    public void can_sort_equipment_requirements_by_identifier() throws Exception {
        Assertions.assertThat(underTest.equipmentRequirements())
                .containsExactly(er1, er2, er3);
    }

    @Test
    public void can_list_equipment_requirements_by_software_requirement() throws Exception {
        underTest.verify();
        Assertions.assertThat(underTest.equipmentRequirements(sr1))
                .containsExactly(er1);
    }

    @Test
    public void can_sort_software_requirements_by_identifier() throws Exception {
        Assertions.assertThat(underTest.softwareRequirements())
                .containsExactly(sr1, sr2, sr3);
    }

    @Test
    public void can_list_software_requirements_by_test_case() throws Exception {
        underTest.verify();
        Assertions.assertThat(underTest.softwareRequirements(tc1))
                .containsExactly(sr1);
    }

    @Test
    public void can_sort_test_cases_by_identifier() throws Exception {
        Assertions.assertThat(underTest.testCases())
                .containsExactly(tc1, tc2, tc3);
    }

    @Test
    public void can_detect_childless_equipment_requirements() {
        EquipmentRequirement equipmentRequirement = new EquipmentRequirement("BARREN", "EQUIPMENT REQUIREMENT");
        underTest.attach(equipmentRequirement);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new ChildlessEquipmentRequirement(equipmentRequirement));
    }

    @Test
    public void can_detect_duplicated_equipment_requirements() {
        EquipmentRequirement equipmentRequirement = new EquipmentRequirement(ER_1, "I am duplicated");
        underTest.attach(equipmentRequirement);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new DuplicatedEquipmentRequirement(equipmentRequirement));
    }

    @Test
    public void can_detect_orphan_and_childless_software_requirements() {
        SoftwareRequirement softwareRequirement = new SoftwareRequirement("ORPHAN", "SOFTWARE REQUIREMENT");
        underTest.attach(softwareRequirement);

        underTest.verify();
        Assertions.assertThat(underTest.warnings())
                .containsExactlyInAnyOrder(
                        new OrphanSoftwareRequirement(softwareRequirement),
                        new ChildlessSoftwareRequirement(softwareRequirement));
    }

    @Test
    public void can_detect_broken_childof_links_in_software_requirements() {
        sr1.childOf(I_AM_UNKNOWN);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new UnknownEquipmentRequirement(sr1, I_AM_UNKNOWN));
    }

    @Test
    public void can_detect_duplicated_software_requirements() {
        SoftwareRequirement softwareRequirement = new SoftwareRequirement(SR_1, "I am duplicated");
        underTest.attach(softwareRequirement);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new DuplicatedSoftwareRequirement(softwareRequirement));
    }


    @Test
    public void can_detect_orphan_test_cases() {
        TestCase testCase = new TestCase("ORPHAN", "TEST CASE");
        underTest.attach(testCase);

        underTest.verify();
        Assertions.assertThat(underTest.warnings())
                .containsExactlyInAnyOrder(
                        new OrphanTestCase(testCase));
    }

    @Test
    public void can_detect_broken_childof_links_in_test_cases() {
        tc1.childOf(I_AM_UNKNOWN);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new UnknownSoftwareRequirement(tc1, I_AM_UNKNOWN));
    }

    @Test
    public void can_detect_duplicated_test_cases() {
        TestCase testCase = new TestCase(TC_1, "I am duplicated");
        underTest.attach(testCase);

        underTest.verify();

        Assertions.assertThat(underTest.warnings())
                .contains(new DuplicatedTestCase(testCase));

    }


}