package com.reqlint.core.model.specification;

import com.reqlint.core.model.specification.items.EquipmentRequirement;
import com.reqlint.core.model.specification.items.SoftwareRequirement;
import com.reqlint.core.model.specification.items.TestCase;

public record TraceabilityMatrixItem(
        EquipmentRequirement equipmentRequirement,
        SoftwareRequirement softwareRequirement,
        TestCase testCase) {}
