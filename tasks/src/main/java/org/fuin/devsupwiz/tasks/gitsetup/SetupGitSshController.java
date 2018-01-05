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

import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconError16x16;
import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconOk24x24;
import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconTodo24x24;
import static org.fuin.devsupwiz.common.DevSupWizUtils.violated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * UI controller for ssh git setup.
 */
@Loggable
public class SetupGitSshController implements SetupController {

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private ComboBox<String> provider;

    @FXML
    private TextField host;

    @FXML
    private Label title;

    @Inject
    private Validator validator;

    private SetupGitSshTask task;

    @Override
    public void init(final SetupTask setupTask) {
        if (!(setupTask instanceof SetupGitSshTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + SetupGitSshTask.class.getName()
                            + ", but was: " + setupTask.getClass().getName());
        }
        task = (SetupGitSshTask) setupTask;

        /*
         * TODO Add github provider for (final GitProvider value :
         * GitProvider.values()) {
         * provider.getItems().add(value.name().toLowerCase()); }
         */
        provider.getItems().add(GitProvider.BITBUCKET.name().toLowerCase());
        if (task.getProvider() == null) {
            provider.setValue(GitProvider.BITBUCKET.name().toLowerCase());
        } else {
            provider.setValue(task.getProvider().name().toLowerCase());
        }
        host.setText(task.getHost());

        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {

        final List<String> errors = new ArrayList<String>();

        if (!task.alreadyExecuted()) {

            // Execute bean validation using a new task instance
            final String selected = provider.getValue().toUpperCase();
            final SetupGitSshTask t = new SetupGitSshTask("x", name.getText(),
                    password.getText(), GitProvider.valueOf(selected),
                    host.getText(), false);
            final Set<ConstraintViolation<SetupGitSshTask>> violations = validator
                    .validate(t);
            for (final ConstraintViolation<SetupGitSshTask> violation : violations) {
                errors.add(violation.getMessage());
            }

            // Show validation errors on UI
            Decorator.removeAllDecorations(name);
            if (violated(violations, name.getId())) {
                Decorator.addDecoration(name, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }
            Decorator.removeAllDecorations(password);
            if (violated(violations, password.getId())) {
                Decorator.addDecoration(password, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }
            Decorator.removeAllDecorations(host);
            if (violated(violations, host.getId())) {
                Decorator.addDecoration(host, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }

        }

        // Return error messages to display on main screen
        return errors;
    }

    @Override
    public void save() {
        task.setName(name.getText());
        task.setPassword(password.getText());
        final String selected = provider.getValue().toUpperCase();
        task.setProvider(GitProvider.valueOf(selected));
        task.setHost(host.getText());
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        final boolean alreadyExecuted = task.alreadyExecuted();
        name.setDisable(alreadyExecuted);
        password.setDisable(alreadyExecuted);
        provider.setDisable(alreadyExecuted);
        host.setDisable(alreadyExecuted);
        if (alreadyExecuted) {
            title.setGraphic(createIconOk24x24());
        } else {
            title.setGraphic(createIconTodo24x24());
        }
    }

}
