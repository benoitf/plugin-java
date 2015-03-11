/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.gradle.client.projecttree;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.gradle.client.event.BeforeGradleModuleOpenEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

/**
 * Node that represents module of multi-module project.
 *
 * @author Vladyslav Zhukovskii
 * @see GradleProjectNode
 */
public class GradleModuleNode extends GradleProjectNode {

    private AppContext appContext;

    @Inject
    public GradleModuleNode(@Assisted TreeNode<?> parent,
                            @Assisted ProjectDescriptor data,
                            @Assisted GradleProjectTreeStructure treeStructure,
                            EventBus eventBus,
                            ProjectServiceClient projectServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            IconRegistry iconRegistry,
                            AppContext appContext) {
        super(parent, data, treeStructure, eventBus, projectServiceClient, dtoUnmarshallerFactory);
        this.appContext = appContext;
        setDisplayIcon(iconRegistry.getIcon("gradle.module").getSVGImage());
    }

    /** {@inheritDoc} */
    @Override
    protected void getChildren(String path, AsyncCallback<Array<ItemReference>> callback) {
        if (!isOpened()) {
            eventBus.fireEvent(new BeforeGradleModuleOpenEvent(this));
        }

        super.getChildren(path, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final TreeNode.DeleteCallback callback) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("No opened project.");
        }

        final String rootProjectPath = currentProject.getRootProject().getPath();
        final String moduleRelativePath = getPath().substring(rootProjectPath.length() + 1);
        projectServiceClient.deleteModule(rootProjectPath, moduleRelativePath, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                GradleModuleNode.super.delete(callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
