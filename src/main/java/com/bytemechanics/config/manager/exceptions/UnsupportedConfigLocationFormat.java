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
package com.bytemechanics.config.manager.exceptions;

import java.net.URI;
import org.bytemechanics.config.manager.internal.commons.string.SimpleFormat;

/**
 * Raised when trying to load configuration from an location with unsupported formats
 * @author afarre
 */
public class UnsupportedConfigLocationFormat extends RuntimeException{

    private static final String MESSAGE="Unsupported config location format {} valid formats should end with {}";
    
    /**
     * Constructor to build the exception
     * @param _location location not supported
     * @param _supportedFormats description of supported formats
     */
    public UnsupportedConfigLocationFormat(final URI _location,final String _supportedFormats) {
        super(SimpleFormat.format(MESSAGE,_location,_supportedFormats));
    }
}
