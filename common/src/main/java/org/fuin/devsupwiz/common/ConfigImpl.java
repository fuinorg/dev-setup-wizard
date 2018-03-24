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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
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

    private static final Charset UTF8 = Charset.forName("utf-8");

    private static final Logger LOG = LoggerFactory.getLogger(ConfigImpl.class);

    @NotEmpty
    @XmlAttribute(name = "name")
    private String name;

    @XmlAnyElement(lax = true)
    @XmlElementWrapper(name = "tasks")
    private List<SetupTask> tasks;

    private transient File file;

    private transient Class<?>[] classes;

    /**
     * Default constructor for JAXB.
     */
    protected ConfigImpl() {
        super();
    }

    /**
     * Constructor with all data (task list).
     * 
     * @param name
     *            Unique name.
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
     * Constructor with all data (task array).
     * 
     * @param name
     *            Unique name.
     * @param tasks
     *            Tasks.
     */
    public ConfigImpl(@NotEmpty final String name,
            @NotNull final SetupTask... tasks) {
        super();
        this.name = name;
        this.tasks = new ArrayList<>();
        if (tasks != null && tasks.length > 0) {
            this.tasks.addAll(Arrays.asList(tasks));
        }
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

    private final void setFile(final File file) {
        this.file = file;
    }

    private final void setClasses(final Class<?>[] classes) {
        this.classes = classes;
    }

    @Override
    public final void persist() {
        // Only persist in case the config was loaded from disk
        if (classes != null) {
            try {
                final JAXBContext ctx = JAXBContext.newInstance(classes);
                final Marshaller marshaller = ctx.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                try (final Writer writer = new BufferedWriter(new FileWriter(file))) {
                    marshaller.marshal(this, writer);
                }
            } catch (final IOException | JAXBException ex) {
                throw new RuntimeException("Error saving config to: " + file,
                        ex);
            }
        }
    }

    /**
     * Initializes the instance.
     */
    public void init() {
        if (tasks != null) {
            for (final SetupTask task : tasks) {
                task.init(this);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SetupTask> T findTask(String key) {
        if (tasks == null) {
            return null;
        }
        for (final SetupTask task : tasks) {
            if (task.getTypeId().equals(key)) {
                return (T) task;
            }
        }
        return null;
    }

    /**
     * Called by JAXB after unmarshalling.
     * 
     * @param unmarshaller
     *            Unmarshaller.
     * @param parent
     *            Parent.
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        init();
    }

    /**
     * Creates a new instance from XML.
     * 
     * @param file
     *            File with XML configuration.
     * 
     * @return New configuration instance.
     */
    public static ConfigImpl load(final File file) {

        LOG.info("Loading config {}", file);
        try {
            final String xmlConfig = FileUtils.readFileToString(file, UTF8);

            final List<String> classNames = DevSupWizUtils
                    .findSetupTasksInClasspath();
            LOG.info("Task classes from classpath: {}", classNames);

            final List<Class<?>> classList = DevSupWizUtils
                    .loadClasses(classNames);
            classList.add(ConfigImpl.class);
            final Class<?>[] classes = classList
                    .toArray(new Class<?>[classList.size()]);
            final ConfigImpl config = JaxbUtils.unmarshal(xmlConfig, classes);
            config.setFile(file);
            config.setClasses(classes);
            return config;

        } catch (final IOException ex) {
            throw new RuntimeException("Failed to load config: " + file, ex);
        }

    }

}
