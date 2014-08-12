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
package com.codenvy.ide.ext.java.client.newresource;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new Java file (e.g. class, enum, ...).
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewJavaResourcePresenter implements NewJavaResourceView.ActionDelegate {
    private static final String DEFAULT_CONTENT = " {\n}\n";
    private NewJavaResourceView view;
    private EditorAgent         editorAgent;
    private SelectionAgent      selectionAgent;
//    private ResourceProvider    resourceProvider;
    private Array<ResourceTypes> types = Collections.createArray();

    @Inject
    public NewJavaResourcePresenter(NewJavaResourceView view,
                                    EditorAgent editorAgent,
                                    SelectionAgent selectionAgent) {
        this.view = view;
        this.editorAgent = editorAgent;
        this.selectionAgent = selectionAgent;
        this.view.setDelegate(this);
        types.add(ResourceTypes.CLASS);
        types.add(ResourceTypes.INTERFACE);
        types.add(ResourceTypes.ENUM);
    }

    public void showDialog() {
        view.setTypes(types);
        view.showDialog();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onOkClicked() {
//        view.close();
//        final String resourceName = view.getName();
//        ensureParent(resourceName, new AsyncCallback<Folder>() {
//            @Override
//            public void onSuccess(Folder result) {
//                final String className = resourceName.substring(resourceName.lastIndexOf('.') + 1);
//                switch (view.getSelectedType()) {
//                    case CLASS:
//                        createClass(className, result);
//                        break;
//                    case INTERFACE:
//                        createInterface(className, result);
//                        break;
//                    case ENUM:
//                        createEnum(className, result);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                Log.error(NewJavaResourcePresenter.class, caught);
//            }
//        });
    }

//    private void createClass(String resourceName, Folder parent) {
//        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
//        StringBuilder content = new StringBuilder(getPackageDeclaration(parent, activeProject.getSourceFolders()));
//        content.append("public class ").append(resourceName).append(DEFAULT_CONTENT);
//        createFile(resourceName, parent, activeProject, content.toString());
//    }

//    private void createInterface(String resourceName, Folder parent) {
//        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
//        StringBuilder content = new StringBuilder(getPackageDeclaration(parent, activeProject.getSourceFolders()));
//        content.append("public interface ").append(resourceName).append(DEFAULT_CONTENT);
//        createFile(resourceName, parent, activeProject, content.toString());
//    }

//    private void createEnum(String resourceName, Folder parent) {
//        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
//        StringBuilder content = new StringBuilder(getPackageDeclaration(parent, activeProject.getSourceFolders()));
//        content.append("public enum ").append(resourceName).append(DEFAULT_CONTENT);
//        createFile(resourceName, parent, activeProject, content.toString());
//    }

//    private void createFile(@NotNull String name, @NotNull Folder parent, @NotNull Project project, @NotNull String content) {
//        ((JavaProject)project).createCompilationUnit(parent, name + ".java", content, new AsyncCallback<CompilationUnit>() {
//            @Override
//            public void onSuccess(CompilationUnit result) {
//                if (result.isFile()) {
////                    editorAgent.openEditor(result);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                new Info(caught.getMessage()).show();
//            }
//        });
//    }

    /** Returns package declaration string. */
//    private String getPackageDeclaration(@NotNull Folder parent, @NotNull Array<SourceFolder> sourceFolders) {
//        if (parent instanceof SourceFolder) {
//            return "\n";
//        }
//        for (SourceFolder sourceFolder : sourceFolders.asIterable()) {
//            if (parent.getPath().startsWith(sourceFolder.getPath())) {
//                String packageName = parent.getPath().replaceFirst(sourceFolder.getPath(), "");
//                packageName = packageName.startsWith("/") ? packageName.replaceFirst("/", "").replaceAll("/", ".")
//                                                          : packageName.replaceAll("/", ".");
//                return "package " + packageName + ";\n\n";
//            }
//        }
//        return "";
//    }

    /** Ensures that parent package for the given class is exist. */
//    private void ensureParent(String className, final AsyncCallback<Folder> callback) {
//        final Project activeProject = resourceProvider.getActiveProject();
//        Folder parent = null;
//        Selection<?> selection = selectionAgent.getSelection();
//        if (selection != null) {
//            if (selection.getFirstElement() instanceof Resource) {
//                Resource selectedResource = (Resource)selection.getFirstElement();
//                if (selectedResource.isFile()) {
//                    parent = selectedResource.getParent();
//                } else {
//                    parent = (Folder)selectedResource;
//                }
//            }
//        } else {
//            parent = activeProject;
//        }
//
//        final int lastDotPos = className.lastIndexOf('.');
//        final boolean withPackagePrefixed = lastDotPos > 0;
//        if (withPackagePrefixed) {
//            final String packageName = className.substring(0, lastDotPos);
//            final String packagePath = parent.getPath() + '/' + packageName.replace('.', '/');
//            final Folder finalParent = parent;
//            activeProject.findResourceByPath(packagePath, new AsyncCallback<Resource>() {
//                @Override
//                public void onSuccess(Resource result) {
//                    callback.onSuccess((Folder)result);
//                }
//
//                @Override
//                public void onFailure(Throwable caught) {
//                    ((JavaProject)activeProject).createPackage(finalParent, packageName, new AsyncCallback<Package>() {
//                        @Override
//                        public void onSuccess(Package result) {
//                            callback.onSuccess(result);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable caught) {
//                            callback.onFailure(caught);
//                        }
//                    });
//                }
//            });
//        } else {
//            callback.onSuccess(parent);
//        }
//    }
}
