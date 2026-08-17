package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.TestCase;

public class OrphanTestCase extends SpecificationTreeWarning {
    public OrphanTestCase(TestCase specificationItem) {
        super(specificationItem, "Test case "
                + specificationItem.identifier()
                + " has no `\\\\childof' indication");
    }
}
