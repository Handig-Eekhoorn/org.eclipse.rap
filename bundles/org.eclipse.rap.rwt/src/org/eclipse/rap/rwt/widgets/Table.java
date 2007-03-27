/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.widgets;

import java.util.*;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;
import org.eclipse.rap.rwt.internal.widgets.IItemHolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;


/**
 * <p>Current state of Table implementation:</p> 
 * <ul>
 * <li>Though Table inherits the font property from Control, it is currently
 * not evaluated client-side.</li>
 * <li>Though it is possible to create a table with MULTI style, only SINGLE
 *  selections are implemented. This also applies when using setSelection with 
 *  more than one TableItem</li>
 *  <li>linesVisible defaults to <code>false</code> as in SWT and can be queried
 *  with getLinesVisible. The setLinesVisible methd is not yet implemented.</li>
 *  <li>VIRTUAL not yet implemented</li>
 *  <li>showSelection and showItem currently do a very rough proximation since
 *  getClientArea is not yet implemented properly</li>
 *  <li>Tables created with style BORDER are not yet drawn correctly</li>
 *  <li>Scroll bars stay enabled even though the table itself is disabled. This
 *  is due to a qooxdoo limitation, see http://bugzilla.qooxdoo.org/post_bug.cgi
 *  </li>
 *  <li>No images yet</li>
 * </ul> 
 */
public class Table extends Composite {
  
  // handle the fact that we have two item types to deal with
  private final class CompositeItemHolder implements IItemHolderAdapter {
    public void add( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.add( item );
      } else {
        columnHolder.add( item );
      }
    }
    public void insert( final Item item, final int index ) {
      if( item instanceof TableItem ) {
        itemHolder.insert( item, index );
      } else {
        columnHolder.insert( item, index );
      }
    }
    public void remove( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.remove( item );
      } else {
        columnHolder.remove( item );
      }
    }
    public Item[] getItems() {
      Item[] items = itemHolder.getItems();
      Item[] columns = columnHolder.getItems();
      Item[] result = new Item[ items.length + columns.length ];
      System.arraycopy( columns, 0, result, 0, columns.length );
      System.arraycopy( items, 0, result, columns.length, items.length );
      return result;
    }
  }
  
  private static final int GRID_WIDTH = 1;
  private static final int DEFAULT_ITEM_HEIGHT = 15;
  private static final TableItem[] EMPTY_SELECTION = new TableItem[ 0 ];
  
  private final ItemHolder itemHolder;
  private final ItemHolder columnHolder;
  private TableItem[] selection;
  private boolean linesVisible;
  private boolean headerVisible;
  private int topIndex;
  
  public Table( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TableItem.class );
    columnHolder = new ItemHolder( TableColumn.class );
    selection = EMPTY_SELECTION;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = new CompositeItemHolder();
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ///////////////////////////
  // Column handling methods

  public int getColumnCount() {
    checkWidget();
    return columnHolder.size();
  }

  public TableColumn[] getColumns() {
    checkWidget();
    return ( TableColumn[] )columnHolder.getItems();
  }

  public TableColumn getColumn( final int index ) {
    checkWidget();
    return ( TableColumn )columnHolder.getItem( index );
  }

  public int indexOf( final TableColumn tableColumn ) {
    checkWidget();
    if( tableColumn == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return columnHolder.indexOf( tableColumn );
  }

  ////////////////////////
  // Item handling methods

  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public TableItem[] getItems() {
    checkWidget();
    return ( TableItem[] )itemHolder.getItems();
  }

  public TableItem getItem( final int index ) {
    checkWidget();
    return ( TableItem )itemHolder.getItem( index );
  }

  public int indexOf( final TableItem tableItem ) {
    checkWidget();
    if( tableItem == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return itemHolder.indexOf( tableItem );
  }
  
  public void removeAll() {
    checkWidget();
    TableItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      if( !items[ i ].isDisposed() ) {
        items[ i ].dispose();
      }
    }
    topIndex = 0;
  }
  
  public void clear( final int index ) {
    checkWidget();
    TableItem item = getItem( index );
    item.clear();
  }
  
  /////////////////////////////
  // Selection handling methods
  
  public int getSelectionIndex() {
    // TODO: [fappel] currently we do not have an focus indicator, so
    //                we return simply return the first index in range
    int selectionIndex = -1;
    TableItem[] currentSelection = getSelection();
    if( currentSelection.length > 0 ) {
      for( int i = 0; selectionIndex == -1 && i < itemHolder.size(); i++ ) {
        if( itemHolder.getItem( i ) == currentSelection[ 0 ] ) {
          selectionIndex = i;
        }
      }
    }
    return selectionIndex;
  }

  public void setSelection( final int index ) {
    if( index >= 0 && index < itemHolder.size() ) {
      TableItem item = ( TableItem )itemHolder.getItem( index );
      selection = new TableItem[] { item };
    } else {
      selection = EMPTY_SELECTION;
    }
  }

  public int getSelectionCount() {
    return getSelection().length;
  }

  public void setSelection( final int start, final int end ) {
    // TODO: [fappel] style bits for single/multi selection
    if( end >= 0 && start <= end ) {
      int actualStart = Math.max( 0, start );
      int actualEnd = Math.min( end, itemHolder.size() - 1 );
      selection = new TableItem[ actualEnd - actualStart + 1 ];
      int count = 0;
      for( int i = actualStart; i < actualEnd + 1; i++ ) {
        selection[ count ] = ( TableItem )itemHolder.getItem( i );
        count++;
      }
    }
  }

  public TableItem[] getSelection() {
    List buffer = new ArrayList();
    for( int i = 0; i < selection.length; i++ ) {
      if( !selection[ i ].isDisposed() ) {
        buffer.add( selection[ i ] );
      }
    }
    selection = new TableItem[ buffer.size() ];
    buffer.toArray( selection );
    return ( TableItem[] )selection.clone();
  }

  public void setSelection( final int[] indices ) {
    if( indices == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    // TODO: [fappel] style bits for single/multi selection
    
    Integer[] filteredIndices = filterIndices( indices );    
    TableItem[] newSelection = new TableItem[ filteredIndices.length ];
    for( int i = 0; i < filteredIndices.length; i++ ) {
      int index = filteredIndices[ i ].intValue();
      newSelection[ i ] = ( TableItem )itemHolder.getItem( index );
    }
    selection = newSelection;
  }
  
  public void setSelection( final TableItem[] items ) {
    if( items == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int[] indices = new int[ items.length ];
    for( int i = 0; i < items.length; i++ ) {
      indices[ i ] = indexOf( items[ i ] );
    }
    setSelection( indices );
  }
  
  public int[] getSelectionIndices() {
    TableItem[] currentSelection = getSelection();
    int[] result = new int[ currentSelection.length ];
    for( int i = 0; i < currentSelection.length; i++ ) {
      result[ i ] = indexOf( currentSelection[ i ] );
    }
    return result;
  }
  
  public boolean isSelected( final int index ) {
    boolean result = false;
    if( index >= 0 && index < itemHolder.size() ) {
      Item item = itemHolder.getItem( index );
      TableItem[] currentSelection = getSelection();
      for( int i = 0; !result && i < currentSelection.length; i++ ) {
        result = currentSelection[ i ] == item;
      }
    }
    return result;
  }
  
  public void deselectAll() {
    checkWidget();
    selection = EMPTY_SELECTION;
  }

  //////////////////////////////////
  // TopIndex and showItem/Selection
  
  public void setTopIndex( final int topIndex ) {
    checkWidget();
    if( topIndex >= 0 && topIndex < getItemCount() ) {
      this.topIndex = topIndex;
    }
  }
  
  public int getTopIndex() {
    checkWidget();
    return topIndex;
  }
  
  public void showItem( final TableItem item ) {
    checkWidget();
    if( item == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      error( RWT.ERROR_INVALID_ARGUMENT );
    }
    int itemIndex = indexOf( item );
    // Show item as top item
    setTopIndex( itemIndex );
    // try to show it 2 rows above the bottom/last item
    int visibleRows = getClientArea().height / getItemHeight();
    int idealTopIndex = itemIndex - visibleRows + 2;
    if( idealTopIndex >= getItemCount() ) {
      idealTopIndex = getItemCount() - 1;
    }
    setTopIndex( idealTopIndex );
  }
  
  public void showSelection() {
    checkWidget();
    int index = getSelectionIndex();
    if( index != -1 ) {
      TableItem item = ( TableItem )itemHolder.getItem( index ); 
      showItem( item );
    }
  }

  ////////////////////
  // Visual appearance
  
  public void setHeaderVisible( final boolean headerVisible ) {
    checkWidget();
    this.headerVisible = headerVisible;
  }
  
  public boolean getHeaderVisible() {
    checkWidget();
    return headerVisible;
  }
  
  public boolean getLinesVisible() {
    checkWidget();
    return linesVisible;
  }
  
  public void setLinesVisible( final boolean linesVisible ) {
    checkWidget();
    this.linesVisible = linesVisible;
  }
  
  ///////////////////////////////////
  // Dimensions and size calculations
  
  public int getItemHeight () {
    checkWidget();
    int result = DEFAULT_ITEM_HEIGHT;
    if( itemHolder.size() > 0 ) {
      TableItem item = ( TableItem )itemHolder.getItem( 0 );
      result = item.getHeight();
    }
    return result;
  }
  
  public int getHeaderHeight() {
    checkWidget();
    int result = 0;
    if( headerVisible ) {
      // TODO [rh] preliminary implementation
      result = FontSizeEstimation.getCharHeight( getFont() ) + 4;
    }
    return result;
  }
  
  public int getGridLineWidth () {
    checkWidget();
    return GRID_WIDTH;
  }
  
  //////////////////
  // Selection event
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }
  
  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  /////////////////////////////
  // Create and destroy columns
  
  final void createColumn( final TableColumn column, final int index ) {
    columnHolder.insert( column, index );
  }
  
  final void destroyColumn( final TableColumn column ) {
    columnHolder.remove( column );
  }
  
  ////////////////////////////
  // Create and destroy items
  
  final void createItem( final TableItem item, final int index ) {
    itemHolder.insert( item, index );
  }
  
  final void destroyItem( final TableItem item ) {
    itemHolder.remove( item );
    if( topIndex > getItemCount() - 1 ) {
      topIndex = getItemCount() - 1;
    }
  }
  
  //////////////////
  // helping methods
  
  private Integer[] filterIndices( final int[] indices ) {
    Set buffer = new HashSet();
    for( int i = 0; i < indices.length; i++ ) {
      if( indices[ i ] >= 0 && indices[ i ] < itemHolder.size() ) {
        buffer.add( new Integer( indices[ i ] ) );
      }
    }
    Integer[] result = new Integer[ buffer.size() ];
    buffer.toArray( result );
    Arrays.sort( result );
    return result;
  }

  private static int checkStyle( final int style ) {
    int result = style;
    result |= RWT.H_SCROLL | RWT.V_SCROLL;
    return checkBits( result, RWT.SINGLE, RWT.MULTI, 0, 0, 0, 0 );
  }
}