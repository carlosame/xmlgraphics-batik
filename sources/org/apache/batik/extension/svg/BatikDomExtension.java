/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.dom.svg.ExtensibleSVGDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.DomExtension;
import org.apache.batik.dom.AbstractDocument;

import org.apache.batik.css.value.DefaultSystemColorResolver;
import org.apache.batik.css.svg.SimpleColorFactory;
import org.apache.batik.css.svg.OpacityFactory;

/**
 * This is a Service interface for classes that want to extend the
 * functionality of the Dom, to support new tags in the rendering tree.
 */
public class BatikDomExtension 
    implements DomExtension, BatikExtConstants {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority() { return 1f; }

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor() {
        return "Thomas DeWeese";
    }

    /**
     * This should contain a contact address (usually an e-mail address).
     */
    public String getContactAddress() {
        return "deweese@apache.org";
    }

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL() {
        return "http://xml.apache.org/batik";
    }

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription() {
        return "Example extension to standard SVG shape tags";
    }

    /**
     * This method should update the DomContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The DomContext instance to be updated
     */
    public void registerTags(ExtensibleSVGDOMImplementation di) {
        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_REGULAR_POLYGON_TAG,
             new BatikRegularPolygonElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_STAR_TAG,
             new BatikStarElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG,
             new BatikHistogramNormalizationElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_MULTI_IMAGE_TAG,
             new BatikMultiImageElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_SOLID_COLOR_TAG,
             new SolidColorElementFactory());

        di.registerCustomCSSValueFactory
            (new SimpleColorFactory(null, BATIK_EXT_SOLID_COLOR_PROPERTY, 
                                    new DefaultSystemColorResolver()));

        di.registerCustomCSSValueFactory
            (new OpacityFactory(null, BATIK_EXT_SOLID_OPACITY_PROPERTY));

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_COLOR_SWITCH_TAG,
             new ColorSwitchElementFactory());
    }

    /**
     * To create a 'regularPolygon' element.
     */
    protected static class BatikRegularPolygonElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikRegularPolygonElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikRegularPolygonElement
                (prefix, (AbstractDocument)doc);
        }
    }


    /**
     * To create a 'star' element.
     */
    protected static class BatikStarElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikStarElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikStarElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'histogramNormalization' element.
     */
    protected static class BatikHistogramNormalizationElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikHistogramNormalizationElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikHistogramNormalizationElement
                (prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'multiImage' element.
     */
    protected static class BatikMultiImageElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikMultiImageElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikMultiImageElement
                (prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'solidColor' element.
     */
    protected static class SolidColorElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public SolidColorElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SolidColorElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'solidColor' element.
     */
    protected static class ColorSwitchElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public ColorSwitchElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new ColorSwitchElement(prefix, (AbstractDocument)doc);
        }
    }

}
