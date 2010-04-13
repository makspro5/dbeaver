/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.ui;

import org.jkiss.dbeaver.model.DBPConnectionInfo;
import org.jkiss.dbeaver.model.DBPDriver;

/**
 * IDataSourceEditorSite
 */
public interface IDataSourceEditorSite
{
    DBPDriver getDriver();

    DBPConnectionInfo getConnectionInfo();

    void updateButtons();

    void updateMessage();

    void testConnection();

    boolean openDriverEditor();
}
