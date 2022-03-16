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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Optional;

/**
 *
 * @author afarre
 */
public interface ConfigProvider {
    
    public Optional<InputStream> openInputStream(final URI _location);
    public default Optional<OutputStream> openOutputStream(final URI _location){
        throw new UnsupportedOperationException("Write operation is not supported for location "+_location);
    }
}
