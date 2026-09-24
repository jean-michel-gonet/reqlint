package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.EquipmentRequirement;

public class ChildlessEquipmentRequirement extends SpecificationTreeWarning {
    public ChildlessEquipmentRequirement(EquipmentRequirement specificationItem) {
        super(specificationItem, "Equipment requirement "
                + specificationItem.identifier()
                + " has no linked software requirements");
    }
}
