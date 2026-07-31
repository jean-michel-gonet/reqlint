package com.reqlint.core.specification.items;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipmentRequirementTest {
    private EquipmentRequirement underTest;

    @BeforeEach
    void setUp() {
        underTest = new EquipmentRequirement();
    }

    @Test
    public void can_attach_software_requirements() {
        SoftwareRequirement softwareRequirement = new SoftwareRequirement();
        underTest.attach(softwareRequirement);
        Assertions.assertThat(underTest.softwareRequirements()).contains(softwareRequirement);
    }
}