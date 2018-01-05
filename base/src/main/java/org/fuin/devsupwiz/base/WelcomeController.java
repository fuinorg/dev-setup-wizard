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

import static org.fuin.devsupwiz.common.DevSupWizUtils.getString;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fuin.devsupwiz.common.Config;
import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * UI controller for welcome screen.
 */
@Loggable
public class WelcomeController implements Initializable, SetupController {

    @FXML
    private Label title;
    
    @Inject
    private Config config;

    private WelcomeTask task;

    private ResourceBundle bundle;
    
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.bundle = resources;
        title.setText(getString(bundle, "title", config.getName()));
    }
    
    @Override
    public void init(final SetupTask task) {
        if (!(task instanceof WelcomeTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + WelcomeTask.class.getName()
                            + ", but was: " + task.getClass().getName());
        }
        this.task = (WelcomeTask) task;
        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public void save() {
        // Do nothing
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        // Do nothing
    }

}
