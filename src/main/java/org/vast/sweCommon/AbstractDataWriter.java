/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.sweCommon;

import java.io.OutputStream;
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataStreamWriter;
import org.vast.data.DataIterator;
import org.vast.data.DataValue;
import org.vast.util.DateTimeFormat;


/**
 * <p><b>Title:</b><br/>
 * Data Writer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract class for writing a CDM data stream.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Feb 10, 2006
 * @version 1.0
 */
public abstract class AbstractDataWriter extends DataIterator implements DataStreamWriter
{
	protected final static String STREAM_ERROR = "IO Error while writing data stream";
	protected final static String CHOICE_ERROR = "Invalid choice selection: ";
	protected final static String NO_HANDLER_ERROR = "A DataHandler must be registered";
	
	protected boolean stopWriting = false;
	
	
	public AbstractDataWriter()
	{
		super(false);
	}


	public abstract void setOutput(OutputStream os) throws CDMException;
	
	public abstract void close() throws CDMException;
	
    public abstract void flush() throws CDMException;
                
    protected abstract void processAtom(DataValue scalarInfo) throws CDMException;

    protected abstract boolean processBlock(DataComponent blockInfo) throws CDMException;
    
    
    public void write(OutputStream outputStream) throws CDMException
    {
        // error if no dataHandler is registered
        if (dataHandler == null)
            throw new IllegalStateException(NO_HANDLER_ERROR);
        
        stopWriting = false;
        
        try
        {
            setOutput(outputStream);
            
            // keep writing until told to stop
            do processNextElement();
            while(!stopWriting);
        }
        finally
        {
            dataComponents.clearData();
        }
    }
    
    
    public void write(DataBlock dataBlock) throws CDMException
    {
        dataComponents.setData(dataBlock);
        
        try
        {
            do processNextElement();
            while(!isEndOfDataBlock());
        }
        finally
        {
            dataComponents.clearData();
        }
    }
    
    
	public synchronized void stop()
	{
        stopWriting = true;
	}
	
	
	/**
	 * Retrieve string representation of value of component
	 * This will convert to an ISO string for appropriate time components
	 * @param scalarInfo
	 * @return
	 */
	protected String getStringValue(DataValue scalarInfo)
	{
		String def = (String)scalarInfo.getProperty(SweConstants.UOM_URI);
		String val;
		
		if (def != null && def.contains("8601"))
			val = DateTimeFormat.formatIso(scalarInfo.getData().getDoubleValue(), 0);
		else
			val = scalarInfo.getData().getStringValue();
		
		return val;
	}
}