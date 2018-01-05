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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Helper methods that require JavaFX.
 */
public final class DevSupWizFxUtils {

    /** Key used for the MDC 'task' value. */
    public static final String MDC_TASK_KEY = "task";

    /** To do icon 24x24 pixel. */
    public static final Image ICON_TODO_24X24 = new Image("/todo-24x24.png");

    /** OK icon 24x24 pixel. */
    public static final Image ICON_OK_24X24 = new Image("/ok-24x24.png");

    /** OK icon 24x24 pixel. */
    public static final Image ICON_ERROR_16X16 = new Image("/error-16x16.png");

    private DevSupWizFxUtils() {
    }

    /**
     * Creates a to do icon (24x24 pixel).
     * 
     * @return New node instance.
     */
    public static ImageView createIconTodo24x24() {
        return new ImageView(ICON_TODO_24X24);
    }

    /**
     * Creates a OK icon (24x24 pixel).
     * 
     * @return New node instance.
     */
    public static ImageView createIconOk24x24() {
        return new ImageView(ICON_OK_24X24);
    }

    /**
     * Creates an error icon (16x16 pixel).
     * 
     * @return New node instance.
     */
    public static ImageView createIconError16x16() {
        return new ImageView(ICON_ERROR_16X16);
    }

}
