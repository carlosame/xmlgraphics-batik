/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Rectangle;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;

/**
 * This is an abstract base class that takes care of most of the
 * normal issues surrounding the implementation of the CachableRed
 * (RenderedImage) interface.  It tries to make no assumptions about
 * the subclass implementation.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public abstract class AbstractTiledRed 
    extends    AbstractRed
    implements TileGenerator {

    private TileStore tiles;

    private static int defaultTileSize = 128;
    public static int getDefaultTileSize() { return defaultTileSize; }

    /**
     * void constructor. The subclass must call one of the
     * flavors of init before the object becomes usable.
     * This is useful when the proper parameters to the init
     * method need to be computed in the subclasses constructor.
     */
    protected AbstractTiledRed() { }


    /**
     * Construct an Abstract RenderedImage from a bounds rect and props
     * (may be null).  The srcs Vector will be empty.
     * @param bounds this defines the extent of the rable in the
     * user coordinate system.
     * @param props this initializes the props Map (may be null)
     */
    protected AbstractTiledRed(Rectangle bounds, Map props) {
        super(bounds, props);
    }

    /**
     * Construct an Abstract RenderedImage from a source image and
     * props (may be null).
     * @param src will be the first (and only) member of the srcs
     * Vector. Src is also used to set the bounds, ColorModel,
     * SampleModel, and tile grid offsets.
     * @param props this initializes the props Map.  */
    protected AbstractTiledRed(CachableRed src, Map props) {
        super(src, props);
    }

    /**
     * Construct an Abstract RenderedImage from a source image, bounds
     * rect and props (may be null).
     * @param src will be the first (and only) member of the srcs
     * Vector. Src is also used to set the ColorModel, SampleModel,
     * and tile grid offsets.
     * @param bounds The bounds of this image.
     * @param props this initializes the props Map.  */
    protected AbstractTiledRed(CachableRed src, Rectangle bounds, Map props) {
        super(src, bounds, props);
    }

    /**
     * Construct an Abstract RenderedImage from a source image, bounds
     * rect and props (may be null).
     * @param src will be the first (and only) member of the srcs
     * Vector. Src is also used to set the ColorModel, SampleModel,
     * and tile grid offsets.
     * @param bounds The bounds of this image.
     * @param cm The ColorModel to use. If null it will default to
     * ComponentColorModel.
     * @param sm The sample model to use. If null it will construct
     * a sample model the matches the given/generated ColorModel and is
     * the size of bounds.
     * @param props this initializes the props Map.  */
    protected AbstractTiledRed(CachableRed src, Rectangle bounds,
                          ColorModel cm, SampleModel sm,
                          Map props) {
        super(src, bounds, cm, sm, props);
    }

    /**
     * Construct an Abstract Rable from a bounds rect and props
     * (may be null).  The srcs Vector will be empty.
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be Filter otherwise an error
     * will be thrown.
     * @param bounds this defines the extent of the rable in the
     * user coordinate system.
     * @param cm The ColorModel to use. If null it will default to
     * ComponentColorModel.
     * @param sm The sample model to use. If null it will construct
     * a sample model the matches the given/generated ColorModel and is
     * the size of bounds.
     * @param tileGridXOff The x location of tile 0,0.
     * @param tileGridYOff The y location of tile 0,0.
     * @param props this initializes the props Map.
     */
    protected AbstractTiledRed(CachableRed src, Rectangle bounds,
                          ColorModel cm, SampleModel sm,
                          int tileGridXOff, int tileGridYOff,
                          Map props) {
        super(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }

    /**
     * This is one of two basic init function (this is for single
     * source rendereds).
     * It is provided so subclasses can compute various values
     * before initializing all the state in the base class.
     * You really should call this method before returning from
     * your subclass constructor.
     *
     * @param src    The source for the filter
     * @param bounds The bounds of the image
     * @param cm     The ColorModel to use. If null it defaults to
     *               ComponentColorModel/ src's ColorModel.
     * @param sm     The Sample modle to use. If this is null it will
     *               use the src's sample model if that is null it will
     *               construct a sample model that matches the ColorModel
     *               and is the size of the whole image.
     * @param tileGridXOff The x location of tile 0,0.
     * @param tileGridYOff The y location of tile 0,0.
     * @param props  Any properties you want to associate with the image.
     */
    protected void init(CachableRed src, Rectangle   bounds,
                        ColorModel  cm,   SampleModel sm,
                        int tileGridXOff, int tileGridYOff,
                        Map props) {
        super.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        
        tiles = createTileStore();
    }

    /**
     * Construct an Abstract Rable from a List of sources a bounds rect
     * and props (may be null).
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be CachableRed otherwise an error
     * will be thrown.
     * @param bounds this defines the extent of the rendered in pixels
     * @param props this initializes the props Map.
     */
    protected AbstractTiledRed(List srcs, Rectangle bounds, Map props) {
        super(srcs, bounds, props);
    }

    /**
     * Construct an Abstract RenderedImage from a bounds rect,
     * ColorModel (may be null), SampleModel (may be null) and props
     * (may be null).  The srcs Vector will be empty.
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be CachableRed otherwise an error
     * will be thrown.
     * @param bounds this defines the extent of the rendered in pixels
     * @param cm The ColorModel to use. If null it will default to
     * ComponentColorModel.
     * @param sm The sample model to use. If null it will construct
     * a sample model the matches the given/generated ColorModel and is
     * the size of bounds.
     * @param props this initializes the props Map.
     */
    protected AbstractTiledRed(List srcs, Rectangle bounds,
                          ColorModel cm, SampleModel sm,
                          Map props) {
        super(srcs, bounds, cm, sm, props);
    }

    /**
     * Construct an Abstract RenderedImage from a bounds rect,
     * ColorModel (may be null), SampleModel (may be null), tile grid
     * offsets and props (may be null).  The srcs Vector will be
     * empty.
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be CachableRed otherwise an error
     * will be thrown.
     * @param bounds this defines the extent of the rable in the
     * user coordinate system.
     * @param cm The ColorModel to use. If null it will default to
     * ComponentColorModel.
     * @param sm The sample model to use. If null it will construct
     * a sample model the matches the given/generated ColorModel and is
     * the size of bounds.
     * @param tileGridXOff The x location of tile 0,0.
     * @param tileGridYOff The y location of tile 0,0.
     * @param props this initializes the props Map.
     */
    protected AbstractTiledRed(List srcs, Rectangle bounds,
                          ColorModel cm, SampleModel sm,
                          int tileGridXOff, int tileGridYOff,
                          Map props) {
        super(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }

    /**
     * This is the basic init function.
     * It is provided so subclasses can compute various values
     * before initializing all the state in the base class.
     * You really should call this method before returning from
     * your subclass constructor.
     *
     * @param srcs   The list of sources
     * @param bounds The bounds of the image
     * @param cm     The ColorModel to use. If null it defaults to
     *               ComponentColorModel.
     * @param sm     The Sample modle to use. If this is null it will
     *               construct a sample model that matches the ColorModel
     *               and is the size of the whole image.
     * @param tileGridXOff The x location of tile 0,0.
     * @param tileGridYOff The y location of tile 0,0.
     * @param props  Any properties you want to associate with the image.
     */
    protected void init(List srcs, Rectangle bounds,
                        ColorModel cm, SampleModel sm,
                        int tileGridXOff, int tileGridYOff,
                        Map props) {
        super.init(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        tiles = createTileStore();
    }


    protected TileStore createTileStore() {
        return TileCache.getTileGrid(this, this);
    }

    public WritableRaster copyData(WritableRaster wr) {
        copyToRasterByBlocks(wr);
        return wr;
    }

    public Raster getTile(int x, int y) {
        return tiles.getTile(x, y);
    }

    public Raster genTile(int x, int y) {
        WritableRaster wr = makeTile(x, y);
        genRect(wr); 
        return wr;
    }

    public abstract void genRect(WritableRaster wr);
    // { copyToRaster(wr); }


    public void setTile(int x, int y, Raster ras) {
        tiles.setTile(x, y, ras);
    }

    /**
     * This class is responsible for breaking up a block of tiles into
     * a set of smaller requests that are a large as possible without
     * rerequesting significant numbers of tiles that are already
     * available 
     */

    public static class TileBlock {
        int occX, occY, occW, occH;
        int xOff, yOff, w, h, benefit;
        boolean [] occupied;

        /**
         * Construct a tile block this represents a block of contigous
         * tiles.
         * @param xloc The x index of left edge of the tile block.
         * @param yloc The y index of top edge of the tile block.
         * @param w    The number of tiles across in the block
         * @param h    The number of tiles down  the block
         * @param occupied Which entries in the block are already
         *                 computed.
         */
        TileBlock(int occX, int occY, int occW, int occH, boolean [] occupied,
                  int xOff, int yOff, int w, int h) {
            this.occX = occX;
            this.occY = occY;
            this.occW = occW;
            this.occH = occH;
            this.xOff = xOff;
            this.yOff = yOff;
            this.w    = w   ;
            this.h    = h   ;
            this.occupied = occupied;



            // System.out.println("Block: [" + 
            //                    xloc + "," + yloc + ","  + 
            //                    w + "," + h + "]");
            for (int y=0; y<h; y++)
                for (int x=0; x<w; x++)                
                    if (!occupied[x+xOff+occW*(y+yOff)])
                        benefit++;
        }

        /**
         * Really nice to string that outlines what tiles are filled
         * and what region this block covers.  Really useful for
         * debugging the TileBlock stuff.
         */
        public String toString() {
            String ret = "";
            for (int y=0; y<occH; y++) {
                for (int x=0; x<occW+1; x++) {
                    if ((x==xOff) || (x==xOff+w)) {
                        if ((y==yOff) || (y==yOff+h-1))
                            ret += "+";
                        else  if ((y>yOff) && (y<yOff+h-1))
                            ret += "|";
                        else 
                            ret += " ";
                    } 
                    else if ((y==yOff)     && (x> xOff) && (x < xOff+w))
                        ret += "-";
                    else if ((y==yOff+h-1) && (x> xOff) && (x < xOff+w))
                        ret += "_";
                    else
                        ret += " ";

                    if (x== occW)
                        continue;

                    if (occupied[x+y*occW]) 
                        ret += "*";
                    else
                        ret += ".";
                }
                ret += "\n";
            }
            return ret;
        }

        /** 
         * Return the x location of this block of tiles
         */
        int getXLoc()    { return occX+xOff; }
        /** 
         * Return the y location of this block of tiles
         */
        int getYLoc()    { return occY+yOff; }
        /** 
         * Return the width of this block of tiles
         */
        int getWidth()   { return w; }
        /** 
         * Return the height of this block of tiles
         */
        int getHeight()  { return h; }

        /** 
         * Return the number of new tiles computed.
         */
        int getBenefit() { return benefit; }
        
        /** 
         * Return the approximate amount of work required to compute
         * those tiles.
         */
        int getWork()    { return w*h+1; }

        /**
         * Returns the total amount of work for the array of tile blocks
         */
        static int getWork(TileBlock [] blocks) { 
            int ret=0;
            for (int i=0; i<blocks.length; i++) 
                ret += blocks[i].getWork();
            return ret;
        }

        /**
         * Returnes an optimized list of TileBlocks to generate that
         * tries to minimize the work to benefit ratio, for the set of
         * blocks defined by this block.
         */
        TileBlock [] getBestSplit() {
            if (simplify())
                return null;
            
            // Optimal split already...
            if (benefit == w*h)
                return new TileBlock [] { this };

            return splitOneGo();

            /*
            TileBlock [] bestSplit = new TileBlock [] { this };
            int bestWork = getWork();

            // Otherwise go through each occupied block try splitting
            // at each and see what the best split for each is.
            for (int y=0; y<h; y++) {
                for (int x=0; x<w; x++) {
                    if (Thread.currentThread().isInterrupted())
                        return null;

                    if (!occupied[x+xOff+occW*(y+yOff)]) 
                        continue;

                    TileBlock [] split = getBestSplitAt(x+xOff, y+yOff);
                    if (split == null) continue;

                    int cWork = getWork(split);
                    if (cWork < bestWork) {
                        bestSplit = split;
                        bestWork  = cWork;
                        if (bestWork <= (benefit+2)*1.1)
                            return bestSplit;
                    }
                }
            }
            */
        }

        public TileBlock [] splitOneGo() {
            boolean [] filled = (boolean [])occupied.clone();
            Vector items = new Vector();
            for (int y=yOff; y<yOff+h; y++)
                for (int x=xOff; x<xOff+w; x++) {
                    if (!filled[x+y*occW]) {
                        // We have an unfilled tile slot, so first we
                        // figure out how long the slot is in this row.
                        int cw = xOff+w-x;
                        for (int cx=x; cx<x+cw; cx++)
                            if (filled[cx+y*occW])
                                cw = cx-x;
                            else
                                filled[cx+y*occW] = true;  // fill as we go..

                        // Then we check the next rows until we hit
                        // a row that doesn't have this slot all free.
                        // at which point we stop...
                        int ch=1;
                        for (int cy=y+1; cy<yOff+h; cy++) {
                            int cx=x;
                            for (; cx<x+cw; cx++) 
                                if (filled[cx+cy*occW])
                                    break;

                            // Partial row so bail (we'll get it later..)
                            if (cx != x+cw)
                                break;

                            // Fill in the slot since we will use it...
                            for (cx=x; cx<x+cw; cx++) 
                                filled[cx+cy*occW] = true;
                            ch++;
                        }
                        items.add(new TileBlock(occX, occY, occW, occH, 
                                                occupied, x, y, cw, ch));
                        x+=(cw-1);
                    }
                }

            TileBlock [] ret = new TileBlock[items.size()];
            Iterator iter = items.iterator();
            int i=0;
            while (iter.hasNext())
                ret[i++] = (TileBlock)iter.next();
            return ret;
        }

         public boolean simplify() {
             for (int y=0; y<h; y++) {
                 int x;
                 for (x=0; x<w; x++)                
                     if (!occupied[x+xOff+occW*(y+yOff)])
                         break;
                 if (x!=w) break;

                 // Fully occupied row so remove it.
                 yOff++;
                 y--;
                 h--;
             }

             // return true if we were simplified out of existance.
             if (h==0) return true;

             // If we make it past here we must have at least one good block.

             for (int y=h-1; y>=0; y--) {
                 int x;
                 for (x=0; x<w; x++)                
                     if (!occupied[x+xOff+occW*(y+yOff)])
                         break;
                 if (x!=w) break;

                 // Fully occupied row so remove it.
                 h--;
             }

             for (int x=0; x<w; x++) {
                 int y;
                 for (y=0; y<h; y++)
                     if (!occupied[x+xOff+occW*(y+yOff)])
                         break;
                 if (y!=h) break;

                 // Fully occupied Col so remove it. 
                 xOff++;
                 x--;
                 w--;
             }

             for (int x=w-1; x>=0; x--) {
                 int y;
                 for (y=0; y<h; y++)
                     if (!occupied[x+xOff+occW*(y+yOff)])
                         break;
                 if (y!=h) break;

                 // Fully occupied Col so remove it. 
                 w--;
             }

             return false;
         }

        /** What follows in residual code that tries to take a more
         * global and sophisticated approach balancing block size
         * vs. regeneration of tiles.  I have deprecated this in favor
         * of the splitOneGo code that is very straight forward,
         * but may not give quiet as good a set of splits (although
         * in practice I think it's results are similar if not better
         * in some important common cases).
         */

//          public static TileBlock merge(TileBlock tb1, TileBlock tb2) {
//              if ((tb1.occX     != tb2.occX) ||
//                  (tb1.occY     != tb2.occY) ||
//                  (tb1.occW     != tb2.occW) ||
//                  (tb1.occH     != tb2.occH) ||
//                  (tb1.occupied != tb2.occupied))
//                  throw new IllegalArgumentException
//                      ("To merge blocks they must be from the same grid");

//              if (tb1.xOff == tb2.xOff) {
//                  // Left edges aligned...
//                  if (tb1.w != tb2.w) 
//                      throw new IllegalArgumentException
//                          ("Blocks must match on a side");
//                  if (tb1.yOff+tb1.h == tb2.yOff) 
//                      return new TileBlock
//                          (tb1.occX, tb1.occY, tb1.occW, tb1.occH, tb1.occupied,
//                           tb1.xOff, tb1.yOff, tb1.w, tb1.h+tb2.h);
//                  else if (tb2.yOff+tb2.h == tb1.yOff) 
//                      return new TileBlock
//                          (tb1.occX, tb1.occY, tb1.occW, tb1.occH, tb1.occupied,
//                           tb2.xOff, tb2.yOff, tb2.w, tb2.h+tb1.h);
//                  else
//                      throw new IllegalArgumentException
//                          ("Blocks top and bottom don't meet");
//              }

//              if (tb1.yOff == tb2.yOff) {
//                  // top edges aligned...
//                  if (tb1.h != tb2.h) 
//                      throw new IllegalArgumentException
//                          ("Blocks must match on a side");
//                  if (tb1.xOff+tb1.w == tb2.xOff) 
//                      return new TileBlock
//                          (tb1.occX, tb1.occY, tb1.occW, tb1.occH, tb1.occupied,
//                           tb1.xOff, tb1.yOff, tb1.w+tb2.w, tb1.h);
//                  else if (tb2.xOff+tb2.w == tb1.xOff) 
//                      return new TileBlock
//                          (tb1.occX, tb1.occY, tb1.occW, tb1.occH, tb1.occupied,
//                           tb2.xOff, tb2.yOff, tb2.w+tb1.w, tb2.h);
//                  else
//                      throw new IllegalArgumentException
//                          ("Blocks left and right don't meet");
//              }

//              throw new IllegalArgumentException
//                  ("Blocks don't align in the grid");
//          }

//          /**
//           * Computes a good set of TileBlocks assuming a split
//           * at location x,y in the current block.  */
//          TileBlock [] getBestSplitAt(int x, int y) {
//              TileBlock [] split = splitAtH(x, y);
//              TileBlock [][] bestSplits = new TileBlock[split.length][];
//              int count=0;
//              int workH =0;
//              for (int i=0; i<split.length; i++) {
//                  bestSplits[i]  =     split [i].getBestSplit();
//                  if (bestSplits[i] == null)
//                      return null;

//                  count         += bestSplits[i].length;
//                  workH         += getWork(bestSplits[i]);
//              }

//              if (workH > benefit+2) {
//                  // H split is not quite optimal check V split.
//                  TileBlock [] splitV = splitAtV(x, y);
//                  TileBlock [][] bestSplitsV = new TileBlock[splitV.length][];
//                  int countV=0;
//                  int workV =0;
//                  for (int i=0; i<splitV.length; i++) {
//                      bestSplitsV[i]  =     splitV [i].getBestSplit();
//                      if (bestSplitsV[i] == null)
//                          return null;

//                      countV         += bestSplitsV[i].length;
//                      workV          += getWork(bestSplitsV[i]);
//                  }
//                  if (workV < workH) {
//                      // System.out.println("Using VSplit");
//                      split      = splitV;
//                      bestSplits = bestSplitsV;
//                      count      = countV;
//                  }
//              }

//              TileBlock[] ret = new TileBlock[count];
//              count =0;
//              for (int i=0; i<split.length; i++) {
//                  System.arraycopy(bestSplits[i], 0, 
//                                   ret, count, bestSplits[i].length);
//                  count += bestSplits[i].length;
//              }
//              return ret;
//          }

//          /**
//           * This splits the current block at x, y
//           * We always split above row y and below y 
//           * the the strip to the left and right of x:
//           *<pre>
//           *            Split 1
//           * __________________________________
//           *    Split 3 | x,y | Split 4
//           * __________________________________
//           *            Split 2
//           *</pre>
//           */
//          TileBlock [] splitAtH(int x, int y) {
//              if ((x<-1) || (x>occW)) 
//                  throw new IllegalArgumentException
//                      ("X out of range: " + x);
//              if ((y<-1) || (y>occH)) 
//                  throw new IllegalArgumentException
//                      ("Y out of range: " + y);

//              int splits = 0;

//              TileBlock block1 = null;
//              if (y > yOff) {
//                  block1 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         xOff, yOff, w, y-yOff);
//                  if (block1.simplify())
//                      block1=null;
//                  else
//                      splits++;
//              }

//              TileBlock block2 = null;
//              if (y<yOff+h-1) {
//                  block2 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         xOff, y+1, w, (yOff+h)-(y+1));
//                  if (block2.simplify())
//                      block2=null;
//                  else
//                      splits++;
//              }

//              TileBlock block3 = null;
//              if (x > xOff) {
//                  block3 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         xOff, y, x-xOff, 1);
//                  if (block3.simplify())
//                      block3=null;
//                  else
//                      splits++;
//              }

//              TileBlock block4 = null;
//              if (x < xOff+w-1) {
//                  block4 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         x+1, y, (xOff+w)-(x+1), 1);
//                  if (block4.simplify())
//                      block4=null;
//                  else
//                      splits++;
//              }

//              TileBlock [] ret = new TileBlock[splits];
//              int count=0;
//              if (block1 != null) ret[count++] = block1;
//              if (block2 != null) ret[count++] = block2;
//              if (block3 != null) ret[count++] = block3;
//              if (block4 != null) ret[count++] = block4;

//              return ret;
//          }

//          /**
//           * This splits the current block at x, y
//           * We always split Left and right of x and
//           * the the columns above and below y,
//           *<pre>
//           *            | Split 3 |
//           *            |_________|
//           *    Split 1 |   x,y   | Split 2
//           *            |_________|
//           *            | Split 4 |
//           *</pre>
//           */
//          TileBlock [] splitAtV(int x, int y) {
//              if ((x<-1) || (x>occW)) 
//                  throw new IllegalArgumentException
//                      ("X out of range: " + x);
//              if ((y<-1) || (y>occH)) 
//                  throw new IllegalArgumentException
//                      ("Y out of range: " + y);

//              int splits = 0;

//              TileBlock block1 = null;
//              if ((x > xOff)) {
//                  block1 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         xOff, yOff, x-xOff, h);
//                  if (block1.simplify())
//                      block1=null;
//                  else
//                      splits++;
//              }

//              TileBlock block2 = null;
//              if (x<xOff+w-1) {
//                  block2 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         x+1, yOff, (xOff+w)-(x+1), h);
//                  if (block2.simplify())
//                      block2=null;
//                  else
//                      splits++;
//              }

//              TileBlock block3 = null;
//              if (y > yOff) {
//                  block3 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         x, yOff, 1, y-yOff);
//                  if (block3.simplify())
//                      block3=null;
//                  else
//                      splits++;
//              }

//              TileBlock block4 = null;
//              if (y < yOff+h-1) {
//                  block4 = new TileBlock(occX, occY, occW, occH, occupied,
//                                         x, y+1, 1, (yOff+h)-(y+1));
//                  if (block4.simplify())
//                      block4=null;
//                  else
//                      splits++;
//              }

//              TileBlock [] ret = new TileBlock[splits];
//              int count=0;
//              if (block1 != null) ret[count++] = block1;
//              if (block2 != null) ret[count++] = block2;
//              if (block3 != null) ret[count++] = block3;
//              if (block4 != null) ret[count++] = block4;

//              return ret;
//          }


    }


    public void copyToRasterByBlocks(WritableRaster wr) {
        final boolean is_INT_PACK = 
            GraphicsUtil.is_INT_PACK_Data(getSampleModel(), false);

        Rectangle bounds = getBounds();
        Rectangle wrR    = wr.getBounds();

        int tx0 = getXTile(wrR.x);
        int ty0 = getYTile(wrR.y);
        int tx1 = getXTile(wrR.x+wrR.width -1);
        int ty1 = getYTile(wrR.y+wrR.height-1);

        if (tx0 < minTileX) tx0 = minTileX;
        if (ty0 < minTileY) ty0 = minTileY;

        if (tx1 >= minTileX+numXTiles) tx1 = minTileX+numXTiles-1;
        if (ty1 >= minTileY+numYTiles) ty1 = minTileY+numYTiles-1;

        if ((tx1 < tx0) || (ty1 < ty0))
            return;

        int insideTx0 = tx0;
        int insideTx1 = tx1;

        int insideTy0 = ty0;
        int insideTy1 = ty1;

        // Now figure out what tiles lie completely inside wr...
        int tx, ty;
        tx = tx0*tileWidth+tileGridXOff;
        if ((tx < wrR.x)  && (bounds.x != wrR.x)) 
            // Partial tile off the left.
            insideTx0++;

        ty= ty0*tileHeight+tileGridYOff;
        if ((ty < wrR.y) && (bounds.y != wrR.y))
            // Partial tile off the top.
            insideTy0++;

        tx= (tx1+1)*tileWidth+tileGridXOff-1;
        if ((tx >= (wrR.x+wrR.width)) && 
            ((bounds.x+bounds.width) != (wrR.x+wrR.width)))
            // Partial tile off right
            insideTx1--;

        ty= (ty1+1)*tileHeight+tileGridYOff-1;
        if ((ty >= (wrR.y+wrR.height)) &&
            ((bounds.y+bounds.height) != (wrR.y+wrR.height)))
            // Partial tile off bottom
            insideTy1--;

        int xtiles = insideTx1-insideTx0+1;
        int ytiles = insideTy1-insideTy0+1;
        boolean [] occupied = null;
        if ((xtiles > 0) && (ytiles > 0))
            occupied = new boolean[xtiles*ytiles];

        boolean [] got = new boolean[2*(tx1-tx0+1) + 2*(ty1-ty0+1)];
        int idx = 0;
        int numFound = 0;
        // Collect all the tiles that we currently have in cache...
        for (int y=ty0; y<=ty1; y++) {
            for (int x=tx0; x<=tx1; x++) {
                Raster ras = tiles.getTileNoCompute(x, y);
                boolean found = (ras != null);
                if ((y>=insideTy0) && (y<=insideTy1) &&
                    (x>=insideTx0) && (x<=insideTx1))
                    occupied[(x-insideTx0)+(y-insideTy0)*xtiles] = found;
                else
                    got[idx++] = found;

                if (!found) continue;

                numFound++;

                if (is_INT_PACK)
                    GraphicsUtil.copyData_INT_PACK(ras, wr);
                else
                    GraphicsUtil.copyData_FALLBACK(ras, wr);
            }
        }

        // System.out.println("Found: " + numFound + " out of " + 
        //                    ((tx1-tx0+1)*(ty1-ty0+1)));

        // Compute the stuff from the middle in the largest possible Chunks.
        if ((xtiles > 0) && (ytiles > 0)) {
            TileBlock block = new TileBlock
                (insideTx0, insideTy0, xtiles, ytiles, occupied,
                 0, 0, xtiles, ytiles);
            // System.out.println("Starting Splits");
            TileBlock [] blocks = block.getBestSplit();

            // System.out.println("Starting Computation: " + this);
            if (blocks != null)
                // System.out.println("Ending Splits: " + blocks.length);

                for (int i=0; i<blocks.length; i++) {
                    TileBlock curr = blocks[i];
                    
                    // System.out.println("Block " + i + ":\n" + curr);

                    int xloc = curr.getXLoc()*tileWidth +tileGridXOff;
                    int yloc = curr.getYLoc()*tileHeight+tileGridYOff;
                    Rectangle tb = new Rectangle(xloc, yloc,
                                                 curr.getWidth()*tileWidth,
                                                 curr.getHeight()*tileHeight);
                    tb = tb.intersection(bounds);

                    WritableRaster child = 
                        wr.createWritableChild(tb.x, tb.y, tb.width, tb.height,
                                               tb.x, tb.y, null);
                    // System.out.println("Computing : " + child);
                    genRect(child);

                    if (Thread.currentThread().isInterrupted())
                        return;
                }
            // Exception e= new Exception("Foo");
            // e.printStackTrace();
        }

        idx = 0;
        // Fill in the ones that weren't in the cache.
        for (ty=ty0; ty<=ty1; ty++) {

            for (tx=tx0; tx<=tx1; tx++) {
                // At least touch the tile...
                Raster ras = tiles.getTileNoCompute(tx, ty);

                if ((ty>=insideTy0) && (ty<=insideTy1) &&
                    (tx>=insideTx0) && (tx<=insideTx1)) {

                    if (ras != null) continue;

                    // Fill the tile from wr (since wr is full now
                    // at least in the middle).
                    WritableRaster tile = makeTile(tx, ty);
                    if (is_INT_PACK)
                        GraphicsUtil.copyData_INT_PACK(wr, tile);
                    else
                        GraphicsUtil.copyData_FALLBACK(wr, tile);

                    tiles.setTile(tx, ty, tile);
                }
                else {
                    if (got[idx++]) continue;

                    // System.out.println("Computing : " + x + "," + y);
                
                    ras = getTile(tx, ty);// Compute the tile..
                    if (Thread.currentThread().isInterrupted())
                        return;

                    if (is_INT_PACK)
                        GraphicsUtil.copyData_INT_PACK(ras, wr);
                    else
                        GraphicsUtil.copyData_FALLBACK(ras, wr);
                }
            }
        }

        // System.out.println("Ending Computation: " + this);
    }

    /**
     * Copies data from this images tile grid into wr.  wr may
     * extend outside the bounds of this image in which case the
     * data in wr outside the bounds will not be touched.
     * @param wr Raster to fill with image data.
     */
    public void copyToRaster(WritableRaster wr) {
        Rectangle wrR = wr.getBounds();
            
        int tx0 = getXTile(wrR.x);
        int ty0 = getYTile(wrR.y);
        int tx1 = getXTile(wrR.x+wrR.width -1);
        int ty1 = getYTile(wrR.y+wrR.height-1);

        if (tx0 < minTileX) tx0 = minTileX;
        if (ty0 < minTileY) ty0 = minTileY;

        if (tx1 >= minTileX+numXTiles) tx1 = minTileX+numXTiles-1;
        if (ty1 >= minTileY+numYTiles) ty1 = minTileY+numYTiles-1;

        final boolean is_INT_PACK = 
            GraphicsUtil.is_INT_PACK_Data(getSampleModel(), false);

        int xtiles = (tx1-tx0+1);
        boolean [] got = new boolean[xtiles*(ty1-ty0+1)];

        // Run through and get the tiles that are just sitting in the
        // cache...
        for (int y=ty0; y<=ty1; y++)
            for (int x=tx0; x<=tx1; x++) {
                Raster r = tiles.getTileNoCompute(x, y);
                if (r == null) continue; // Not there.

                got[x-tx0 + (y-ty0)*xtiles] = true;

                if (is_INT_PACK)
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                else
                    GraphicsUtil.copyData_FALLBACK(r, wr);
            }

        // Run through and pick up the ones we need to compute...
        for (int y=ty0; y<=ty1; y++)
            for (int x=tx0; x<=tx1; x++) {
                if (got[x-tx0 + (y-ty0)*xtiles]) continue; // already have.

                Raster r = getTile(x, y);
                if (is_INT_PACK)
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                else
                    GraphicsUtil.copyData_FALLBACK(r, wr);
            }
    }

}

