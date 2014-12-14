package net.opengis.swe.v20;


/**
 * POJO class for XML type CategoryRangeType(@http://www.opengis.net/swe/2.0).
 *
 * This is a complex type.
 */
@SuppressWarnings("javadoc")
public interface CategoryRange extends RangeComponent, HasCodeSpace, HasConstraints<AllowedTokens>
{
    
    @Override
    public CategoryRange copy();
    
        
    /**
     * Gets the value property
     */
    public String[] getValue();
    
    
    /**
     * Checks if value is set
     */
    public boolean isSetValue();
    
    
    /**
     * Sets the value property
     */
    public void setValue(String[] value);
}