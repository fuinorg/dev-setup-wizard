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
package org.fuin.devsupwiz.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Test for the {@link ConfigPreview} class.
 */
public class ConfigPreviewTest {

    @Test
    public void testLoad() {

        // PREPARE
        final String xml = "<dev-setup-wizard><tasks>"
                + "<abc task-class=\"java.lang.String\" />"
                + "<def task-class=\"java.lang.Integer\" />"
                + "<ghi task-class=\"java.math.BigDecimal\" />"
                + "</tasks></dev-setup-wizard>";

        final ConfigPreview testee = new ConfigPreview(xml);

        // TEST
        testee.load();

        // VERIFY
        assertThat(testee.getClassNames()).containsOnly(String.class.getName(),
                Integer.class.getName(), BigDecimal.class.getName());

    }

}
