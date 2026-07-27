package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.items.TestCase;

public class OrphanTestCase extends SpecificationTreeWarning {
    public OrphanTestCase(TestCase specificationItem) {
        super(specificationItem, "Test case "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
