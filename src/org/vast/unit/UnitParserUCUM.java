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

package org.vast.unit;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vast.util.ExceptionSystem;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * <p><b>Title:</b>
 * Unit Parser UCUM
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Parses a string containing a UCUM unit definition and create
 * a Unit object with the right scale factors and powers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date May 4, 2006
 * @version 1.0
 */
public class UnitParserUCUM implements UnitParser
{
	private static Hashtable<String, Double> prefixTable;
    private static String termRegex = "[./]?[0-9 a-z A-Z \\[ \\] \\- \\+ \\! \\# \\* \\_]+";
    private static String intRegex = "[./]?[-+]?[0-9]+([-+][0-9])?";
    private static String funcRegex = "^[0-9 a-z A-Z]+\\(.+\\)$";
    private static Hashtable<String, Unit> unitTable = new Hashtable<String, Unit>();
    
    
    static
    {
        prefixTable = new Hashtable<String, Double>();
        
        // load prefix scale factors
        prefixTable.put("Y", 1e24);
        prefixTable.put("Z", 1e21);
        prefixTable.put("E", 1e18);
        prefixTable.put("P", 1e15);
        prefixTable.put("T", 1e12);
        prefixTable.put("G", 1e9);
        prefixTable.put("M", 1e6);
        prefixTable.put("k", 1e3);
        prefixTable.put("h", 1e2);
        prefixTable.put("da", 1e1);
        prefixTable.put("d", 1e-1);
        prefixTable.put("c", 1e-2);
        prefixTable.put("m", 1e-3);
        prefixTable.put("u", 1e-6);
        prefixTable.put("n", 1e-9);
        prefixTable.put("p", 1e-12);
        prefixTable.put("f", 1e-15);
        prefixTable.put("a", 1e-18);
        prefixTable.put("z", 1e-21);
        prefixTable.put("y", 1e-24);
    }
    
    
    public UnitParserUCUM()
    {
        if (unitTable.isEmpty())
            this.preloadUCUMUnits();
    }
    
    
    /**
     * Call this to get a Unit object corresponding to the given ucum string
     */
    public Unit getUnit(String ucumDef)
    {
        ucumDef = ucumDef.trim();
        
        // first check if we already have it in the table
        Unit uom = unitTable.get(ucumDef);
        if (uom != null)
            return uom.copy();        
        
        // otherwise it is probably a composed unit so create a new one
        uom = new Unit();
        uom.setExpression(ucumDef);
        
        // check if there is a function (special units)
        if (ucumDef.matches(funcRegex))
        {
            int openBracketIndex = ucumDef.indexOf('(');
            int spaceIndex = ucumDef.indexOf(' ');
            int closeBracketIndex = ucumDef.indexOf(')');
            
            // extract function name, scale and term
            String funcName = ucumDef.substring(0, openBracketIndex);            
            String scale = ucumDef.substring(openBracketIndex+1, spaceIndex);
            ucumDef = ucumDef.substring(spaceIndex+1, closeBracketIndex);
            
            // create function
            UnitFunction func = UnitFunction.createFunction(funcName, Double.parseDouble(scale));
            uom.setFunction(func);
        }
        
        // separate tokens between '/' and '.'
        Pattern pattern = Pattern.compile(termRegex);
        Matcher matcher = pattern.matcher(ucumDef);
        
        // take care of all following tokens
        while (matcher.find())
        {
            // get matching substring
            String token = matcher.group();
            //System.out.println(token);
            
            // detect if integer number
            if (token.matches(intRegex))
                parseNumber(token, uom);
            
            else if (token.contains("10*"))
                parsePower10(token, uom);
            
            else
                parseUnitCode(token, uom);
        }        
        
        return uom;
    }
    
    
    private void parseUnitCode(String token, Unit uom)
    {
        Unit unit = null;
        double power = 1.0;
        double prefixScale = 1.0;
        
        // extract trailing power
        int expIndex = this.findExponentIndex(token);
        String expString = token.substring(expIndex);
        String unitString = token.substring(0, expIndex);
        if (expString.length() != 0)
            power = Double.parseDouble(expString);
        
        // see if we had '.' or '/'
        if (unitString.charAt(0) == '/')
        {
            power = -power;
            unitString = unitString.substring(1);
        }
        else if ((token.charAt(0) == '.'))
        {
            unitString = unitString.substring(1);
        }
                
        // first try all prefixes and see if they match
        boolean prefixFound = false;
        Enumeration<String> prefixList = prefixTable.keys();
        while (prefixList.hasMoreElements())
        {
            String prefix = prefixList.nextElement();
            if (unitString.startsWith(prefix))
            {
                // get remaining of string after prefix
                String unitAtom = unitString.substring(prefix.length());                
                                
                // only if remaining of string also matches a unit
                if (unitTable.containsKey(unitAtom))
                {
                    // get unit from table
                    unit = unitTable.get(unitAtom);
                    
                    // make sure the unit is metric
                    if (!unit.isMetric())
                        continue;
                    
                    // get prefix scale factor
                    prefixScale = prefixTable.get(prefix);                    
                    prefixFound = true;
                    
                    break;
                }
            }
        }
        
        // get unit if no prefix was found
        if (prefixFound)
            unit = unit.copy();
        else
        {
            if (!unitTable.containsKey(unitString))
                throw new IllegalStateException("Unknown unit: " + unitString);
            
            unit = unitTable.get(unitString).copy();
        }
        
        // update unit scale factor
        if (unit.function != null)
            unit.function.scaleFactor *= prefixScale;
        else
            unit.scaleToSI *= prefixScale;
        
        // raise unit to given power
        if (power != 1.0)
            unit.power(power);
        
        // combine unit with current uom
        uom.multiply(unit);
    }
    
    
    private int findExponentIndex(String token)
    {
        int i = token.length();
        char c;
        
        do
        {
            i--;
            
            if (i < 0)
                break;
            
            c = token.charAt(i);
        }
        while (c == '+' || c == '-' || (c >= '0' && c <= '9'));        
        
        return i+1;
    }
    
    
    private void parsePower10(String token, Unit uom)
    {
        String powString = token.substring(token.indexOf('*')+1); 
        double power = Double.parseDouble(powString);
        
        if (token.charAt(0) == '/')
            uom.scaleToSI /= Math.pow(10, power);
        else
            uom.scaleToSI *= Math.pow(10, power);        
    }
    
    
    private void parseNumber(String token, Unit uom)
    {
        double val;
        
        if (token.charAt(0) == '/')
            val = 1.0 / Double.parseDouble(token.substring(1));            
        else if ((token.charAt(0) == '.'))
            val = Double.parseDouble(token.substring(1));
        else
            val = Double.parseDouble(token);           
        
        uom.scaleToSI *= val;
    }


    public static Hashtable<String, Unit> getUnitTable()
    {
        if (unitTable.isEmpty())
            new UnitParserUCUM().preloadUCUMUnits();
        
        return unitTable;
    }
    
    
    /**
     * Call this method to preload all units defined in UCUM essence
     */
    public void preloadUCUMUnits()
    {
        try
        {
            // load UCUM essence file
            URL url = UnitParserUCUM.class.getResource("ucum-essence.xml");
            DOMHelper dom = new DOMHelper(url.toString(), false);
                    
            // read all base units first
            NodeList baseUnitList = dom.getElements("base-unit");
            for (int i = 0; i < baseUnitList.getLength(); i++)
            {
                Element unitElt = (Element) baseUnitList.item(i);
                String unitCode = dom.getAttributeValue(unitElt, "Code");
                String unitName = dom.getElementValue(unitElt, "name");                
                String unitSymbol = readPrintSymbol(dom, unitElt);
                String property = dom.getElementValue(unitElt, "property");
                
                Unit unit = new Unit();
                unit.setName(unitName);
                unit.setCode(unitCode);
                unit.setPrintSymbol(unitSymbol);
                unit.setExpression(unitCode);
                unit.setProperty(property);
                unit.setMetric(true);
                
                if (unitCode.equals("m"))
                    unit.setMeter(1.0);
                else if (unitCode.equals("s"))
                    unit.setSecond(1.0);                
                else if (unitCode.equals("rad"))
                    unit.setRadian(1.0);
                else if (unitCode.equals("K"))
                    unit.setKelvin(1.0);                
                else if (unitCode.equals("cd"))
                    unit.setCandela(1.0);
                else if (unitCode.equals("g"))
                {
                    unit.setKilogram(1.0);
                    unit.setScaleToSI(1e-3);
                }
                else if (unitCode.equals("C"))
                {
                    unit.setAmpere(1.0);
                    unit.setSecond(1.0);
                }
                
                unitTable.put(unitCode, unit);
                //System.out.println(unit);
            }
            
            // process all other units
            NodeList unitList = dom.getElements("unit");
            for (int i = 0; i < unitList.getLength(); i++)
            {
                Element unitElt = (Element) unitList.item(i);
                String unitCode = dom.getAttributeValue(unitElt, "Code");
                String unitName = dom.getElementValue(unitElt, "name");
                String unitString = dom.getAttributeValue(unitElt, "value/@Unit");
                String isMetric = dom.getAttributeValue(unitElt, "isMetric");
                String unitSymbol = readPrintSymbol(dom, unitElt);
                String property = dom.getElementValue(unitElt, "property");
    
                // skip units that we already have and 10*
                if (unitCode.startsWith("10"))
                    continue;
                if (unitTable.get(unitCode) != null)
                    continue;
    
                Unit ucumUnit;
                try
                {
                    ucumUnit = getUnit(unitString);
                    ucumUnit.setCode(unitCode);
                    ucumUnit.setPrintSymbol(unitSymbol);
                    ucumUnit.setName(unitName);
                    ucumUnit.setProperty(property);
    
                    if (isMetric != null && isMetric.equals("yes"))
                        ucumUnit.setMetric(true);
                }
                catch (RuntimeException e)
                {
                    System.out.println(unitCode + " = ? [ERROR]");
                    continue;
                }
    
                String value = dom.getAttributeValue(unitElt, "value/@value");
                if (value != null)
                {
                    double val = Double.parseDouble(value);
                    ucumUnit.multiply(val);
                }
                
                unitTable.put(unitCode, ucumUnit);
                //System.out.println(ucumUnit);
            }
        }
        catch (DOMHelperException e)
        {
            ExceptionSystem.display(e);
        }
    }
    
    
    private String readPrintSymbol(DOMHelper dom, Element unitElt)
    {
        Element printSymElt = dom.getElement(unitElt, "printSymbol");
        
        // use code if printSymbol not present
        if (printSymElt == null)
        {
            String code = dom.getAttributeValue(unitElt, "Code");
            // remove brackets if present
            if (code.matches("^\\[.*\\]$"))
                code = code.substring(1, code.length()-1);                        
            return code;
        }
                
        // if there are some HTML tags inside skip them
        if (dom.existElement(printSymElt, "*"))
        {
            StringBuffer buf = new StringBuffer();
            Element elt = dom.getElement(unitElt, "printSymbol");
            do
            {
                if (elt.getLocalName().equalsIgnoreCase("sub"))
                    buf.append("_");
                buf.append(dom.getElementValue(elt, ""));
                elt = dom.getFirstChildElement(elt);
            }
            while (elt != null);
            return buf.toString();
        }
        
        // otherwise just return printSymbol value
        return dom.getElementValue(unitElt, "printSymbol");
    }
}