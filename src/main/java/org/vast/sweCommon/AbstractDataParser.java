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
import java.net.URI;
import org.vast.cdm.common.*;
import org.vast.data.DataIterator;


/**
 * <p><b>Title:</b><br/>
 * Data Parser
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract class for parsing a CDM data stream
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 16, 2005
 * @version 1.0
 */
public abstract class AbstractDataParser extends DataIterator implements DataStreamParser
{
	protected final static String STREAM_ERROR = "IO Error while parsing data stream";
	protected final static String CHOICE_ERROR = "Invalid choice selection: ";
	protected boolean stopParsing = false;
		
	
	public AbstractDataParser()
	{
		super(true);
	}
	
	
	/**
	 * Stop the parsing from another thread
	 */
	public synchronized void stop()
	{
		stopParsing = true;
	}
	
	
	@Override
	public void reset() throws CDMException
    {
	    super.reset();
	    
	    // generate new data block if not parsing to an array
	    if (parentArray == null)
	        dataComponents.renewDataBlock();
    }
	
	
	/**
	 * Default parse method from a URI string
	 */
	public void parse(String uri) throws CDMException
	{
		InputStream in = URIStreamHandler.openStream(uri);
		this.parse(in);
	}

	
	/**
	 * Default parse method from a URI object
	 */
	public void parse(URI uri) throws CDMException
	{
		InputStream in = URIStreamHandler.openStream(uri);
		this.parse(in);
	}
	
	
	/**
	 * Parse next atom from stream
	 */
	public void parseNextElement() throws CDMException
	{
		this.processNextElement();
	}
}