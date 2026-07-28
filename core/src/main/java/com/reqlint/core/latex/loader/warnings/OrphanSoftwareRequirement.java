package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.SoftwareRequirement;

public class OrphanSoftwareRequirement extends SpecificationTreeWarning {
    public OrphanSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
