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

package net.ggtools.grand.ui.actions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.ggtools.grand.ui.graph.GraphControlerProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Christophe Labouisse
 * @see org.eclipse.jface.action.Action
 */
public class ExportGraphAction extends GraphControlerAction {

    private static final Log log = LogFactory.getLog(ExportGraphAction.class);

    private static final String[] FILTER_EXTENSIONS = new String[]{"*.jpg", "*.jpeg", "*"};

    private static final String DEFAULT_ACTION_NAME = "Export Graph";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        final FileDialog dialog = new FileDialog(getGraphControler()
                .getWindow().getShell(),SWT.SAVE);
        dialog.setFilterExtensions(FILTER_EXTENSIONS);
        dialog.setText("Export graph as image");
        final String fileName = dialog.open();
        log.debug("Dialog returned " + fileName);
        if (fileName != null) {
            FileOutputStream result = null;
            try {
                result = new FileOutputStream(fileName);

                Image image = null;
                try {
                    image = getGraphControler().createImageForGraph();
                    final ImageLoader imageLoader = new ImageLoader();
                    imageLoader.data = new ImageData[]{image.getImageData()};
                    imageLoader.save(result, SWT.IMAGE_JPEG);
                } finally {
                    if (image != null) {
                        image.dispose();
                    }
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (IOException e) {
                        log.warn("Got exception exporting graph", e);
                    }
                }
            }
        }
    }

    /**
     * Creates a new ReloadGraphAction object.
     * 
     * @param parent
     */
    public ExportGraphAction(final GraphControlerProvider parent) {
        this(parent, DEFAULT_ACTION_NAME);
    }

    /**
     * Creates a new QuickOpenFileAction object with specific name.
     * 
     * @param name
     * @param parent
     */
    public ExportGraphAction(final GraphControlerProvider parent, final String name) {
        super(parent, name);
    }
}