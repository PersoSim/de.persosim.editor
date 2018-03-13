package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.MaxValueChecker;
import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;
import de.persosim.editor.ui.editor.checker.LengthChecker;
import de.persosim.editor.ui.editor.checker.NumberChecker;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.exception.AccessDeniedException;

public class ChangeablePasswortAuthObjectHandler extends PasswordAuthObjectHandler implements ObjectHandler {

	public ChangeablePasswortAuthObjectHandler(Collection<Integer> allowedIds) {
		super(allowedIds);
	}
	
	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		
		ChangeablePasswordAuthObject authObject = ((ChangeablePasswordAuthObject)item.getData());
		
		EditorFieldHelper.createField(item, false, composite, new AbstractObjectModifier() {
			
			@Override
			public void setValue(String string) {
				try {
					ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
					if (!new String(authObject.getPassword(), StandardCharsets.US_ASCII).equals(string)) {
						authObject.setPassword(string.getBytes(StandardCharsets.US_ASCII));
						if (handler != null) {
							handler.changed(item);
						}
					}
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}
			
			@Override
			public void remove() {
				//Not intended
			}
			
			@Override
			public String getValue() {
				return new String(authObject.getPassword(), StandardCharsets.US_ASCII);
			}
			
			@Override
			public void setActivationState() {
				try {
					ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
					switch (authObject.getLifeCycleState()) {
					case CREATION_OPERATIONAL_ACTIVATED:
					case CREATION:
					case OPERATIONAL_ACTIVATED:
						authObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION_OPERATIONAL_DEACTIVATED);
						if (handler != null) {
							handler.changed(item);
						}
						break;
					default:
						break;
					}
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}

			@Override
			public boolean getActivationState() {
				switch (authObject.getLifeCycleState()) {
				case CREATION_OPERATIONAL_ACTIVATED:
				case CREATION:
				case OPERATIONAL_ACTIVATED:
					return true;
				default:
					return false;
				}
			}
		}, new AndChecker(new NumberChecker(), new LengthChecker(5,6,State.ERROR)), authObject.getPasswordName() + ", possible lengths are 5 or 6 characters");
		
		if (authObject instanceof PasswordAuthObjectWithRetryCounter){
			PasswordAuthObjectWithRetryCounter pwdWithRetryCounter = (PasswordAuthObjectWithRetryCounter) authObject;
			EditorFieldHelper.createField(item, true, composite, new AbstractObjectModifier() {
				
				@Override
				public void setValue(String string) {
					try {
						int initialValue = Integer.parseInt(getValue());
						int newValue = Integer.parseInt(string);
						
						if (initialValue == newValue) {
							return;
						}
						
						if (newValue >= 0 && newValue <= pwdWithRetryCounter.getRetryCounterDefaultValue()){
							pwdWithRetryCounter.resetRetryCounterToDefault();
							while (pwdWithRetryCounter.getRetryCounterCurrentValue() != Integer.parseInt(string)){
								pwdWithRetryCounter.decrementRetryCounter();
							}	
							ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
							if (handler != null) {
								handler.changed(item);
							}
						}
					} catch (AccessDeniedException | NumberFormatException e) {
						BasicLogger.logException(getClass(), e, LogLevel.WARN);
					}
				}
				
				@Override
				public String getValue() {
					return Integer.toString(pwdWithRetryCounter.getRetryCounterCurrentValue());
				}
				
			}, new AndChecker(new NumberChecker(State.ERROR), new MaxValueChecker(pwdWithRetryCounter.getRetryCounterDefaultValue())), "Retry counter, max. value is " + pwdWithRetryCounter.getRetryCounterDefaultValue());
		}
	}
	
}
