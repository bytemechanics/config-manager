/*
 * Copyright 2022 Byte Mechanics.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytemechanics.config.manager.exceptions;

import java.net.URI;
import org.bytemechanics.config.manager.internal.commons.string.SimpleFormat;

/**
 * Raised when trying to load configuration from an unknown location scheme
 * @see URI#getScheme() 
 * @author afarre
 */
public class UnsupportedConfigLocationScheme extends RuntimeException{

    protected static final String MESSAGE="Unknown config location scheme {} valid schemes are {}";
    
    /**
     * Constructor to build the exception
     * @param _location URI not supported
     * @param _supportedSchemes description of supported schemes
     */
    public UnsupportedConfigLocationScheme(final URI _location,final String _supportedSchemes) {
        super(SimpleFormat.format(MESSAGE,_location,_supportedSchemes));
    }
}
