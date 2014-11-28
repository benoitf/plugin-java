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

import com.codenvy.commons.xml.Element;
import com.codenvy.commons.xml.FromElementFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Section for management of default dependency information for use in a group of POMs.
 *
 * @author Eugene Voevodin
 */
public class DependencyManagement {

    private List<Dependency> dependencies;

    private Element element;

    public DependencyManagement() {
    }

    DependencyManagement(Element element) {
        this.element = element;
        //TODO store function in static field
        dependencies = element.getChildren(new FromElementFunction<Dependency>() {
            @Override
            public Dependency apply(Element element) {
                return new Dependency(element);
            }
        });
    }

    public void addDependency(Dependency dependency) {
        getDependencies().add(dependency);
    }

    public java.util.List<Dependency> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    public void removeDependency(Dependency dependency) {
        getDependencies().remove(dependency);
    }

    /**
     * Set the dependencies specified here are not used until they
     * are referenced in a
     * POM within the group. This allows the
     * specification of a "standard" version for a
     * particular dependency.
     */
    public void setDependencies(java.util.List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}