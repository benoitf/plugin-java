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
package com.codenvy.ide.extension.ant.client.projecttree;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.ext.java.client.projecttree.JavaProjectNode;
import com.codenvy.ide.ext.java.client.projecttree.JavaSourceFolderUtil;
import com.codenvy.ide.ext.java.client.projecttree.JavaTreeStructure;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;

/**
 * Node that represents Ant project.
 *
 * @author Vladyslav Zhukovskii
 */
public class AntProjectNode extends JavaProjectNode {

    /** Create instance of {@link AntProjectNode}. */
    protected AntProjectNode(TreeNode<?> parent, ProjectDescriptor data,
                          JavaTreeStructure treeStructure,
                          TreeSettings settings, EventBus eventBus,
                          ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, treeStructure, settings, eventBus, projectServiceClient, dtoUnmarshallerFactory);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    protected AbstractTreeNode<?> createChildNode(ItemReference item) {
        if (JavaSourceFolderUtil.isSourceFolder(item, getProject())) {
            return ((AntProjectTreeStructure)treeStructure).newSourceFolderNode(AntProjectNode.this, item);
        } else if ("folder".equals(item.getType())) {
            return ((AntProjectTreeStructure)treeStructure).newJavaFolderNode(AntProjectNode.this, item);
        } else {
            return super.createChildNode(item);
        }
    }

//    /** {@inheritDoc} */
//    @Override
//    public void refreshChildren(final AsyncCallback<TreeNode<?>> callback) {
//        getChildren(data.getPath(), new AsyncCallback<Array<ItemReference>>() {
//            @Override
//            public void onSuccess(Array<ItemReference> children) {
//                final boolean isShowHiddenItems = settings.isShowHiddenItems();
//                Array<TreeNode<?>> newChildren = Collections.createArray();
//                setChildren(newChildren);
//                for (ItemReference item : children.asIterable()) {
//                    if (isShowHiddenItems || !item.getName().startsWith(".")) {
//                        AbstractTreeNode node = createChildNode(item);
//                        if (node != null) {
//                            newChildren.add(node);
//                        }
//                    }
//                }
//                callback.onSuccess(AntProjectNode.this);
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                callback.onFailure(caught);
//            }
//        });
//    }
}
