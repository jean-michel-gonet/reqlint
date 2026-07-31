package com.reqlint.core.specification.warnings;

import com.reqlint.core.specification.items.TestCase;

public class OrphanTestCase extends SpecificationTreeWarning {
    public OrphanTestCase(TestCase specificationItem) {
        super(specificationItem, "Test case "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
