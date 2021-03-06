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
package org.trellisldp.ext.aws.neptune.lambda;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.glassfish.jersey.server.ResourceConfig;
import org.trellisldp.http.TrellisHttpResource;
import org.trellisldp.http.core.ServiceBundler;

public class TrellisLambdaHandler implements RequestStreamHandler {

    private static final ServiceBundler serviceBundler = new TrellisServiceBundler();

    private static final ResourceConfig jerseyApplication
        = new ResourceConfig().register(new TrellisHttpResource(serviceBundler));

    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler
            = JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);

    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context)
            throws IOException {
        handler.proxyStream(input, output, context);
        output.close();
    }
}
