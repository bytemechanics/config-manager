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
package org.bytemechanics.config.manager.internal;

import java.net.URI;

/**
 *
 * @author afarre
 */
public class URIUtils {
    
    /**
     * Extract host plus path in one single concatenated string
     * @param _uri uri from where extract the path
     * @return concatenated host and path
     */
    public static final String getHostAndPath(final URI _uri){
        return ""+_uri.getHost()+_uri.getPath();
    }
}
