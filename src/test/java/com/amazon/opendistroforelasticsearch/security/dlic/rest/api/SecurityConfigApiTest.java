/*
 * Copyright 2016-2017 by floragunn GmbH - All rights reserved
 * 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed here is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * This software is free of charge for non-commercial and academic use. 
 * For commercial use in a production environment you have to obtain a license 
 * from https://floragunn.com
 * 
 */

package com.amazon.opendistroforelasticsearch.security.dlic.rest.api;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.elasticsearch.common.settings.Settings;
import org.junit.Assert;
import org.junit.Test;

import com.amazon.opendistroforelasticsearch.security.support.ConfigConstants;
import com.amazon.opendistroforelasticsearch.security.test.helper.file.FileHelper;
import com.amazon.opendistroforelasticsearch.security.test.helper.rest.RestHelper.HttpResponse;

public class SecurityConfigApiTest extends AbstractRestApiUnitTest {

	@Test
	public void testSecurityConfigApiRead() throws Exception {

		setup();

		rh.keystore = "restapi/kirk-keystore.jks";
		rh.sendHTTPClientCertificate = true;

		HttpResponse response = rh.executeGetRequest("/_opendistro/_security/api/config", new Header[0]);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

		response = rh.executePutRequest("/_opendistro/_security/api/config", "{\"xxx\": 1}", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());

        response = rh.executePostRequest("/_opendistro/_security/api/config", "{\"xxx\": 1}", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());
        
        response = rh.executePatchRequest("/_opendistro/_security/api/config", "{\"xxx\": 1}", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());
        
        response = rh.executeDeleteRequest("/_opendistro/_security/api/config", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());
        
	}
	
	@Test
	public void testSecurityConfigApiWrite() throws Exception {

	    Settings settings = Settings.builder().put(ConfigConstants.OPENDISTRO_SECURITY_UNSUPPORTED_RESTAPI_ALLOW_CONFIG_MODIFICATION, true).build();
        setup(settings);

        rh.keystore = "restapi/kirk-keystore.jks";
        rh.sendHTTPClientCertificate = true;

        HttpResponse response = rh.executeGetRequest("/_opendistro/_security/api/config", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        response = rh.executePutRequest("/_opendistro/_security/api/config/xxx", FileHelper.loadFile("restapi/securityconfig.json"), new Header[0]);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
        
        response = rh.executePutRequest("/_opendistro/_security/api/config/config", FileHelper.loadFile("restapi/securityconfig.json"), new Header[0]);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        
        response = rh.executePutRequest("/_opendistro/_security/api/config/config", FileHelper.loadFile("restapi/invalid_config.json"), new Header[0]);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertTrue(response.getContentType(), response.isJsonContentType());
        Assert.assertTrue(response.getBody().contains("Unrecognized field"));

        response = rh.executeGetRequest("/_opendistro/_security/api/config", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        response = rh.executePostRequest("/_opendistro/_security/api/config", "{\"xxx\": 1}", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());
        
        response = rh.executePatchRequest("/_opendistro/_security/api/config", "[{\"op\": \"replace\",\"path\": \"/config/dynamic/hosts_resolver_mode\",\"value\": \"other\"}]", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        
        response = rh.executeDeleteRequest("/_opendistro/_security/api/config", new Header[0]);
        Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusCode());
        
    }
}
