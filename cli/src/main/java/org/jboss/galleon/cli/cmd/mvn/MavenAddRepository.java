/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.cli.cmd.mvn;

import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.aesh.command.CommandDefinition;
import org.aesh.command.option.Option;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.cli.AbstractCompleter;
import org.jboss.galleon.cli.CommandExecutionException;
import static org.jboss.galleon.cli.CliMavenArtifactRepositoryManager.DEFAULT_REPOSITORY_TYPE;
import org.jboss.galleon.cli.PmCommandInvocation;
import org.jboss.galleon.cli.PmCompleterInvocation;
import org.jboss.galleon.cli.PmSessionCommand;
import org.jboss.galleon.cli.cmd.CliErrors;
import org.jboss.galleon.cli.config.mvn.MavenConfig;
import org.jboss.galleon.cli.config.mvn.MavenRemoteRepository;

/**
 *
 * @author jdenise@redhat.com
 */
@CommandDefinition(name = "add-repository", description = "Add a maven repo")
public class MavenAddRepository extends PmSessionCommand {

    public static class UpdatePolicyCompleter extends AbstractCompleter {

        @Override
        protected List<String> getItems(PmCompleterInvocation completerInvocation) {
            return MavenConfig.getUpdatePolicies();
        }
    }

    @Option(description = "Maven remote repository URL", required = true)
    private String url;

    @Option(description = "Maven remote repository type, \"" + DEFAULT_REPOSITORY_TYPE + "\" by default",
            required = false, defaultValue = DEFAULT_REPOSITORY_TYPE)
    private String type;

    @Option(description = "Maven remote repository name", required = true)
    private String name;

    @Option(name = "release-update-policy", completer = UpdatePolicyCompleter.class,
            description = "Maven release update policy. NB: Interval is expressed in minutes", required = false)
    private String releaseUpdatePolicy;

    @Option(name = "snapshot-update-policy", completer = UpdatePolicyCompleter.class,
            description = "Maven snapshot update policy. NB: Interval is expressed in minutes", required = false)
    private String snapshotUpdatePolicy;

    @Option(hasValue = true, name = "enable-snapshot", description = "Enable snapshot")
    private Boolean enableSnapshot;

    @Option(hasValue = true, name = "enable-release", description = "Enable release")
    private Boolean enableRelease;

    @Override
    protected void runCommand(PmCommandInvocation session) throws CommandExecutionException {
        try {
            session.getPmSession().getPmConfiguration().getMavenConfig().
                    addRemoteRepository(new MavenRemoteRepository(name, type,
                            releaseUpdatePolicy, snapshotUpdatePolicy, enableRelease, enableSnapshot, url));
        } catch (ProvisioningException | XMLStreamException | IOException ex) {
            throw new CommandExecutionException(session.getPmSession(), CliErrors.addRepositoryFailed(), ex);
        }
    }

}
