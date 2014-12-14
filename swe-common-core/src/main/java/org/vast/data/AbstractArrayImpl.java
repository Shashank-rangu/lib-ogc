/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2014
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin <alex.robin@sensiasoftware.com> or
 Mike Botts <mike.botts@botts-inc.net> for more information.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.data;

import net.opengis.OgcProperty;
import net.opengis.OgcPropertyImpl;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;
import net.opengis.swe.v20.BlockComponent;
import net.opengis.swe.v20.Count;
import net.opengis.swe.v20.DataArray;
import net.opengis.swe.v20.EncodedValues;


/**
 * <p>
 * Provides common methods for all implementations of block components
 * </p>
 *
 * <p>Copyright (c) 2014 Sensia Software LLC</p>
 * @author Alexandre Robin <alex.robin@sensiasoftware.com>
 * @since Nov 10, 2014
 */
public abstract class AbstractArrayImpl extends AbstractDataComponentImpl implements DataArray, BlockComponent
{
    private static final long serialVersionUID = -2536261971844652828L;
    public final static String ELT_COUNT_NAME = "elementCount";
    
    protected OgcPropertyImpl<Count> elementCount = new OgcPropertyImpl<Count>();
    protected OgcPropertyImpl<DataComponent> elementType;
    protected AbstractEncodingImpl encoding;
    protected EncodedValues values;


    public AbstractArrayImpl()
    {
        // special property object to correctly set parent and name
        elementType = new OgcPropertyImpl<DataComponent>() 
        {
            @Override
            public void setValue(DataComponent value)
            {
                ((AbstractDataComponentImpl)value).setName(this.name);
                ((AbstractDataComponentImpl)value).setParent(AbstractArrayImpl.this);
                super.setValue(value);
            }
        };
        
        // special property object to correctly set element count name
        elementCount = new OgcPropertyImpl<Count>() 
        {
            @Override
            public void setValue(Count value)
            {
                ((CountImpl)value).setName(AbstractArrayImpl.ELT_COUNT_NAME);
                super.setValue(value);
            }
        };        
    }
    
    
    @Override
    public abstract AbstractArrayImpl copy();
    
    
    protected void copyTo(AbstractArrayImpl other)
    {
        super.copyTo(other);
        
        other.elementCount = this.elementCount.copy();
        other.elementType = this.elementType.copy();
        
        if (this.encoding != null)
            other.encoding = this.encoding.copy();
        else
            other.encoding = null;
    }
    
    
    @Override
    public void addComponent(String name, DataComponent component)
    {
        if (elementType.hasValue())
            throw new IllegalStateException("The array element type is already set. Use setElementType() to replace it");
            
        setElementType(name, (DataComponent)component);
    }
    
    
    @Override
    public DataComponent removeComponent(int index)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public DataComponent removeComponent(String name)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean hasConstraints()
    {
        return ((DataValue) elementType.getValue()).hasConstraints();
    }


    /**
     * Gets the elementCount property
     */
    @Override
    public Count getElementCount()
    {
        return elementCount.getValue();
    }


    /**
     * Gets extra info (name, xlink, etc.) carried by the elementCount property
     */
    @Override
    public OgcProperty<Count> getElementCountProperty()
    {
        return elementCount;
    }


    /**
     * Sets the elementCount property
     */
    @Override
    public void setElementCount(Count elementCount)
    {
        this.elementCount.setValue(elementCount);
    }


    /**
     * Gets the elementType property
     */
    @Override
    public DataComponent getElementType()
    {
        return (DataComponent)elementType.getValue();
    }


    @Override
    public OgcProperty<DataComponent> getElementTypeProperty()
    {
        return elementType;
    }


    /**
     * Sets the elementType property
     */
    @Override
    public void setElementType(String name, DataComponent component)
    {
        elementType.setName(name);
        elementType.setValue(component);
    }


    /**
     * Gets the encoding property
     */
    @Override
    public DataEncoding getEncoding()
    {
        return encoding;
    }


    /**
     * Checks if encoding is set
     */
    @Override
    public boolean isSetEncoding()
    {
        return (encoding != null);
    }


    /**
     * Sets the encoding property
     */
    @Override
    public void setEncoding(DataEncoding encoding)
    {
        this.encoding = (AbstractEncodingImpl) encoding;
    }


    /**
     * Gets the values property
     */
    @Override
    public EncodedValues getValues()
    {
        return values;
    }


    /**
     * Checks if values is set
     */
    @Override
    public boolean isSetValues()
    {
        return (values != null);
    }


    /**
     * Sets the values property
     */
    @Override
    public void setValues(EncodedValues values)
    {
        this.values = values;
    }

}