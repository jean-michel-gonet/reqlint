package com.reqlint.core.util;

import org.apache.maven.plugin.logging.Log;
import java.util.ArrayList;
import java.util.List;

import static com.reqlint.core.util.LogLevel.*;

/**
 * Simple in‑memory implementation of {@link Log} for unit tests.
 * Captures all messages sent to {@link #info} in a list.
 */
public class InMemoryLog implements Log {
    private final List<LogRecord> messages = new ArrayList<>();

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(CharSequence charSequence) {
        messages.add(new LogRecord(DEBUG, charSequence));
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        messages.add(new LogRecord(DEBUG, charSequence, throwable));
    }

    @Override
    public void debug(Throwable throwable) {
        messages.add(new LogRecord(DEBUG, throwable));
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(CharSequence charSequence) {
        messages.add(new LogRecord(INFO, charSequence));
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        messages.add(new LogRecord(INFO, charSequence, throwable));
    }

    @Override
    public void info(Throwable throwable) {
        messages.add(new LogRecord(INFO, throwable));
    }

    @Override public boolean isWarnEnabled() { return true; }

    @Override
    public void warn(CharSequence charSequence) {

    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void warn(Throwable throwable) {

    }

    @Override public boolean isErrorEnabled() { return true; }

    @Override
    public void error(CharSequence charSequence) {

    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void error(Throwable throwable) {

    }

    public List<LogRecord> getMessages() {
        return messages;
    }
}
