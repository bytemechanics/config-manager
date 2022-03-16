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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
public class ConfigTest {

    @BeforeAll
    public static void setup() throws IOException {
        System.out.println(">>>>> ConfigTest >>>> setupSpec");
        try ( InputStream inputStream = ConfigTest.class.getResourceAsStream("/logging.properties")) {
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
                Arguments.of("2", "left-value"),
                Arguments.of("right-value", "2.2d"),
                Arguments.of("right-value-not-null", null)
        );
    }

    @ParameterizedTest(name = "When constructor is called with key={0} and value={1} then the same key/value pair must be recorded")
    @MethodSource("dataPack")
    public void testConstructor(final String _key,final String _value) {
        Config instance = Config.of(_key,_value);
        Assertions.assertAll(() -> Assertions.assertEquals(_key,instance.getKey())
                            ,() -> Assertions.assertEquals(_value,instance.getValue()));
    }
    @Test
    public void testConstructor_failure() {
        Assertions.assertThrows(NullPointerException.class,() -> new Config(null, "whatever"));
    }


    @ParameterizedTest(name = "Two instance with the same values key={0} and value={1} should have the same hashcode")
    @MethodSource("dataPack")
    public void testHashCode(final String _key,final String _value) {
        Config instance1 = Config.of(_key,_value);
        Config instance2 = Config.of(_key,_value);
        Assertions.assertEquals(instance1.hashCode(), instance2.hashCode());
    }
    @ParameterizedTest(name = "Two instance with the distinct values key={0} and value={1} should have disctinct hashcode")
    @MethodSource("dataPack")
    public void testHashCode_distinct(final String _key,final String _value) {
        Config instance1 = Config.of(_key,_value);
        Config instance2 = Config.of("a","b");
        Assertions.assertNotEquals(instance1.hashCode(), instance2.hashCode());
    }

    @ParameterizedTest(name = "Two instance with the same values key={0} and value={1} should return true on equals comparison")
    @MethodSource("dataPack")
    public void testEquals(final String _key,final String _value) {
        Config instance1 = Config.of(_key,_value);
        Config instance2 = Config.of(_key,_value);
        Assertions.assertAll(() ->  Assertions.assertTrue(instance1.equals(instance2))
                            ,() -> Assertions.assertTrue(instance2.equals(instance1)));
    }
    @ParameterizedTest(name = "Two instance with the distinct values key={0} and value={1} should return false on equals comparison")
    @MethodSource("dataPack")
    public void testEquals_distinct(final String _key,final String _value) {
        Config instance1 = Config.of(_key,_value);
        Config instance2 = Config.of("a","b");
        Assertions.assertAll(() ->  Assertions.assertFalse(instance1.equals(instance2))
                            ,() -> Assertions.assertFalse(instance2.equals(instance1)));
    }

    static Stream<Arguments> toStringDataPack() {
        return Stream.of(
                Arguments.of("2", "left-value","Config{key=2, value=left-value}"),
                Arguments.of("2", null,"Config{key=2, value=null}")
        );
    }
    @ParameterizedTest(name = "toString of {0} to {1} should return {2}")
    @MethodSource("toStringDataPack")
    public void testToString(final String _key,final String _value,final String _expected) {
        Assertions.assertEquals(_expected,Config.of(_key, _value).toString());
    }

    static Stream<Arguments> compareToDataPack() {
        return Stream.of(
                Arguments.of(Config.of("2", "left-value"),Config.of("2", "left-value"),"2".compareTo("2")),
                Arguments.of(Config.of("2", "left-value1"),Config.of("2", "left-value"),"2".compareTo("2")),
                Arguments.of(Config.of("2", "left-value"),Config.of("2", "left-value1"),"2".compareTo("2")),
                Arguments.of(Config.of("22", "left-value"),Config.of("2", "left-value"),"22".compareTo("2")),
                Arguments.of(Config.of("22", "left-value"),null,-1),
                Arguments.of(Config.of("2", "left-value"),Config.of("22", "left-value"),"2".compareTo("22"))
        );
    }
    @ParameterizedTest(name = "Comparing {0} to {1} should return {2}")
    @MethodSource("compareToDataPack")
    public void testCompareTo(final Config _config1,final Config _config2, int _expected) {
        Assertions.assertEquals(_expected,_config1.compareTo(_config2));
    }
}
