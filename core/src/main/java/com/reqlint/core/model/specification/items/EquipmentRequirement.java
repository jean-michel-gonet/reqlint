package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.SpecificationItem;

import java.util.ArrayList;
import java.util.List;

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
