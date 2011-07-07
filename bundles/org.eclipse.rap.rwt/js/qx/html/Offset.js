/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define("qx.html.Offset",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /*
    Mozilla seems to be a little buggy here.
    Mozilla/5.0 (Windows; U; Windows NT 5.1; de-DE; rv:1.7.5) Gecko/20041108 Firefox/1.0

    It calculates some borders and/or paddings to the offsetProperties.
    */

    /**
     * TODOC
     *
     * @type static
     * @param vElement {var} TODOC
     * @return {void}
     * @signature function(el)
     */
    getLeft : qx.core.Variant.select("qx.client",
    {
      "gecko" : function(el)
      {
        var val = el.offsetLeft;
        var pa = el.parentNode;

        var pose = qx.html.Style.getStyleProperty(el, "position");
        var posp = qx.html.Style.getStyleProperty(pa, "position");

        // If element is positioned non-static: Substract the border of the element
        if (pose != "absolute" && pose != "fixed") {
          val -= qx.html.Style.getBorderLeft(pa);
        }

        // If parent is positioned static: Substract the border of the first
        // parent element which is ab positioned non-static.
        if (posp != "absolute" && posp != "fixed")
        {
          while (pa)
          {
            pa = pa.parentNode;

            if (!pa || typeof pa.tagName !== "string") {
              break;
            }

            var posi = qx.html.Style.getStyleProperty(pa, "position");

            if (posi == "absolute" || posi == "fixed")
            {
              val -= qx.html.Style.getBorderLeft(pa) + qx.html.Style.getPaddingLeft(pa);
              break;
            }
          }
        }

        return val;
      },

      "default" : function(el) {
        return el.offsetLeft;
      }
    }),


    /**
     * TODOC
     *
     * @type static
     * @param vElement {var} TODOC
     * @return {void}
     * @signature function(el)
     */
    getTop  : qx.core.Variant.select("qx.client",
    {
      "gecko" : function(el)
      {
        var val = el.offsetTop;
        var pa = el.parentNode;

        var pose = qx.html.Style.getStyleProperty(el, "position");
        var posp = qx.html.Style.getStyleProperty(pa, "position");

        // If element is positioned non-static: Substract the border of the element
        if (pose != "absolute" && pose != "fixed") {
          val -= qx.html.Style.getBorderTop(pa);
        }

        // If parent is positioned static: Substract the border of the first
        // parent element which is ab positioned non-static.
        if (posp != "absolute" && posp != "fixed")
        {
          while (pa)
          {
            pa = pa.parentNode;

            if (!pa || typeof pa.tagName !== "string") {
              break;
            }

            var posi = qx.html.Style.getStyleProperty(pa, "position");

            if (posi == "absolute" || posi == "fixed")
            {
              val -= qx.html.Style.getBorderTop(pa) + qx.html.Style.getPaddingTop(pa);
              break;
            }
          }
        }

        return val;
      },

      "default" : function(el) {
        return el.offsetTop;
      }
    })
  }
});
