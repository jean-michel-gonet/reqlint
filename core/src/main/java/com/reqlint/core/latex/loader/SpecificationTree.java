package com.reqlint.core.latex.loader;

import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.loader.items.TestCase;

import java.util.ArrayList;
import java.util.List;

public class SpecificationTree {
    private final List<EquipmentRequirement> equipmentRequirements = new ArrayList<>();
    private final List<SoftwareRequirement> softwareRequirements = new ArrayList<>();
    private final List<TestCase> testCases = new ArrayList<>();

    public List<EquipmentRequirement> equipmentRequirements() {
        return equipmentRequirements.stream().sorted().toList();
    }

    public List<SoftwareRequirement> softwareRequirements() {
        return softwareRequirements.stream().sorted().toList();
    }

    public List<TestCase> testCases() {
        return testCases.stream().sorted().toList();
    }

    public void attach(EquipmentRequirement equipmentRequirement) {
        equipmentRequirements.add(equipmentRequirement);
    }

    public void attach(SoftwareRequirement softwareRequirement) {
        softwareRequirements.add(softwareRequirement);
    }

    public void attach(TestCase testCase) {
        this.testCases.add(testCase);
    }

    public void verify() {

    }

}
