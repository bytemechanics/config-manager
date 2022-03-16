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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.exceptions.UnreadableConfigLocation;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationScheme;
import org.bytemechanics.config.manager.exceptions.UnwritableConfigLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 *
 * @author afarre
 */
public class ConfigProviderFactoryTest {
    
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> ConfigProviderFactoryTest >>>> setupSpec");
        try ( InputStream inputStream = ConfigProviderFactoryTest.class.getResourceAsStream("/logging.properties")) {
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
                Arguments.of("file://src/test/resources/test.properties",ConfigProviderFactory.FILE,Charset.forName("UTF-8"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("file://src/test/resources/test.yaml",ConfigProviderFactory.FILE,Charset.forName("ISO-8859-1"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("file://src/test/resources/test.yml",ConfigProviderFactory.FILE,Charset.forName("UTF-8"),Paths.get("src/test/resources/test.yml")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.properties",ConfigProviderFactory.CLASSPATH,Charset.forName("UTF-8"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yaml",ConfigProviderFactory.CLASSPATH,Charset.forName("ISO-8859-1"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yml",ConfigProviderFactory.CLASSPATH,Charset.forName("UTF-8"),Paths.get("src/test/resources/test.yml")),
                Arguments.of("classpath://test.properties",ConfigProviderFactory.CLASSPATH,Charset.forName("ISO-8859-1"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("classpath://test.yaml",ConfigProviderFactory.CLASSPATH,Charset.forName("UTF-8"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("classpath://test.yml",ConfigProviderFactory.CLASSPATH,Charset.forName("ISO-8859-1"),Paths.get("src/test/resources/test.yml"))
        );
    }

    @ParameterizedTest(name = "When try to get ConfigProvider of {0} should return {1}")
    @MethodSource("dataPack")
    public void testValueOf_URI(final String _uri,final ConfigProvider _configProvider) throws URISyntaxException {
        URI _location = new URI(_uri);
        ConfigProvider result = ConfigProviderFactory.valueOf(_location);
        Assertions.assertSame(_configProvider, result);
    }
    @Test
    public void testValueOf_null() {
        Assertions.assertThrows(NullPointerException.class,() ->  ConfigProviderFactory.valueOf((URI)null));
    }
    @Test
    public void testValueOf_notSupported() throws URISyntaxException {
        URI location = new URI("http://my/uri");
        Assertions.assertThrows(UnsupportedConfigLocationScheme.class,() ->  ConfigProviderFactory.valueOf(location));
    }
    
    @Test
    public void testValidSchemes() {
        String expResult = "[classpath, file]";
        String result = ConfigProviderFactory.validSchemes();
        Assertions.assertEquals(expResult, result);
    }

    @ParameterizedTest(name = "When openInput to {0} using charset {2} should load the same content as {3}")
    @MethodSource("dataPack")
    public void testOpenInputStream_URI_Charset(final String _uri,final ConfigProvider _provider, final Charset _charset,final Path _path) throws IOException, URISyntaxException {
        URI _location = new URI(_uri);
        String expResult = new String(Files.readAllBytes(_path),_charset);
        Optional<Reader> result = ConfigProviderFactory.openInputStream(_location, _charset);
        Assertions.assertTrue(result.isPresent());
        String stringResult;
        try(BufferedReader reader=new BufferedReader(result.get())){
            stringResult=reader.lines()
                                .collect(Collectors.joining("\n"));
        }
        Assertions.assertEquals(expResult,stringResult);
    }
    @ParameterizedTest(name = "When openInput to {0} using charset UTF-8 should return an empty stream")
    @ValueSource(strings = {"classpath://test2.properties","classpath://org/test.yaml","classpath://my/test.yml","file://nothing.yaml"})
    public void testOpenInputStream_notFound(final String _uri) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        Assertions.assertFalse(ConfigProviderFactory.openInputStream(_location, Charset.forName("UTF-8"))
                                                        .isPresent());
    }
    static Stream<Arguments> inputWrongDataPack() {
        return Stream.of(
                Arguments.of("http://target/tests/config-out.properties",UnsupportedConfigLocationScheme.class),
                Arguments.of("file://target",UnreadableConfigLocation.class)
        );
    }
    @ParameterizedTest(name = "When openInput to {0} using charset UTF-8 should raise {1}")
    @MethodSource("inputWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testOpenInputStream_failure(final String _uri,final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        Assertions.assertThrows(_exception,() -> ConfigProviderFactory.openInputStream(_location, Charset.forName("UTF-8")));
    }

    
    static Stream<Arguments> outputDataPack() {
        return Stream.of(
                Arguments.of("file://target/tests/config-out.properties",Charset.forName("UTF-8"),Paths.get("src/test/resources/test.properties")),
                Arguments.of("file://target/tests/config-out.yaml",Charset.forName("UTF-8"),Paths.get("src/test/resources/test.yaml")),
                Arguments.of("file://target/tests/config-out.yml",Charset.forName("UTF-8"),Paths.get("src/test/resources/test.yml"))
        );
    }

    @ParameterizedTest(name = "When openOutput to {0} using charset {1} should write the same content as {2}")
    @MethodSource("outputDataPack")
    public void testOpenOutputStream(final String _uri,final Charset _charset,final Path _path) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        String expResult = new String(Files.readAllBytes(_path),_charset);
        Optional<Writer> result = ConfigProviderFactory.openOutputStream(_location, _charset);
        
        Assertions.assertTrue(result.isPresent());
        try(BufferedWriter writer=new BufferedWriter(result.get())){
            writer.append(expResult);
        }
        
        String stringResult = new String(Files.readAllBytes(Paths.get(_uri.substring(7))),_charset);
        Assertions.assertEquals(expResult,stringResult);
    }

    static Stream<Arguments> outputWrongDataPack() {
        return Stream.of(
                Arguments.of("classpath://target/tests/config-out.properties",UnsupportedOperationException.class),
                Arguments.of("http://target/tests/config-out.properties",UnsupportedConfigLocationScheme.class),
                Arguments.of("file://target",UnwritableConfigLocation.class)
        );
    }
    
    @ParameterizedTest(name = "When openOutput to {0} using charset UTF-8 should raise {1}")
    @MethodSource("outputWrongDataPack")
    @SuppressWarnings("unchecked")
    public void testOpenOutputStream_failure(final String _uri,final Class _exception) throws URISyntaxException, IOException {
        URI _location = new URI(_uri);
        Assertions.assertThrows(_exception,() -> ConfigProviderFactory.openOutputStream(_location, Charset.forName("UTF-8")));
    }
}
