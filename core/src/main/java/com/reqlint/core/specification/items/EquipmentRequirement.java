package com.reqlint.core.specification.items;

import com.reqlint.core.specification.SpecificationItem;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquipmentRequirement extends SpecificationItem  {

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
