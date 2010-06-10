package org.jkiss.dbeaver.ext.generic.model;

import net.sf.jkiss.utils.CommonUtils;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.struct.DBSCatalog;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSUtils;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCConstants;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * GenericCatalog
 */
public class GenericCatalog extends GenericStructureContainer implements DBSCatalog
{
    private GenericDataSource dataSource;
    private String catalogName;
    private List<GenericSchema> schemas;
    private boolean isInitialized = false;

    public GenericCatalog(GenericDataSource dataSource, String catalogName)
    {
        this.dataSource = dataSource;
        this.catalogName = catalogName;
    }

    public GenericDataSource getDataSource()
    {
        return dataSource;
    }

    public GenericCatalog getCatalog()
    {
        return this;
    }

    public GenericSchema getSchema()
    {
        return null;
    }

    public GenericCatalog getObject()
    {
        return this;
    }

    public List<GenericSchema> getSchemas()
        throws DBException
    {
        if (schemas == null && !isInitialized) {
            this.schemas = this.loadSchemas();
            this.isInitialized = true;
        }
        return schemas;
    }

    public GenericSchema getSchema(String name)
    {
        return DBSUtils.findObject(schemas, name);
    }

    public String getName()
    {
        return catalogName;
    }

    public String getDescription()
    {
        return null;
    }

    public DBSObject getParentObject()
    {
        return getDataSource().getContainer();
    }

    public boolean refreshObject()
        throws DBException
    {
        return false;
    }

    private List<GenericSchema> loadSchemas()
        throws DBException
    {
        try {
            DatabaseMetaData metaData = getDataSource().getConnection().getMetaData();
            List<GenericSchema> tmpSchemas = new ArrayList<GenericSchema>();
            ResultSet dbResult = metaData.getSchemas();
            try {
                while (dbResult.next()) {
                    String catalogName = JDBCUtils.safeGetString(dbResult, JDBCConstants.TABLE_CATALOG);
/*
                    if (CommonUtils.isEmpty(catalogName) || !catalogName.equals(catalogName)) {
                        // Invalid schema's catalog or schema without catalog (then do not use schemas as structure) 
                        continue;
                    }
*/
                    String schemaName = JDBCUtils.safeGetString(dbResult, JDBCConstants.TABLE_SCHEM);
                    if (!CommonUtils.isEmpty(schemaName)) {
                        GenericSchema schema = new GenericSchema(this, schemaName);
                        tmpSchemas.add(schema);
                    }
                }
            } finally {
                dbResult.close();
            }
            return tmpSchemas;
        } catch (SQLException ex) {
            // Schemas do not supported - jsut ignore this error
            return null;
        }
    }

    public Collection<? extends DBSObject> getChildren(DBRProgressMonitor monitor)
        throws DBException
    {
        if (!CommonUtils.isEmpty(getSchemas())) {
            return getSchemas();
        } else {
            return getTables();
        }
    }

    public DBSObject getChild(DBRProgressMonitor monitor, String childName)
        throws DBException
    {
        if (!CommonUtils.isEmpty(getSchemas())) {
            return getSchema(childName);
        } else {
            return super.getChild(monitor, childName);
        }
    }
}
