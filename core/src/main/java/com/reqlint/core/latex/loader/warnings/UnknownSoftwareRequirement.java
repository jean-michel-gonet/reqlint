package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.TestCase;

public class UnknownSoftwareRequirement extends SpecificationTreeWarning {
    public UnknownSoftwareRequirement(TestCase specificationItem, String identifier) {
        super(specificationItem, "Test case "
                + specificationItem.identifier()
                + " is `\\\\childof' an unknown software requirement: "
                + identifier);
    }
}
