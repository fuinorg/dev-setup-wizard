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
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Caches all bytes until a line feed and writes then a text line to the log.
 */
public final class LogOutputStream extends OutputStream {

    private static final Logger LOG = LoggerFactory
            .getLogger("org.fuin.devsupwiz.tasks");

    private StringBuilder line;

    private final Level level;

    /**
     * Constructor wih all data.
     * 
     * @param level
     *            Level to use for writing log messages.
     */
    public LogOutputStream(final Level level) {
        super();
        this.line = new StringBuilder();
        this.level = level;
    }

    @Override
    public void write(final int b) throws IOException {
        final char ch = (char) b;
        line.append(ch);
        if (ch == '\n') {
            writeLine();
        }
    }

    private void writeLine() {
        final String msg = line.toString().trim();
        if (level == Level.TRACE) {
            LOG.trace(msg);
        } else if (level == Level.DEBUG) {
            LOG.debug(msg);
        } else if (level == Level.INFO) {
            LOG.info(msg);
        } else if (level == Level.WARN) {
            LOG.warn(msg);
        } else if (level == Level.ERROR) {
            LOG.error(msg);
        } else {
            throw new IllegalStateException("Unknown log level: " + level);
        }
        line = new StringBuilder();
    }

    @Override
    public void close() throws IOException {
        writeLine();
        super.close();
    }

}
