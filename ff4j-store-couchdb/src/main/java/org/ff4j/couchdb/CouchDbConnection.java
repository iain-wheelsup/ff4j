package org.ff4j.couchdb;

/*
 * #%L
 * ff4j-store-couchbase
 * %%
 * Copyright (C) 2013 - 2017 FF4J
 * %%
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
 * #L%
 */

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import static org.ff4j.couchdb.CouchDbConstants.*;

/**
 * Wrapper to handle connectivity to CouchDb.
 * <p>
 * This uses the Ektorp library.
 * https://github.com/helun/Ektorp
 *
 * @author Curtis White (@drizztguen77)
 */
public class CouchDbConnection {

    /**
     * logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbConnection.class);

    /**
     * Database Name
     */
    private String dbName = DEFAULT_DBNAME;

    /**
     * UserName.
     */
    private String userName = null;

    /**
     * Password.
     */
    private String password = null;

    /**
     * Host
     **/
    private String host = null;

    /**
     * Host
     **/
    private boolean enableSsl = false;

    /**
     * Port
     **/
    private Integer port = DEFAULT_DATABASE_PORT;

    /**
     * Max Connections
     **/
    private Integer maxConnections = 100;

    /**
     * Connection Timeout
     **/
    private Integer connectionTimeout = 0;

    /**
     * URl to CouchDB
     **/
    private String url = null;

    /**
     * Create the database if it doesn't exist
     **/
    private boolean createDatabaseIfNotExists = false;

    /**
     * Type for feature store.
     */
    private String ff4jFeatureType = DEFAULT_FEATURE_TYPE;

    /**
     * Type for property store.
     */
    private String ff4jPropertyType = DEFAULT_PROPERTY_TYPE;

    /**
     * Type for event store.
     */
    private String ff4jEventType = DEFAULT_EVENT_TYPE;

    /**
     * Http Client
     */
    private HttpClient httpClient;

    /**
     * CouchDB Instance
     */
    private CouchDbInstance couchDbInstance;

    /**
     * CouchDB Connector.
     */
    private CouchDbConnector couchDbConnector;

    /**
     * Default Constructor
     */
    public CouchDbConnection() {
    }

    /**
     * Constructor
     */
    public CouchDbConnection(String url) {
        this.url = url;
    }

    /**
     * Constructor
     */
    public CouchDbConnection(String dbName, String host, Integer port, String userName, String password, boolean enableSsl, Integer maxConnections, Integer connectionTimeout, boolean createDatabaseIfNotExists) {
        this.dbName = dbName;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.enableSsl = enableSsl;
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
        this.createDatabaseIfNotExists = createDatabaseIfNotExists;
    }

    /**
     * Fluent method to init connectivity.
     */
    public CouchDbConnection featureType(String featureType) {
        this.ff4jFeatureType = featureType;
        return this;
    }

    /**
     * Fluent method to init connectivity.
     */
    public CouchDbConnection propertyType(String propertyType) {
        this.ff4jPropertyType = propertyType;
        return this;
    }

    /**
     * Fluent method to init connectivity.
     */
    public CouchDbConnection eventType(String eventType) {
        this.ff4jEventType = eventType;
        return this;
    }

    /**
     * Set a url
     *
     * @param url new value for a url
     */
    public CouchDbConnection url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set database name
     *
     * @param dbName new value for database name
     */
    public CouchDbConnection dbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    /**
     * Set the host name or IP address
     *
     * @param host new value for host
     */
    public CouchDbConnection host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Set the port
     *
     * @param port new value for the port
     */
    public CouchDbConnection port(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * Set the userName
     *
     * @param userName new value for userName
     */
    public CouchDbConnection userName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Set the password
     *
     * @param password new value for password
     */
    public CouchDbConnection password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Enable SSL. Default is false.
     *
     * @param enableSsl true to enable SSL, otherwise false
     */
    public CouchDbConnection enableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return this;
    }

    /**
     * Set max connections
     *
     * @param maxConnections new value for max connections
     */
    public CouchDbConnection maxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    /**
     * Set the connection timeout
     *
     * @param connectionTimeout new value for connection timeout
     */
    public CouchDbConnection connectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public CouchDbConnection createDatabaseIfNotExists(boolean createDatabaseIfNotExists) {
        this.createDatabaseIfNotExists = createDatabaseIfNotExists;
        return this;
    }

    /**
     * Get the CouchDB connector
     *
     * @return CouchDB connector
     */
    public CouchDbConnector getCouchDbConnector() {
        initClient();
        return couchDbConnector;
    }

    /**
     * Disconnect from database
     */
    public void disconnect() {
        httpClient.shutdown();
    }

    /**
     * Initialization of the client.
     */
    public void initClient() {

        if (null != this.url) {
            LOGGER.info("Initializing connectivity from a URL");

            try {
                this.httpClient = new StdHttpClient.Builder()
                        .url(this.url)
                        .username(this.userName)
                        .password(this.password)
                        .build();

                this.couchDbInstance = new StdCouchDbInstance(httpClient);
                this.couchDbConnector = new StdCouchDbConnector(this.dbName, this.couchDbInstance);

            } catch (MalformedURLException e) {
                LOGGER.info("Failed to create HttpClient");
            }

        } else {
            LOGGER.info("Initializing connectivity from inputs");
            this.httpClient = new StdHttpClient.Builder()
                    .username(this.userName)
                    .password(this.password)
                    .host(this.host)
                    .port(this.port)
                    .enableSSL(this.enableSsl)
                    .connectionTimeout(this.connectionTimeout)
                    .maxConnections(this.maxConnections)
                    .build();

            this.couchDbInstance = new StdCouchDbInstance(httpClient);
            this.couchDbConnector = new StdCouchDbConnector(this.dbName, this.couchDbInstance);
//            if (createDatabaseIfNotExists) this.couchDbConnector.createDatabaseIfNotExists();
        }

        LOGGER.info("Connection Established " + this.couchDbConnector);
    }
}
