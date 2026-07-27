package com.reqlint.core.testutils;

import java.io.PrintWriter;
import java.io.StringWriter;

public record LogRecord(LogLevel logLevel, CharSequence charSequence, Throwable throwable) {
    public LogRecord(LogLevel logLevel, CharSequence charSequence) {
        this(logLevel, charSequence, null);
    }

    public LogRecord(LogLevel logLevel, Throwable throwable) {
        this(logLevel, null, throwable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(logLevel.name());

        if (charSequence != null) {
            sb.append(" - ").append(charSequence);
        }

        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            sb.append("\n").append(stringWriter);
        }

        return sb.toString();
    }
}
