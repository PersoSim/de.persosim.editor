package de.persosim.editor.ui.editor;

import java.util.List;

public class DefaultHandlerProvider implements HandlerProvider {

	private List<ObjectHandler> handlers;

	public DefaultHandlerProvider(List<ObjectHandler> handlers) {
		this.handlers = handlers;
	}
	
	@Override
	public ObjectHandler get(Object object) {
		for (ObjectHandler handler : handlers) {
			if (handler.canHandle(object)) {
				return handler;
			}
		}
		return null;
	}

}
