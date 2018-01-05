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
package org.fuin.devsupwiz.tasks.gitsetup;

import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconOk24x24;
import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconTodo24x24;

import java.util.Collections;
import java.util.List;

import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * UI controller for git clone task.
 */
@Loggable
public class GitCloneController implements SetupController {

    @FXML
    private ListView<String> repositories;

    @FXML
    private Label title;

    @FXML
    private TextField directory;

    private GitCloneTask task;

    private ObservableList<String> repoList;

    @Override
    public void init(final SetupTask setupTask) {
        if (!(setupTask instanceof GitCloneTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + GitCloneTask.class.getName()
                            + ", but was: " + setupTask.getClass().getName());
        }
        task = (GitCloneTask) setupTask;
        repoList = FXCollections.observableArrayList(task.getRepositories());
        repositories.setItems(repoList);
        directory.setText(task.getTargetDir());
        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public void save() {
        // Nothing to to
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        final boolean alreadyExecuted = task.alreadyExecuted();
        repositories.setDisable(alreadyExecuted);
        directory.setDisable(alreadyExecuted);
        if (alreadyExecuted) {
            title.setGraphic(createIconOk24x24());
        } else {
            title.setGraphic(createIconTodo24x24());
        }
    }

}
