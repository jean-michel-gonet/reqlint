package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationItem;
import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCase implements SpecificationItem, Comparable<TestCase> {
    private static final Comparator<TestCase> COMPARATOR = Comparator
            .comparing(TestCase::identifier, Comparator.nullsLast(Comparator.naturalOrder()));
    private static final Pattern ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");
    private enum Patterns implements AssociatedPattern {
        CLOSE(Pattern.compile("\\\\end\\{testcase}")),
        TEST_PROCEDURE(Pattern.compile("\\\\begin\\{testprocedure}")),
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

    private TestProcedure testProcedure;

    private String identifier;
    private String title;

    public TestCase(SpecificationTree specificationTree) {
        specificationTree.attach(this);
    }

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        identifier = matcher.group(1);
        title = matcher.group(2);

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
                    case TEST_PROCEDURE -> {
                        testProcedure = new TestProcedure(identifier);
                        line = testProcedure.load(line, reader);
                    }
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(this);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String title() {
        return title;
    }

    /**
     * @return The list of {@link SoftwareRequirement#identifier()} this software requirement is child of.
     */
    public List<String> childOf() {
        return childOf.stream().sorted().toList();
    }

    /**
     * @return A list with all attached test cases.
     */
    public TestProcedure testCaseProcedure() {
        return testProcedure;
    }

    @Override
    public int compareTo(@NonNull TestCase o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "Test case: " + identifier + " - " + title;
    }
}
