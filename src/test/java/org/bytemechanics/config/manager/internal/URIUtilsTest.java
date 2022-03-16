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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author afarre
 */
public class URIUtilsTest {
    
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> URIUtilsTest >>>> setupSpec");
        try ( InputStream inputStream = URIUtilsTest.class.getResourceAsStream("/logging.properties")) {
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
                Arguments.of("file://src/test/resources/test.properties","src/test/resources/test.properties"),
                Arguments.of("file://src/test/resources/test.yaml","src/test/resources/test.yaml"),
                Arguments.of("file://src/test/resources/test.yml","src/test/resources/test.yml"),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.properties","org/bytemechanics/config/manager/internal/test.properties"),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yaml","org/bytemechanics/config/manager/internal/test.yaml"),
                Arguments.of("classpath://org/bytemechanics/config/manager/internal/test.yml","org/bytemechanics/config/manager/internal/test.yml"),
                Arguments.of("classpath://test.properties","test.properties"),
                Arguments.of("http://test.yaml","test.yaml"),
                Arguments.of("https://test.yml","test.yml")
        );
    }
    

    @ParameterizedTest(name = "When try to get retrieve hostAndPath from {0} should return {1}")
    @MethodSource("dataPack")
    public void testGetHostAndPath(final String _uri,final String _expectedHostAndPath) throws URISyntaxException {
        URI _location = new URI(_uri);
        String result = URIUtils.getHostAndPath(_location);
        Assertions.assertEquals(_expectedHostAndPath, result);
    }
}
