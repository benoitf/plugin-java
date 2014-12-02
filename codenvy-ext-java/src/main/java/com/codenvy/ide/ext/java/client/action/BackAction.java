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

package com.codenvy.ide.ext.java.client.action;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaLocalizationConstant;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.client.documentation.QuickDocumentation;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class BackAction extends Action {

    private QuickDocumentation quickDocumentation;

    @Inject
    public BackAction(JavaResources resources, JavaLocalizationConstant constant, QuickDocumentation quickDocumentation) {
        super(constant.actionQuickdocBack(), constant.actionQuickdocBack(), null, resources.leftArrow());

        this.quickDocumentation = quickDocumentation;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        quickDocumentation.back();
    }
}
