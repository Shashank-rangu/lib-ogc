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

import java.io.DataOutput;
import java.io.IOException;


/**
 * <p><b>Title:</b><br/>
 * Data Output Interface Extension
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Extension of DataOutputStream to support writing unsigned byte,
 * short, int and long values as well as ASCII (0 terminated) strings.
 * </p>
 *
 * <p>Copyright (c) 2009</p>
 * @author Alexandre Robin
 * @date Jan 10, 2009
 * @version 1.0
 */
public interface DataOutputExt extends DataOutput
{
    
    /**
     * Writes the lowest significant byte of the given
     * short value as an unsigned byte
     * @return
     * @throws IOException
     */
    public void writeUnsignedByte(short val) throws IOException;
    
    
    /**
     * Writes the 2 lowest significant bytes of the given
     * int value as an unsigned short
     * @return
     * @throws IOException
     */
    public void writeUnsignedShort(int val) throws IOException;
    
    
    /**
	 * Writes the 4 lowest significant bytes of the given
	 * long value as an unsigned int
	 * @return
	 * @throws IOException
	 */
	public void writeUnsignedInt(long val) throws IOException;
	
	
	/**
	 * Writes long value as unsigned.
	 * @return
	 * @throws IOException
	 */
	public void writeUnsignedLong(long val) throws IOException;
	
	
	/**
	 * Writes a 0 terminated ASCII string to stream
	 * @return
	 * @throws IOException
	 */
	public void writeASCII(String val) throws IOException;
	
	
	/**
	 * Flushes this data output stream. This forces any buffered
	 * output bytes to be written out to the stream
	 * @throws IOException
	 */
	public void flush() throws IOException;
	
	
	/**
	 * Flushes and closes the underlying stream
	 * @throws IOException
	 */
	public void close() throws IOException;
}