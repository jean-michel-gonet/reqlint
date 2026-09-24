package com.reqlint.core.input.latex.loader.itemloaders;

import com.reqlint.core.input.latex.loader.SpecificationItemLatexLoader;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.model.specification.items.TestProcedure;
import com.reqlint.core.model.specification.items.TestStage;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestProcedureLatexLoader implements SpecificationItemLatexLoader<TestProcedure> {

    private enum Patterns implements AssociatedPattern {
        CLOSE(Pattern.compile("\\\\end\\{testprocedure}")),
        STAGE(Pattern.compile("\\\\stage\\s"));

        private final Pattern pattern;

        Patterns(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    private final TestProcedure testProcedure;

    /**
     * Class constructor.
     * @param identifier The identifier of the test case owning the test procedure.
     */
    public TestProcedureLatexLoader(String identifier) {
        this.testProcedure = new  TestProcedure(identifier);
    }

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        int stagePosition = 0;
        StringBuilder stageDescription = new StringBuilder();
        do {
            while (!line.isEmpty()) {
                MatchingLiteral<Patterns> matchingLiteral =
                        FindMatchingLiteral.findClosestMatch(Patterns.class, line);
                if (matchingLiteral == null) {
                    if (stagePosition > 0) {
                        stageDescription.append(' ').append(line);
                    }
                    break;
                }
                if (stagePosition > 0) {
                    stageDescription.append(' ').append(line, 0, matchingLiteral.start());
                    testProcedure.stage(new TestStage(stagePosition, stageDescription.toString().trim()));
                }
                line = line.substring(matchingLiteral.end());
                switch (matchingLiteral.literal()) {
                    case STAGE -> {
                        stagePosition++;
                        stageDescription = new StringBuilder();
                    }
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(testProcedure);
    }

    @Override
    public TestProcedure specificationItem() {
        return testProcedure;
    }

}
