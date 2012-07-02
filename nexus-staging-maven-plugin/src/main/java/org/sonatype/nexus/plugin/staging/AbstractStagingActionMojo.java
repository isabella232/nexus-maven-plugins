/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.staging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.nexus.client.staging.StagingWorkflowV2Service;
import org.sonatype.nexus.plugin.AbstractStagingMojo;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Super class of non-RC Actions. These mojos are "plain" non-aggregator ones, and will use the property file from
 * staged repository to get the repository ID they need. This way, you can integrate these mojos in your build directly
 * (ie. to release or promote even).
 * 
 * @author cstamas
 */
public abstract class AbstractStagingActionMojo
    extends AbstractStagingMojo
{
    @Override
    public final void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( shouldExecute() )
        {
            getLog().info( "Connecting to Nexus..." );
            createTransport( getNexusUrl() );
            createNexusClient();

            final StagingWorkflowV2Service stagingWorkflow = getStagingWorkflowService();

            try
            {
                doExecute( stagingWorkflow );
            }
            catch ( UniformInterfaceException e )
            {
                // dump the response until no smarter error handling
                getLog().error( e.getResponse().getEntity( String.class ) );
                // fail the build
                throw new MojoExecutionException( "Could not perform action: "
                    + e.getResponse().getClientResponseStatus().getReasonPhrase(), e );
            }
        }
    }

    protected boolean shouldExecute()
    {
        return true;
    }

    protected abstract void doExecute( final StagingWorkflowV2Service stagingWorkflow )
        throws MojoExecutionException, MojoFailureException;
}