package com.reqlint.core.latex.loader;

import com.reqlint.core.specification.SpecificationItem;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Populates the properties of the specification item with the data found in the latex source.
 * @param <T> The specification item type.
 */
public interface SpecificationItemLatexLoader<T extends SpecificationItem> {
    /**
     * Consumes the specified remainder and the reader.
     * @param line The first line of the content.
     * @param reader The reader to fetch more of the content.
     * @return The remainder of the last line, after consuming all the content.
     */
    String load(String line, BufferedReader reader) throws IOException;

    /**
     * @return Convenient access to the loaded specification item.
     */
    public T specificationItem();
}
