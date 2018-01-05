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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * UI controller for git configuration.
 */
@Loggable
public class CreateGitConfigController
        implements Initializable, SetupController {

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private ComboBox<String> pushDefault;

    @FXML
    private Label title;

    @Inject
    private Validator validator;

    private CreateGitConfigTask task;

    @Override
    public void init(final SetupTask task) {
        if (!(task instanceof CreateGitConfigTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + CreateGitConfigTask.class.getName()
                            + ", but was: " + task.getClass().getName());
        }
        this.task = (CreateGitConfigTask) task;
        refreshStatus();
    }
    
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        for (final PushDefault value : PushDefault.values()) {
            pushDefault.getItems().add(value.name().toLowerCase());
        }
        pushDefault.setValue(PushDefault.SIMPLE.name().toLowerCase());
    }

    @Override
    public List<String> getValidationErrors() {

        final List<String> errors = new ArrayList<String>();

        if (!task.alreadyExecuted()) {

            // Execute bean validation using a new task instance
            final String selected = pushDefault.getValue().toUpperCase();
            final CreateGitConfigTask t = new CreateGitConfigTask("x",
                    name.textProperty().get(), email.textProperty().get(),
                    PushDefault.valueOf(selected));
            final Set<ConstraintViolation<CreateGitConfigTask>> violations = validator
                    .validate(t);
            for (final ConstraintViolation<CreateGitConfigTask> violation : violations) {
                errors.add(violation.getMessage());
            }

            // Show validation errors on UI
            Decorator.removeAllDecorations(name);
            if (violated(violations, name.getId())) {
                Decorator.addDecoration(name, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }
            Decorator.removeAllDecorations(email);
            if (violated(violations, email.getId())) {
                Decorator.addDecoration(email, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }

        }

        // Return error messages to display on main screen
        return errors;
    }

    @Override
    public void save() {
        task.setName(name.getText());
        task.setEmail(email.getText());
        final String selected = pushDefault.getValue().toUpperCase();
        task.setPushDefault(PushDefault.valueOf(selected));
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        final boolean alreadyExecuted = task.alreadyExecuted();
        name.setDisable(alreadyExecuted);
        email.setDisable(alreadyExecuted);
        pushDefault.setDisable(alreadyExecuted);
        if (alreadyExecuted) {
            title.setGraphic(createIconOk24x24());
        } else {
            title.setGraphic(createIconTodo24x24());
        }
    }

}
