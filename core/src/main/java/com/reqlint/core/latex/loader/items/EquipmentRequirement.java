package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationItem;
import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquipmentRequirement implements SpecificationItem, Comparable<EquipmentRequirement> {
    private static final Comparator<EquipmentRequirement> COMPARATOR = Comparator
            .comparing(EquipmentRequirement::identifier, Comparator.nullsLast(Comparator.naturalOrder()));
    private static final Pattern ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");
    private static final Pattern CLOSE_EQUIPMENT_REQUIREMENT = Pattern.compile("\\\\end\\{equipmentrequirement}");

    private final List<SoftwareRequirement> softwareRequirements = new ArrayList<>();

    private String identifier;
    private String title;

    public EquipmentRequirement(SpecificationTree specificationTree) {
        specificationTree.attach(this);
    }

    @Override
    public String load(String line, BufferedReader reader) throws IOException  {

        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        identifier = matcher.group(1);
        title = matcher.group(2);

        // The content of the requirement starts at the end of the arguments:
        line = line.substring(matcher.end());

        // Consume the content until the close pattern:
        while (!(matcher = CLOSE_EQUIPMENT_REQUIREMENT.matcher(line)).find()) {
            line = reader.readLine();
            // If we reach EOF before the close pattern, then raise an exception:
            if (line == null) {
                throw new SpecificationItemNotClosedException(this);
            }
        }

        // The rest of the content starts at the end of the close pattern:
        line = line.substring(matcher.end());
        return line;
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
     * Attach a software requirement to this equipment requirement.
     * @param softwareRequirement The software requirement.
     */
    public void attach(SoftwareRequirement softwareRequirement) {
        softwareRequirements.add(softwareRequirement);
    }

    /**
     * @return A list with all attached software requirements
     */
    public List<SoftwareRequirement> softwareRequirements() {
        return softwareRequirements.stream().sorted().toList();
    }

    @Override
    public int compareTo(EquipmentRequirement o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "Equipment requirement: " + identifier + " - " + title;
    }
}
