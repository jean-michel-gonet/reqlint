package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.SoftwareRequirement;

public class ChildlessSoftwareRequirement extends SpecificationTreeWarning {
    public ChildlessSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no linked test cases");
    }
}
