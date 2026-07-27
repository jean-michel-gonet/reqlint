package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.SoftwareRequirement;

public class ChildlessSoftwareRequirement extends SpecificationTreeWarning {
    public ChildlessSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no linked test cases");
    }
}
