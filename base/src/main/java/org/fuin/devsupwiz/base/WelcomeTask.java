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
package org.fuin.devsupwiz.base;

import javax.enterprise.context.ApplicationScoped;

import org.fuin.devsupwiz.common.AbstractSetupTask;

/**
 * Welcome screen task.
 */
@ApplicationScoped
public class WelcomeTask extends AbstractSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "welcome";

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
    public void execute() {
        // Do nothing
    }

    @Override
    public String getType() {
        return KEY;
    }

    @Override
    public String getTypeId() {
        return KEY;
    }

}
