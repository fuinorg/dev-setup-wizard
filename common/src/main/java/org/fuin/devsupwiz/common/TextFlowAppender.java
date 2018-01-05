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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

/**
 * Appends log events to a JavaFX text flow.
 * 
 * @param <E>
 *            Event type.
 */
public final class TextFlowAppender<E> extends OutputStreamAppender<E> {

    private TextFlowOutputStream targetStream;

    private TextFlow textFlow;

    /**
     * Default constructor.
     */
    public TextFlowAppender() {
        super();
        textFlow = new TextFlow();
    }
    
    /**
     * Returns the text flow that is used to append log messages.
     * 
     * @return Text flow.
     */
    public TextFlow getTextFlow() {
        return textFlow;
    }

    @Override
    public void start() {
        targetStream = new TextFlowOutputStream(textFlow, Color.BLACK);
        setOutputStream(targetStream);
        super.start();
    }

    @Override
    protected void subAppend(final E event) {
        if (event instanceof ILoggingEvent) {
            final ILoggingEvent ev = (ILoggingEvent) event;
            if (ev.getLevel() == Level.WARN) {
                targetStream.setColor(Color.DARKORANGE);
            } else if (ev.getLevel() == Level.ERROR) {
                targetStream.setColor(Color.RED);
            } else {
                targetStream.setColor(Color.BLACK);
            }
        }
        super.subAppend(event);
    }

}
