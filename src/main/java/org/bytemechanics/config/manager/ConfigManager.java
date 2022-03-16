/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package org.bytemechanics.config.manager;

import java.net.URI;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.exceptions.UnreadableConfigLocation;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationFormat;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationScheme;

/**
 * Config Manager service, there are several things to take in account:
 * Overwrited lists can have unexpected behaviours please convert it to maps if you want to override any attribute, otherwise you need to take in account:
 * If you are overriding with yaml files include all its elements changing only what you want to change
 * If you change the size of a list from properties origin the helper field length should no be correct
 * @author afarre
 */
public interface ConfigManager {
    
    /**
     * Reads the uri location if possible returning a configuration stream
     * @param _location location where read the configuration
     * @return Stream of configurations or an empty stream
     * @throws UnreadableConfigLocation if the location can not be readed
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws UnsupportedConfigLocationScheme if the scheme of the location is not supported (location type)
     */
    public Stream<Config> read(final URI _location);

    /**
     * Utility method to update configuration locations when possible
     * @param _location location to update/create configuration
     * @param _config stream of config files to write
     * @throws UnwritableConfigLocation when for any reason the configuration can not be writen
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws UnsupportedConfigLocationScheme if the scheme of the location is not supported (location type)
     * @throws UnsupportedOperationException if write operation is not supported by the location format and/or scheme
     */
    public void write(final URI _location, Stream<Config> _config);
    /**
     * Reads all configured locations and return as stream
     * @return Stream of configurations or an empty stream
     * @throws UnreadableConfigLocation if the location can not be readed
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws UnsupportedConfigLocationScheme if the scheme of the location is not supported (location type)
     */
    public Stream<Config> stream();
    /**
     * Reads all configured locations and populate into System properties
     * @throws UnreadableConfigLocation if the location can not be readed
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws UnsupportedConfigLocationScheme if the scheme of the location is not supported (location type)
     */
    public void load();
}
