package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.SoftwareRequirement;

public class ChildlessSoftwareRequirement extends SpecificationTreeWarning {
    public ChildlessSoftwareRequirement(SoftwareRequirement specificationItem) {
        super(specificationItem, "Software requirement "
                + specificationItem.identifier()
                + " has no linked test cases");
    }
}
