package com.reqlint.core.input.latex.loader;

import com.reqlint.core.input.latex.loader.itemloaders.EquipmentRequirementLatexLoader;
import com.reqlint.core.input.latex.loader.itemloaders.SoftwareRequirementLatexLoader;
import com.reqlint.core.input.latex.loader.itemloaders.TestCaseLatexLoader;
import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    private final SpecificationTree specificationTree;

    /**
     * Default class constructor.
     */
    public SpecificationTreeLoader() {
        this(new SpecificationTree());
    }

    /**
     * Use this constructor if you want to complete an existing {@link SpecificationTree}.
     * @param specificationTree The specification tree to complete.
     */
    public SpecificationTreeLoader(SpecificationTree specificationTree) {
        this.specificationTree = specificationTree;
    }

    public void load(Reader latexReader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(latexReader);
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
                SpecificationItemLatexLoader<?> specificationItemLatexLoader = switch (closestMatch.literal()) {
                    case EQUIPMENT_REQUIREMENT -> new EquipmentRequirementLatexLoader();
                    case SOFTWARE_REQUIREMENT -> new SoftwareRequirementLatexLoader();
                    case TEST_CASE -> new TestCaseLatexLoader();
                };

                // Attach the specification item to the tree:
                specificationTree.attach(specificationItemLatexLoader.specificationItem());

                // Remove from the line what we've already consumed:
                line = line.substring(closestMatch.end());

                // Let the specification item the remainder of the line, and more if needed.
                line = specificationItemLatexLoader.load(line, bufferedReader);
            } while (!line.isEmpty());
        }
    }

    /**
     * @return The loaded specification tree.
     */
    public SpecificationTree specificationTree() {
        return specificationTree;
    }
}
