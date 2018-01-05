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

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Caches all bytes until a line feed and writes then a text line to the text
 * flow.
 */
public final class TextFlowOutputStream extends OutputStream {

    private StringBuilder line;

    private Color color;

    private final TextFlow textFlow;

    /**
     * Constructor wih all data.
     * 
     * @param textFlow
     *            Flow to write to.
     * @param color
     *            Color to use.
     */
    public TextFlowOutputStream(final TextFlow textFlow, final Color color) {
        super();
        this.line = new StringBuilder();
        this.textFlow = textFlow;
        this.color = color;
    }

    /**
     * Sets the color for the next write.
     * 
     * @param color
     *            Color to use.
     */
    public void setColor(final Color color) {
        this.color = color;
    }

    /**
     * Returns the current color.
     * 
     * @return Color.
     */
    public Color getColor() {
        return color;
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
        final Text text = new Text(line.toString());
        text.setFill(color);
        text.setFont(Font.font("MONOSPACED"));
        if (Platform.isFxApplicationThread()) {
            textFlow.getChildren().add(text);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    textFlow.getChildren().add(text);
                }
            });
        }
        line = new StringBuilder();
    }

    @Override
    public void close() throws IOException {
        writeLine();
        super.close();
    }

}
