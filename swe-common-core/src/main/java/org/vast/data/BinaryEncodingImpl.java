package org.vast.data;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import net.opengis.HasCopy;
import net.opengis.swe.v20.BinaryEncoding;
import net.opengis.swe.v20.BinaryBlock;
import net.opengis.swe.v20.BinaryMember;
import net.opengis.swe.v20.ByteEncoding;
import net.opengis.swe.v20.BinaryComponent;
import net.opengis.swe.v20.DataComponent;


/**
 * POJO class for XML type BinaryEncodingType(@http://www.opengis.net/swe/2.0).
 *
 * This is a complex type.
 */
public class BinaryEncodingImpl extends AbstractEncodingImpl implements BinaryEncoding
{
    static final long serialVersionUID = 1L;
    protected List<BinaryMember> memberList = new ArrayList<BinaryMember>();
    protected ByteOrder byteOrder;
    protected ByteEncoding byteEncoding;
    protected Long byteLength;
    
    
    public BinaryEncodingImpl()
    {
    }
    
    
    @Override
    public BinaryEncodingImpl copy()
    {
        BinaryEncodingImpl newObj = new BinaryEncodingImpl();
        for (BinaryMember member: memberList)
            newObj.memberList.add((BinaryMember)((HasCopy)member).copy());
        newObj.byteOrder = this.byteOrder;
        newObj.byteEncoding = this.byteEncoding;
        newObj.byteLength = this.byteLength;
        return newObj;
    }
    
    
    /**
     * Gets the list of member properties
     */
    @Override
    public List<BinaryMember> getMemberList()
    {
        return memberList;
    }
    
    
    /**
     * Returns number of member properties
     */
    @Override
    public int getNumMembers()
    {
        if (memberList == null)
            return 0;
        return memberList.size();
    }
    
    
    /**
     * Adds a new memberAsComponent property
     */
    @Override
    public void addMemberAsComponent(BinaryComponent member)
    {
        this.memberList.add(member);
    }
    
    
    /**
     * Adds a new memberAsBlock property
     */
    @Override
    public void addMemberAsBlock(BinaryBlock member)
    {
        this.memberList.add(member);
    }
    
    
    /**
     * Gets the byteOrder property
     */
    @Override
    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }
    
    
    /**
     * Sets the byteOrder property
     */
    @Override
    public void setByteOrder(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
    }
    
    
    /**
     * Gets the byteEncoding property
     */
    @Override
    public ByteEncoding getByteEncoding()
    {
        return byteEncoding;
    }
    
    
    /**
     * Sets the byteEncoding property
     */
    @Override
    public void setByteEncoding(ByteEncoding byteEncoding)
    {
        this.byteEncoding = byteEncoding;
    }
    
    
    /**
     * Gets the byteLength property
     */
    @Override
    public long getByteLength()
    {
        return byteLength;
    }
    
    
    /**
     * Checks if byteLength is set
     */
    @Override
    public boolean isSetByteLength()
    {
        return (byteLength != null);
    }
    
    
    /**
     * Sets the byteLength property
     */
    @Override
    public void setByteLength(long byteLength)
    {
        this.byteLength = byteLength;
    }
    
    
    /**
     * Unsets the byteLength property
     */
    @Override
    public void unSetByteLength()
    {
        this.byteLength = null;
    }
    
    
    public static BinaryEncodingImpl getDefaultEncoding(DataComponent dataComponents)
    {
        BinaryEncodingImpl encoding = new BinaryEncodingImpl();
        encoding.byteEncoding = ByteEncoding.RAW;
        encoding.byteOrder = ByteOrder.BIG_ENDIAN;
        
        // use default encoding info for each data value
        ScalarIterator it = new ScalarIterator(dataComponents);
        while (it.hasNext())
        {
            DataComponent[] nextPath = it.nextPath();
            DataValue nextScalar = (DataValue)nextPath[nextPath.length-1];
            
            // build path
            StringBuffer pathString = new StringBuffer();
            for (DataComponent component: nextPath)
                pathString.append(component.getName() + '/');
            
            BinaryComponentImpl binaryOpts = new BinaryComponentImpl();
            binaryOpts.setCdmDataType(nextScalar.getDataType());
            binaryOpts.setRef(pathString.substring(0, pathString.length()-1));
            
            encoding.addMemberAsComponent(binaryOpts);
            nextScalar.setEncodingInfo(binaryOpts);
        }
        
        return encoding;
    }
}