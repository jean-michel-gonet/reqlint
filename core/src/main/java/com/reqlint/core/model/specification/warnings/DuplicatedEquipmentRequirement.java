package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.EquipmentRequirement;

public class DuplicatedEquipmentRequirement extends SpecificationTreeWarning {
    public DuplicatedEquipmentRequirement(EquipmentRequirement specificationItem) {
        super(specificationItem, "There are several equipment requirements with the same identifier: "
                + specificationItem.identifier());
    }

}
