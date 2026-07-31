package com.reqlint.core.specification;

import com.reqlint.core.specification.items.EquipmentRequirement;
import com.reqlint.core.specification.items.SoftwareRequirement;
import com.reqlint.core.specification.items.TestCase;

public record TraceabilityMatrixItem(
        EquipmentRequirement equipmentRequirement,
        SoftwareRequirement softwareRequirement,
        TestCase testCase) {}
