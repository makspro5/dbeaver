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
package org.jkiss.dbeaver.ext.db2.model.cache;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.db2.model.DB2Routine;
import org.jkiss.dbeaver.ext.db2.model.DB2Schema;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCExecutionContext;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCObjectCache;
import org.jkiss.dbeaver.model.struct.rdb.DBSProcedureType;

/**
 * Cache for DB2 Procedures
 * 
 * @author Denis Forveille
 * 
 */
public class DB2RoutineCache extends JDBCObjectCache<DB2Schema, DB2Routine> {

   private static final String SQL_P = "SELECT * FROM SYSCAT.ROUTINES WHERE ROUTINETYPE= 'P' AND ROUTINESCHEMA = ? ORDER BY ROUTINENAME WITH UR";
   private static final String SQL_F = "SELECT * FROM SYSCAT.ROUTINES WHERE ROUTINETYPE= 'F' AND ROUTINESCHEMA = ? ORDER BY ROUTINENAME WITH UR";

   private String              sql;

   public DB2RoutineCache(DBSProcedureType procedureType) {
      super();
      if (procedureType.equals(DBSProcedureType.FUNCTION)) {
         sql = SQL_F;
      } else {
         sql = SQL_P;
      }
   }

   @Override
   protected JDBCStatement prepareObjectsStatement(JDBCExecutionContext context, DB2Schema db2Schema) throws SQLException {
      JDBCPreparedStatement dbStat = context.prepareStatement(sql);
      dbStat.setString(1, db2Schema.getName());
      return dbStat;
   }

   @Override
   protected DB2Routine fetchObject(JDBCExecutionContext context, DB2Schema db2Schema, ResultSet dbResult) throws SQLException,
                                                                                                          DBException {
      return new DB2Routine(db2Schema, dbResult);
   }
}