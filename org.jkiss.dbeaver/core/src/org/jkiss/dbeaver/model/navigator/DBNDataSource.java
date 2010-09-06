/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.navigator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.registry.DataSourceDescriptor;
import org.jkiss.dbeaver.registry.tree.DBXTreeNode;
import org.jkiss.dbeaver.ui.actions.EditConnectionAction;

/**
 * DBNDataSource
 */
public class DBNDataSource extends DBNTreeNode
{
    static final Log log = LogFactory.getLog(DBNDataSource.class);

    private DataSourceDescriptor dataSource;
    private DBXTreeNode treeRoot;

    public DBNDataSource(DBNRoot parentNode, DataSourceDescriptor dataSource)
    {
        super(parentNode);
        this.dataSource = dataSource;
        this.treeRoot = dataSource.getDriver().getProviderDescriptor().getTreeDescriptor();
        this.getModel().addNode(this);
    }

    protected void dispose()
    {
        this.getModel().removeNode(this);
        if (this.dataSource.isConnected()) {
            try {
                this.dataSource.disconnect(this);
            }
            catch (DBException ex) {
                log.error("Error disconnecting datasource", ex);
            }
        }
        this.dataSource = null;
        super.dispose();
    }

    public DataSourceDescriptor getObject()
    {
        return dataSource;
    }

    public Object getValueObject()
    {
        return dataSource.getDataSource();
    }

    public String getNodeName()
    {
        return dataSource.getName();
    }

    public String getNodeDescription()
    {
        return dataSource.getDescription();
    }

    public Class<EditConnectionAction> getDefaultAction()
    {
        return EditConnectionAction.class;
    }

    public boolean isLazyNode()
    {
        return false;
    }

    public DBXTreeNode getMeta()
    {
        return treeRoot;
    }

    @Override
    protected void reloadObject(DBRProgressMonitor monitor, DBSObject object) {
        dataSource = (DataSourceDescriptor) object;
    }

    protected boolean initializeNode(DBRProgressMonitor monitor)
        throws DBException
    {
        if (!dataSource.isConnected()) {
            dataSource.connect(this);
        }
        return dataSource.isConnected();
    }

}
