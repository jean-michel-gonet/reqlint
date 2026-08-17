package com.reqlint.core.input.latex.loader.itemloaders;

import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemMissingArgumentsException;
import com.reqlint.core.input.latex.loader.exceptions.SpecificationItemNotClosedException;
import com.reqlint.core.input.latex.loader.itemloaders.EquipmentRequirementLatexLoader;
import com.reqlint.core.model.specification.items.EquipmentRequirement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import static com.reqlint.core.testutils.TextFileContent.CRLF;
import static com.reqlint.core.testutils.TextFileContent.stringReaderOf;

class EquipmentRequirementLatexLoaderTest {
    private static final String BLA = "BLA";
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String TITLE = "TITLE";
    private static final String EXPECTED_REMAINDER = "Expected remainder";
    private static final String DERIVED_RATIONALE = "Because of something rational";
    private static final String STATUS = "Flabbergasted";

    private EquipmentRequirementLatexLoader underTest;

    @BeforeEach
    public void setUp() {
        underTest = new EquipmentRequirementLatexLoader();
    }

    @Test
    public void can_load_its_content() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                BLA,
                CRLF,
                BLA,
                CRLF,
                "\\end{equipmentrequirement}",
                EXPECTED_REMAINDER));

        String actualReminder = underTest.load(line, reader);
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(equipmentRequirement.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(equipmentRequirement.title()).isEqualTo(TITLE);
    }

    @Test
    public void can_load_its_content_when_it_is_all_in_the_same_line() throws Exception {
        String actualReminder = underTest.load(
                String.format("{%s}{%s}\\end{equipmentrequirement}%s", IDENTIFIER, TITLE, EXPECTED_REMAINDER),
                null);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);

        Assertions.assertThat(equipmentRequirement.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(equipmentRequirement.title()).isEqualTo(TITLE);
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
                "\\end{equipmentrequirement}"));

        underTest.load(line, reader);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(equipmentRequirement.derived()).isTrue();
        Assertions.assertThat(equipmentRequirement.derivedRationale()).isEqualTo(DERIVED_RATIONALE);
    }

    @Test
    public void can_have_a_status() throws Exception{
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                String.format("\\status{%s}\r\n", STATUS),
                "\\end{equipmentrequirement}"));

        underTest.load(line, reader);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(equipmentRequirement.status()).isEqualTo(STATUS);
    }

    @Test
    public void can_concern_security() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                "\\security\r\n",
                "\\end{equipmentrequirement}"));

        underTest.load(line, reader);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(equipmentRequirement.concernsSecurity()).isTrue();
        Assertions.assertThat(equipmentRequirement.concernsSafety()).isFalse();
    }

    @Test
    public void can_concern_safety() throws Exception {
        String line = String.format("{%s}{%s}", IDENTIFIER, TITLE);
        BufferedReader reader = new BufferedReader(stringReaderOf(
                "\\safety\r\n",
                "\\end{equipmentrequirement}"));

        underTest.load(line, reader);

        EquipmentRequirement equipmentRequirement = underTest.specificationItem();
        Assertions.assertThat(equipmentRequirement.concernsSecurity()).isFalse();
        Assertions.assertThat(equipmentRequirement.concernsSafety()).isTrue();
    }

}