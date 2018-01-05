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

/**
 * Default for pushing to a git repository.
 */
public enum PushDefault {

    /**
     * Do not push anything (error out) unless a refspec is explicitly given.
     */
    NOTHING,

    /**
     * Push the current branch to update a branch with the same name on the
     * receiving end.
     */
    CURRENT,

    /**
     * Push the current branch back to the branch whose changes are usually
     * integrated into the current branch.
     */
    UPSTREAM,

    /**
     * Refuse to push if the upstream branch’s name is different from the local
     * one.
     */
    SIMPLE,

    /** Push all branches having the same name on both ends. */
    MATCHING

}
