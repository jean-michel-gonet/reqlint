package com.reqlint.core.latex.loader;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.loader.items.TestCase;

public record TraceabilityMatrixItem(
        EquipmentRequirement equipmentRequirement,
        SoftwareRequirement softwareRequirement,
        TestCase testCase) {}
