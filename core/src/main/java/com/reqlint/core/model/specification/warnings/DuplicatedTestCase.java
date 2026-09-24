package com.reqlint.core.model.specification.warnings;

import com.reqlint.core.model.specification.items.TestCase;

public class DuplicatedTestCase extends SpecificationTreeWarning {
    public DuplicatedTestCase(TestCase specificationItem) {
        super(specificationItem, "There are several test cases with the same identifier: "
                + specificationItem.identifier());
    }

}
