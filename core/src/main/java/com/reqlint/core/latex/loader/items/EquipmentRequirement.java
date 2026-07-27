package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationItem;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquipmentRequirement extends SpecificationItem  {
    private static final Pattern ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");
    private static final Pattern CLOSE_EQUIPMENT_REQUIREMENT = Pattern.compile("\\\\end\\{equipmentrequirement}");

    private final List<SoftwareRequirement> softwareRequirements = new ArrayList<>();

    /**
     * Default constructor.
     */
    public EquipmentRequirement() {
        super();
    }

    public EquipmentRequirement(String identifier, String title) {
        super(identifier, title);
    }

    @Override
    public String load(String line, BufferedReader reader) throws IOException  {

        // Obtain the arguments from the remainder:
        Matcher matcher = ARGUMENTS.matcher(line);
        if (!matcher.find()) {
            throw new SpecificationItemMissingArgumentsException(line);
        }
        setIdentifier(matcher.group(1));
        setTitle(matcher.group(2));

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
}
