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
package com.codenvy.ide.extension.maven.client.projecttree;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.ext.java.client.navigation.JavaNavigationService;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class MavenProjectTreeStructureTest {
    @Mock
    private MavenNodeFactory          nodeFactory;
    @Mock
    private EventBus                  eventBus;
    @Mock
    private AppContext                appContext;
    @Mock
    private ProjectServiceClient      projectServiceClient;
    @Mock
    private IconRegistry              iconRegistry;
    @Mock
    private JavaNavigationService     javaNavigationService;
    @Mock
    private DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    @InjectMocks
    private MavenProjectTreeStructure treeStructure;

    @Test
    public void testGetNodeFactory() throws Exception {
        assertEquals(nodeFactory, treeStructure.getNodeFactory());
    }

    @Test
    public void testNewJavaFolderNode() throws Exception {
        AbstractTreeNode parent = mock(AbstractTreeNode.class);
        ItemReference data = mock(ItemReference.class);
        when(data.getType()).thenReturn("folder");

        treeStructure.newJavaFolderNode(parent, data);

        verify(nodeFactory).newMavenFolderNode(eq(parent), eq(data), eq(treeStructure));
    }

    @Test
    public void testNewModuleNode() throws Exception {
        AbstractTreeNode parent = mock(AbstractTreeNode.class);
        ProjectDescriptor data = mock(ProjectDescriptor.class);

        treeStructure.newModuleNode(parent, data);

        verify(nodeFactory).newModuleNode(eq(parent), eq(data), eq(treeStructure));
    }
}
