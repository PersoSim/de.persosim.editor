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
import de.persosim.editor.ui.editor.checker.LengthChecker;
import de.persosim.editor.ui.editor.checker.NumberChecker;
import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;
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
		
		EditorFieldHelper.createField(item, false, composite, new TlvModifier() {
			
			@Override
			public void setValue(String string) {
				try {
					authObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION_OPERATIONAL_ACTIVATED);
					authObject.setPassword(string.getBytes(StandardCharsets.US_ASCII));
					ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
					if (handler != null) {
						handler.changed(item);
					}
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}
			
			@Override
			public void remove() {
				try {
					authObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION_OPERATIONAL_DEACTIVATED);
					ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
					if (handler != null) {
						handler.changed(item);
					}
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}
			
			@Override
			public String getValue() {
				return new String(authObject.getPassword(), StandardCharsets.US_ASCII);
			}
		}, new AndChecker(new NumberChecker(), new LengthChecker(5,6,State.ERROR)), authObject.getPasswordName() + ", possible lengths are 5 or 6 characters");
		
		if (authObject instanceof PasswordAuthObjectWithRetryCounter){
			PasswordAuthObjectWithRetryCounter pwdWithRetryCounter = (PasswordAuthObjectWithRetryCounter) authObject;
			EditorFieldHelper.createField(item, true, composite, new TlvModifier() {
				
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
				public void remove() {
					// not intended
				}
				
				@Override
				public String getValue() {
					return Integer.toString(pwdWithRetryCounter.getRetryCounterCurrentValue());
				}
				
			}, new AndChecker(new NumberChecker(State.ERROR), new MaxValueChecker(pwdWithRetryCounter.getRetryCounterDefaultValue())), "Retry counter, max. value value is " + pwdWithRetryCounter.getRetryCounterDefaultValue());
		}
	}
	
}
