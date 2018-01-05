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
package org.fuin.devsupwiz.tasks.hostname;

import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.fuin.devsupwiz.common.LogOutputStream;
import org.fuin.devsupwiz.common.SetupTask;
import org.fuin.devsupwiz.common.ShellCommandExecutor;
import org.fuin.devsupwiz.common.ValidateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

/**
 * Sets the hostname for the system.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = SetHostnameTask.KEY)
public class SetHostnameTask implements SetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "set-hostname";

    private static final Logger LOG = LoggerFactory
            .getLogger(SetHostnameTask.class);

    @XmlTransient
    private Preferences userPrefs;

    @NotEmpty
    @XmlAttribute(name = "id")
    private String id;

    @Pattern(regexp = "[a-z][a-z0-9\\-]*", message = "{set-hostname.pattern}")
    @NotEmpty(message = "{set-hostname.empty}")
    @XmlTransient
    private String name;

    /**
     * Default constructor for JAXB.
     */
    protected SetHostnameTask() {
        super();
        userPrefs = Preferences.userRoot();
    }

    /**
     * Constructor with name.
     * 
     * @param id
     *            Unique task identifier.
     * @param name
     *            Host name.
     */
    public SetHostnameTask(@NotEmpty final String id,
            @NotEmpty final String name) {
        super();
        userPrefs = Preferences.userRoot();
        this.id = id;
        this.name = name;
    }

    /**
     * Returns name.
     * 
     * @return Host name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * 
     * @param name
     *            Host name.
     */
    public void setName(@NotEmpty final String name) {
        this.name = name;
    }

    @Override
    public boolean alreadyExecuted() {
        return userPrefs.getBoolean(getPrefKey(), false);
    }
    
    private String getPrefKey() {
        return getType() + "-" + getId();
    }

    @ValidateInstance
    @Override
    public void execute() {

        MDC.put(MDC_TASK_KEY, getTypeId());
        try {

            if (!alreadyExecuted()) {

                final ShellCommandExecutor executor = new ShellCommandExecutor(
                        "hostnamectl set-hostname '" + name + "'", 5,
                        new HashMap<String, String>(),
                        new LogOutputStream(Level.INFO),
                        new LogOutputStream(Level.ERROR));

                final int result = executor.execute();
                if (result == 0) {
                    try {
                        userPrefs.putBoolean(getPrefKey(), true);
                        userPrefs.flush();
                    } catch (final BackingStoreException ex) {
                        throw new RuntimeException("Failed to save the setup key '" + getPrefKey() + "'", ex);
                    }
                    LOG.info("Successfully set the host name to '{}'", name);
                }

            }

        } finally {
            MDC.remove(MDC_TASK_KEY);
        }

    }

    @Override
    public String getResource() {
        return this.getClass().getPackage().getName().replace('.', '/') + "/"
                + KEY;
    }

    @Override
    public String getFxml() {
        return "/" + getResource() + ".fxml";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return KEY;
    }

    @Override
    public String getTypeId() {
        return getType() + "[" + getId() + "]";
    }
    
    
}
