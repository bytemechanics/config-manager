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
package com.bytemechanics.config.manager.internal.loaders;

import com.bytemechanics.config.manager.exceptions.UnreadableConfigLocation;
import com.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationScheme;
import com.bytemechanics.config.manager.exceptions.UnwritableConfigLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.internal.commons.functional.LambdaUnchecker;

/**
 *
 * @author afarre
 */
public enum ConfigProviderFactory implements ConfigProvider{
    
    CLASSPATH{
        @Override
        public Optional<InputStream> openInputStream(final URI _location) {
            return Optional.ofNullable(_location)
                            .map(URI::getPath)
                            .map(ClassLoader::getSystemResourceAsStream);        
        }
    },
    FILE{
        private InputStream openInputStream(final Path _path){
            
            try {
                return Files.newInputStream(_path,StandardOpenOption.READ);
            } catch (IOException ex) {
                throw new UnreadableConfigLocation("Unable to open "+_path+" file",ex);
            }
        }
        @Override
        public Optional<InputStream> openInputStream(final URI _location) {
            return Optional.ofNullable(_location)
                            .map(URI::getPath)
                            .map(Paths::get)
                            .filter(Files::exists)
                            .map(this::openInputStream);        
        }
        private Path createFolders(final Path _path){
            
            try {
                Files.createDirectories(_path);
            } catch (IOException ex) {
                throw new UnwritableConfigLocation("Can not create "+_path+" file parent folders",ex);
            }
            
            return _path;
        }
        private OutputStream openOutputStream(final Path _path){
            
            try {
                return Files.newOutputStream(_path,StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                throw new UnwritableConfigLocation("Unable to create "+_path+" file",ex);
            }
        }
        @Override
        public Optional<OutputStream> openOutputStream(final URI _location) {
            return Optional.ofNullable(_location)
                            .map(URI::getPath)
                            .map(Paths::get)
                            .map(this::createFolders)
                            .map(this::openOutputStream);        
       }
    },
    ;

    private final Supplier<ConfigProvider> loaderClass;
    
    ConfigProviderFactory(){
        this(null);
    }
    ConfigProviderFactory(final Supplier<ConfigProvider> _loaderClass){
        this.loaderClass=_loaderClass;
    }

    @Override
    public Optional<InputStream> openInputStream(URI _location) {
        return this.loaderClass.get()
                                .openInputStream(_location);
    }

    public static final String validSchemes(){
        return Stream.of(ConfigProviderFactory.values())
                        .map(ConfigProviderFactory::name)
                        .collect(Collectors.joining(", ", "[","]"));
    }
    public static final ConfigProvider valueOf(final URI _location) {
        
        final String scheme=_location.getScheme();
        return Stream.of(ConfigProviderFactory.values())
                        .filter(configProvider -> scheme.equalsIgnoreCase(configProvider.name()))
                        .findFirst()
                            .orElseThrow(() -> new UnsupportedConfigLocationScheme(_location, validSchemes()));
    }
    
    public static final Optional<Reader> openInputStream(final URI _location,final Charset _charset) {

        return Optional.ofNullable(_location)
                        .map(ConfigProviderFactory::valueOf)
                        .flatMap(configProvider -> configProvider.openInputStream(_location))
                        .map(LambdaUnchecker.uncheckedFunction(inputStream -> new InputStreamReader(inputStream,_charset)));
    }    
    public static final Optional<Writer> openOutputStream(final URI _location,final Charset _charset) {

        return Optional.ofNullable(_location)
                        .map(ConfigProviderFactory::valueOf)
                        .flatMap(configProvider -> configProvider.openOutputStream(_location))
                        .map(LambdaUnchecker.uncheckedFunction(inputStream -> new OutputStreamWriter(inputStream,_charset)));
    }    
}
