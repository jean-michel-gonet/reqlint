package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.SoftwareRequirement;

public class UnknownEquipmentRequirement extends SpecificationTreeWarning {
    public UnknownEquipmentRequirement(SoftwareRequirement specificationItem, String equipmentRequirementIdentifier) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " is `\\\\childof' an unknown equipment requirement: "
                + equipmentRequirementIdentifier);
    }
}
