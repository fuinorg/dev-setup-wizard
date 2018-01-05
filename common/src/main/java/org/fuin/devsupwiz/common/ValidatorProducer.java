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

import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

/**
 * CDI factory that creates a {@link Validator} based on a
 * {@link PlatformResourceBundleLocator}.
 */
public class ValidatorProducer {

    /**
     * Creates a new validator instance.
     * 
     * @return New instance.
     */
    @Produces
    public Validator createValidator() {

        final PlatformResourceBundleLocator resourceBundleLocator = new PlatformResourceBundleLocator(
                ResourceBundleMessageInterpolator.USER_VALIDATION_MESSAGES,
                null, true);

        return Validation.byDefaultProvider().configure()
                .messageInterpolator(new ResourceBundleMessageInterpolator(
                        resourceBundleLocator))
                .buildValidatorFactory().getValidator();
        
    }

}
