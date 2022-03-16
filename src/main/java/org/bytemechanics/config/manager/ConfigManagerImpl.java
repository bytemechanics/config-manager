/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package org.bytemechanics.config.manager;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bytemechanics.config.manager.exceptions.UnreadableConfigLocation;
import org.bytemechanics.config.manager.exceptions.UnsupportedConfigLocationFormat;
import org.bytemechanics.config.manager.exceptions.UnwritableConfigLocation;
import org.bytemechanics.config.manager.internal.ConfigParserFactory;
import org.bytemechanics.config.manager.internal.ConfigProviderFactory;

/**
 * Config manager service implementation
 * @author afarre
 */
public class ConfigManagerImpl implements ConfigManager{

    private final Charset charset;
    private final List<URI> locations;

    /**
     * Config manager constructor
     * @param _locations list of URIs to load configurations
     * @param _charset charset to use to load the configurations
     */
    public ConfigManagerImpl(final List<URI> _locations,final Charset _charset) {
        this.locations=Objects.requireNonNull(_locations,"Mandatory parameter _locations");
        this.charset=Objects.requireNonNull(_charset,"Mandatory parameter _charset");
    }
    /**
     * Config manager constructor
     * @param _charset charset to use to load the configurations
     * @param _locations list of URIs to load configurations
     */
    public ConfigManagerImpl(final Charset _charset,final URI... _locations) {
        this(Stream.of(_locations)
                    .collect(Collectors.toList())
                ,_charset);
    }
    /**
     * Config manager constructor
     * @param _charset charset to use to load the configurations
     * @param _locations list of locations to load configurations
     */
    public ConfigManagerImpl(final Charset _charset,final String... _locations) {
        this(Stream.of(_locations)
                    .map(URI::create)
                    .collect(Collectors.toList())
                ,_charset);
    }
    /**
     * Config manager constructor using the default charset
     * @param _locations list of URIs to load configurations
     */
    public ConfigManagerImpl(final URI... _locations) {
        this(Stream.of(_locations)
                    .collect(Collectors.toList())
                ,Charset.defaultCharset());
    }
    /**
     * Config manager constructor using the default charset
     * @param _locations list of locations to load configurations
     */
    public ConfigManagerImpl(final String... _locations) {
        this(Stream.of(_locations)
                    .map(URI::create)
                    .collect(Collectors.toList())
                ,Charset.defaultCharset());
    }

    /** 
     * Retrieve the effective charset (provided or default) to use loading configurations
     * @return effective charset
     */
    public Charset getCharset() {
        return charset;
    }
    /** 
     * Retrieve the list of locations from where to load configurations
     * @return locations to load configurations
     */
    public List<URI> getLocations() {
        return locations;
    }
    
    
    /**
     * Read configuration from the given _reader population as stream
     * @param _location configuration location to discern reader the format
     * @param _reader reader from where configuration must be readed
     * @return stream of read configurations
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws NullPointerException if any of parameters are null
     */
    protected Stream<Config> read(final URI _location,Reader _reader){
        
        Objects.requireNonNull(_location,"Mandatory parameter _location");
        Objects.requireNonNull(_reader,"Mandatory parameter _reader");
        try{
            return ConfigParserFactory.read(_reader, _location);
        } catch (UncheckedIOException ex) {
            throw new UnreadableConfigLocation(_location, ex);
        }
    }
    /**
     * Write configuration to the given _writer from the given _config stream
     * @param _location configuration location to discern reader the format
     * @param _writer writer where configuration must be write down
     * @param _config configuration to be writen
     * @throws UnwritableConfigLocation when for any reason the configuration can not be writen
     * @throws UnsupportedConfigLocationFormat if the format of the location is not supported
     * @throws UnsupportedOperationException if write operation is not supported by the location format and/or scheme
     * @throws NullPointerException if any of parameters are null
     */
    protected void write(final URI _location,Writer _writer, Stream<Config> _config){
        
        Objects.requireNonNull(_location,"Mandatory parameter _location");
        Objects.requireNonNull(_writer,"Mandatory parameter _writer");
        Objects.requireNonNull(_config,"Mandatory parameter _config");
        try{//(Writer writer=_writer){
            ConfigParserFactory.write(_writer, _location,_config);
        } catch (UncheckedIOException ex) {
            throw new UnwritableConfigLocation(_location, ex);
        }
    }

    
    /** @see ConfigManager#read(java.net.URI) */
    @Override
    public Stream<Config> read(final URI _location){
        return ConfigProviderFactory.openInputStream(_location, this.charset)
                                    .map(reader -> this.read(_location,reader))
                                    .orElse(Stream.empty());
    }
    /** @see ConfigManager#write(java.net.URI, java.util.stream.Stream) */
    @Override
    public void write(final URI _location, Stream<Config> _config){
        ConfigProviderFactory.openOutputStream(_location, this.charset)
                                .ifPresent(writer -> this.write(_location,writer,_config));
    }

    /** @see ConfigManager#stream() */
    @Override
    public Stream<Config> stream(){
        return this.locations.stream()
                                .sequential()
                                .flatMap(this::read);
    }
    /** @see ConfigManager#load()  */
    @Override
    public void load(){
        stream()
           .forEach(config -> System.setProperty(config.getKey(),config.getValue()));
    }
}
