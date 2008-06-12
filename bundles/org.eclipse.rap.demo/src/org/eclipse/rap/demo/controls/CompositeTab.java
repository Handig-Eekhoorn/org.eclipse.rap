/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public final class CompositeTab extends ExampleTab {

  private Composite composite;
  private boolean addMouseListener;
  private int backgroundMode;

  public CompositeTab( final CTabFolder topFolder ) {
    super( topFolder, "Composite" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    createBackgroundModeControls( parent );
    Button cbAddMouseListener = new Button( parent, SWT.CHECK );
    cbAddMouseListener.setText( "Attach MouseListener" );
    cbAddMouseListener.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        addMouseListener = !addMouseListener;
        createNew();
      }
    } );
    cbAddMouseListener.setSelection( addMouseListener );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    parent.setBackgroundMode( backgroundMode );
    composite = new Composite( parent, getStyle() );
    if( addMouseListener ) {
      MouseListener listener = new MouseListener(  ) {
        public void mouseDoubleClick( MouseEvent e ) {
          log( "mouseDoubleClick: " + e );
        }
        public void mouseDown( MouseEvent e ) {
          log( "mouseDown: " + e );
        }
        public void mouseUp( MouseEvent e ) {
          log( "mouseUp: " + e );
        }
      };
      composite.addMouseListener( listener );
    }
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    registerControl( composite );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Label" );
    Button pushButton = new Button( composite, SWT.PUSH );
    pushButton.setText( "Push Button" );
    Button radioButton = new Button( composite, SWT.RADIO );
    radioButton.setText( "Radio Button" );
    Button checkButton = new Button( composite, SWT.CHECK );
    checkButton.setText( "Check Box" );
    Text text = new Text( composite, SWT.SINGLE | SWT.BORDER );
    text.setText( "text" );
    Text multiText = new Text( composite, SWT.MULTI | SWT.BORDER );
    multiText.setText( "Multiline Text" );
    multiText.setLayoutData( new RowData( 80, 60 ) );
    Combo combo = new Combo( composite, SWT.NONE );
    combo.add( "Item 1" );
    combo.add( "Item 2" );
    combo.add( "Item 3" );
    List list = new List( composite, SWT.BORDER );
    list.add( "Item 1" );
    list.add( "Item 2" );
    list.add( "Item 3" );
    Composite composite2 = new Composite( composite, SWT.NONE );
    composite2.setBackground( BG_COLOR_GREEN );
    Group group = new Group( composite, SWT.NONE );
    group.setText( "Group" );
  }

  protected void createBackgroundModeControls( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Background Mode" );
    group.setLayout( new GridLayout() );
    final Button noneButton = new Button( group, SWT.RADIO );
    noneButton.setText( "SWT.INHERIT_NONE" );
    final Button defaultButton = new Button( group, SWT.RADIO );
    defaultButton.setText( "SWT.INHERIT_DEFAULT" );
    final Button forceButton = new Button( group, SWT.RADIO );
    forceButton.setText( "SWT.INHERIT_FORCE" );
    SelectionListener selectionAdapter = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        if( defaultButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_DEFAULT;
        } else if( forceButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_FORCE;
        } else {
          backgroundMode = SWT.INHERIT_NONE;
        }
        composite.setBackgroundMode( backgroundMode );
      }
    };
    noneButton.addSelectionListener( selectionAdapter );
    defaultButton.addSelectionListener( selectionAdapter );
    forceButton.addSelectionListener( selectionAdapter );
    noneButton.setSelection( true );
  }
}
