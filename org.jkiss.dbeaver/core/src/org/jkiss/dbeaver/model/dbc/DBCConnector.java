/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.dbc;

import java.sql.Connection;

/**
 * DBCConnector
 */
public interface DBCConnector
{
    Connection getConnection();
}
