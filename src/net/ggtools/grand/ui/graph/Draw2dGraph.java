// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2003, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.ggtools.grand.ui.graph;

import java.util.Collection;

import net.ggtools.grand.ui.Application;
import net.ggtools.grand.ui.widgets.CanvasScroller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.InputEvent;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.swt.SWT;

import sf.jzgraph.IVertex;

/**
 * @author Christophe Labouisse
 */
public class Draw2dGraph extends Panel implements SelectionManager {
    private final class ZoomListener extends KeyListener.Stub {
        private final float[] ZOOM_STEPS = {0.134217728f, 0.16777216f, 0.2097152f, 0.262144f,
                0.32768f, 0.4096f, 0.512f, 0.64f, 0.8f, 1.0f, 1.25f, 1.5625f, 1.953125f,
                2.44140625f};

        private int zoomStep = 9;

        private final Log log;

        private ZoomListener(Log log) {
            super();
            this.log = log;
        }

        {
            zoom = ZOOM_STEPS[zoomStep];
        }

        public void keyReleased(KeyEvent ke) {
            switch (ke.keycode) {
            case SWT.PAGE_DOWN:
                if (zoomStep > 0) {
                    setZoom(ZOOM_STEPS[--zoomStep]);
                }
                break;

            case SWT.PAGE_UP:
                if (zoomStep < ZOOM_STEPS.length - 1) {
                    setZoom(ZOOM_STEPS[++zoomStep]);
                }
                break;

            default:
                break;
            }
            log.trace("Zoom: " + zoom);
        }
    }

    private final class GraphMouseListener extends MouseListener.Stub {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.draw2d.MouseListener.Stub#mousePressed(org.eclipse.draw2d.MouseEvent)
         */
        public void mousePressed(MouseEvent me) {
            switch (me.button) {
            case (1):
                deselectAllNodes();
                me.consume();

            case (2):
                if (scroller != null) {
                    scroller.enterDragMode();
                }
                break;

            case (3):
                if (graphControler != null) {
                    graphControler.getDest().getContextMenu().setVisible(true);
                }
                break;
            }
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.draw2d.MouseListener.Stub#mouseReleased(org.eclipse.draw2d.MouseEvent)
         */
        public void mouseReleased(MouseEvent me) {
            switch (me.button) {
            case (1):
            case (2):
                if (scroller != null) {
                    scroller.leaveDragMode();
                }
                break;
            }
        }
    }

    private final class NodeMouseListener extends MouseListener.Stub {
        private final Log log = LogFactory.getLog(NodeMouseListener.class);

        private final Draw2dNode node;

        private NodeMouseListener(Draw2dNode node) {
            super();
            this.node = node;
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.draw2d.MouseListener#mouseDoubleClicked(org.eclipse.draw2d.MouseEvent)
         */
        public void mouseDoubleClicked(MouseEvent me) {
            log.trace("Double click on " + me.button);
            switch (me.button) {
            case (1): {
                final boolean addToSelection;
                if ((me.getState() & InputEvent.CONTROL) == 0) {
                    addToSelection = false;
                }
                else {
                    addToSelection = true;
                }
                selectNode(node, addToSelection);
                graphControler.openNodeFile(node);
            }
            }
            me.consume();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.draw2d.MouseListener.Stub#mousePressed(org.eclipse.draw2d.MouseEvent)
         */
        public void mousePressed(MouseEvent me) {
            log.trace("Got mouse down");
            switch (me.button) {
            case (1): {
                final boolean addToSelection;
                if ((me.getState() & InputEvent.CONTROL) == 0) {
                    addToSelection = false;
                }
                else {
                    addToSelection = true;
                }
                toggleSelection(node, addToSelection);
                me.consume();
                break;
            }
            case (3): {
                if (!node.isSelected()) {
                    selectNode(node, false);
                }
                // TODO rewrite in a clean way
                if (graphControler != null) {
                    ((GraphControler) graphControler).getDest().getContextMenu().setVisible(true);
                }
                break;
            }
            }
        }

    }

    private static final Log log = LogFactory.getLog(Draw2dGraph.class);

    private GraphControler graphControler;

    /*
     * (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#addNotify()
     */
    public void addNotify() {
        if (log.isTraceEnabled()) log.trace("Adding listeners");
        super.addNotify();
        graphMouseListener = new GraphMouseListener();
        addMouseListener(graphMouseListener);
        zoomListener = new ZoomListener(log);
        addKeyListener(zoomListener);
        setFocusTraversable(true);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#removeNotify()
     */
    public void removeNotify() {
        if (log.isTraceEnabled()) log.trace("Removing listeners");
        super.removeNotify();
        if (graphMouseListener != null) removeMouseListener(graphMouseListener);
        if (zoomListener != null) removeKeyListener(zoomListener);
        setFocusTraversable(false);
    }

    private CanvasScroller scroller;

    private float zoom;

    private GraphMouseListener graphMouseListener;

    private ZoomListener zoomListener;

    public Draw2dGraph() {
        super();
        scroller = null;
        setLayoutManager(new XYLayout());
    }

    /**
     * @param listener
     */
    public void addListener(GraphListener listener) {
        if (graphControler != null) graphControler.addListener(listener);
    }

    public Draw2dNode createNode(IVertex vertex) {
        final Draw2dNode node = new Draw2dNode(this, vertex);
        add(node, node.getBounds());
        node.setFont(Application.getInstance().getFont(Application.NODE_FONT));
        node.addMouseListener(new NodeMouseListener(node));
        node.setCursor(Cursors.HAND);
        return node;
    }

    /**
     *  
     */
    public void deselectAllNodes() {
        if (graphControler != null) graphControler.deselectAllNodes();
    }

    /**
     * @param node
     */
    public void deselectNode(Draw2dNode node) {
        if (graphControler != null) graphControler.deselectNode(node);
    }

    /**
     * @see org.eclipse.draw2d.Figure#getClientArea()
     */
    public Rectangle getClientArea(Rectangle rect) {
        super.getClientArea(rect);
        rect.width /= zoom;
        rect.height /= zoom;
        return rect;
    }

    /**
     * @return Returns the controler.
     */
    public final SelectionManager getControler() {
        return graphControler;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#getMinimumSize(int, int)
     */
    public Dimension getMinimumSize(int wHint, int hHint) {
        final Dimension d = super.getMinimumSize(wHint, hHint);
        int w = getInsets().getWidth();
        int h = getInsets().getHeight();
        return d.getExpanded(-w, -h).scale(zoom).expand(w, h);
    }

    public Dimension getPreferredSize(int wHint, int hHint) {
        final Dimension d = super.getPreferredSize(wHint, hHint);
        int w = getInsets().getWidth();
        int h = getInsets().getHeight();
        return d.getExpanded(-w, -h).scale(zoom).expand(w, h);
    }

    /**
     * @return Returns the scroller.
     */
    public final CanvasScroller getScroller() {
        return scroller;
    }

    /*
     * (non-Javadoc)
     * @see net.ggtools.grand.ui.graph.SelectionManager#getSelection()
     */
    public Collection getSelection() {
        if (graphControler != null) return graphControler.getSelection();
        return null;
    }

    /**
     * @param listener
     */
    public void removeSelectionListener(GraphListener listener) {
        if (graphControler != null) graphControler.removeSelectionListener(listener);
    }

    /**
     * @param node
     * @param addToSelection
     */
    public void selectNode(Draw2dNode node, boolean addToSelection) {
        if (graphControler != null) graphControler.selectNode(node, addToSelection);
    }

    /**
     * @param scroller
     *            The scroller to set.
     */
    public final void setScroller(CanvasScroller scroller) {
        this.scroller = scroller;
    }

    /**
     * @param graphControler
     *            The controler to set.
     */
    public final void setSelectionManager(GraphControler graphControler) {
        this.graphControler = graphControler;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        revalidate();
        repaint();
    }

    /**
     * @see org.eclipse.draw2d.Figure#translateFromParent(Translatable)
     */
    public void translateFromParent(Translatable t) {
        super.translateFromParent(t);
        t.performScale(1 / zoom);
    }

    /**
     * @see org.eclipse.draw2d.Figure#translateToParent(Translatable)
     */
    public void translateToParent(Translatable t) {
        t.performScale(zoom);
        super.translateToParent(t);
    }

    private void toggleSelection(final Draw2dNode node, final boolean addToSelection) {
        if (node.isSelected()) {
            deselectNode(node);
        }
        else {
            selectNode(node, addToSelection);
        }
    }

    /**
     * @see org.eclipse.draw2d.Figure#paintClientArea(Graphics)
     */
    protected void paintClientArea(Graphics graphics) {
        if (getChildren().isEmpty()) return;

        boolean optimizeClip = getBorder() == null || getBorder().isOpaque();

        ScaledGraphics g = new ScaledGraphics(graphics);

        if (!optimizeClip) g.clipRect(getBounds().getCropped(getInsets()));
        g.translate(getBounds().x + getInsets().left, getBounds().y + getInsets().top);
        g.scale(zoom);
        g.pushState();
        paintChildren(g);
        g.popState();
        g.dispose();
        graphics.restoreState();
    }

    /**
     * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
     */
    protected boolean useLocalCoordinates() {
        return true;
    }
}