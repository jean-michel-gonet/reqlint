package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.items.EquipmentRequirement;
import com.reqlint.core.model.specification.items.SoftwareRequirement;
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