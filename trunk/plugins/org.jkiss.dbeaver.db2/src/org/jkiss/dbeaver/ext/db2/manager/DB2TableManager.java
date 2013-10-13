/*
 * Copyright (C) 2013      Denis Forveille titou10.titou10@gmail.com
 * Copyright (C) 2010-2013 Serge Rieder serge@jkiss.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jkiss.dbeaver.ext.db2.manager;

import org.eclipse.ui.IWorkbenchWindow;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.IDatabasePersistAction;
import org.jkiss.dbeaver.ext.db2.model.*;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEObjectRenamer;
import org.jkiss.dbeaver.model.impl.DBSObjectCache;
import org.jkiss.dbeaver.model.impl.edit.AbstractDatabasePersistAction;
import org.jkiss.dbeaver.model.impl.jdbc.edit.struct.JDBCTableManager;
import org.jkiss.dbeaver.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DB2 Table Manager
 * 
 * @author Denis Forveille
 * 
 */
public class DB2TableManager extends JDBCTableManager<DB2Table, DB2Schema> implements DBEObjectRenamer<DB2Table> {

    private static final String NEW_TABLE_NAME = "NEW_TABLE";

    private static final String SQL_ALTER = "ALTER TABLE ";
    private static final String SQL_RENAME_TABLE = "RENAME TABLE %s TO %s";
    private static final String SQL_COMMENT = "COMMENT ON TABLE %s IS '%s'";

    private static final String CLAUSE_IN_TS = "IN ";
    private static final String CLAUSE_IN_TS_IX = "INDEX IN ";
    private static final String CLAUSE_IN_TS_LONG = "LONG IN ";

    private static final String CMD_ALTER = "Alter Table";
    private static final String CMD_COMMENT = "Comment on Table";
    private static final String CMD_RENAME = "Rename Table";

    private static final String LINE_SEPARATOR = ContentUtils.getDefaultLineSeparator();

    private static final Class<?>[] CHILD_TYPES = { DB2TableColumn.class, DB2TableUniqueKey.class, DB2TableForeignKey.class,
        DB2Index.class };

    // -----------------
    // Business Contract
    // -----------------

    @Override
    public Class<?>[] getChildTypes()
    {
        return CHILD_TYPES;
    }

    @Override
    public DBSObjectCache<DB2Schema, DB2Table> getObjectsCache(DB2Table object)
    {
        return object.getSchema().getTableCache();
    }

    // ------
    // Create
    // ------

    @Override
    public DB2Table createDatabaseObject(IWorkbenchWindow workbenchWindow, DBECommandContext context, DB2Schema db2Schema,
        Object copyFrom)
    {
        return new DB2Table(db2Schema, NEW_TABLE_NAME);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void appendTableModifiers(DB2Table db2Table, NestedObjectCommand tableProps, StringBuilder ddl)
    {

        try {
            // Add Tablespaces infos
            if (db2Table.getTablespace(VoidProgressMonitor.INSTANCE) != null) {
                ddl.append(LINE_SEPARATOR);
                ddl.append(CLAUSE_IN_TS);
                ddl.append(getTablespaceName(db2Table.getTablespace(VoidProgressMonitor.INSTANCE)));
            }
            if (db2Table.getIndexTablespace(VoidProgressMonitor.INSTANCE) != null) {
                ddl.append(LINE_SEPARATOR);
                ddl.append(CLAUSE_IN_TS_IX);
                ddl.append(getTablespaceName(db2Table.getIndexTablespace(VoidProgressMonitor.INSTANCE)));
            }
            if (db2Table.getLongTablespace(VoidProgressMonitor.INSTANCE) != null) {
                ddl.append(LINE_SEPARATOR);
                ddl.append(CLAUSE_IN_TS_LONG);
                ddl.append(getTablespaceName(db2Table.getLongTablespace(VoidProgressMonitor.INSTANCE)));
            }
        } catch (DBException e) {
            // Never be here
            log.warn(e);
        }
    }

    private static String getTablespaceName(Object tablespace)
    {
        if (tablespace instanceof DB2Tablespace) {
            return ((DB2Tablespace) tablespace).getName();
        } else if (tablespace != null) {
            return String.valueOf(tablespace);
        } else {
            return null;
        }
    }

    @Override
    public IDatabasePersistAction[] makeStructObjectCreateActions(StructCreateCommand command)
    {
        // Eventually add Comment
        IDatabasePersistAction commentAction = buildCommentAction(command.getObject());
        if (commentAction == null) {
            return super.makeStructObjectCreateActions(command);
        } else {
            List<IDatabasePersistAction> actionList = new ArrayList<IDatabasePersistAction>(Arrays.asList(super
                .makeStructObjectCreateActions(command)));
            actionList.add(commentAction);
            return actionList.toArray(new IDatabasePersistAction[actionList.size()]);
        }
    }

    // ------
    // Alter
    // ------

    @Override
    public IDatabasePersistAction[] makeObjectModifyActions(ObjectChangeCommand command)
    {
        DB2Table db2Table = command.getObject();

        List<IDatabasePersistAction> actions = new ArrayList<IDatabasePersistAction>(2);

        if (command.getProperties().size() > 1) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(SQL_ALTER);
            sb.append(db2Table.getFullQualifiedName());
            sb.append(" ");

            appendTableModifiers(command.getObject(), command, sb);

            actions.add(new AbstractDatabasePersistAction(CMD_ALTER, sb.toString()));
        }

        IDatabasePersistAction commentAction = buildCommentAction(db2Table);
        if (commentAction != null) {
            actions.add(commentAction);
        }

        return actions.toArray(new IDatabasePersistAction[actions.size()]);
    }

    // ------
    // Rename
    // ------
    @Override
    public IDatabasePersistAction[] makeObjectRenameActions(ObjectRenameCommand command)
    {
        String sql = String.format(SQL_RENAME_TABLE, command.getObject().getName(), command.getNewName());
        IDatabasePersistAction[] actions = new IDatabasePersistAction[1];
        actions[0] = new AbstractDatabasePersistAction(CMD_RENAME, sql);
        return actions;
    }

    @Override
    public void renameObject(DBECommandContext commandContext, DB2Table object, String newName) throws DBException
    {
        processObjectRename(commandContext, object, newName);
    }

    // -------
    // Helpers
    // -------
    private IDatabasePersistAction buildCommentAction(DB2Table db2Table)
    {
        if (CommonUtils.isNotEmpty(db2Table.getDescription())) {
            String commentSQL = String.format(SQL_COMMENT, db2Table.getFullQualifiedName(), db2Table.getDescription());
            return new AbstractDatabasePersistAction(CMD_COMMENT, commentSQL);
        } else {
            return null;
        }
    }
}
