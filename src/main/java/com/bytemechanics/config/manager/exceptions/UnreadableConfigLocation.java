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
 * Raised when unable to read config location from unknown reasons
 * @author afarre
 */
public class UnreadableConfigLocation extends RuntimeException{

    private static final String MESSAGE="Unable to read config location {}";
    
    /**
     * Constructor to build the exception
     * @param _location location not supported
     * @param _cause underlaying exception
     */
    public UnreadableConfigLocation(final URI _location,final Throwable _cause) {
        this(SimpleFormat.format(MESSAGE,_location),_cause);
    }

    /**
     * Constructor to build the exception
     * @param _message message to use
     * @param _cause underlaying exception
     */
    public UnreadableConfigLocation(final String _message,final Throwable _cause) {
        super(_message,_cause);
    }
}
