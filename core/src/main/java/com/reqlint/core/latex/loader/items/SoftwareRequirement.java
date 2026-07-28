package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationItem;
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
    private static final Pattern ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");
    private enum Patterns implements AssociatedPattern {
        CLOSE(Pattern.compile("\\\\end\\{softwarerequirement}")),
        CHILD_OF(Pattern.compile("\\\\childof\\{([^}]+)}"));

        private final Pattern pattern;

        Patterns(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher find(String line) {
            return pattern.matcher(line);
        }
    }

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

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        setIdentifier(matcher.group(1));
        setTitle(matcher.group(2));

        // The content of the requirement starts at the end of the arguments:
        line = line.substring(matcher.end());

        // Consume the rest of the content:
        do {
            while (!line.isEmpty()) {
                MatchingLiteral<Patterns> matchingLiteral =
                        FindMatchingLiteral.findClosestMatch(Patterns.class, line);
                if (matchingLiteral == null) {
                    break;
                }
                line = line.substring(matchingLiteral.end());
                switch (matchingLiteral.literal()) {
                    case CHILD_OF -> childOf.add(matchingLiteral.group(1));
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(this);
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
