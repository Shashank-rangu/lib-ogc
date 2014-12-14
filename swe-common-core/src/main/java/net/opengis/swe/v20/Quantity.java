package net.opengis.swe.v20;


/**
 * POJO class for XML type QuantityType(@http://www.opengis.net/swe/2.0).
 *
 * This is a complex type.
 */
@SuppressWarnings("javadoc")
public interface Quantity extends ScalarComponent, HasUom, HasConstraints<AllowedValues>
{
    
    @Override
    public Quantity copy();
    
    
    /**
     * Gets the value property
     */
    public double getValue();
    
    
    /**
     * Checks if value is set
     */
    public boolean isSetValue();
    
    
    /**
     * Sets the value property
     */
    public void setValue(double value);
    
    
    /**
     * Unsets the value property
     */
    public void unSetValue();
}