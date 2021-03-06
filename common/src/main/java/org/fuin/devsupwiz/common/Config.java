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

import java.util.List;

/**
 * Setup configuration.
 */
public interface Config {

    /**
     * Returns the name.
     * 
     * @return Unique name.
     */
    public String getName();

    /**
     * Returns the list of tasks.
     * 
     * @return Immutable task list.
     */
    public List<SetupTask> getTasks();

    /**
     * Saves any changes made.
     */
    public void persist();

    /**
     * Tries to locate a task by it's unique type id.
     * 
     * @param key
     *            Value returned by {@link SetupTask#getTypeId()}.
     * 
     * @return Task or <code>null</code> if no task with that identifier was
     *         found.
     */
    public <T extends SetupTask> T findTask(String key);

}
