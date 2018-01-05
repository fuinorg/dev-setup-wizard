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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Basic functionality for interceptors.
 */
public abstract class AbstractInterceptor {

    /**
     * Create a signature string for a given method.
     * 
     * @param method
     *            Method to create a string from.
     * 
     * @return String representation of the method.
     */
    protected final String signature(final Method method) {
        String name;
        if (method.getReturnType() == Void.class) {
            name = "void";
        } else {
            name = method.getReturnType().getSimpleName();
        }
        name = name + " " + method.getName();
        if (method.getParameterCount() == 0) {
            name = name + "()";
        } else {
            final StringBuffer sb = new StringBuffer("(");
            final Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(params[i].getType().getSimpleName());
            }
            sb.append(")");
            name = name + sb;
        }
        return name;
    }

}
