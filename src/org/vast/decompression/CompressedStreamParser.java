/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.decompression;

import org.vast.cdm.common.BinaryBlock;
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataInputExt;



/**
 * <p><b>Title:</b>
 * Compressed Stream Parser
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for all compressed block decoder
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Oct 17, 2009
 * @version 1.0
 */
public interface CompressedStreamParser
{

    /**
     * Reads compressed data from input stream and assigns result to the component data block
     * @param inputStream
     * @param blockComponent
     * @throws CDMException
     */
    public abstract void decode(DataInputExt inputStream, DataComponent blockComponent) throws CDMException;


    /**
     * Initializes decoder with block data component and its binary encoding info
     * @param binaryBlock
     * @param blockComponent
     * @throws CDMException
     */
    public abstract void init(DataComponent blockComponent, BinaryBlock binaryBlock) throws CDMException;

}