package com.reqlint.core.latex.loader.warnings;

import com.reqlint.core.latex.loader.SpecificationItem;

import java.util.Objects;

public class SpecificationTreeWarning {
    private final SpecificationItem specificationItem;
    private final String description;

    protected SpecificationTreeWarning(SpecificationItem specificationItem, String description) {
        this.specificationItem = specificationItem;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpecificationTreeWarning other) {
            return Objects.equals(specificationItem, other.specificationItem)
                    && Objects.equals(description, other.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return specificationItem.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return specificationItem + ": " + description;
    }

    /**
     * @return The specification item associated with this warning.
     */
    public SpecificationItem specificationItem() {
        return specificationItem;
    }

    /**
     * @return The description of the warning.
     */
    public String description() {
        return description;
    }
}
