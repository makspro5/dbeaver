/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.ext.mysql.model;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.mysql.MySQLConstants;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.impl.struct.AbstractProcedure;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSProcedureType;
import org.jkiss.dbeaver.utils.ContentUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * GenericProcedure
 */
public class MySQLProcedure extends AbstractProcedure<MySQLDataSource, MySQLCatalog>
{
    //static final Log log = LogFactory.getLog(MySQLProcedure.class);

    private DBSProcedureType procedureType;
    private String resultType;
    private String bodyType;
    private String body;
    private transient String clientBody;
    private String charset;
    private List<MySQLProcedureColumn> columns;

    public MySQLProcedure(MySQLCatalog catalog)
    {
        super(catalog, false);
        this.procedureType = DBSProcedureType.PROCEDURE;
        this.body = "BEGIN" + ContentUtils.getDefaultLineSeparator() + "END";
        this.bodyType = "SQL";
        this.resultType = "";
    }

    public MySQLProcedure(
        MySQLCatalog catalog,
        ResultSet dbResult)
    {
        super(catalog, true);
        loadInfo(dbResult);
    }

    private void loadInfo(ResultSet dbResult)
    {
        setName(JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_ROUTINE_NAME));
        setDescription(JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_ROUTINE_COMMENT));
        this.procedureType = DBSProcedureType.valueOf(JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_ROUTINE_TYPE).toUpperCase());
        this.resultType = JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_DTD_IDENTIFIER);
        this.bodyType = JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_ROUTINE_BODY);
        this.body = JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_ROUTINE_DEFINITION);
        this.charset = JDBCUtils.safeGetString(dbResult, MySQLConstants.COL_CHARACTER_SET_CLIENT);
    }

    @Property(name = "Procedure Type", editable = true, order = 2)
    public DBSProcedureType getProcedureType()
    {
        return procedureType ;
    }

    public void setProcedureType(DBSProcedureType procedureType)
    {
        this.procedureType = procedureType;
    }

    @Property(name = "Result Type", order = 2)
    public String getResultType()
    {
        return resultType;
    }

    @Property(name = "Body Type", order = 3)
    public String getBodyType()
    {
        return bodyType;
    }

    @Property(name = "Body", hidden = true, editable = true, updatable = true, order = -1)
    public String getBody()
    {
        return body;
    }

    @Property(name = "ClientBody", hidden = true, editable = true, updatable = true, order = -1)
    public String getClientBody(DBRProgressMonitor monitor)
        throws DBException
    {
        if (clientBody == null) {
            StringBuilder cb = new StringBuilder(body.length() + 100);
            cb.append(procedureType).append(' ').append(getFullQualifiedName()).append(" (");

            int colIndex = 0;
            for (MySQLProcedureColumn column : getColumns(monitor)) {
                if (colIndex > 0) {
                    cb.append(", ");
                }
                cb.append(column.getColumnType()).append(' ').append(column.getName()).append(' ').append(column.getTypeName());
                colIndex++;
            }
            cb.append(")").append(ContentUtils.getDefaultLineSeparator());
            cb.append(body);
            clientBody = cb.toString();
        }
        return clientBody;
    }

    public String getClientBody()
    {
        return clientBody;
    }

    public void setClientBody(String clientBody)
    {
        this.clientBody = clientBody;
    }

    //@Property(name = "Client Charset", order = 4)
    public String getCharset()
    {
        return charset;
    }

    public List<MySQLProcedureColumn> getColumns(DBRProgressMonitor monitor)
        throws DBException
    {
        if (columns == null) {
            if (!isPersisted()) {
                columns = new ArrayList<MySQLProcedureColumn>();
            }
            getContainer().proceduresCache.loadChildren(monitor, getContainer(), this);
        }
        return columns;
    }

    public boolean isColumnsCached()
    {
        return this.columns != null;
    }

    public void cacheColumns(List<MySQLProcedureColumn> columns)
    {
        this.columns = columns;
    }

    public String getFullQualifiedName()
    {
        return DBUtils.getFullQualifiedName(getDataSource(),
            getContainer(),
            this);
    }

}
