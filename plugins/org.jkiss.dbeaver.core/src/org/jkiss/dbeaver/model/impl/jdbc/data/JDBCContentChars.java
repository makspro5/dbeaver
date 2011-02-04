/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.impl.jdbc.data;

import net.sf.jkiss.utils.CommonUtils;
import net.sf.jkiss.utils.streams.MimeTypes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.data.DBDContent;
import org.jkiss.dbeaver.model.data.DBDContentStorage;
import org.jkiss.dbeaver.model.data.DBDValueClonable;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;
import org.jkiss.dbeaver.utils.ContentUtils;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBCContentChars
 *
 * @author Serge Rider
 */
public class JDBCContentChars extends JDBCContentAbstract implements DBDContent, DBDValueClonable, DBDContentStorage {

    static final Log log = LogFactory.getLog(JDBCContentChars.class);

    private String originalData;
    private String data;

    public JDBCContentChars(String data) {
        this.data = this.originalData = data;
    }

    public String getData() {
        return data;
    }

    public InputStream getContentStream()
        throws IOException
    {
        if (data == null) {
            // Empty content
            return new ByteArrayInputStream(new byte[0]);
        } else {
            return new ByteArrayInputStream(data.getBytes());
        }
    }

    public Reader getContentReader()
        throws IOException
    {
        if (data == null) {
            // Empty content
            return new StringReader("");
        } else {
            return new StringReader(data);
        }
    }

    public long getContentLength() {
        if (data == null) {
            return 0;
        }
        return data.length();
    }

    public String getContentType()
    {
        return MimeTypes.TEXT_PLAIN;
    }

    public DBDContentStorage getContents(DBRProgressMonitor monitor)
        throws DBCException
    {
        return this;
    }

    public boolean updateContents(
        DBRProgressMonitor monitor,
        DBDContentStorage storage)
        throws DBException
    {
        if (storage == null) {
            data = null;
        } else {
            try {
                Reader reader = storage.getContentReader();
                try {
                    StringWriter sw = new StringWriter((int)storage.getContentLength());
                    ContentUtils.copyStreams(reader, storage.getContentLength(), sw, monitor);
                    data = sw.toString();
                }
                finally {
                    ContentUtils.close(reader);
                }
            }
            catch (IOException e) {
                throw new DBCException(e);
            }
        }
        return false;
    }

    public String getCharset()
    {
        return ContentUtils.DEFAULT_FILE_CHARSET;
    }

    public JDBCContentChars cloneStorage(DBRProgressMonitor monitor)
    {
        return cloneValue(monitor);
    }

    public void bindParameter(DBCExecutionContext context, PreparedStatement preparedStatement,
                              DBSTypedObject columnType, int paramIndex)
        throws DBCException
    {
        try {
            if (data != null) {
                preparedStatement.setString(paramIndex, data);
            } else {
                preparedStatement.setNull(paramIndex, columnType.getValueType());
            }
        }
        catch (SQLException e) {
            throw new DBCException("JDBC error", e);
        }
    }

    public boolean isNull()
    {
        return data == null;
    }

    public JDBCContentChars makeNull()
    {
        return new JDBCContentChars(null);
    }

    public void release()
    {
        this.data = this.originalData;
    }

    @Override
    public boolean equals(Object obj)
    {
        return
            obj instanceof JDBCContentChars &&
            CommonUtils.equalObjects(data, ((JDBCContentChars) obj).data);
    }

    @Override
    public String toString() {
        return data;
    }

    public JDBCContentChars cloneValue(DBRProgressMonitor monitor)
    {
        return new JDBCContentChars(data);
    }

}
