package com.reqlint.core.specification;

import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Describes
 */
public abstract class SpecificationItem implements Comparable<SpecificationItem> {
    private static final Comparator<SpecificationItem> COMPARATOR = Comparator
            .comparing(SpecificationItem::identifier, Comparator.nullsLast(Comparator.naturalOrder()));

    private String identifier;
    private String title;
    private boolean concernsSecurity = false;
    private boolean concernsSafety = false;
    private String status;
    private boolean derived = false;
    private String derivedRationale;

    public SpecificationItem() {
        // Nothing to do
    }

    public SpecificationItem(String identifier, String title) {
        this.identifier = identifier;
        this.title = title;
    }

    @Override
    public int compareTo(@NonNull SpecificationItem o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.getClass().isAssignableFrom(obj.getClass())) {
            SpecificationItem other = (SpecificationItem) obj;
            return Objects.equals(identifier, other.identifier) && Objects.equals(title, other.title);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode() + title.hashCode();
    }

    /**
     * @return The identifier of this item.
     */
    public String identifier() {
        return identifier;
    }

    /**
     * @return The title of this item.
     */
    public String title() {
        return title;
    }

    /**
     * @param identifier The identifier of this item.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @param title The title of this item.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Does this item concern security.
     */
    public boolean concernsSecurity() {
        return concernsSecurity;
    }

    /**
     * @param concernsSecurity Does this item concern security.
     */
    public void setConcernsSecurity(boolean concernsSecurity) {
        this.concernsSecurity = concernsSecurity;
    }

    /**
     * @return Does this item concern safety?
     */
    public boolean concernsSafety() {
        return concernsSafety;
    }

    /**
     * @param concernsSafety Does this item concern safety.
     */
    public void setConcernsSafety(boolean concernsSafety) {
        this.concernsSafety = concernsSafety;
    }

    /**
     * @return The status
     */
    public String status() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Is this item derived.
     */
    public boolean derived() {
        return derived;
    }

    /**
     * @return Why the item is derived.
     */
    public String derivedRationale() {
        return derivedRationale;
    }

    /**
     * Specifies if the item is derived, and associates a rationale.
     * @param derived The derived
     * @param derivedRationale Why the item is derived.
     */
    public void setDerived(boolean derived, String derivedRationale) {
        this.derived = derived;
        this.derivedRationale = derivedRationale;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
