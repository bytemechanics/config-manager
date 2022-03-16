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
package org.bytemechanics.config.manager.exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.internal.commons.functional.LambdaUnchecker;
import org.bytemechanics.config.manager.internal.commons.string.SimpleFormat;
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
public class UnsupportedConfigLocationSchemeTest {
    
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> UnsupportedConfigLocationSchemeTest >>>> setupSpec");
        try ( InputStream inputStream = UnsupportedConfigLocationSchemeTest.class.getResourceAsStream("/logging.properties")) {
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
                Arguments.of("file://uri/location","[a, b]"),
                Arguments.of(null,"[a, b,c]"),
                Arguments.of("file://uri/location", null),
                Arguments.of(null, null)
        );
    }
    
    @ParameterizedTest(name = "When constructor is called with key={0} and value={1} then the same key/value pair must be recorded")
    @MethodSource("dataPack")
    public void testUriConstructor(final String _uri,final String _supportedFormats) {
        UnsupportedConfigLocationScheme instance = new UnsupportedConfigLocationScheme(Optional.ofNullable(_uri)
                                                                                    .map(LambdaUnchecker.uncheckedFunction(URI::new))
                                                                                    .orElse(null)
                                                                        ,_supportedFormats);
        Assertions.assertAll(() -> Assertions.assertEquals(SimpleFormat.format(UnsupportedConfigLocationScheme.MESSAGE, _uri,_supportedFormats),instance.getMessage())
                            ,() -> Assertions.assertEquals(null,instance.getCause()));
    }
}
