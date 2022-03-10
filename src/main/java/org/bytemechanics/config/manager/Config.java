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
package org.bytemechanics.config.manager;

import java.util.Objects;

/**
 * Configuration sotre bean
 * @author afarre
 */
public class Config {
    
    private final String key;
    private final String value;

    /**
     * Builds configuraiton from the given key/value pairs
     * @param _key key to store
     * @param _value value to store
     * @throws NullPointerException if the given _key is null
     */
    public Config(final String _key, final String _value) {
        Objects.requireNonNull(_key, "No null _key allowed");
        this.key = _key;
        this.value = _value;
    }

    
    /**
     * Retrieve key value
     * @return configuration key value
     */
    public String getKey() {
        return key;
    }
    /**
     * Retrieve value 
     * @return configuration value 
     */
    public String getValue() {
        return value;
    }

    
    /** @see Object#hashCode() */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.key);
        hash = 53 * hash + Objects.hashCode(this.value);
        return hash;
    }
    /** @see Object#equals(java.lang.Object) */
    @Override
    public boolean equals(final Object _other) {
        if (this == _other) {
            return true;
        }
        if (_other == null) {
            return false;
        }
        if (getClass() != _other.getClass()) {
            return false;
        }
        final Config other = (Config) _other;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
    
    
    /**
     * Utility method to call constructor without new
     * @param _key key to store
     * @param _value value to store
     * @return new Config created
     * @throws NullPointerException if the given _key is null
     */
    public static final Config of(final String _key, final String _value){
        return new Config(_key, _value);
    }
}
