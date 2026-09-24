package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.SpecificationItem;

import java.util.ArrayList;
import java.util.List;

public class SoftwareRequirement extends SpecificationItem {

    private final List<String> childOf = new ArrayList<>();

    private final List<TestCase> testCases = new ArrayList<>();

    /**
     * Default class constructor.
     */
    public SoftwareRequirement() {
        super();
    }

    /**
     * Class constructor setting properties.
     * @param identifier The identifier.
     * @param title The title.
     */
    public SoftwareRequirement(String identifier, String title) {
        super(identifier, title);
    }


    /**
     * @return The list of {@link EquipmentRequirement#identifier()} this software requirement is child of.
     */
    public List<String> childOf() {
        return childOf.stream().sorted().toList();
    }

    /**
     * Directly adds an identifier in the list of {@link #childOf()}.
     * @param identifier The identifier to add.
     */
    public SoftwareRequirement childOf(String identifier) {
        this.childOf.add(identifier);
        return this;
    }

    /**
     * Attach a test case to this software requirement.
     * @param testCase The test case.
     */
    public void attach(TestCase testCase) {
        testCases.add(testCase);
    }

    /**
     * @return A list with all attached test cases.
     */
    public List<TestCase> testCases() {
        return testCases.stream().sorted().toList();
    }
}
