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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.fuin.devsupwiz.common.Config;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Represents the current state during task execution.
 */
@ApplicationScoped
public class TaskModel {

    @Inject
    private Instance<FXMLLoader> loaderInstance;

    @Inject
    private Config config;

    private WelcomeTask welcomeTask;

    private SummaryTask summaryTask;
    
    private int index;

    private NodeControllerPair<SetupController> current;

    private List<NodeControllerPair<SetupController>> nodeControllers;

    /**
     * Initializes the task model.
     */
    @PostConstruct
    public void init() {
        nodeControllers = new ArrayList<>();

        welcomeTask = new WelcomeTask();
        summaryTask = new SummaryTask();
        
        final NodeControllerPair<SetupController> ncw = NodeControllerPair.load(
                welcomeTask.getTypeId(), loaderInstance, welcomeTask.getFxml(),
                welcomeTask.getResource());
        ncw.getController().init(welcomeTask);
        nodeControllers.add(ncw);
        
        for (final SetupTask task : config.getTasks()) {
            final NodeControllerPair<SetupController> nc = NodeControllerPair
                    .load(task.getTypeId(), loaderInstance, task.getFxml(),
                            task.getResource());
            nc.getController().init(task);
            nodeControllers.add(nc);
        }

        final NodeControllerPair<SetupController> ncs = NodeControllerPair.load(
                summaryTask.getTypeId(), loaderInstance, summaryTask.getFxml(),
                summaryTask.getResource());
        ncs.getController().init(summaryTask);
        nodeControllers.add(ncs);

        index = 0;
        current = nodeControllers.get(index);
    }

    /**
     * Returns the current UI.
     * 
     * @return Panel to display.
     */
    public Node getNode() {
        return current.getParent();
    }

    /**
     * Returns the current UI controller.
     * 
     * @return Controller.
     */
    public SetupController getController() {
        return current.getController();
    }

    /**
     * Returns the current task. Convenience method for
     * <code>getController().getTask()</code>.
     * 
     * @return Task.
     */
    public SetupTask getTask() {
        return getController().getTask();
    }

    /**
     * Returns a text for displaying the current position.
     * 
     * @return Text like "1 / 5" (one of five).
     */
    public String getPosText() {
        return (index + 1) + " / " + nodeControllers.size();
    }

    /**
     * Determines if it's possible to switch to the previous task.
     * 
     * @return <code>true</code> if a previous task is available.
     */
    public boolean hasPrevious() {
        return index > 0;
    }

    /**
     * Determines if it's possible to switch to the next task.
     * 
     * @return <code>true</code> if a next task is available.
     */
    public boolean hasNext() {
        return index < nodeControllers.size() - 1;
    }

    /**
     * Switch to the previous task.
     */
    public void previous() {
        if (index == 0) {
            throw new IllegalStateException("This is already the first task");
        }
        index = index - 1;
        current = nodeControllers.get(index);
    }

    /**
     * Switch to the next task.
     */
    public void next() {
        if (index == nodeControllers.size() - 1) {
            throw new IllegalStateException("This is already the last task");
        }
        index = index + 1;
        current = nodeControllers.get(index);
    }

    /**
     * Validates the current task.
     * 
     * @return List of error messages or an empty list of the task is valid.
     */
    public List<String> validate() {
        return current.getController().getValidationErrors();
    }

    /**
     * Saves the current data.
     */
    public void save() {
        current.getController().save();
    }

}
