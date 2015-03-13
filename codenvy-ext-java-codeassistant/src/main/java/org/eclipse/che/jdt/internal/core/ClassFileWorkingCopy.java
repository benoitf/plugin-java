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
package org.eclipse.che.jdt.internal.core;

import org.eclipse.che.jdt.core.ToolFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.util.ClassFileBytesDisassembler;
import org.eclipse.jdt.core.util.IClassFileReader;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.core.util.Disassembler;
import org.eclipse.jdt.internal.core.util.Util;

import java.io.File;

/**
 * A working copy on an <code>IClassFile</code>.
 */
public class ClassFileWorkingCopy extends CompilationUnit {

    public ClassFile classFile;

    public ClassFileWorkingCopy(ClassFile classFile, WorkingCopyOwner owner) {
        super((PackageFragment)classFile.getParent(), classFile.manager, ((BinaryType)classFile.getType()).getSourceFileName(null/*no info
         available*/),
              owner);
        this.classFile = classFile;
    }

    public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException {
        throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, this));
    }

    public IBuffer getBuffer() throws JavaModelException {
        if (isWorkingCopy())
            return super.getBuffer();
        else
            return this.classFile.getBuffer();
    }

    public char[] getContents() {
        try {
            IBuffer buffer = getBuffer();
            if (buffer == null) return CharOperation.NO_CHAR;
            char[] characters = buffer.getCharacters();
            if (characters == null) return CharOperation.NO_CHAR;
            return characters;
        } catch (JavaModelException e) {
            return CharOperation.NO_CHAR;
        }
    }

    public IPath getPath() {
        return this.classFile.getPath();
    }

    public IJavaElement getPrimaryElement(boolean checkOwner) {
        if (checkOwner && isPrimary()) return this;
        return new ClassFileWorkingCopy(this.classFile, DefaultWorkingCopyOwner.PRIMARY);
    }

    public File resource(PackageFragmentRoot root) {
        if (root.isArchive())
            return root.resource(root);
        return this.classFile.resource(root);
    }

    /**
     * @see Openable#openBuffer(org.eclipse.core.runtime.IProgressMonitor, Object)
     */
    protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {

        // create buffer
        IBuffer buffer = BufferManager.createBuffer(this);

        // set the buffer source
        IBuffer classFileBuffer = this.classFile.getBuffer();
        if (classFileBuffer != null) {
            buffer.setContents(classFileBuffer.getCharacters());
        } else {
            // Disassemble
            IClassFileReader reader = ToolFactory.createDefaultClassFileReader(this.classFile, IClassFileReader.ALL, manager);
            Disassembler disassembler = new Disassembler();
            String contents = disassembler.disassemble(reader, Util.getLineSeparator("", getJavaProject()),
                                                       ClassFileBytesDisassembler.WORKING_COPY); //$NON-NLS-1$
            buffer.setContents(contents);
        }

        // add buffer to buffer cache
        BufferManager bufManager = getBufferManager();
        bufManager.addBuffer(buffer);

        // listen to buffer changes
        buffer.addBufferChangedListener(this);

        return buffer;
    }

    protected void toStringName(StringBuffer buffer) {
        buffer.append(this.classFile.getElementName());
    }

}
