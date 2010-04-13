/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.impl.jdbc;

import org.jkiss.dbeaver.model.dbc.DBCException;

import java.sql.SQLException;

/**
 * JDBCException
 */
public class JDBCException extends DBCException
{
    public JDBCException()
    {
    }

    public JDBCException(String message)
    {
        super(message);
    }

    public JDBCException(String message, SQLException cause)
    {
        super(message, cause);
    }

    public JDBCException(SQLException cause)
    {
        super(cause);
    }

    public SQLException getCause()
    {
        return (SQLException)super.getCause();
    }
}
