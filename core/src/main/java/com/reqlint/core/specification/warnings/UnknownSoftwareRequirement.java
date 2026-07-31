package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.TestCase;

public class UnknownSoftwareRequirement extends SpecificationTreeWarning {
    public UnknownSoftwareRequirement(TestCase specificationItem, String identifier) {
        super(specificationItem, "Test case "
                + specificationItem.identifier()
                + " is `\\\\childof' an unknown software requirement: "
                + identifier);
    }
}
