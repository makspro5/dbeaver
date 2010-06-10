/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.struct;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

/**
 * DBSStructureContainerActive
 */
public interface DBSStructureContainerActive
{
    boolean supportsActiveChildChange();

    DBSObject getActiveChild(DBRProgressMonitor monitor) throws DBException;

    void setActiveChild(DBRProgressMonitor monitor, DBSObject child) throws DBException;

}