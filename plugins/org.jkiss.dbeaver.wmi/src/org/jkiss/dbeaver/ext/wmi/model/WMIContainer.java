/*
 * Copyright (c) 2012, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.wmi.model;

import org.jkiss.dbeaver.model.struct.DBSObject;

/**
 * Entity container
 */
public abstract class WMIContainer extends WMIPropertySource implements DBSObject
{
    protected final WMINamespace parent;

    protected WMIContainer(WMINamespace parent)
    {
        this.parent = parent;
    }

    public String getDescription()
    {
        return null;
    }

    public DBSObject getParentObject()
    {
        return parent;
    }

    public WMIDataSource getDataSource()
    {
        return parent.getDataSource();
    }

    public boolean isPersisted()
    {
        return true;
    }

}
