package com.reqlint.core.latex.loader;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.loader.items.TestCase;
import com.reqlint.core.latex.reader.LatexReader;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;

public class SpecificationTreeLoader {
    public enum Patterns implements AssociatedPattern {
        EQUIPMENT_REQUIREMENT(java.util.regex.Pattern.compile("\\\\begin\\{equipmentrequirement}")),
        SOFTWARE_REQUIREMENT(java.util.regex.Pattern.compile("\\\\begin\\{softwarerequirement}")),
        TEST_CASE(java.util.regex.Pattern.compile("\\\\begin\\{testcase}"));

        private final java.util.regex.Pattern pattern;

        Patterns(java.util.regex.Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher find(String line) {
            return pattern.matcher(line);
        }
    }

    private final LatexReader latexReader;

    public SpecificationTreeLoader(LatexReader latexReader) {
        this.latexReader = latexReader;
    }

    SpecificationTree load() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(latexReader);
        SpecificationTree specificationTree = new SpecificationTree();
        String line;
        while( (line = bufferedReader.readLine()) != null) {
            do {
                // Look for any specification item in the line:
                MatchingLiteral<Patterns> closestMatch = FindMatchingLiteral
                        .findClosestMatch(Patterns.class, line);

                // If none, then continue to the next line:
                if (closestMatch == null) {
                    line = "";
                    continue;
                }

                // Create the appropriate specification item:
                SpecificationItem specificationItem = switch (closestMatch.literal()) {
                    case EQUIPMENT_REQUIREMENT -> new EquipmentRequirement();
                    case SOFTWARE_REQUIREMENT -> new SoftwareRequirement();
                    case TEST_CASE -> new TestCase();
                };

                // Attach the specification item to the tree:
                specificationTree.attach(specificationItem);

                // Remove from the line what we've already consumed:
                line = line.substring(closestMatch.end());

                // Let the specification item the remainder of the line, and more if needed.
                line = specificationItem.load(line, bufferedReader);
            } while (!line.isEmpty());
        }
        return specificationTree;
    }
}
