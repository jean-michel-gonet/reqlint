package com.reqlint.sandbox.cucumber.renderers;

import java.util.List;

/**
 * Defines the contract for turning any collection of entities into a raw grid of strings
 * @param <T> The entity to render.
 */
public interface Tablifier<T> {

    /**
     * @return The list of all column header names.
     */
    List<String> getHeaders();

    /**
     * Renders the specified entity as a list of strings.
     * @param entity The entity.
     * @return A list of strings.
     */
    List<String> getRow(T entity);
}
