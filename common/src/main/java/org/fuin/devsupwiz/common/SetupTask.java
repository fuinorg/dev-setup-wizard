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

/**
 * Task to create or update some kind of setup. Every task is required to
 * implement equals and hash code based on the {@link #getTypeId()} method.
 */
public interface SetupTask {

    /**
     * Returns the type of the task.
     * 
     * @return Unique type.
     */
    public String getType();

    /**
     * Returns the type and identifier of the task. In case of singleton tasks
     * (only one instance in a config allowed) this will return only the same
     * information as {@link #getType()}.
     * 
     * @return Unique type and identifier.
     */
    public String getTypeId();

    /**
     * Returns the FXML that is used to display and configure this task.
     * 
     * @return FXML path and name.
     */
    public String getFxml();

    /**
     * Returns the resource that is used with the FXML resource.
     * 
     * @return Resource path and name.
     */
    public String getResource();

    /**
     * Determines if the task was already executed.
     * 
     * @return <code>false</code> if {@link #execute()} will do some work or
     *         <code>true</code> if the task already was executed.
     */
    public boolean alreadyExecuted();

    /**
     * Initializes the task with necessary runtime configuration. Must be called
     * once before running {@link #execute()}.
     * 
     * @param config
     *            Current configuration.
     */
    public void init(Config config);

    /**
     * Executes the setup task. Does nothing if {@link #alreadyExecuted()}
     * returns <code>true</code>. The method will store some kind of persistent
     * information after execution or it will determine some other way if
     * execution is necessary.
     */
    public void execute();

    /**
     * Sets the task to 'executed' and persists the task's data. Method
     * {@link SetupTask#alreadyExecuted()} will return true after calling this
     * method.
     */
    public void success();

}
