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
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationFormat;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationScheme;
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
public class ConfigManagerImplTest {
    
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> ConfigManagerImplTest >>>> setupSpec");
        try ( InputStream inputStream = ConfigManagerImplTest.class.getResourceAsStream("/logging.properties")) {
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

    @Test
    public void testConstructor_uri_charset() throws URISyntaxException {
        final List<URI> locations=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        final Charset charset=Charset.forName("UTF-8");
        ConfigManagerImpl instance = new ConfigManagerImpl(locations, charset);
        Assertions.assertAll(() -> Assertions.assertEquals(locations, instance.getLocations()),
                               () -> Assertions.assertEquals(charset, instance.getCharset()));
    }
    @Test
    public void testConstructor_nulls() throws URISyntaxException {
        final List<URI> locations=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        final Charset charset=Charset.forName("UTF-8");
        Assertions.assertAll(() -> Assertions.assertThrows(NullPointerException.class,() -> new ConfigManagerImpl(null, charset)),
                               () -> Assertions.assertThrows(NullPointerException.class,() -> new ConfigManagerImpl(locations, null)));
    }
    @Test
    public void testConstructor_charset_locations() throws URISyntaxException {
        final List<URI> expected=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        final Charset charset=Charset.forName("UTF-8");
        ConfigManagerImpl instance = new ConfigManagerImpl(charset,"file://src/test/resources/test.properties","file://src/test/resources/test.yaml","classpath://org/bytemechanics/config/manager/internal/test.Yml");
        Assertions.assertAll(() -> Assertions.assertEquals(expected, instance.getLocations()),
                               () -> Assertions.assertEquals(charset, instance.getCharset()));
    }
    @Test
    public void testConstructor_locations() throws URISyntaxException {
        final List<URI> expected=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        ConfigManagerImpl instance = new ConfigManagerImpl("file://src/test/resources/test.properties","file://src/test/resources/test.yaml","classpath://org/bytemechanics/config/manager/internal/test.Yml");
        Assertions.assertAll(() -> Assertions.assertEquals(expected, instance.getLocations()),
                               () -> Assertions.assertEquals(Charset.defaultCharset(), instance.getCharset()));
    }
    @Test
    public void testConstructor_charset_uris() throws URISyntaxException {
        final List<URI> expected=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        final Charset charset=Charset.forName("UTF-8");
        ConfigManagerImpl instance = new ConfigManagerImpl(charset,new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"));
        Assertions.assertAll(() -> Assertions.assertEquals(expected, instance.getLocations()),
                               () -> Assertions.assertEquals(charset, instance.getCharset()));
    }
    @Test
    public void testConstructor_uris() throws URISyntaxException {
        final List<URI> expected=Stream.of(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"))
                                        .collect(Collectors.toList());
        ConfigManagerImpl instance = new ConfigManagerImpl(new URI("file://src/test/resources/test.properties"),new URI("file://src/test/resources/test.yaml"),new URI("classpath://org/bytemechanics/config/manager/internal/test.Yml"));
        Assertions.assertAll(() -> Assertions.assertEquals(expected, instance.getLocations()),
                               () -> Assertions.assertEquals(Charset.defaultCharset(), instance.getCharset()));
    }

    static Stream<Arguments> readDataPack() {
        return Stream.of(
                Arguments.of("file://src/test/resources/test.properties",Paths.get("src/test/resources/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("file://src/test/resources/test.yaml",Paths.get("src/test/resources/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("file://src/test/resources/test.yml",Paths.get("src/test/resources/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.properties",Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yaml",Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yml",Paths.get("src/test/resources/org/bytemechanics/config/manager/internal/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://test.properties",Paths.get("src/test/resources/test.properties"),Paths.get("src/test/resources/test-parsed-properties.properties")),
                Arguments.of("classpath://test.yaml",Paths.get("src/test/resources/test.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties")),
                Arguments.of("classpath://test.yml",Paths.get("src/test/resources/test.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties"))
        );
    }

    @ParameterizedTest(name = "When try to read from location {0} from source {1} should load {2}")
    @MethodSource("readDataPack")
    public void testRead_URI_Reader(final String _uri,final Path _source, final Path _expected) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);

        Properties properties=new Properties();
        try(Reader reader=Files.newBufferedReader(_expected,Charset.forName("UTF-8"))){
            properties.load(reader);
        }
        List<Config> expected=properties.entrySet()
                                            .stream()
                                                .map(entry -> Config.of((String)entry.getKey(),(String)entry.getValue()))
                                                .sorted()
                                                .collect(Collectors.toList());

        try(Reader reader = Files.newBufferedReader(_source)){
            List<Config> actual=instance.read(_location,reader)
                                        .sorted()
                                        //.peek(System.out::println)
                                        .collect(Collectors.toList());
            Assertions.assertEquals(expected,actual);
        }
    }

    static Stream<Arguments> inputReadWrongDataPack() {
        return Stream.of(
                Arguments.of("file://target/tests/config-out.properties",null,NullPointerException.class),
                Arguments.of(null,Paths.get("src/test/resources/test-parsed-yaml.properties"),NullPointerException.class),
                Arguments.of(null,null,NullPointerException.class),
                Arguments.of("file://target/tests/config-out.propertis",Paths.get("src/test/resources/test-parsed-yaml.properties"),UnsupportedConfigLocationFormat.class),
                Arguments.of("classpath://target/tests/config-out.yamel",Paths.get("src/test/resources/test-parsed-yaml.properties"),UnsupportedConfigLocationFormat.class),
                Arguments.of("file://target",Paths.get("src/test/resources/test-parsed-yaml.properties"),UnsupportedConfigLocationFormat.class)
        );
    }
    @ParameterizedTest(name = "When read from {0} and {1} should raise {2}")
    @MethodSource("inputReadWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testRead_URI_Reader_failure(final String _uri,final Path _source, final Class _exception) throws URISyntaxException, IOException {
        URI _location = (_uri!=null)? new URI(_uri) : null;
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        if(_source!=null){
            try(Reader reader=Files.newBufferedReader(_source,Charset.forName("UTF-8"))){
                Assertions.assertThrows(_exception,() -> instance.read(_location,reader));
            }
        }else{
            Assertions.assertThrows(_exception,() -> instance.read(_location,null));
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
    public void testWrite_URI_Writer_Stream(final String _uri,final Path _target,final Path _source,final Path _expected) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        List<String> expected = Files.lines(_expected,Charset.forName("UTF-8"))
                                        .filter(line -> !line.startsWith("#"))
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
        
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        try(BufferedWriter writer=Files.newBufferedWriter(_target,Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING)){
            instance.write(_location,writer,source.stream());
        }      
        List<String> result = Files.lines(_target,Charset.forName("UTF-8"))
                                    .filter(line -> !line.startsWith("#"))
                                    .collect(Collectors.toList());
        Assertions.assertEquals(expected,result);
    }

   static Stream<Arguments> inputWriterWrongDataPack() {
        return Stream.of(
                Arguments.of("file://target/tests/config-out.properties",null,NullPointerException.class),
                Arguments.of(null,Paths.get("target/tests/config-read-out-wrong.yml"),NullPointerException.class),
                Arguments.of(null,null,NullPointerException.class),
                Arguments.of("file://target/tests/config-out.propertis",Paths.get("target/tests/config-read-out-wrong.yml"),UnsupportedConfigLocationFormat.class),
                Arguments.of("classpath://target/tests/config-out.yamel",Paths.get("target/tests/config-read-out-wrong.yml"),UnsupportedConfigLocationFormat.class),
                Arguments.of("file://target",Paths.get("target/tests/config-read-out-wrong.yml"),UnsupportedConfigLocationFormat.class)
        );
    }
     @ParameterizedTest(name = "When write to {0} at {1} should raise {2}")
    @MethodSource("inputWriterWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testWrite_URI_Writer_failure(final String _uri,final Path _target, final Class _exception) throws URISyntaxException, IOException {
        URI _location = (_uri!=null)? new URI(_uri) : null;
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        if(_target!=null){
            Files.createDirectories(_target.getParent());
            try(BufferedWriter writer=Files.newBufferedWriter(_target,Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING)){
                 Assertions.assertThrows(_exception,() -> instance.write(_location,writer,Stream.empty()));
            }
        }else{
            Assertions.assertThrows(_exception,() -> instance.write(_location,null,Stream.empty()));
        }
    }

    
    @ParameterizedTest(name = "When try to read from location {0} should load {2}")
    @MethodSource("readDataPack")
    public void testRead_URI(final String _uri,final Path _source, final Path _expected) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);

        Properties properties=new Properties();
        try(Reader reader=Files.newBufferedReader(_expected,Charset.forName("UTF-8"))){
            properties.load(reader);
        }
        List<Config> expected=properties.entrySet()
                                            .stream()
                                                .map(entry -> Config.of((String)entry.getKey(),(String)entry.getValue()))
                                                .sorted()
                                                .collect(Collectors.toList());

        List<Config> actual=instance.read(_location)
                                   .sorted()
                                   .peek(System.out::println)
                                   .collect(Collectors.toList());
        Assertions.assertEquals(expected,actual);
    }

    static Stream<Arguments> inputReadSimpleWrongDataPack() {
        return Stream.of(
                Arguments.of("http://target/tests/config-out.properties",UnsupportedConfigLocationScheme.class),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/URIUtils.javaw",UnsupportedConfigLocationFormat.class)
        );
    }
    @ParameterizedTest(name = "When read from {0} should raise {1}")
    @MethodSource("inputReadSimpleWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testRead_URI_failure(final String _uri, final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        Assertions.assertThrows(_exception,() -> instance.read(_location));
    }
    
    static Stream<Arguments> writeSimpleDataPack() {
        return Stream.of(
                Arguments.of("file://target/tests/config-write-uri-out.properties",Paths.get("target/tests/config-write-uri-out.properties"),Paths.get("src/test/resources/test-parsed-properties.properties"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("file://target/tests/config-write-uri-out.yaml",Paths.get("target/tests/config-write-uri-out.yaml"),Paths.get("src/test/resources/test-parsed-yaml.properties"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("file://target/tests/config-write-uri-out.yml",Paths.get("target/tests/config-write-uri-out.yml"),Paths.get("src/test/resources/test-parsed-yaml.properties"),Paths.get("src/test/resources/test.yml"))
        );
    }
    @ParameterizedTest(name = "When write to {0} writing {2} whould write the same content as {3}")
    @MethodSource("writeSimpleDataPack")
    public void testWrite_URI(final String _uri,final Path _target,final Path _source,final Path _expected) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        List<String> expected = Files.lines(_expected,Charset.forName("UTF-8"))
                                        .filter(line -> !line.startsWith("#"))
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
        
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        instance.write(_location,source.stream());
        List<String> result = Files.lines(_target,Charset.forName("UTF-8"))
                                    .filter(line -> !line.startsWith("#"))
                                    .collect(Collectors.toList());
        Assertions.assertEquals(expected,result);
    }

    static Stream<Arguments> inputWriteSimpleWrongDataPack() {
        return Stream.of(
                Arguments.of("http://target/tests/config-out.properties",UnsupportedConfigLocationScheme.class),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/URIUtils.javaw",UnsupportedOperationException.class)
        );
    }
    @ParameterizedTest(name = "When write to {0} at {1} should raise {2}")
    @MethodSource("inputWriteSimpleWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testWrite_URI_failure(final String _uri,final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        ConfigManagerImpl instance = new ConfigManagerImpl((URI[])new URI[0]);
        Assertions.assertThrows(_exception,() -> instance.write(_location,Stream.empty()));
    }
}
