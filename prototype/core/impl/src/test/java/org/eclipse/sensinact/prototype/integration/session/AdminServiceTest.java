/*********************************************************************
* Copyright (c) 2022 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   Kentyou - initial implementation
**********************************************************************/
package org.eclipse.sensinact.prototype.integration.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.eclipse.sensinact.prototype.ResourceDescription;
import org.eclipse.sensinact.prototype.SensiNactSession;
import org.eclipse.sensinact.prototype.SensiNactSessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the behavior around the admin service
 */
@ExtendWith(ServiceExtension.class)
public class AdminServiceTest {

    private static final String USER = "user";

    private static final String PROVIDER = "AdminServiceTestProvider";
    private static final String SERVICE = "service";
    private static final String RESOURCE = "resource";

    @InjectService
    SensiNactSessionManager sessionManager;
    SensiNactSession session;

    @BeforeEach
    void start() {
        session = sessionManager.getDefaultSession(USER);
    }

    @AfterEach
    void stop() {
        session = null;
    }

    /**
     * Tests admin resource creation with provider and update
     */
    @Test
    void testAdminCreateUpdate() {
        final Instant timestamp = Instant.now();

        // Create resource & provider
        session.setResourceValue(PROVIDER, SERVICE, RESOURCE, 42, timestamp);

        // Admin resources must have the same timestamp
        ResourceDescription descr = session.describeResource(PROVIDER, "admin", "friendlyName");
        assertEquals(PROVIDER, descr.value);
        assertEquals(timestamp, descr.timestamp);

        descr = session.describeResource(PROVIDER, "admin", "location");
        assertNull(descr.value);
        assertEquals(timestamp, descr.timestamp);

        // Ensure we reject setting a value with an earlier timestamp
        session.setResourceValue(PROVIDER, "admin", "friendlyName", "foo", timestamp.minusSeconds(1));
        descr = session.describeResource(PROVIDER, "admin", "friendlyName");
        assertEquals(PROVIDER, descr.value);
        assertEquals(timestamp, descr.timestamp);

        session.setResourceValue(PROVIDER, "admin", "location", "bar", timestamp.minusSeconds(1));
        descr = session.describeResource(PROVIDER, "admin", "location");
        assertNull(descr.value);
        assertEquals(timestamp, descr.timestamp);

        // Set the value with the same timestamp
        session.setResourceValue(PROVIDER, "admin", "friendlyName", "foo", timestamp);
        descr = session.describeResource(PROVIDER, "admin", "friendlyName");
        assertEquals("foo", descr.value);
        assertEquals(timestamp, descr.timestamp);

        session.setResourceValue(PROVIDER, "admin", "location", "bar", timestamp);
        descr = session.describeResource(PROVIDER, "admin", "location");
        assertEquals("bar", descr.value);
        assertEquals(timestamp, descr.timestamp);

        // Set the value with a future timestamp
        final Instant future = timestamp.plusSeconds(1);
        session.setResourceValue(PROVIDER, "admin", "friendlyName", "eclipse", future);
        descr = session.describeResource(PROVIDER, "admin", "friendlyName");
        assertEquals("eclipse", descr.value);
        assertEquals(future, descr.timestamp);

        session.setResourceValue(PROVIDER, "admin", "location", "sensiNact", future);
        descr = session.describeResource(PROVIDER, "admin", "location");
        assertEquals("sensiNact", descr.value);
        assertEquals(future, descr.timestamp);
    }
}
