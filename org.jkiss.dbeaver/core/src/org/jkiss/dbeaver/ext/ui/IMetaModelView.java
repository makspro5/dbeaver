/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPart;
import org.jkiss.dbeaver.model.navigator.DBNModel;

/**
 * INavigatorView
 */
public interface IMetaModelView
{
    DBNModel getMetaModel();
    
    Viewer getViewer();

    IWorkbenchPart getWorkbenchPart();

}
