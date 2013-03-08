package org.jkiss.dbeaver.tools.transfer.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.exec.DBCResultSet;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.tools.transfer.IDataTransferConsumer;
import org.jkiss.dbeaver.tools.transfer.stream.IStreamDataExporter;
import org.jkiss.dbeaver.tools.transfer.stream.StreamConsumerSettings;

import java.util.Map;

/**
* Stream transfer consumer
*/
public class DatabaseTransferConsumer implements IDataTransferConsumer<StreamConsumerSettings, IStreamDataExporter> {

    static final Log log = LogFactory.getLog(DatabaseTransferConsumer.class);

    public DatabaseTransferConsumer()
    {
    }

    @Override
    public void fetchStart(DBCExecutionContext context, DBCResultSet resultSet) throws DBCException
    {
        initExporter(context);

    }

    @Override
    public void fetchRow(DBCExecutionContext context, DBCResultSet resultSet) throws DBCException
    {
    }

    @Override
    public void fetchEnd(DBCExecutionContext context) throws DBCException
    {
        closeExporter();
    }

    @Override
    public void close()
    {
    }

    private void initExporter(DBCExecutionContext context) throws DBCException
    {
    }

    private void closeExporter()
    {
    }

    @Override
    public void initTransfer(DBSObject sourceObject, StreamConsumerSettings settings, IStreamDataExporter processor, Map<Object, Object> processorProperties)
    {
    }

    @Override
    public void finishTransfer()
    {
    }

    @Override
    public String getTargetName()
    {
        return "";
    }

}
