/*
 * Copyright (c) 2010, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.model.impl.jdbc.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.data.DBDValueController;
import org.jkiss.dbeaver.model.dbc.DBCException;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC number value handler
 */
public class JDBCBooleanValueHandler extends JDBCAbstractValueHandler {

    public static final JDBCBooleanValueHandler INSTANCE = new JDBCBooleanValueHandler();

    static Log log = LogFactory.getLog(JDBCBooleanValueHandler.class);

    protected Object getColumnValue(ResultSet resultSet, DBSTypedObject columnType, int columnIndex)
        throws DBCException, SQLException
    {
        return resultSet.getBoolean(columnIndex);
    }

    protected void bindParameter(PreparedStatement statement, DBSTypedObject paramType, int paramIndex, Object value) throws SQLException
    {
        if (value == null) {
            statement.setNull(paramIndex, paramType.getValueType());
        } else {
            statement.setBoolean(paramIndex, (Boolean)value);
        }
    }

    public Object copyValueObject(Object value)
    {
        // Boolean is immutable
        return value;
    }

    public boolean editValue(final DBDValueController controller)
        throws DBException
    {
        if (controller.isInlineEdit()) {
            Object value = controller.getValue();

            Combo editor = new Combo(controller.getInlinePlaceholder(), SWT.READ_ONLY);
            editor.add("true");
            editor.add("false");
            editor.setText(value == null ? "false" : value.toString());
            editor.setFocus();
            initInlineControl(controller, editor, new ValueExtractor<Combo>() {
                public Object getValueFromControl(Combo control)
                {
                    switch (control.getSelectionIndex()) {
                        case 0: return Boolean.TRUE;
                        case 1: return Boolean.FALSE;
                        default: return null;
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

}