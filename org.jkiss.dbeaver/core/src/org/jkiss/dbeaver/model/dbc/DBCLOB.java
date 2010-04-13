/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.dbc;

/**
 * DBC LOB
 *
 * @author Serge Rider
 */
public interface DBCLOB {

    long getLength() throws DBCException;

}
