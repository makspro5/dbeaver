/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.dbc;

import java.util.List;

/**
 * DBCResultSetMetaData
 */
public interface DBCResultSetMetaData
{
    DBCResultSet getResultSet();

    List<DBCColumnMetaData> getColumns();
}
