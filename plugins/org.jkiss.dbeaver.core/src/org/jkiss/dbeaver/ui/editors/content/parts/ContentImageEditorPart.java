/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.editors.content.parts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;
import org.jkiss.dbeaver.ext.IContentEditorPart;
import org.jkiss.dbeaver.ui.DBIcon;
import org.jkiss.dbeaver.ui.controls.imageview.ImageViewControl;

import javax.activation.MimeType;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * LOB text editor
 */
public class ContentImageEditorPart extends EditorPart implements IContentEditorPart, IResourceChangeListener {

    static final Log log = LogFactory.getLog(ContentImageEditorPart.class);

    private ImageViewControl imageViewer;
    private boolean contentValid;

    public void doSave(IProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    @Override
    public void dispose()
    {
        if (imageViewer != null) {
            imageViewer.dispose();
            imageViewer = null;
        }
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void createPartControl(Composite parent) {
        imageViewer = new ImageViewControl(parent, SWT.NONE);

        loadImage();
    }

    private void loadImage() {
        IEditorInput input = getEditorInput();
        if (input instanceof IPathEditorInput) {
            try {
                final IPath absolutePath = Platform.getLocation().append(
                    ((IPathEditorInput) input).getPath());
                File file = absolutePath.toFile();
                if (file.exists()) {
                    InputStream inputStream = new FileInputStream(file);
                    try {
                        contentValid = imageViewer.loadImage(inputStream);
                        imageViewer.update();
                    }
                    finally {
                        inputStream.close();
                    }
                }
            }
            catch (Exception e) {
                log.error("Could not load image contents", e);
            }
        }
    }

    public void setFocus() {
        imageViewer.setFocus();
    }

    public void initPart(IEditorPart contentEditor, MimeType mimeType)
    {
    }

    public IEditorActionBarContributor getActionBarContributor()
    {
        return null;
    }

    public String getContentTypeTitle()
    {
        return "Image";
    }

    public Image getContentTypeImage()
    {
        return DBIcon.TYPE_IMAGE.getImage();
    }

    public String getPreferredMimeType()
    {
        return "image";
    }

    public long getMaxContentLength()
    {
        return 20 * 1024 * 1024;
    }

    public boolean isPreferredContent()
    {
        return contentValid;
    }

    public boolean isOptionalContent()
    {
        return true;
    }

    public void resourceChanged(IResourceChangeEvent event) {

        IResourceDelta delta = event.getDelta();
        if (delta == null) {
            return;
        }
        IEditorInput input = getEditorInput();
        IPath localPath = null;
        if (input instanceof IPathEditorInput) {
            localPath = ((IPathEditorInput) input).getPath();
        }
        if (localPath == null) {
            return;
        }
        delta = delta.findMember(localPath);
        if (delta == null) {
            return;
        }
        if (delta.getKind() == IResourceDelta.CHANGED) {
            // Refresh editor
            getSite().getShell().getDisplay().asyncExec(new Runnable() {
                public void run()
                {
                    loadImage();
                }
            });
        }
    }

}