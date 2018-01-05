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

import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * The object that contains the annotated method will be validated on execution
 * of the method. An {@link IllegalStateException} will be thrown if the object
 * is invalid.
 */
@Interceptor
@ValidateInstance
public final class ValidateInstanceInterceptor extends AbstractInterceptor {

    @AroundInvoke
    public final Object validateInstanceOfMethod(final InvocationContext ctx)
            throws Exception {

        final Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        final Set<ConstraintViolation<Object>> violations = validator
                .validate(ctx.getTarget());
        if (!violations.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            violations.forEach((v) -> {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(v.getMessage());
            });
            final String className = ctx.getMethod().getDeclaringClass().getName();
            final String method = signature(ctx.getMethod());
            throw new IllegalStateException("The instance of type '" + className
                    + "' was invalid when calling method '" + method + "': "
                    + sb.toString());
        }
        return ctx.proceed();

    }

}
