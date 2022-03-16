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

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.Config;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationFormat;
import org.bytemechanics.config.manager.internal.commons.io.YAMLPropertyReader;
import org.bytemechanics.config.manager.internal.commons.io.YAMLPropertyWriter;

/**
 *
 * @author afarre
 */
public enum ConfigParserFactory implements ConfigParser{
    
    PROPERTIES(".properties"){
        @Override
        public Stream<Config> read(Reader _reader) {
            
            try {
                final Properties properties=new Properties();
                properties.load(_reader);
                return properties.entrySet()
                                    .stream()
                                        .map(entry -> new Config((String)entry.getKey(),(String)entry.getValue()));
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void write(Writer _writer, Stream<Config> _config) {
            try {
                ((Properties)_config.collect(Collectors.toMap(Config::getKey, Config::getValue, (a,b) -> b,Properties::new)))
                        .store(_writer, "");
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }        
        }
    },
    YAML(".yaml",".yml"){
        @Override
        public Stream<Config> read(Reader _reader) {
            return new YAMLPropertyReader(_reader)
                        .stream()
                            .map(property -> new Config(property.getKey(),property.getValue()));
        }

        @Override
        public void write(Writer _writer, Stream<Config> _config) {
            try(YAMLPropertyWriter writer=new YAMLPropertyWriter(_writer)){
                Stream<YAMLPropertyWriter.Property> properties=_config
                                                                    .sorted()
                                                                    .map(config -> new YAMLPropertyWriter.Property(config.getKey(),config.getValue()));
                writer.write(properties);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    },
    ;

    private final String[] suffixes;
    

    ConfigParserFactory(String... _suffixes){
        this.suffixes=_suffixes;
    }

    public String[] getSuffixes() {
        return suffixes;
    }
    public boolean canRead(final String _path){
        return Stream.of(this.suffixes)
                        .filter(suffix -> _path.toLowerCase().endsWith(suffix))
                        .map(suffix -> true)
                        .findAny()
                            .orElse(false);
    }
    
    
    public static final String validFormats(){
        return Stream.of(ConfigParserFactory.values())
                        .map(ConfigParserFactory::getSuffixes)
                        .flatMap(Stream::of)
                        .collect(Collectors.joining(", ", "[","]"));
    }
    public static final ConfigParser valueOf(final URI _location) {
        
        final String path=URIUtils.getHostAndPath(_location);
        return Stream.of(ConfigParserFactory.values())
                        .filter(configReader -> configReader.canRead(path))
                        .findFirst()
                            .orElseThrow(() -> new UnsupportedConfigLocationFormat(_location, validFormats()));
    }
    
    public static Stream<Config> read(Reader _reader,final URI _location) {
        return Optional.ofNullable(_location)
                        .map(ConfigParserFactory::valueOf)
                        .map(configReader -> configReader.read(_reader))
                        .orElse(Stream.empty());
    }    
    public static void write(Writer _writer,final URI _location, Stream<Config> _config) {
        Optional.ofNullable(_location)
                .map(ConfigParserFactory::valueOf)
                .ifPresent(configReader -> configReader.write(_writer,_config));
    }    
}
