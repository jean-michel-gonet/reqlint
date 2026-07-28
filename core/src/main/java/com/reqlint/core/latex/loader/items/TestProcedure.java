package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationItem;
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

public class TestProcedure extends SpecificationItem {
    protected static final String TITLE = "Test procedure";

    private enum Patterns implements AssociatedPattern {
        CLOSE(Pattern.compile("\\\\end\\{testprocedure}")),
        STAGE(Pattern.compile("\\\\stage\\s"));

        private final Pattern pattern;

        Patterns(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher find(String line) {
            return pattern.matcher(line);
        }
    }

    private final List<TestStage> stages = new ArrayList<>();

    public TestProcedure(String identifier) {
        super(identifier, TITLE);
    }

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
                    stages.add(new TestStage(stagePosition, stageDescription.toString().trim()));
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

        throw new SpecificationItemNotClosedException(this);
    }

    public List<TestStage> stages() {
        return stages.stream().toList();
    }
}
