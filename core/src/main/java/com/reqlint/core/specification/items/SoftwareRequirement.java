package com.reqlint.core.specification.items;

import com.reqlint.core.specification.SpecificationItem;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
