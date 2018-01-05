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

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs all method calls on TRACE level annotated with the {@link Loggable}
 * annotation.
 */
@Interceptor
@Loggable
public final class LoggingInterceptor extends AbstractInterceptor {

    @AroundInvoke
    public final Object logMethodEntry(final InvocationContext ctx) throws Exception {
        final Logger log = LoggerFactory.getLogger(ctx.getMethod().getDeclaringClass());
        if (log.isTraceEnabled()) {
            final int targetHashCode = ctx.getTarget().hashCode();
            final Method method = ctx.getMethod();
            final String methodName = signature(method);
            log.trace("BEGIN {} {}", targetHashCode, methodName);
            if (method.getParameterCount() > 0) {
                final Parameter[] params = method.getParameters();
                for (int i = 0; i < params.length; i++) {
                    log.trace(params[i].getName() + "=" + ctx.getParameters()[i]);
                }
            }
            final Object retVal = ctx.proceed();
            if (method.getReturnType() != void.class) {
                log.trace("returns: {}", retVal);
            }
            log.trace("END   {} {}", targetHashCode, methodName);
            return retVal;
        } else {
            return ctx.proceed();
        }

    }

}
