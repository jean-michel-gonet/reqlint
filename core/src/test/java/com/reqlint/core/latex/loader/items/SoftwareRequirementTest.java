package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import static com.reqlint.core.testutils.TextFileContent.CRLF;
import static com.reqlint.core.testutils.TextFileContent.stringReaderOf;

class SoftwareRequirementTest {
    private static final String BLA = "BLA";
    private static final String CHILD_OF_1 = "ER101";
    private static final String CHILD_OF_2 = "ER102";
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String TITLE = "TITLE";
    private static final String EXPECTED_REMAINDER = "Expected remainder";

    private SpecificationTree specificationTree;
    private SoftwareRequirement underTest;

    @BeforeEach
    public void setUp() {
        specificationTree = new SpecificationTree();
        underTest = new SoftwareRequirement(specificationTree);
    }

    @Test
    public void can_attach_itself_to_the_specification_tree() {
        Assertions.assertThat(specificationTree.softwareRequirements())
                .contains(underTest);
    }

    @Test
    public void can_load_its_content() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                BLA,
                CRLF,
                String.format("\\childof{%s}", CHILD_OF_2),
                CRLF,
                BLA,
                CRLF,
                String.format("\\childof{%s}", CHILD_OF_1),
                CRLF,
                "\\end{softwarerequirement}",
                EXPECTED_REMAINDER));

        String actualReminder = underTest.load(line, reader);

        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TITLE);
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);
        Assertions.assertThat(underTest.childOf()).containsExactly(CHILD_OF_1, CHILD_OF_2);
    }

    @Test
    public void can_load_its_content_when_it_is_all_in_the_same_line() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                BLA,
                String.format("\\childof{%s}", CHILD_OF_2),
                BLA,
                String.format("\\childof{%s}", CHILD_OF_1),
                "\\end{softwarerequirement}",
                EXPECTED_REMAINDER));

        String actualReminder = underTest.load(line, reader);

        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TITLE);
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);
        Assertions.assertThat(underTest.childOf()).containsExactly(CHILD_OF_1, CHILD_OF_2);
    }

    @Test
    public void raises_an_exception_if_the_arguments_are_broken() {
        String line = String.format("{%s}%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                BLA));

        Assertions.assertThatExceptionOfType(SpecificationItemMissingArgumentsException.class)
                .isThrownBy(() -> underTest.load(line, reader));
    }

    @Test
    public void raises_an_exception_if_there_is_no_closing_tag() {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(BLA));

        Assertions.assertThatExceptionOfType(SpecificationItemNotClosedException.class)
                .isThrownBy(() -> underTest.load(line, reader));
    }

    @Test
    public void can_attach_test_cases() {
        TestCase testCase = new TestCase(specificationTree);
        underTest.attach(testCase);
        Assertions.assertThat(underTest.testCases()).contains(testCase);
    }

    @Test
    public void can_sort_alphabetically_by_identifier() throws Exception {
        SoftwareRequirement underTest1 = new SoftwareRequirement(specificationTree);
        underTest1.load(String.format("{%s}{%s}\\end{softwarerequirement}", "CCC", TITLE), null);

        SoftwareRequirement underTest2 = new SoftwareRequirement(specificationTree);
        underTest2.load(String.format("{%s}{%s}\\end{softwarerequirement}", "AAA", TITLE), null);

        SoftwareRequirement underTest3 = new SoftwareRequirement(specificationTree);
        underTest3.load(String.format("{%s}{%s}\\end{softwarerequirement}", "BBB", TITLE), null);

        Assertions.assertThat(specificationTree.softwareRequirements())
                .containsExactly(underTest2, underTest3, underTest1, underTest);
    }
}