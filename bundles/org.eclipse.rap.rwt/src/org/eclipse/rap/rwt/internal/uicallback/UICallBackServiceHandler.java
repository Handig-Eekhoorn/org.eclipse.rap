/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.uicallback;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.IServiceHandler;


public class UICallBackServiceHandler implements IServiceHandler {

  public final static String HANDLER_ID = "org.eclipse.rap.uicallback";

  public void service() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    UICallBackManager.getInstance().processRequest( response );
  }

}
