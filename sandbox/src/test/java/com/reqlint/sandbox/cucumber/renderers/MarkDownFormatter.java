package com.reqlint.sandbox.cucumber.renderers;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the provided list of entities as a mark-down table
 * using the provided {@link Tablifier}.
 * The rendering is done in the {@link #toString()} method, so it is optimized for logging.
 * @param <T> The rendered entity.
 * @see Tablifier
 */
public class MarkDownFormatter<T> {

    private final List<String> headers;
    private final List<List<String>> rows = new ArrayList<>();

    /**
     * Use this constructor if you have a list of entities to render.
     * @param data The data to render.
     * @param tablifier The tablifier to use.
     */
    public MarkDownFormatter(List<T> data, Tablifier<T> tablifier) {
        this.headers = tablifier.getHeaders();
        for (T item : data) {
            this.rows.add(tablifier.getRow(item));
        }
    }

    /**
     * Use this constructor if you have a single entity to render.
     * @param data The data to render.
     * @param tablifier The tablifier to use.
     */
    public MarkDownFormatter(T data, Tablifier<T> tablifier) {
        this(List.of(data), tablifier);
    }

    @Override
    public String toString() {
        if (headers == null || headers.isEmpty()) {
            return "";
        }

        // 1. Calculate optimal column widths
        int[] colWidths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            colWidths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                String val = row.get(i) != null ? row.get(i) : "null";
                colWidths[i] = Math.max(colWidths[i], val.length());
            }
        }

        StringBuilder sb = new StringBuilder("\n");

        // 2. Render Header Row
        appendRow(sb, headers, colWidths);

        // 3. Render Markdown Separator Line (|---|---|)
        sb.append("|");
        for (int width : colWidths) {
            sb.append("-".repeat(width + 2)).append("|");
        }
        sb.append("\n");

        // 4. Render Data Rows
        for (List<String> row : rows) {
            appendRow(sb, row, colWidths);
        }

        return sb.toString();
    }

    private void appendRow(StringBuilder sb, List<String> values, int[] widths) {
        sb.append("|");
        for (int i = 0; i < widths.length; i++) {
            String val = (i < values.size() && values.get(i) != null) ? values.get(i) : "";
            // Left-align text with padding matching calculated max column widths
            sb.append(String.format(" %-" + widths[i] + "s |", val));
        }
        sb.append("\n");
    }
}