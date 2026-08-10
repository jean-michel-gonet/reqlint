package com.reqlint.core.latex.loader.itemloaders;

import com.reqlint.core.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.specification.items.EquipmentRequirement;
import com.reqlint.core.specification.items.SoftwareRequirement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import static com.reqlint.core.testutils.TextFileContent.CRLF;
import static com.reqlint.core.testutils.TextFileContent.stringReaderOf;

class SoftwareRequirementLatexLoaderTest {
    private static final String BLA = "BLA";
    private static final String CHILD_OF_1 = "ER101";
    private static final String CHILD_OF_2 = "ER102";
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String TITLE = "TITLE";
    private static final String EXPECTED_REMAINDER = "Expected remainder";
    private static final String DERIVED_RATIONALE = "Because of something rational";
    private static final String STATUS = "Flabbergasted";


    private SoftwareRequirementLatexLoader underTest;

    @BeforeEach
    public void setUp() {
        underTest = new SoftwareRequirementLatexLoader();
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
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(softwareRequirement.title()).isEqualTo(TITLE);
        Assertions.assertThat(softwareRequirement.childOf()).containsExactly(CHILD_OF_1, CHILD_OF_2);
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
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(softwareRequirement.title()).isEqualTo(TITLE);
        Assertions.assertThat(softwareRequirement.childOf()).containsExactly(CHILD_OF_1, CHILD_OF_2);
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
    public void can_be_derived() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                String.format("\\derived{%s}\r\n", DERIVED_RATIONALE),
                "\\end{softwarerequirement}"));

        underTest.load(line, reader);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.derived()).isTrue();
        Assertions.assertThat(softwareRequirement.derivedRationale()).isEqualTo(DERIVED_RATIONALE);
    }

    @Test
    public void can_have_a_status() throws Exception{
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                String.format("\\status{%s}\r\n", STATUS),
                "\\end{softwarerequirement}"));

        underTest.load(line, reader);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.status()).isEqualTo(STATUS);
    }

    @Test
    public void can_concern_security() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                "\\security\r\n",
                "\\end{softwarerequirement}"));

        underTest.load(line, reader);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.concernsSecurity()).isTrue();
        Assertions.assertThat(softwareRequirement.concernsSafety()).isFalse();
    }

    @Test
    public void can_concern_safety() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                "\\safety\r\n",
                "\\end{softwarerequirement}"));

        underTest.load(line, reader);

        SoftwareRequirement softwareRequirement = underTest.specificationItem();
        Assertions.assertThat(softwareRequirement.concernsSecurity()).isFalse();
        Assertions.assertThat(softwareRequirement.concernsSafety()).isTrue();
    }
}