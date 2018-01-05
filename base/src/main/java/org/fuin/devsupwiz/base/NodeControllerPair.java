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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.enterprise.inject.Instance;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Combines a node and it's controller.
 * 
 * @param <T>
 *            Type of the controller.
 */
public final class NodeControllerPair<T> {

    private static final Logger LOG = LoggerFactory
            .getLogger(NodeControllerPair.class);

    private final Parent parent;

    private T controller;

    /**
     * Constructor with all data.
     * 
     * @param parent
     *            Parent node.
     * @param controller
     *            UI controller.
     */
    public NodeControllerPair(@NotNull final Parent parent,
            @NotNull final T controller) {
        super();
        this.parent = parent;
        this.controller = controller;
    }

    /**
     * Returns the parent node.
     * 
     * @return Node.
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * Returns the controller.
     * 
     * @return UI Controller.
     */
    public T getController() {
        return controller;
    }

    /**
     * Creates a new node/controller pair by loading it from an FXML file.
     * 
     * @param name
     *            Name for logging purposes.
     * @param loaderInstance
     *            Instance to use for getting the loader bean.
     * @param fxml
     *            FXML resource path.
     * @param resource
     *            Resource name.
     * 
     * @return New instance.
     * 
     * @param <T>
     *            Expected type of the UI controller.
     */
    public static <T> NodeControllerPair<T> load(@NotNull final String name,
            @NotNull final Instance<FXMLLoader> loaderInstance,
            @NotNull final String fxml, @NotNull final String resource) {
        final FXMLLoader loader = loaderInstance.select(FXMLLoader.class).get();
        try {
            try {
                final ResourceBundle bundle = ResourceBundle
                        .getBundle(resource);
                final URL url = NodeControllerPair.class.getResource(fxml);
                if (url == null) {
                    throw new IllegalStateException("FXML not found: " + fxml);
                }
                LOG.info("Load ({}) {}", name, url.toString());
                loader.setLocation(url);
                loader.setResources(bundle);
                final NodeControllerPair<T> pair = new NodeControllerPair<T>(
                        loader.load(), loader.getController());
                return pair;
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            loaderInstance.destroy(loader);
        }
    }

}
