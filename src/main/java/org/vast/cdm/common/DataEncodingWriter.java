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

package org.vast.cdm.common;

import org.vast.xml.DOMHelper;
import org.w3c.dom.*;


/**
 * <p><b>Title:</b><br/>
 * Data Encoding XML Writer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Concrete implementations of this interface are responsible for
 * creating an XML element containing the data encoding structure
 * corresponding to the specified DataEncoding object.  
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @since Aug 12, 2005
 * @version 1.0
 */
public interface DataEncodingWriter
{
	/**
     * Creates a W3C DOM element containing the given encoding information
     * @param dom
     * @param dataEncoding
     * @return
     * @throws CDMException
	 */
    public Element writeEncoding(DOMHelper dom, DataEncoding dataEncoding) throws CDMException;
}