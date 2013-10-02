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

import net.ggtools.grand.filters.GraphFilter;
import net.ggtools.grand.filters.IsolatedNodeFilter;
import net.ggtools.grand.ui.graph.GraphControlerProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An action to filter the isolated nodes.
 *
 * @author Christophe Labouisse
 */
public class FilterIsolatedNodesAction extends GraphControlerAction {
    /**
     * Field log.
     */
    @SuppressWarnings("unused")
    private static final Log LOG =
            LogFactory.getLog(FilterIsolatedNodesAction.class);

    /**
     * Field DEFAULT_ACTION_NAME.
     * (value is ""Filter out isolated nodes"")
     */
    private static final String DEFAULT_ACTION_NAME =
            "Filter out isolated nodes";

    /**
     * Method run.
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public final void run() {
        final GraphFilter filter = new IsolatedNodeFilter();
        getGraphControler().addFilter(filter);
    }

    /**
     * Constructor for FilterIsolatedNodesAction.
     * @param parent GraphControlerProvider
     */
    public FilterIsolatedNodesAction(final GraphControlerProvider parent) {
        super(parent, DEFAULT_ACTION_NAME);
    }
}
