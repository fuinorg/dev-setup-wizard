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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.fuin.utils4j.JaxbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setup configuration.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dev-setup-wizard")
public final class ConfigImpl implements Config {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigImpl.class);

    @NotEmpty
    @XmlAttribute(name = "name")
    private String name;

    @XmlAnyElement(lax = true)
    @XmlElementWrapper(name = "tasks")
    private List<SetupTask> tasks;

    /**
     * Default constructor for JAXB.
     */
    protected ConfigImpl() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param name
     *            Unique name.
     * @param dependencies
     *            Dependencies in "G:A:V" format (G=groupId, A=artifactId,
     *            V=version).
     * @param tasks
     *            Tasks.
     */
    public ConfigImpl(@NotEmpty final String name,
            @NotNull final List<SetupTask> tasks) {
        super();
        this.name = name;
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the name.
     * 
     * @return Unique name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the list of tasks.
     * 
     * @return Immutable task list.
     */
    public final List<SetupTask> getTasks() {
        if (tasks == null) {
            LOG.warn("No tasks defined!");
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Creates a new instance from XML.
     * 
     * @param url
     *            URL with XML configuration.
     * 
     * @return New configuration instance.
     */
    public static ConfigImpl load(final URL url) {

        LOG.info("Loading config " + url);

        try (final InputStream input = url.openStream()) {

            final String xmlConfig = IOUtils.toString(input,
                    Charset.forName("utf-8"));

            final ConfigPreview preview = new ConfigPreview(xmlConfig).load();
            preview.getClassNames().add(ConfigImpl.class.getName());

            return JaxbUtils.unmarshal(xmlConfig,
                    loadClasses(preview.getClassNames()));

        } catch (final IOException ex) {
            LOG.error("Error loading config", ex);
            throw new RuntimeException("Failed to load config " + url, ex);
        }

    }

    private static Class<?>[] loadClasses(final List<String> classNames) {

        final List<Class<?>> classes = new ArrayList<>();
        for (final String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (final ClassNotFoundException ex) {
                throw new RuntimeException("Failed to load : " + className, ex);
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);

    }

}
