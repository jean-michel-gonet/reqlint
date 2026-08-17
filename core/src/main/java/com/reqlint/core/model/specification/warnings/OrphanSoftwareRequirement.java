package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.SoftwareRequirement;

public class OrphanSoftwareRequirement extends SpecificationTreeWarning {
    public OrphanSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
