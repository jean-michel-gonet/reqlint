package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.EquipmentRequirement;

public class ChildlessEquipmentRequirement extends SpecificationTreeWarning {
    public ChildlessEquipmentRequirement(EquipmentRequirement specificationItem) {
        super(specificationItem, "Equipment requirement "
                + specificationItem.identifier()
                + " has no linked software requirements");
    }
}
