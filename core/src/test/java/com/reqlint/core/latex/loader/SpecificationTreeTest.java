package com.reqlint.core.latex.loader;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecificationTreeTest {
    private SpecificationTree underTest;

    @BeforeEach
    public void setUp() {
        underTest = new SpecificationTree();
    }
    @Test
    public void can_do_something_nice() {
        EquipmentRequirement equipmentRequirement = new EquipmentRequirement(underTest);
    }

}