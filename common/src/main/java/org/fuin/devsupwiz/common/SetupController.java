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
 * UI Controller that handles a setup task.
 */
public interface SetupController {
    
    /**
     * Associates the task with the controller.
     * 
     * @param task Task the controller works on.
     */
    public void init(SetupTask task);

    /**
     * Returns a list of validation messages from the controller.
     * 
     * @return List of error messages or empty list if the content is valid.
     */
    public List<String> getValidationErrors();

    /**
     * The controller saves the data to it's assigned task. This method should
     * only be called if the {@link #isValid()} returned <code>true</code>.
     */
    public void save();

    /**
     * Returns the controller's task.
     * 
     * @return Task including the saved data from the UI.
     */
    public SetupTask getTask();

    /**
     * Refreshes the current status of the task.
     */
    public void refreshStatus();

}
