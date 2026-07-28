package com.reqlint.core.latex.loader;

import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
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
     * Consumes the specified remainder and the reader.
     * @param line The first line of the content.
     * @param reader The reader to fetch more of the content.
     * @return The remainder of the last line, after consuming all the content.
     */
    public abstract String load(String line, BufferedReader reader) throws IOException;

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
    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @param title The title of this item.
     */
    protected void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
