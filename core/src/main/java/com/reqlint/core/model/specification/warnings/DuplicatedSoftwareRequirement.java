package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.SoftwareRequirement;

public class DuplicatedSoftwareRequirement extends SpecificationTreeWarning {
    public DuplicatedSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "There are several software requirements with the same identifier: "
                + specificationItem.identifier());
    }

}
