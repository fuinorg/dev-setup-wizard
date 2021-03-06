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

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Base class for setup tasks that implements the hash code and equals functions
 * based on the {@link #getTypeId()} method.
 */
public abstract class AbstractSetupTask implements SetupTask {

    private transient Config config;

    @XmlAttribute(name = "executed")
    private Boolean executed;

    /**
     * Returns the current configuration.
     * 
     * @return Configuration.
     */
    protected final Config getConfig() {
        return config;
    }

    @Override
    public final void init(final Config config) {
        this.config = config;
    }

    @Override
    public final void success() {
        if (config == null) {
            throw new IllegalStateException(
                    "Configuration not set - Did you forget to call 'init(..)' method in a test?");
        }
        executed = true;
        config.persist();
    }

    @Override
    public final boolean alreadyExecuted() {
        return executed != null && executed;
    }

    @Override
    public final int hashCode() {
        return getTypeId().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractSetupTask other = (AbstractSetupTask) obj;
        return getTypeId().equals(other.getTypeId());
    }

}
