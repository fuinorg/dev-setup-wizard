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

import static org.fuin.devsupwiz.common.DevSupWizUtils.findAppender;
import static org.fuin.devsupwiz.common.DevSupWizUtils.getString;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupTask;
import org.fuin.devsupwiz.common.TextFlowAppender;
import org.fuin.devsupwiz.common.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

/**
 * Main application controller.
 */
@Loggable
public class MainController implements Initializable {

    private static final Logger LOG = LoggerFactory
            .getLogger(MainController.class);

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane taskPane;

    @FXML
    private Label messagesTitle;

    @FXML
    private TextArea messages;

    @FXML
    private Button previous;

    @FXML
    private Label posLabel;

    @FXML
    private Button next;

    @FXML
    private ScrollPane logScrollPane;

    @Inject
    private TaskModel taskModel;

    @Inject
    private Instance<FXMLLoader> loaderInstance;

    @Inject
    private Validator validator;

    private ResourceBundle bundle;

    private NodeControllerPair<ProgressController> progressNodeControllerPair;

    private TextFlow logOutput;

    @Override
    public void initialize(final URL location, final ResourceBundle bundle) {

        final TextFlowAppender<ILoggingEvent> appender = findAppender("UI");
        if (appender == null) {
            throw new IllegalStateException("Appender 'UI' not found!");
        }
        logOutput = appender.getTextFlow();
        logScrollPane.setContent(logOutput);

        this.bundle = bundle;

        messagesTitle.setText(bundle.getString("messages.title.default"));
        messagesTitle.setGraphic(new ImageView(new Image("/info-24x24.png")));

        taskPane.setCenter(taskModel.getNode());

        // Always scroll to the last log line
        logOutput.getChildren()
                .addListener((ListChangeListener<Node>) ((change) -> {
                    logOutput.layout();
                    logScrollPane.layout();
                    logScrollPane.setVvalue(1.0f);
                }));

        updateUI();

        focus(next);

    }

    @FXML
    private void onPrevious(final ActionEvent event) {
        clearMessages();
        if (taskModel.getTask().alreadyExecuted()) {
            previous();
        } else {
            if (isValid()) {
                taskModel.save();
                executeTask(this::previous);
            }
        }
    }

    private void previous() {
        taskModel.previous();
        taskPane.setCenter(taskModel.getNode());
        updateUI();
        if (taskModel.hasPrevious()) {
            focus(previous);
        } else {
            focus(next);
        }
    }

    @FXML
    private void onNext(final ActionEvent event) {
        clearMessages();
        if (taskModel.getTask().alreadyExecuted()) {
            next();
        } else {
            if (isValid()) {
                taskModel.save();
                executeTask(this::next);
            }
        }
    }

    private void next() {
        taskModel.next();
        taskPane.setCenter(taskModel.getNode());
        updateUI();
        if (taskModel.hasNext()) {
            focus(next);
        } else {
            focus(previous);
        }
    }

    private void focus(final Node node) {
        Platform.runLater(new Runnable() {
            public void run() {
                node.requestFocus();
            }
        });
    }

    private void assertValid(final SetupTask task) {
        final Set<ConstraintViolation<Object>> violations = validator
                .validate(task, Default.class, UserInput.class);
        if (!violations.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            violations.forEach((v) -> {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(v.getMessage());
            });
            final String className = task.getClass().getName();
            throw new IllegalStateException("The instance of type '" + className
                    + "' was invalid when running 'execute()': "
                    + sb.toString());
        }
    }

    private void executeTask(final Runnable onSuccess) {

        if (progressNodeControllerPair == null) {
            progressNodeControllerPair = NodeControllerPair.load("progress",
                    loaderInstance, "/org/fuin/devsupwiz/base/progress.fxml",
                    "org/fuin/devsupwiz/base/progress");
        }

        final SetupTask setupTask = taskModel.getTask();
        assertValid(setupTask);

        final Node progressUI = progressNodeControllerPair.getParent();
        stackPane.getChildren().add(progressUI);

        final Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {
                setupTask.execute();
                return null;
            }

            @Override
            protected void succeeded() {
                LOG.info("Task '" + setupTask.getTypeId() + "' succeeded");
                stackPane.getChildren().remove(progressUI);
                onSuccess.run();
            }

            @Override
            protected void cancelled() {
                LOG.info("Task '" + setupTask.getTypeId() + "' cancelled");
                stackPane.getChildren().remove(progressUI);
                messages.setText(getString(bundle, "messages.task.cancelled",
                        taskModel.getTask().getTypeId()));
            }

            @Override
            protected void failed() {
                LOG.info("Task '" + setupTask.getTypeId() + "' failed",
                        getException());
                stackPane.getChildren().remove(progressUI);
                showError(
                        getString(bundle, "messages.task.failed",
                                taskModel.getTask().getTypeId()),
                        message(getException()));

            }
        };
        new Thread(task).start();

    }

    private static String message(final Throwable t) {
        if (t.getMessage() == null || t.getMessage().isEmpty()) {
            return t.getClass().getName();
        }
        return t.getMessage();
    }

    private void updateUI() {
        previous.setDisable(!taskModel.hasPrevious());
        next.setDisable(!taskModel.hasNext());
        posLabel.setText(taskModel.getPosText());
        taskModel.getController().refreshStatus();
        if (taskModel.getTask().alreadyExecuted()) {
            messages.setText(getString(bundle, "messages.task.already-executed",
                    taskModel.getTask().getTypeId()));
        }
    }

    private void clearMessages() {
        if (messages.getText().length() > 0) {
            messagesTitle.setText(bundle.getString("messages.title.default"));
            messagesTitle
                    .setGraphic(new ImageView(new Image("/info-24x24.png")));
            messages.clear();
        }
    }

    private void showError(final String title, final String error) {
        showErrors(title, Arrays.asList(error));
    }

    private void showErrors(final String title, final List<String> errors) {
        messagesTitle.setText(title);
        messagesTitle.setGraphic(new ImageView(new Image("/error-24x24.png")));
        final StringBuilder sb = new StringBuilder();
        for (final String error : errors) {
            sb.append(error + "\n");
        }
        messages.setText(sb.toString());
    }

    private boolean isValid() {

        // Clear previous errors
        clearMessages();

        // Verify current screen
        final List<String> errors = taskModel.validate();
        if (errors.isEmpty()) {
            return true;
        }

        // Show errors
        showErrors(bundle.getString("messages.title.error"), errors);

        return false;
    }

}
