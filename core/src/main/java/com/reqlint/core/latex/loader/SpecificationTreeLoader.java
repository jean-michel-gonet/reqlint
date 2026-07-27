package com.reqlint.core.latex.loader;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.loader.items.TestCase;
import com.reqlint.core.latex.reader.LatexReader;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;

public class SpecificationTreeLoader {
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
                // Look for any environment command in the line:
                MatchingLiteral<SpecificationEnvironment> closestMatch = FindMatchingLiteral
                        .findClosestMatch(SpecificationEnvironment.class, line);

                // If none, then continue to the next line:
                if (closestMatch == null) {
                    continue;
                }

                // Create the appropriate environment command:
                SpecificationItem specificationItem = switch (closestMatch.literal()) {
                    case EQUIPMENT_REQUIREMENT -> new EquipmentRequirement(specificationTree);
                    case SOFTWARE_REQUIREMENT -> new SoftwareRequirement(specificationTree);
                    case TEST_CASE -> new TestCase(specificationTree);
                };

                // Remove from the line what we've already consumed:
                line = line.substring(closestMatch.end());

                // Let the command consume the remainder of the line, and more if needed.
                line = specificationItem.load(line, bufferedReader);
            } while (!line.isEmpty());
        }
        return null;
    }
}
