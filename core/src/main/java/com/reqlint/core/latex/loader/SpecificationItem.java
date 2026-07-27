package com.reqlint.core.latex.loader;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Describes
 */
public interface SpecificationItem {
    /**
     * Consumes the specified remainder and the reader.
     * @param remainder
     * @param reader
     * @return The remainder.
     */
    String load(String remainder, BufferedReader reader) throws IOException;

    /**
     * @return The identifier of this item.
     */
    String identifier();

    /**
     * @return The title of this item.
     */
    String title();


}
