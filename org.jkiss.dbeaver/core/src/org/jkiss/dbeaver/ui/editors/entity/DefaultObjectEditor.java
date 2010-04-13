/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.editors.entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jkiss.dbeaver.model.meta.DBMNode;
import org.jkiss.dbeaver.model.meta.DBMTreeFolder;
import org.jkiss.dbeaver.model.meta.DBMTreeNode;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.ui.actions.OpenEntityEditorAction;
import org.jkiss.dbeaver.ui.views.properties.PropertiesPage;

import java.util.ArrayList;
import java.util.List;

/**
 * DefaultObjectEditor
 */
class DefaultObjectEditor extends EditorPart
{
    static Log log = LogFactory.getLog(DefaultObjectEditor.class);

    private DBMNode node;
    private PropertiesPage properties;

    DefaultObjectEditor(DBMNode node)
    {
        this.node = node;
    }

    public void createPartControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(1, true);
        container.setLayout(gl);

        {
            Group infoGroup = new Group(container, SWT.NONE);
            infoGroup.setText("Information");
            gl = new GridLayout(3, false);
            infoGroup.setLayout(gl);

            List<DBMTreeNode> nodeList = new ArrayList<DBMTreeNode>();
            for (DBMNode n = node; n != null; n = n.getParentNode()) {
                if (n instanceof DBMTreeNode && !(n instanceof DBMTreeFolder)) {
                    nodeList.add(0, (DBMTreeNode)n);
                }
            }
            for (final DBMTreeNode treeNode : nodeList) {
                Label objectIcon = new Label(infoGroup, SWT.NONE);
                objectIcon.setImage(treeNode.getNodeIconDefault());

                Label objectLabel = new Label(infoGroup, SWT.NONE);
                objectLabel.setText(treeNode.getMeta().getLabel() + ":");

                Link objectLink = new Link(infoGroup, SWT.NONE);
                //Text objectText = new Text(infoGroup, SWT.BORDER);
                GridData gd = new GridData(GridData.FILL_HORIZONTAL);
                objectLink.setLayoutData(gd);

                if (treeNode == node) {
                    objectLink.setText(treeNode.getNodeName());
                } else {
                    objectLink.setText("<A>" + treeNode.getNodeName() + "</A>");
                    objectLink.addSelectionListener(new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            OpenEntityEditorAction.openEntityEditor(treeNode, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
                        }
                    });
                    objectLink.setToolTipText("Open '" + treeNode.getNodeName() + "' viewer");
                }
            }
        }

        {
            Group propsGroup = new Group(container, SWT.NONE);
            propsGroup.setText("Properties");
            gl = new GridLayout(2, false);
            propsGroup.setLayout(gl);
            GridData gd = new GridData(GridData.FILL_BOTH);
            propsGroup.setLayoutData(gd);

            DBSObject itemObject = node.getObject();
            //final PropertyCollector propertyCollector = new PropertyCollector(itemObject);
            //List<PropertyAnnoDescriptor> annoProps = PropertyAnnoDescriptor.extractAnnotations(itemObject);

            properties = new PropertiesPage();
            //propertiesView.
            properties.createControl(propsGroup);
            gd = new GridData(GridData.FILL_BOTH);
            //gd.heightHint = 100;
            properties.getControl().setLayoutData(gd);
            properties.setCurrentObject(getSite().getPart(), itemObject);
        }
    }

    @Override
    public void dispose()
    {
        if (properties != null) {
            properties.dispose();
        }
        super.dispose();
    }

    public void setFocus()
    {
    }

    public void doSave(IProgressMonitor monitor)
    {

    }

    public void doSaveAs()
    {
    }

    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException
    {
        setSite(site);
        setInput(input);
    }

    public boolean isDirty()
    {
        return false;
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }
}