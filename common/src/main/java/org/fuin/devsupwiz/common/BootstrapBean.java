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

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import javafx.application.Application.Parameters;

/**
 * CDI bean that contains bootstrap information.
 */
@ApplicationScoped
public class BootstrapBean {

    private Parameters parameters;

    /**
     * Returns the parameters.
     * 
     * @return Parameters.
     */
    @NotNull
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters to a new value.
     * 
     * @param parameters
     *            Parameters.
     */
    public void setParameters(@NotNull final Parameters parameters) {
        this.parameters = parameters;
    }

}
