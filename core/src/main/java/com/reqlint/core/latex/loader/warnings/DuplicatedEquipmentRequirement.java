package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;

public class DuplicatedEquipmentRequirement extends SpecificationTreeWarning {
    public DuplicatedEquipmentRequirement(EquipmentRequirement specificationItem) {
        super(specificationItem, "There are several equipment requirements with the same identifier: "
                + specificationItem.identifier());
    }

}
