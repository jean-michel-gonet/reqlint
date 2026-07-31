package com.reqlint.core.latex.loader.itemloaders;

import com.reqlint.core.latex.loader.SpecificationItemLatexLoader;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.specification.items.SoftwareRequirement;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Populates the properties of the specification item with the data found in the latex source.
 */
public class SoftwareRequirementLatexLoader implements SpecificationItemLatexLoader<SoftwareRequirement> {
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

    private final SoftwareRequirement softwareRequirement = new  SoftwareRequirement();

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        softwareRequirement.setIdentifier(matcher.group(1));
        softwareRequirement.setTitle(matcher.group(2));

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
                    case CHILD_OF -> softwareRequirement.childOf(matchingLiteral.group(1));
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(softwareRequirement);
    }

    @Override
    public SoftwareRequirement specificationItem() {
        return softwareRequirement;
    }
}
