package com.reqlint.core.latex.loader.itemloaders;

import com.reqlint.core.latex.loader.SpecificationItemLatexLoader;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.specification.items.EquipmentRequirement;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Populates the properties of the specification item with the data found in the latex source.
 */
public class EquipmentRequirementLatexLoader implements SpecificationItemLatexLoader<EquipmentRequirement> {
    private static final Pattern ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");
    private static final Pattern CLOSE_EQUIPMENT_REQUIREMENT = Pattern.compile("\\\\end\\{equipmentrequirement}");

    private final EquipmentRequirement equipmentRequirement = new  EquipmentRequirement();

    @Override
    public String load(String line, BufferedReader reader) throws IOException {
        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        equipmentRequirement.setIdentifier(matcher.group(1));
        equipmentRequirement.setTitle(matcher.group(2));

        // The content of the requirement starts at the end of the arguments:
        line = line.substring(matcher.end());

        // Consume the content until the close pattern:
        while (!(matcher = CLOSE_EQUIPMENT_REQUIREMENT.matcher(line)).find()) {
            line = reader.readLine();
            // If we reach EOF before the close pattern, then raise an exception:
            if (line == null) {
                throw new SpecificationItemNotClosedException(equipmentRequirement);
            }
        }

        // The rest of the content starts at the end of the close pattern:
        line = line.substring(matcher.end());
        return line;
    }

    @Override
    public EquipmentRequirement specificationItem() {
        return equipmentRequirement;
    }
}
