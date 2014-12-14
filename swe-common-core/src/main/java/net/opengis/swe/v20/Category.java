package net.opengis.swe.v20;


/**
 * POJO class for XML type CategoryType(@http://www.opengis.net/swe/2.0).
 *
 * This is a complex type.
 */
@SuppressWarnings("javadoc")
public interface Category extends ScalarComponent, HasCodeSpace, HasConstraints<AllowedTokens>
{
    
    @Override
    public Category copy();
    
    
    /**
     * Gets the value property
     */
    public String getValue();
    
    
    /**
     * Checks if value is set
     */
    public boolean isSetValue();
    
    
    /**
     * Sets the value property
     */
    public void setValue(String value);
}