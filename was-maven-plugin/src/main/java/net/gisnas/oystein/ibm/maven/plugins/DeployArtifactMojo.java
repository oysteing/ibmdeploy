package net.gisnas.oystein.ibm.maven.plugins;

import java.io.File;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Deploy JEE application to an WebSphere Application Server deployment manager
 * 
 * Deploy consists of upload of artifact to deployment manager, install and start.
 * If an application with the same name already exists, redeploy will be performed.
 * 
 * @goal deploy-artifact
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class DeployArtifactMojo extends DeployMojo {

	/**
	 * GroupId of artifact to deploy. Must be among project dependencies.
	 * 
	 * @parameter expression="${was.groupId}"
	 */
	protected String groupId;

	/**
	 * ArtifactId of artifact to deploy. Must be among project dependencies.
	 * 
	 * @parameter expression="${was.artifactId}"
	 */
	protected String artifactId;

	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject project;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		initConnection();
		if (groupId != null && artifactId != null) {
			getLog().info("Looking for dependency " + groupId + ":" + artifactId);
			earFile = getDependency(groupId, artifactId);
		}
		getLog().info("Deploying application " + earFile);
		appManager.deploy(earFile, applicationName, cluster);
	}

	private File getDependency(String groupId, String artifactId) throws MojoFailureException {
		@SuppressWarnings("unchecked")
		Set<Artifact> dependencies = project.getArtifacts();
		Artifact artifact = null;
        for (final Artifact a : dependencies) {
            if (a.getArtifactId().equals(artifactId) &&
                    a.getGroupId().equals(groupId)) {
                artifact = a;
                break;
            }
        }
        if (artifact == null) {
            throw new MojoFailureException(String.format("Could not resolve artifact to deploy %s:%s", groupId, artifactId));
        }
        return artifact.getFile();
	}
	
}
