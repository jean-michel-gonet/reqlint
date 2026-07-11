package com.reqlint.core;

import java.util.ArrayList;
import java.util.List;

public class StepFactory {
    public List<Step> buildChain(ExecutionContext ctx) {
        List<Step> steps = new ArrayList<>();
        // Fixed order: HelloWorldStep first
        steps.add(new HelloWorldStep());
        return steps;
    }
}
