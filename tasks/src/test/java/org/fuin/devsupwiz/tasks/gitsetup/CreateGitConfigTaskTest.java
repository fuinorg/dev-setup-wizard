/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.devsupwiz.tasks.gitsetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.apache.commons.io.IOUtils;
import org.fuin.utils4j.JaxbUtils;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test for the {@link CreateGitConfigTask} class.
 */
public class CreateGitConfigTaskTest {

    @Test
    public void testExecute() throws IOException {

        // PREPARE
        final Charset utf8 = Charset.forName("utf-8");
        final String expected = IOUtils
                .resourceToString("/" + CreateGitConfigTask.class.getPackage()
                        .getName().replace('.', '/') + "/test-gitconfig", utf8);
        final File file = new File("target/.test-gitconfig");
        file.delete();
        final CreateGitConfigTask testee = new CreateGitConfigTask("x",
                "Peter Parker", "peter.parker@somewhere.com",
                PushDefault.SIMPLE, file);

        // TEST
        testee.execute();

        // VERIFY
        assertThat(file).usingCharset(utf8).hasContent(expected);

    }

    @Test
    public void testExecuteValidateInstance() {

        try (final SeContainer container = SeContainerInitializer.newInstance()
                .initialize()) {
            final CreateGitConfigTask testee = container
                    .select(CreateGitConfigTask.class).get();
            try {
                testee.execute();
                fail();
            } catch (final IllegalStateException ex) {
                assertThat(ex.getMessage()).startsWith("The instance of type '"
                        + CreateGitConfigTask.class.getName() + "' "
                        + "was invalid when calling method");
                assertThat(ex.getMessage())
                        .contains("Push Default is required");
                assertThat(ex.getMessage()).contains("EMail is required");
                assertThat(ex.getMessage()).contains("Name is required");
            }
        }

    }

    @Test
    public void testMarshal() {

        // PREPARE
        final File file = new File("target/.test-gitconfig");
        file.delete();
        final CreateGitConfigTask testee = new CreateGitConfigTask("x",
                "Peter Parker", "peter.parker@somewhere.com",
                PushDefault.SIMPLE, file);

        // TEST
        final String xml = JaxbUtils.marshal(testee, CreateGitConfigTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder
                .compare(JaxbUtils.XML_PREFIX + "<create-git-config id=\"x\" />")
                .withTest(xml).ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences())
                .describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() {

        // PREPARE
        final String xml = "<create-git-config id=\"x\" />";

        // TEST
        final CreateGitConfigTask testee = JaxbUtils.unmarshal(xml,
                CreateGitConfigTask.class);

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getId()).isEqualTo("x");
        assertThat(testee.getEmail()).isNull();
        assertThat(testee.getName()).isNull();
        assertThat(testee.getPushDefault()).isNull();
        assertThat(testee.getResource()).isNotEmpty();
        assertThat(testee.getFxml()).isNotEmpty();

    }

}
