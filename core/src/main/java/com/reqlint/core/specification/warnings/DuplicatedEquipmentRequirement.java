package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.EquipmentRequirement;

public class DuplicatedEquipmentRequirement extends SpecificationTreeWarning {
    public DuplicatedEquipmentRequirement(EquipmentRequirement specificationItem) {
        super(specificationItem, "There are several equipment requirements with the same identifier: "
                + specificationItem.identifier());
    }

}
