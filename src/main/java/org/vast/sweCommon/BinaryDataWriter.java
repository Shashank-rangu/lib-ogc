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

import java.io.*;
import org.vast.data.*;
import org.vast.cdm.common.*;


/**
 * <p><b>Title:</b><br/>
 * Binary Data Writer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Writes CDM binary data stream using the given data components
 * structure and binary encoding information. This supports raw
 * binary and base64 for now.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin & Gregoire Berthiau
 * @date Feb 10, 2006
 * @version 1.0
 */
public class BinaryDataWriter extends AbstractDataWriter
{
    protected DataOutputExt dataOutput;
	protected boolean componentEncodingResolved = false;
	
	
	public BinaryDataWriter()
	{
	}

	
	@Override
	public void setOutput(OutputStream outputStream) throws CDMException
	{
	    // use Base64 converter
        switch (((BinaryEncoding)dataEncoding).byteEncoding)
        {
            case BASE64:
                outputStream = new Base64Encoder(outputStream);
                break;
                
            case RAW:
                break;
                
            default:
                throw new CDMException("Unsupported byte encoding");
        }
        
        // create right data output stream
        if (((BinaryEncoding)dataEncoding).byteOrder == BinaryEncoding.ByteOrder.BIG_ENDIAN)
            dataOutput = new DataOutputStreamBI(new BufferedOutputStream(outputStream));
        else if (((BinaryEncoding)dataEncoding).byteOrder == BinaryEncoding.ByteOrder.LITTLE_ENDIAN)
            dataOutput = new DataOutputStreamLI(new BufferedOutputStream(outputStream));
	}
	
		
	@Override
	public void reset() throws CDMException
	{
		if (!componentEncodingResolved)
			resolveComponentEncodings();
		
		super.reset();
	}
	
	
	@Override
	public void close() throws CDMException
	{
	    try
        {
	        dataOutput.flush();
	        dataOutput.close();
        }
        catch (IOException e)
        {
            throw new CDMException(STREAM_ERROR, e);
        }
	}
	
	
	@Override
	public void flush() throws CDMException
    {
        try
        {
            dataOutput.flush();
        }
        catch (IOException e)
        {
            throw new CDMException(STREAM_ERROR, e);
        }
    }
	
	
	/**
	 * Maps a given scalar component to the corresponding BinaryValue
	 * object containing binary encoding information. This also
	 * forces the DataValues primitive type to be the same as the ones
	 * specified in the binary encoding section.
	 * @throws CDMException
	 */
	protected void resolveComponentEncodings()
	{
		BinaryOptions[] encodingList = ((BinaryEncoding)dataEncoding).componentEncodings;
		
	    for (BinaryOptions binaryOpts: encodingList)
		{
			String [] dataPath = binaryOpts.componentName.split("/");
			DataComponent dataComponent = null;
			
			// find component in tree
            for (int j=0; j<dataPath.length; j++)
            {
                if (j==0)
                {
                    if (dataPath[0].equals(dataComponents.getName()))
                        dataComponent = dataComponents;
                }
                else
                    dataComponent = dataComponent.getComponent(dataPath[j]);
                
                if (dataComponent == null)
                {
                    System.err.println("Unknown component " + dataPath[j]);
                    continue;
                }
            }
			
			dataComponent.setEncodingInfo(binaryOpts);	
		}
		
		componentEncodingResolved = true;
	}
	
	
	@Override
	protected void processAtom(DataValue scalarInfo) throws CDMException
	{
		// get encoding info for component
		BinaryComponent binaryComponent = (BinaryComponent)scalarInfo.getEncodingInfo();
		
		// write token = dataAtom					
		writeBinaryAtom(scalarInfo, binaryComponent);
	}
	
	
	/**
	 * Parse binary component using info and encoding options
	 * Decoded value is assigned to each DataValue
	 * @param scalarInfo
	 * @param binaryInfo
	 * @throws CDMException
	 */
	private void writeBinaryAtom(DataValue scalarInfo, BinaryComponent binaryInfo) throws CDMException
	{
		try
		{
			switch (binaryInfo.type)
			{
				case BOOLEAN:
					boolean boolValue = scalarInfo.getData().getBooleanValue();
                    dataOutput.writeBoolean(boolValue);										
					break;
				
				case BYTE:
					byte byteValue = scalarInfo.getData().getByteValue();
                    dataOutput.writeByte(byteValue);
					break;
					
				case UBYTE:
					short ubyteValue = scalarInfo.getData().getShortValue();
					dataOutput.writeUnsignedByte(ubyteValue);
					break;
					
				case SHORT:
					short shortValue = scalarInfo.getData().getShortValue();
                    dataOutput.writeByte(shortValue);
					break;
					
				case USHORT:
                    int ushortValue = scalarInfo.getData().getIntValue();
					dataOutput.writeUnsignedShort(ushortValue);
					break;
					
				case INT:
					int intValue = scalarInfo.getData().getIntValue();
                    dataOutput.writeInt(intValue);
					break;
					
				case UINT:
					long uintValue = scalarInfo.getData().getLongValue();
					dataOutput.writeUnsignedInt(uintValue);
					break;
					
				case LONG:
					long longValue = scalarInfo.getData().getLongValue();
                    dataOutput.writeLong(longValue);
					break;
					
				case ULONG:
                    long ulongValue = scalarInfo.getData().getLongValue();
                    dataOutput.writeLong(ulongValue);
					break;
					
				case FLOAT:
					float floatValue = scalarInfo.getData().getFloatValue();
                    dataOutput.writeFloat(floatValue);
					break;
					
				case DOUBLE:
                    double doubleValue = scalarInfo.getData().getDoubleValue();
                    dataOutput.writeDouble(doubleValue);
					break;
					
				case UTF_STRING:
					String utfValue = scalarInfo.getData().getStringValue();
                    dataOutput.writeUTF(utfValue);
					break;
					
				case ASCII_STRING:
                    String asciiValue = scalarInfo.getData().getStringValue();
                    dataOutput.writeASCII(asciiValue);
					break;
			}
		}
		catch (RuntimeException e)
		{
			throw new CDMException("Error while writing component " + scalarInfo.getName(), e);
		}
		catch (IOException e)
		{
			throw new CDMException(STREAM_ERROR, e);
		}
	}
	
	
	@Override
	protected boolean processBlock(DataComponent blockInfo) throws CDMException
	{		
		if (blockInfo instanceof DataChoice)
		{
			// write implicit choice index
			try
			{
				int selected = ((DataChoice)blockInfo).getSelected();
				dataOutput.writeByte(selected);
			}
			catch (IllegalStateException e)
			{
				throw new CDMException(CHOICE_ERROR);
			}
			catch (IOException e)
			{
				throw new CDMException(STREAM_ERROR);
			}
		}
		else
		{
			// get next encoding block
			BinaryBlock binaryBlock = (BinaryBlock)blockInfo.getEncodingInfo();
			
			// write whole block at once
			if (binaryBlock != null)
			{
				writeBinaryBlock(blockInfo, binaryBlock);
				return false;
			}
		}
		
		return true;		
	}

	
	private void writeBinaryBlock(DataComponent scalarInfo,	BinaryBlock binaryBlock)  throws CDMException
	{
		// TODO implement writeBinaryBlock: call special compressed writer
	}
}