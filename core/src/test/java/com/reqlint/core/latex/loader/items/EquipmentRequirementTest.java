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

class EquipmentRequirementTest {
    private static final String BLA = "BLA";
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String TITLE = "TITLE";
    private static final String EXPECTED_REMAINDER = "Expected remainder";

    private SpecificationTree specificationTree;
    private EquipmentRequirement underTest;

    @BeforeEach
    public void setUp() {
        specificationTree = new SpecificationTree();
        underTest = new EquipmentRequirement(specificationTree);
    }

    @Test
    public void can_attach_itself_to_the_specification_tree() {
        Assertions.assertThat(specificationTree.equipmentRequirements())
                .contains(underTest);
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

        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TITLE);
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);
    }

    @Test
    public void can_load_its_content_when_it_is_all_in_the_same_line() throws Exception {
        String actualReminder = underTest.load(
                String.format("{%s}{%s}\\end{equipmentrequirement}%s", IDENTIFIER, TITLE, EXPECTED_REMAINDER),
                null);

        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TITLE);
        Assertions.assertThat(actualReminder).isEqualTo(EXPECTED_REMAINDER);
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
    public void can_attach_software_requirements() {
        SoftwareRequirement softwareRequirement = new SoftwareRequirement(specificationTree);
        underTest.attach(softwareRequirement);
        Assertions.assertThat(underTest.softwareRequirements()).contains(softwareRequirement);
    }

    @Test
    public void can_sort_alphabetically_by_identifier() throws Exception {
        EquipmentRequirement underTest1 = new EquipmentRequirement(specificationTree);
        underTest1.load(String.format("{%s}{%s}\\end{equipmentrequirement}", "CCC", TITLE), null);

        EquipmentRequirement underTest2 = new EquipmentRequirement(specificationTree);
        underTest2.load(String.format("{%s}{%s}\\end{equipmentrequirement}", "AAA", TITLE), null);

        EquipmentRequirement underTest3 = new EquipmentRequirement(specificationTree);
        underTest3.load(String.format("{%s}{%s}\\end{equipmentrequirement}", "BBB", TITLE), null);

        Assertions.assertThat(specificationTree.equipmentRequirements())
                .containsExactly(underTest2, underTest3, underTest1, underTest);
    }

}