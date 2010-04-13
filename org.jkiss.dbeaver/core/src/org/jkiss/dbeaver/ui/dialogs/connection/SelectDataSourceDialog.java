/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ui.dialogs.connection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jkiss.dbeaver.core.DBeaverCore;
import org.jkiss.dbeaver.model.meta.DBMRoot;
import org.jkiss.dbeaver.registry.DataSourceDescriptor;
import org.jkiss.dbeaver.registry.DataSourceRegistry;
import org.jkiss.dbeaver.ui.controls.itemlist.ItemListControl;

import java.util.List;

/**
 * SelectDataSourceDialog
 *
 * @author Serge Rieder
 */
public class SelectDataSourceDialog extends Dialog {

    private DataSourceRegistry registry;
    private DataSourceDescriptor dataSource = null;

    private SelectDataSourceDialog(Shell parentShell)
    {
        super(parentShell);
        this.registry = DataSourceRegistry.getDefault();
    }

    protected boolean isResizable()
    {
        return true;
    }

    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Select datasource");

        Composite group = (Composite) super.createDialogArea(parent);
        GridData gd = new GridData(GridData.FILL_BOTH);
        group.setLayoutData(gd);

        DBMRoot rootNode = DBeaverCore.getInstance().getMetaModel().getRoot();

        ItemListControl dsList = new ItemListControl(group, SWT.BORDER, null, rootNode);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        gd.widthHint = 400;
        dsList.setLayoutData(gd);
        dsList.fillData();
        dsList.getViewer().addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.isEmpty()) {
                    dataSource = null;
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                } else {
                    dataSource = (DataSourceDescriptor) selection.getFirstElement();
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                }
            }
        });
        dsList.setDoubleClickHandler(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                if (getButton(IDialogConstants.OK_ID).isEnabled()) {
                    okPressed();
                }
            }
        });

        return group;
    }

    protected Control createContents(Composite parent)
    {
        Control ctl = super.createContents(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        return ctl;
    }

    public DataSourceDescriptor getDataSource()
    {
        return dataSource;
    }

    public static DataSourceDescriptor selectDataSource(Shell parentShell)
    {
        List<DataSourceDescriptor> datasources = DBeaverCore.getInstance().getDataSourceRegistry().getDataSources();
        if (datasources.isEmpty()) {
            MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_INFORMATION | SWT.OK);
            messageBox.setMessage("Create new datasource first.");
            messageBox.setText("No datasources exists");
            messageBox.open();
            return null;
        } else if (datasources.size() == 1) {
            return datasources.get(0);
        } else {
            SelectDataSourceDialog scDialog = new SelectDataSourceDialog(parentShell);
            if (scDialog.open() == IDialogConstants.OK_ID) {
                return scDialog.getDataSource();
            } else {
                return null;
            }
        }
    }
}
