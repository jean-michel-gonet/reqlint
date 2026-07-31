package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.SoftwareRequirement;

public class OrphanSoftwareRequirement extends SpecificationTreeWarning {
    public OrphanSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
