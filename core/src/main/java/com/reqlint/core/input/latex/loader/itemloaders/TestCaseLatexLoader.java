package com.reqlint.core.input.latex.loader.itemloaders;

import com.reqlint.core.input.latex.loader.LatexPatterns;
import com.reqlint.core.input.latex.loader.SpecificationItemLatexLoader;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCaseLatexLoader implements SpecificationItemLatexLoader<TestCase> {

    private enum Patterns implements AssociatedPattern {
        CLOSE(LatexPatterns.CLOSE_TEST_CASE),
        TEST_PROCEDURE(Pattern.compile("\\\\begin\\{testprocedure}")),
        CHILD_OF(Pattern.compile("\\\\childof\\{([^}]+)}"));

        private final Pattern pattern;

        Patterns(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    private final TestCase testCase = new TestCase();

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = LatexPatterns.TWO_ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        testCase.setIdentifier(matcher.group(1));
        testCase.setTitle(matcher.group(2));

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
                    case CHILD_OF -> testCase.childOf(matchingLiteral.group(1));
                    case TEST_PROCEDURE -> {
                        TestProcedureLatexLoader testProcedureLoader = new TestProcedureLatexLoader(testCase.identifier());
                        line = testProcedureLoader.load(line, reader);
                        testCase.testProcedure(testProcedureLoader.specificationItem());
                    }
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(testCase);
    }

    @Override
    public TestCase specificationItem() {
        return testCase;
    }

}
