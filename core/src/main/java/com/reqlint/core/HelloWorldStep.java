package com.reqlint.core;

// No external logging dependency

public class HelloWorldStep implements Step {
    @Override
    public void execute(ExecutionContext ctx) {
        ctx.getLog().info("Hello World");
    }
}
