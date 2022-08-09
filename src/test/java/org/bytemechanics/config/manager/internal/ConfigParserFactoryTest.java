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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.Config;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author afarre
 */
public class ConfigParserFactoryTest {
    
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> ConfigParserFactoryTest >>>> setupSpec");
        try ( InputStream inputStream = ConfigParserFactoryTest.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }

    @BeforeEach
    void beforeEachTest(final TestInfo testInfo) {
        System.out.println(">>>>> " + this.getClass().getSimpleName() + " >>>> " + testInfo.getTestMethod().map(Method::getName).orElse("Unkown") + "" + testInfo.getTags().toString() + " >>>> " + testInfo.getDisplayName());
    }

    static Stream<Arguments> dataPack() {
        return Stream.of(
                Arguments.of("file://src/test/resources/test.properties",ConfigParserFactory.PROPERTIES,Paths.get("src/test/resources/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("file://src/test/resources/test.yaml",ConfigParserFactory.YAML,Paths.get("src/test/resources/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("file://src/test/resources/test.yml",ConfigParserFactory.YAML,Paths.get("src/test/resources/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.PROPERTIES",ConfigParserFactory.PROPERTIES,Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.YAML",ConfigParserFactory.YAML,Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.Yml",ConfigParserFactory.YAML,Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://test.properties",ConfigParserFactory.PROPERTIES,Paths.get("src/test/resources/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("classpath://test.yaml",ConfigParserFactory.YAML,Paths.get("src/test/resources/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://test.yml",ConfigParserFactory.YAML,Paths.get("src/test/resources/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties"))
        );
    }


    @ParameterizedTest(name = "When try to get ConfigParser of {0} should return {1}")
    @MethodSource("dataPack")
    public void testValueOf_URI(final String _uri,final ConfigParser _configParser) throws URISyntaxException {
        URI _location = new URI(_uri);
        ConfigParser result = ConfigParserFactory.valueOf(_location);
        Assertions.assertSame(_configParser, result);
    }
    @Test
    public void testValueOf_null() {
        Assertions.assertThrows(NullPointerException.class,() ->  ConfigParserFactory.valueOf((URI)null));
    }
    @Test
    public void testValueOf_notSupported() throws URISyntaxException {
        URI location = new URI("http://my/uri");
        Assertions.assertThrows(UnsupportedConfigLocationFormat.class,() ->  ConfigParserFactory.valueOf(location));
    }
    
    @Test
    public void testValidFormats() {
        String expResult = "[.properties, .yaml, .yml]";
        String result = ConfigParserFactory.validFormats();
        Assertions.assertEquals(expResult, result);
    }

    @ParameterizedTest(name = "When read contents from {0} located at {2} should load same content as {3}")
    @MethodSource("dataPack")
    public void testRead(final String _uri,final ConfigParser _provider, final Path _source, final Path _expected) throws IOException, URISyntaxException {
        URI _location = new URI(_uri);
        Properties properties=new Properties();
        try(Reader reader=Files.newBufferedReader(_expected,Charset.forName("UTF-8"))){
            properties.load(reader);
        }
        List<Config> expected=properties.entrySet()
                                            .stream()
                                                .map(entry -> Config.of((String)entry.getKey(),(String)entry.getValue()))
                                                .sorted()
                                                .collect(Collectors.toList());
        
        try(Reader reader=Files.newBufferedReader(_source,Charset.forName("UTF-8"))){
            List<Config> result=ConfigParserFactory.read(reader,_location)
                                                    .sorted()
                                                    .collect(Collectors.toList());
            Assertions.assertEquals(expected.size(),result.size());
            Assertions.assertEquals(expected,result);
        }
    }
    static Stream<Arguments> inputWrongDataPack() {
        return Stream.of(
                Arguments.of("http://target/tests/config-out.properties",null,NullPointerException.class),
                Arguments.of("http://target/tests/config-out.yaml",null,NullPointerException.class),
                Arguments.of("http://target/tests/config-out.yml",null,NullPointerException.class),
                Arguments.of("file://target",Paths.get("src/test/resources/test.yaml"),UnsupportedConfigLocationFormat.class)
        );
    }
    @ParameterizedTest(name = "When read from {0} and {1} should raise {2}")
    @MethodSource("inputWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testRead_failure(final String _uri,final Path _source, final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        if(_source!=null){
            try(Reader reader=Files.newBufferedReader(_source,Charset.forName("UTF-8"))){
                Assertions.assertThrows(_exception,() -> ConfigParserFactory.read(reader,_location));
            }
        }else{
            Assertions.assertThrows(_exception,() -> ConfigParserFactory.read(null,_location));
        }
    }

    
    static Stream<Arguments> writeDataPack() {
        return Stream.of(
                Arguments.of("file://target/tests/config-write-out.properties",Paths.get("target/tests/config-write-out.properties"),Paths.get("src/test/resources/test-parsed-properties.properties"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("file://target/tests/config-write-out.yaml",Paths.get("target/tests/config-write-out.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("file://target/tests/config-write-out.yml",Paths.get("target/tests/config-write-out.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties"),Paths.get("src/test/resources/test.yml"))
        );
    }

    @ParameterizedTest(name = "When write to {0} at {1} writing {2} whould write the same content as {3}")
    @MethodSource("writeDataPack")
    public void testWrite(final String _uri,final Path _target,final Path _source,final Path _expected) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        List<String> expected = Files.lines(_expected,Charset.forName("UTF-8"))
                                        .filter(line -> !line.startsWith("#"))
										.sorted()
                                        .collect(Collectors.toList());
        
         Properties properties=new Properties();
        try(Reader reader=Files.newBufferedReader(_source,Charset.forName("UTF-8"))){
            properties.load(reader);
        }
        List<Config> source=properties.entrySet()
                                            .stream()
                                                .map(entry -> Config.of((String)entry.getKey(),(String)entry.getValue()))
                                                .sorted()
                                                .collect(Collectors.toList());
        
        try(BufferedWriter writer=Files.newBufferedWriter(_target,Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING)){
            ConfigParserFactory.write(writer,_location,source.stream());
        }      
        List<String> result = Files.lines(_target,Charset.forName("UTF-8"))
                                    .filter(line -> !line.startsWith("#"))
									.sorted()
                                    .collect(Collectors.toList());
        Assertions.assertEquals(expected,result);
    }

    static Stream<Arguments> inputWriteWrongDataPack() {
        return Stream.of(
                Arguments.of("http://target/tests/config-out.properties",null,NullPointerException.class),
                Arguments.of("http://target/tests/config-out.yaml",null,NullPointerException.class),
                Arguments.of("http://target/tests/config-out.yml",null,NullPointerException.class),
                Arguments.of("file://target",Paths.get("target/tests/config-write-out-wrong.yml"),UnsupportedConfigLocationFormat.class)
        );
    }
    @ParameterizedTest(name = "When write to {0} at {1} should raise {2}")
    @MethodSource("inputWriteWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testWrite_failure(final String _uri,final Path _target, final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        if(_target!=null){
            try(BufferedWriter writer=Files.newBufferedWriter(_target,Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING)){
                 Assertions.assertThrows(_exception,() -> ConfigParserFactory.write(writer,_location,Stream.empty()));
            }
        }else{
            Assertions.assertThrows(_exception,() -> ConfigParserFactory.write(null,_location,Stream.empty()));
        }
    }
}
