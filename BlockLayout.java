/*
 * Blocklayout
 *
 * Copyright (C) 2010 Christian Simon - http://www.planet-metax.de
 * 
 */


package de.planet_metax.blocklayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Layout Manager, which lays components out vertically, like the "display=block" layout in HTML does.
 * Additionally padding of the container and margin of each component can be adjusted.
 * @author Christian Simon
 */
public class BlockLayout implements LayoutManager2, Serializable {

	private static final long serialVersionUID = -8696604975858663470L;
	
	/** Vertical gap between elements. */
	int vgap;
	
	/** minimum empty space between elements and top of container. */
	int padding_top;
	
	/** minimum empty space between elements and right side of container. */
	int padding_right;
	
	/** minimum empty space between elements and bottom of container. */
	int padding_bottom;
	
	/** minimum empty space between elements and left side of container. */
	int padding_left;
	
	/** default alignments for elements. */
	int align;
	
	/** Alignment constraint: center element vertically. */
	public static final int ALIGN_CENTER = 0;
	
	/** Alignment constraint: align element to the left. */
	public static final int ALIGN_LEFT = 1;
	
	/** Alignment constraint: align element to the right. */
	public static final int ALIGN_RIGHT = 2;
	
	/** Alignment constraint: stretch element to the full horizontal size. */
	public static final int ALIGN_JUSTIFY = 3;
	
	/** Alignment constraint: Don't change alignment for this object and take default alignment property of the layout.
	 * Only for use in {@link BlockLayoutConstraints}! */
	public static final int ALIGN_INHERIT = -1;
	
	/** List of components in this layout. */
	private LinkedList components;
	
	public BlockLayout() {
		this(ALIGN_CENTER);
	}
	
	public BlockLayout(int align_default) {
		this(align_default, 0);
	}
	
	public BlockLayout(int align_default, int vgap) {
		this(align_default, vgap, 0);
	}
	
	public BlockLayout(int align_default, int vgap, int padding) {
		this(align_default, vgap, padding, padding, padding, padding);
	}
	
	public BlockLayout(int align_default, int vgap, int ptop, int pright, int pbottom, int pleft) {
		setAlign(align_default);
		setVgap(vgap);
		setPadding(ptop, pright, pbottom, pleft);
		components = new LinkedList();
	}
	
	public void setPadding(int ptop, int pright, int pbottom, int pleft) {
		padding_top = ptop;
		padding_right = pright;
		padding_bottom = pbottom;
		padding_left = pleft;
	}
	
	public int getAlign() {
		return align;
	}
	
	public int getPadding_top() {
		return padding_top;
	}
	
	public int getPadding_right() {
		return padding_right;
	}
	
	public int getPadding_bottom() {
		return padding_bottom;
	}
	
	public int getPadding_left() {
		return padding_left;
	}
	
	public int getVgap() {
		return vgap;
	}
	
	public void setAlign(int align) {
		this.align = align;
	}
	
	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		synchronized (comp.getTreeLock()) {
			if (constraints == null) {
				components.add(new BlockLayoutElement(comp, new BlockLayoutConstraints(ALIGN_INHERIT)));
			} else if (constraints instanceof BlockLayoutConstraints) {
				components.add(new BlockLayoutElement(comp, (BlockLayoutConstraints) constraints));
			} else {
				throw new IllegalArgumentException(
						"cannot add to layout: constraint must be a BlockLayoutConstraints (or null)");
			}
		}
	}

	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	public void invalidateLayout(Container target) { }

	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
     * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>.
     */
	public void addLayoutComponent(String name, Component comp) {
		synchronized (comp.getTreeLock()) {
			int align = ALIGN_INHERIT;
			if ("Left".equalsIgnoreCase(name)) {
				align = ALIGN_LEFT;
			} else if ("Right".equalsIgnoreCase(name)) {
				align = ALIGN_RIGHT;
			} else if ("Center".equalsIgnoreCase(name)) {
				align = ALIGN_CENTER;
			} else if ("Justify".equalsIgnoreCase(name)) {
				align = ALIGN_JUSTIFY;
			}
			addLayoutComponent(comp, new BlockLayoutConstraints(align));
		}
	}

	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int top = insets.top + padding_top;
			int left = insets.left + padding_left;
			int right = target.getWidth() - insets.right - padding_right;
			for (int i = 0; i < components.size(); i++) {
				BlockLayoutElement item = (BlockLayoutElement) components.get(i);
				Component comp = item.comp;
				int align = this.align;
				if (item.constr.align == ALIGN_LEFT || item.constr.align == ALIGN_CENTER
						|| item.constr.align == ALIGN_RIGHT || item.constr.align == ALIGN_JUSTIFY) {
					align = item.constr.align;
				}
				
				top += item.constr.margin_top;
				
				int inner_left = left + item.constr.margin_left;
				int inner_right = right - item.constr.margin_right;
				
				int comp_height = comp.getPreferredSize().height;
				int comp_width = comp.getPreferredSize().width;
				if (align == ALIGN_LEFT) {
					comp.setSize(comp_width, comp_height);
					Dimension d = comp.getPreferredSize();
					comp.setBounds(inner_left, top, comp_width, d.height);
				} else if (align == ALIGN_RIGHT) {
					comp.setSize(comp_width, comp_height);
					Dimension d = comp.getPreferredSize();
					comp.setBounds(inner_right - comp_width, top, comp_width, d.height);
				} else if (align == ALIGN_JUSTIFY) {
					comp.setSize(inner_right - inner_left, comp_height);
					Dimension d = comp.getPreferredSize();
					comp.setBounds(inner_left, top, inner_right - inner_left, d.height);
				} else { // ALIGN_CENTER
					comp.setSize(comp_width, comp_height);
					Dimension d = comp.getPreferredSize();
					comp.setBounds((inner_right -  inner_left - comp_width) / 2, top, comp_width, d.height);
				}
				
				top += comp.getHeight() + item.constr.margin_bottom + vgap;
			}
		}
	}

	public Dimension minimumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			if (!components.isEmpty()) {
				dim.height += padding_top + padding_bottom; // Padding is only relevant, if there are elements in there.
				dim.height -= vgap; // With n components, only n-1 vgaps are needed (when there is at least 1 component).
			}
			for (int i = 0; i < components.size(); i++) {
				BlockLayoutElement item = (BlockLayoutElement) components.get(i);
				dim.height += item.comp.getMinimumSize().height + vgap + item.constr.margin_top + item.constr.margin_bottom;
				dim.width = Math.max(dim.width, item.comp.getMinimumSize().width + padding_left + padding_right
					+ item.constr.margin_left + item.constr.margin_right);
			}
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}

	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			if (!components.isEmpty()) {
				dim.height += padding_top + padding_bottom; // Padding is only relevant, if there are elements in there.
				dim.height -= vgap; // With n components, only n-1 vgaps are needed (when there is at least 1 component).
			}
			for (int i = 0; i < components.size(); i++) {
				BlockLayoutElement item = (BlockLayoutElement) components.get(i);
				dim.height += item.comp.getPreferredSize().height + vgap + item.constr.margin_top + item.constr.margin_bottom;
				dim.width = Math.max(dim.width, item.comp.getPreferredSize().width + padding_left + padding_right
					+ item.constr.margin_left + item.constr.margin_right);
			}
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}

	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			for (int i = 0; i < components.size(); i++) {
				BlockLayoutElement item = (BlockLayoutElement) components.get(i);
				if (item.comp == comp) {
					components.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * Constraints object for BlockLayout.
	 * @author Christian Simon
	 */
	public static class BlockLayoutConstraints {
		
		public int align;
		
		public int margin_left;
		public int margin_right;
		public int margin_top;
		public int margin_bottom;
		
		public BlockLayoutConstraints() {
			this(ALIGN_INHERIT);
		}
		
		public BlockLayoutConstraints(int align) {
			this(align, 0, 0, 0, 0);
		}
		
		public BlockLayoutConstraints(int align, int margin) {
			this(align, margin, margin, margin, margin);
		}
		
		public BlockLayoutConstraints(int align, int margin_top, int margin_right, int margin_bottom, int margin_left) {
			this.align = align;
			setMargin(margin_top, margin_right, margin_bottom, margin_left);
		}
		
		public int getAlign() {
			return align;
		}
		
		public void setAlign(int align) {
			this.align = align;
		}
		
		public int getMargin_bottom() {
			return margin_bottom;
		}
		
		public int getMargin_left() {
			return margin_left;
		}
		
		public int getMargin_right() {
			return margin_right;
		}
		
		public int getMargin_top() {
			return margin_top;
		}
		
		public void setMargin(int margin_top, int margin_right, int margin_bottom, int margin_left) {
			this.margin_top = margin_top;
			this.margin_right = margin_right;
			this.margin_bottom = margin_bottom;
			this.margin_left = margin_left;
		}
		
	}
	
	/**
	 * Tupel of ({@link Component}, {@link BlockLayoutConstraints}) for internal usage in {@link BlockLayout}.
	 * @author Christian Simon
	 */
	private static class BlockLayoutElement {
		Component comp;
		BlockLayoutConstraints constr;
		
		BlockLayoutElement(Component comp, BlockLayoutConstraints constraints) {
			this.comp = comp;
			this.constr = constraints;
		}
	}

}