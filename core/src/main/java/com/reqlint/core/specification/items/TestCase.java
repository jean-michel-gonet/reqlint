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
