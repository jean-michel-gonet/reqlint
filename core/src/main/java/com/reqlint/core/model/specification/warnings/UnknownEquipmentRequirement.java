package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.SoftwareRequirement;

public class UnknownEquipmentRequirement extends SpecificationTreeWarning {
    public UnknownEquipmentRequirement(SoftwareRequirement specificationItem, String equipmentRequirementIdentifier) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " is `\\\\childof' an unknown equipment requirement: "
                + equipmentRequirementIdentifier);
    }
}
