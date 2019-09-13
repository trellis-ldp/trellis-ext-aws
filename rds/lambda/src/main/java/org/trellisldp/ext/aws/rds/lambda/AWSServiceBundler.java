/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trellisldp.ext.aws.rds.lambda;

import static java.util.Collections.singletonList;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

import java.util.List;

import org.eclipse.microprofile.config.Config;
import org.jdbi.v3.core.Jdbi;
import org.trellisldp.agent.DefaultAgentService;
import org.trellisldp.api.AgentService;
import org.trellisldp.api.AuditService;
import org.trellisldp.api.ConstraintService;
import org.trellisldp.api.IOService;
import org.trellisldp.api.MementoService;
import org.trellisldp.api.NamespaceService;
import org.trellisldp.api.ResourceService;
import org.trellisldp.audit.DefaultAuditService;
import org.trellisldp.constraint.LdpConstraintService;
import org.trellisldp.event.DefaultActivityStreamService;
import org.trellisldp.ext.aws.AbstractAWSServiceBundler;
import org.trellisldp.ext.aws.S3MementoService;
import org.trellisldp.ext.db.DBNamespaceService;
import org.trellisldp.ext.db.DBResourceService;
import org.trellisldp.ext.db.DBWrappedMementoService;
import org.trellisldp.http.core.DefaultTimemapGenerator;
import org.trellisldp.http.core.TimemapGenerator;
import org.trellisldp.io.JenaIOService;
import org.trellisldp.rdfa.DefaultRdfaWriterService;

/**
 * An AWS-Based service bundler.
 */
public class AWSServiceBundler extends AbstractAWSServiceBundler {

    private AgentService agentService;
    private AuditService auditService;
    private IOService ioService;
    private MementoService mementoService;
    private ResourceService resourceService;
    private List<ConstraintService> constraintServices;
    private TimemapGenerator timemapGenerator;

    /**
     * Get an AWS-based service bundler using Newton.
     */
    public AWSServiceBundler() {
        super(new DefaultActivityStreamService());
        final Config config = getConfig();
        final Jdbi jdbi = Jdbi.create(config.getValue("trellis.jdbc.url", String.class),
                config.getValue("trellis.jdbc.username", String.class),
                config.getValue("trellis.jdbc.password", String.class));
        final NamespaceService nsService = new DBNamespaceService(jdbi);
        agentService = new DefaultAgentService();
        ioService = new JenaIOService(nsService, new DefaultRdfaWriterService(nsService));
        mementoService = new DBWrappedMementoService(jdbi, new S3MementoService());
        auditService = new DefaultAuditService();
        resourceService = new DBResourceService(jdbi);
        constraintServices = singletonList(new LdpConstraintService());
        timemapGenerator = new DefaultTimemapGenerator();
    }

    @Override
    public ResourceService getResourceService() {
        return resourceService;
    }

    @Override
    public MementoService getMementoService() {
        return mementoService;
    }

    @Override
    public AgentService getAgentService() {
        return agentService;
    }

    @Override
    public AuditService getAuditService() {
        return auditService;
    }

    @Override
    public IOService getIOService() {
        return ioService;
    }

    @Override
    public TimemapGenerator getTimemapGenerator() {
        return timemapGenerator;
    }

    @Override
    public Iterable<ConstraintService> getConstraintServices() {
        return constraintServices;
    }
}
