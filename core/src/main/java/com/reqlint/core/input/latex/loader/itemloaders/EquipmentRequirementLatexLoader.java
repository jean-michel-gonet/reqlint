package com.reqlint.core.input.latex.loader.itemloaders;

import com.reqlint.core.input.latex.loader.LatexPatterns;
import com.reqlint.core.input.latex.loader.SpecificationItemLatexLoader;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.model.specification.items.EquipmentRequirement;
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
public class EquipmentRequirementLatexLoader implements SpecificationItemLatexLoader<EquipmentRequirement> {

    private enum Patterns implements AssociatedPattern {
        CLOSE(Pattern.compile("\\\\end\\{equipmentrequirement}")),
        STATUS(LatexPatterns.STATUS),
        DERIVED(LatexPatterns.DERIVED),
        SECURITY(LatexPatterns.SECURITY),
        SAFETY(LatexPatterns.SAFETY);

        private final Pattern pattern;

        Patterns(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    private final EquipmentRequirement equipmentRequirement = new  EquipmentRequirement();

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = LatexPatterns.TWO_ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        equipmentRequirement.setIdentifier(matcher.group(1));
        equipmentRequirement.setTitle(matcher.group(2));

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
                    case DERIVED -> equipmentRequirement.setDerived(true, matchingLiteral.group(1));
                    case STATUS -> equipmentRequirement.setStatus(matchingLiteral.group(1));
                    case SAFETY -> equipmentRequirement.setConcernsSafety(true);
                    case SECURITY -> equipmentRequirement.setConcernsSecurity(true);
                    case CLOSE -> {
                        return line;
                    }
                }
            }
        } while ( (line = reader.readLine()) != null);

        throw new SpecificationItemNotClosedException(equipmentRequirement);
    }

    @Override
    public EquipmentRequirement specificationItem() {
        return equipmentRequirement;
    }
}
