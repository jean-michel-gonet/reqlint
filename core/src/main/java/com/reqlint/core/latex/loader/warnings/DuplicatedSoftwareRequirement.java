package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.SoftwareRequirement;

public class DuplicatedSoftwareRequirement extends SpecificationTreeWarning {
    public DuplicatedSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "There are several software requirements with the same identifier: "
                + specificationItem.identifier());
    }

}
