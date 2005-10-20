/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.ui.menus;import org.eclipse.core.commands.common.NamedHandleObject;import org.eclipse.core.commands.common.NotDefinedException;import org.eclipse.jface.menus.SReference;import org.eclipse.jface.util.ListenerList;import org.eclipse.ui.internal.util.Util;/** * <p> * A group of actions that can be made visible or invisible as a group. Action * sets can also be associated with particular parts or perspectives. The end * user is also able to enable and disable action sets for a given perspective. * </p> * <p> * Action sets are defined using the <code>org.eclipse.ui.menus</code> * extension point. They can be associated with parts using the * <code>org.eclipse.ui.actionSetPartAssociations</code> extension point. They * can be associated with perspectives using the * <code>org.eclipse.ui.perspectiveExtensions</code> extension point. * </p> * <p> * Clients may instantiate, but they must extend. * </p> * <p> * <strong>EXPERIMENTAL</strong>. This class or interface has been added as * part of a work in progress. There is a guarantee neither that this API will * work nor that it will remain the same. Please do not use this API without * consulting with the Platform/UI team. * </p> *  * @since 3.2 */public final class SActionSet extends NamedHandleObject {	/**	 * A collection of objects listening to changes to this action set. This	 * collection is <code>null</code> if there are no listeners.	 */	private transient ListenerList listenerList = null;	/**	 * References to menu elements in the workbench. These are the menu elements	 * that are in this action set.	 */	private SReference[] references;	/**	 * Whether this action set should be visible in all perspectives by default.	 */	private boolean visible = false;	/**	 * Constructs a new instance of <code>SActionSet</code>	 * 	 * @param id	 *            The identifier of the action set to create; must not be	 *            <code>null</code>.	 */	public SActionSet(final String id) {		super(id);	}	/**	 * Adds a listener to this action set that will be notified when this action	 * set's state changes.	 * 	 * @param listener	 *            The listener to be added; must not be <code>null</code>.	 */	public final void addListener(final IActionSetListener listener) {		if (listenerList == null) {			listenerList = new ListenerList(1);		}		listenerList.add(listener);	}	/**	 * <p>	 * Defines this command by giving it a name, visibility and a collection of	 * references. The description is optional. The defined property	 * automatically becomes <code>true</code>.	 * </p>	 * 	 * @param name	 *            The name of the action set; must not be <code>null</code>.	 *            This name should short (one or two words) and human-readable.	 * @param description	 *            The description for the action set; may be <code>null</code>	 *            if there is no description. The description can be longer: one	 *            or two sentences.	 * @param visible	 *            Whether the action should be visible in all perspective by	 *            default.	 * @param references	 *            References to the menu elements that are included in this	 *            action set. This value must not be <code>null</code> and it	 *            must not be empty.	 */	public final void define(final String name, final String description,			final boolean visible, final SReference[] references) {		if (name == null) {			throw new NullPointerException("An action set needs a name"); //$NON-NLS-1$		}		if (references == null) {			throw new NullPointerException(					"The action set must reference at least one menu element"); //$NON-NLS-1$		}		if (references.length == 0) {			throw new IllegalArgumentException(					"The action set must reference at least one menu element"); //$NON-NLS-1$		}		ActionSetEvent event = null;		if (isListenerAttached()) {			final boolean nameChanged = !Util.equals(this.name, name);			final boolean descriptionChanged = !Util.equals(this.description,					description);			final boolean visibleChanged = !Util.equals(this.visible, visible);			final boolean referencesChanged = !Util.equals(this.references,					references);			final boolean definedChanged = !this.defined;			event = new ActionSetEvent(this, nameChanged, descriptionChanged,					visibleChanged, referencesChanged, definedChanged);		}		this.name = name;		this.description = description;		this.visible = visible;		this.references = references;		this.defined = true;		fireActionSetChanged(event);	}	/**	 * Tests whether this action set is equal to another object. An action set	 * is only equal to another action set with the same id.	 * 	 * @param object	 *            The object with which to compare; may be <code>null</code>.	 * @return <code>true</code> if the action sets are equal;	 *         <code>false</code> otherwise.	 */	public final boolean equals(final Object object) {		// Check if they're the same.		if (object == this) {			return true;		}		// Check if they're the same type.		if (!(object instanceof SActionSet))			return false;		// Check each property in turn.		final SActionSet actionSet = (SActionSet) object;		return Util.equals(id, actionSet.id);	}	/**	 * Notifies listeners to this action set that it has changed in some way.	 * 	 * @param event	 *            The event to fire; may be <code>null</code>.	 */	private final void fireActionSetChanged(final ActionSetEvent event) {		if (event == null) {			return;		}		if (listenerList != null) {			final Object[] listeners = listenerList.getListeners();			for (int i = 0; i < listeners.length; i++) {				final IActionSetListener listener = (IActionSetListener) listeners[i];				listener.actionSetChanged(event);			}		}	}	/**	 * Returns the references for this action set. This performs a copy of the	 * internal data structure.	 * 	 * @return The references for this action set; never <code>null</code>.	 * @throws NotDefinedException	 *             If the handle is not currently defined.	 */	public final SReference[] getReferences() throws NotDefinedException {		if (!isDefined()) {			throw new NotDefinedException(					"Cannot get the references from an undefined action set"); //$NON-NLS-1$		}		final SReference[] result = new SReference[references.length];		System.arraycopy(references, 0, result, 0, references.length);		return result;	}	/**	 * Whether one or more listeners are attached to the action set.	 * 	 * @return <code>true</code> if listeners are attached to the action set;	 *         <code>false</code> otherwise.	 */	private final boolean isListenerAttached() {		return listenerList != null;	}	/**	 * Whether the action set should be visible in every perspective by default.	 * 	 * @return <code>true</code> if the action set is visible in every	 *         perspective; <code>false</code> otherwise.	 * @throws NotDefinedException	 *             If the handle is not currently defined.	 */	public final boolean isVisible() throws NotDefinedException {		if (!isDefined()) {			throw new NotDefinedException(					"Cannot get the visibility from an undefined action set"); //$NON-NLS-1$		}		return visible;	}	/**	 * Removes a listener from this action set.	 * 	 * @param listener	 *            The listener to be removed; must not be <code>null</code>.	 */	public final void removeListener(final IActionSetListener listener) {		if (listenerList != null) {			listenerList.remove(listener);		}		if (listenerList.isEmpty()) {			listenerList = null;		}	}	/**	 * The string representation of this action set -- for debugging purposes	 * only. This string should not be shown to an end user.	 * 	 * @return The string representation; never <code>null</code>.	 */	public final String toString() {		if (string == null) {			final StringBuffer stringBuffer = new StringBuffer();			stringBuffer.append("SGroup("); //$NON-NLS-1$			stringBuffer.append(id);			stringBuffer.append(',');			stringBuffer.append(name);			stringBuffer.append(',');			stringBuffer.append(description);			stringBuffer.append(',');			stringBuffer.append(visible);			stringBuffer.append(',');			stringBuffer.append(references);			stringBuffer.append(',');			stringBuffer.append(defined);			stringBuffer.append(')');			string = stringBuffer.toString();		}		return string;	}	/**	 * Makes this action set become undefined. This has the side effect of	 * changing the name, description and references to <code>null</code>.	 * Notification is sent to all listeners.	 */	public final void undefine() {		string = null;		ActionSetEvent event = null;		if (isListenerAttached()) {			final boolean nameChanged = name != null;			final boolean descriptionChanged = description != null;			final boolean visibleChanged = visible != false;			final boolean referencesChanged = references != null;			final boolean definedChanged = defined;			event = new ActionSetEvent(this, nameChanged, descriptionChanged,					visibleChanged, referencesChanged, definedChanged);		}		defined = false;		name = null;		description = null;		visible = false;		references = null;		fireActionSetChanged(event);	}}