package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.SpecificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Test procedure holds an ordered collection of {@link TestStage} that belongs to a {@link TestCase}.
 */
public class TestProcedure extends SpecificationItem {
    public static final String TITLE = "Test procedure";

    private final List<TestStage> stages = new ArrayList<>();

    /**
     * Class constructor.
     * @param identifier Normally the identifier of the test case.
     */
    public TestProcedure(String identifier) {
        super(identifier, TITLE);
    }

    /**
     * @param stage Adds the specified stage to this test procedure.
     */
    public void stage(TestStage stage) {
        stages.add(stage);
    }

    /**
     * @return The stages held by this test procedure.
     */
    public List<TestStage> stages() {
        return stages.stream().toList();
    }
}
