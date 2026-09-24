package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.SpecificationItem;

import java.util.ArrayList;
import java.util.List;

public class TestCase extends SpecificationItem {

    private final List<String> childOf = new ArrayList<>();

    private TestProcedure testProcedure;

    /**
     * Default class constructor.
     */
    public TestCase() {
        super();
    }

    /**
     * Class constructor
     * @param identifier The identifier.
     * @param title The title.
     */
    public TestCase(String identifier, String title) {
        super(identifier, title);
    }

    /**
     * @return The list of {@link SoftwareRequirement#identifier()} this software requirement is child of.
     */
    public List<String> childOf() {
        return childOf.stream().sorted().toList();
    }

    /**
     * Directly adds an identifier in the list of {@link #childOf()}.
     * @param identifier The identifier to add.
     */
    public TestCase childOf(String identifier) {
        this.childOf.add(identifier);
        return this;
    }

    /**
     * @return A list with all attached test cases.
     */
    public TestProcedure testProcedure() {
        return testProcedure;
    }

    /**
     * @param testProcedure The test procedure
     */
    public void testProcedure(TestProcedure testProcedure) {
        this.testProcedure = testProcedure;
    }
}
