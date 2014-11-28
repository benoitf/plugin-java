/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.maven.tools;

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.commons.xml.Element;
import com.codenvy.commons.xml.FromElementFunction;
import com.codenvy.commons.xml.XMLTree;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.commons.xml.NewElement.createElement;
import static java.util.Objects.requireNonNull;

/**
 * TODO add ability to create model not only from files
 * TODO implement setters
 * The <code>&lt;project&gt;</code> element is the root of
 * the descriptor.
 * The following table lists all of the possible child elements.
 */
public class Model {

    public static Model readModel(File file) throws IOException {
        return fetchModel(XMLTree.from(file));
    }

    public static Model readModel(VirtualFile file) throws ServerException, ForbiddenException, IOException {
        return fetchModel(XMLTree.from(file.getContent().getStream()));
    }

    private static final ToModuleFunction     TO_MODULE_FUNCTION     = new ToModuleFunction();
    private static final ToDependencyFunction TO_DEPENDENCY_FUNCTION = new ToDependencyFunction();

    private String               modelVersion;
    private String               groupId;
    private String               artifactId;
    private String               version;
    private String               packaging;
    private String               name;
    private String               description;
    private Parent               parent;
    private Build                build;
    private DependencyManagement dependencyManagement;
    private Map<String, String>  properties;
    private List<String>         modules;
    private List<Dependency>     dependencies;

    private final XMLTree tree;

    public Model(XMLTree tree) {
        this.tree = tree;
    }

    /**
     * Get the identifier for this artifact that is unique within
     * the group given by the
     * group ID. An artifact is something that is
     * either produced or used by a project.
     * Examples of artifacts produced by Maven for a
     * project include: JARs, source and binary
     * distributions, and WARs.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Get a detailed description of the project, used by Maven
     * whenever it needs to
     * describe the project, such as on the web site.
     * While this element can be specified as
     * CDATA to enable the use of HTML tags within the
     * description, it is discouraged to allow
     * plain text representation. If you need to modify
     * the index page of the generated web
     * site, you are able to specify your own instead
     * of adjusting this text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get a universally unique identifier for a project. It is
     * normal to
     * use a fully-qualified package name to
     * distinguish it from other
     * projects with a similar name (eg.
     * <code>org.apache.maven</code>).
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Get declares to which version of project descriptor this POM conforms.
     */
    public String getModelVersion() {
        return modelVersion;
    }

    /**
     * Get the full name of the project.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of artifact this project produces, for example
     * <code>jar</code>
     * <code>war</code>
     * <code>ear</code>
     * <code>pom</code>.
     * Plugins can create their own packaging, and
     * therefore their own packaging types,
     * so this list does not contain all possible
     * types.
     */
    public String getPackaging() {
        return packaging;
    }

    /**
     * Get the location of the parent project, if one exists.
     * Values from the parent
     * project will be the default for this project if
     * they are left unspecified. The location
     * is given as a group ID, artifact ID and version.
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * Get the current version of the artifact produced by this project.
     */
    public String getVersion() {
        return version;
    }

    public Build getBuild() {
        return build;
    }

    /**
     * Returns project dependencies
     */
    public List<Dependency> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    /**
     * Get default dependency information for projects that inherit
     * from this one. The
     * dependencies in this section are not immediately
     * resolved. Instead, when a POM derived
     * from this one declares a dependency described by
     * a matching groupId and artifactId, the
     * version and other values from this section are
     * used for that dependency if they were not
     * already specified.
     */
    public DependencyManagement getDependencyManagement() {
        return dependencyManagement;
    }

    /**
     * Returns project modules
     */
    public List<String> getModules() {
        if (modules == null) {
            modules = new ArrayList<>();
        }

        return modules;
    }

    /**
     * Returns project properties
     */
    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        return properties;
    }

    /**
     * Adds new dependency to the project
     */
    public Model addDependency(Dependency newDependency) {
        requireNonNull(newDependency);
        getDependencies().add(newDependency);
        //add dependency to xml tree
        final Element root = tree.getRoot();
        if (root.hasChild("dependencies")) {
            root.getSingleChild("dependencies").appendChild(newDependency.toNewElement());
        } else {
            root.appendChild(createElement("dependencies", newDependency.toNewElement()));
        }
        newDependency.setElement(root.getSingleChild("dependencies").getLastChild());
        return this;
    }

    /**
     * Adds new module to the project
     */
    public Model addModule(String newModule) {
        requireNonNull(newModule);
        getModules().add(newModule);
        //add module to xml tree
        final Element root = tree.getRoot();
        if (root.hasChild("modules")) {
            root.getSingleChild("modules").appendChild(createElement("module", newModule));
        } else {
            root.appendChild(createElement("modules", createElement("module", newModule)));
        }
        return this;
    }

    /**
     * Adds new property to the project
     */
    public Model addProperty(String key, String value) {
        final Element root = tree.getRoot();
        if (getProperties().containsKey(key)) {
            root.getSingleChild("properties")
                .getSingleChild(key)
                .setText(value);
        } else if (properties.isEmpty()) {
            root.appendChild(createElement("properties", createElement(key, value)));
        } else {
            root.getSingleChild("properties")
                .appendChild(createElement(key, value));
        }
        getProperties().put(key, value);
        return this;
    }


    /**
     * Removes dependency from model.
     * If last dependency has been removed removes dependencies element as well.
     */
    public Model removeDependency(Dependency dependency) {
        getDependencies().remove(dependency);
        //remove dependency from xml tree
        if (dependencies.isEmpty()) {
            tree.getRoot().removeChild("dependencies");
        } else {
            dependency.remove();
        }
        return this;
    }

    /**
     * Removes module from the model.
     * If last module has been removed removes modules element as well.
     */
    public Model removeModule(String module) {
        getModules().remove(module);
        //remove module from tree
        if (modules.isEmpty()) {
            tree.getRoot().removeChild("modules");
        } else {
            tree.getRoot()
                .getSingleChild("modules")
                .removeChild(module);
        }
        return this;
    }

    /**
     * Sets build settings for project.
     */
    public Model setBuild(Build build) {
        this.build = build;
        //set build to tree
        final Element buildEl;
        if (tree.getRoot().hasSingleChild("build")) {
            buildEl = tree.getRoot()
                          .getSingleChild("build")
                          .replaceWith(build.toNewElement());
        } else {
            buildEl = tree.getRoot()
                          .appendChild(build.toNewElement());
        }
        build.setElement(buildEl);
        return this;
    }

    /**
     * Sets dependencies associated with a project.
     * These dependencies are used to construct a
     * classpath for your
     * project during the build process. They are
     * automatically downloaded from the
     * repositories defined in this project.
     * See <a href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">the
     * dependency mechanism</a> for more information.
     */
    public Model setDependencies(Collection<Dependency> dependencies) {
        this.dependencies = new ArrayList<>(dependencies);
        //TODO
        return this;
    }

    //TODO

    /**
     * Set default dependency information for projects that inherit
     * from this one. The
     * dependencies in this section are not immediately
     * resolved. Instead, when a POM derived
     * from this one declares a dependency described by
     * a matching groupId and artifactId, the
     * version and other values from this section are
     * used for that dependency if they were not
     * already specified.
     */
    public void setDependencyManagement(DependencyManagement dependencyManagement) {
        this.dependencyManagement = dependencyManagement;
    }

    /**
     * Set the modules (sometimes called sub projects) to build as a
     * part of this
     * project. Each module listed is a relative path
     * to the directory containing the module.
     */
    public void setModules(java.util.List<String> modules) {
        this.modules = modules;
    }

    /**
     * Set properties that can be used throughout the POM as a
     * substitution, and
     * are used as filters in resources if enabled.
     * The format is
     * <code>&lt;name&gt;value&lt;/name&gt;</code>.
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Set the identifier for this artifact that is unique within
     * the group given by the
     * group ID. An artifact is something that is
     * either produced or used by a project.
     * Examples of artifacts produced by Maven for a
     * project include: JARs, source and binary
     * distributions, and WARs.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        tree.getRoot().setChildText("artifactId", artifactId, true);
    }

    /**
     * Set a detailed description of the project, used by Maven
     * whenever it needs to
     * describe the project, such as on the web site.
     * While this element can be specified as
     * CDATA to enable the use of HTML tags within the
     * description, it is discouraged to allow
     * plain text representation. If you need to modify
     * the index page of the generated web
     * site, you are able to specify your own instead
     * of adjusting this text.
     */
    public void setDescription(String description) {
        this.description = description;
        tree.getRoot().setChildText("description", description, true);
    }

    /**
     * Set a universally unique identifier for a project. It is
     * normal to
     * use a fully-qualified package name to
     * distinguish it from other
     * projects with a similar name (eg.
     * <code>org.apache.maven</code>).
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
        tree.getRoot().setChildText("groupId", groupId, true);
    }

    /**
     * Set the current version of the artifact produced by this project.
     */
    public void setVersion(String version) {
        this.version = version;
        tree.getRoot().setChildText("version", version, true);
    }

    /**
     * Set declares to which version of project descriptor this POM conforms.
     */
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
        tree.getRoot().setChildText("modelVersion", modelVersion, true);
    }

    /**
     * Set the full name of the project.
     */
    public void setName(String name) {
        this.name = name;
        tree.getRoot().setChildText("name", name, true);
    }

    /**
     * Set the type of artifact this project produces, for example
     * <code>jar</code>
     * <code>war</code>
     * <code>ear</code>
     * <code>pom</code>.
     * Plugins can create their own packaging, and
     * therefore their own packaging types,
     * so this list does not contain all possible
     * types.
     */
    public void setPackaging(String packaging) {
        this.packaging = packaging;
        tree.getRoot().setChildText("packaging", packaging, true);
    }

    /**
     * Set the location of the parent project, if one exists.
     * Values from the parent
     * project will be the default for this project if
     * they are left unspecified. The location
     * is given as a group ID, artifact ID and version.
     */
    public void setParent(Parent parent) {
        this.parent = parent;

    }

    /**
     * @return the model id as <code>groupId:artifactId:packaging:version</code>
     */
    public String getId() {
        return (version == null ? "[inherited]" : groupId) +
               ':' +
               artifactId +
               ':' +
               packaging +
               ':' +
               (version == null ? "[inherited]" : version);
    }

    public void save(File file) throws IOException {
        tree.writeTo(file);
    }

    @Override
    public String toString() {
        return getId();
    }

    public void writeTo(File file) throws IOException {
        tree.writeTo(file);
    }

    public void writeTo(VirtualFile file) throws ServerException, ForbiddenException {
        file.updateContent(new ByteArrayInputStream(tree.getBytes()), null);
    }

    private static Model fetchModel(XMLTree tree) {
        final Model model = new Model(tree);
        final Element root = tree.getRoot();
        model.modelVersion = root.getChildText("modelVersion");
        model.artifactId = root.getChildText("artifactId");
        model.groupId = root.getChildText("groupId");
        model.version = root.getChildText("version");
        model.name = root.getChildText("name");
        model.description = root.getChildText("description");
        model.packaging = root.getChildText("packaging");
        if (root.hasSingleChild("parent")) {
            model.parent = new Parent(root.getSingleChild("parent"));
        }
        if (root.hasSingleChild("dependencyManagement")) {
            model.dependencyManagement = new DependencyManagement(root.getSingleChild("dependencyManagement"));
        }
        if (root.hasSingleChild("build")) {
            model.build = new Build(root.getSingleChild("build"));
        }
        if (root.hasSingleChild("dependencies")) {
            model.dependencies = tree.getElements("/project/dependencies/dependency", TO_DEPENDENCY_FUNCTION);
        }
        if (root.hasSingleChild("modules")) {
            model.modules = tree.getElements("/project/modules/module", TO_MODULE_FUNCTION);
        }
        if (root.hasSingleChild("properties")) {
            model.properties = fetchProperties(root.getSingleChild("properties"));
        }
        return model;
    }

    private static Map<String, String> fetchProperties(Element propertiesElement) {
        final Map<String, String> properties = new HashMap<>();
        for (Element property : propertiesElement.getChildren()) {
            properties.put(property.getName(), property.getText());
        }
        return properties;
    }

    private static class ToDependencyFunction implements FromElementFunction<Dependency> {

        @Override
        public Dependency apply(Element element) {
            return new Dependency(element);
        }
    }

    private static class ToModuleFunction implements FromElementFunction<String> {

        @Override
        public String apply(Element element) {
            return element.getText();
        }
    }
}