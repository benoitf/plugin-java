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
package org.eclipse.che.gradle.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle event when Gradle project configuration received.
 *
 * @author Vladyslav Zhukovskii
 */
public interface ProjectConfigurationReceivedHandler extends EventHandler {
    /** Perform action when Gradle project configuration received. */
    void onConfigurationReceived(ProjectConfigurationReceivedEvent event);
}
