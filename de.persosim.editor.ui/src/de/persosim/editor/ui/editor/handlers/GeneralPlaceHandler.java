package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.StringJoiner;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.LengthChecker;
import de.persosim.editor.ui.editor.checker.OrChecker;
import de.persosim.editor.ui.editor.checker.TextFieldChecker;
import de.persosim.editor.ui.editor.checker.UpperCaseTextFieldChecker;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class GeneralPlaceHandler extends ConstructedTlvHandler {

	public GeneralPlaceHandler(boolean compress) {
		super(compress);
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ConstructedTlvDataObject) {
			ConstructedTlvDataObject generalPlace = (ConstructedTlvDataObject) object;
			if (TlvConstants.TAG_SEQUENCE.equals(generalPlace.getTlvTag()) || TlvConstants.TAG_A1.equals(generalPlace.getTlvTag()) || TlvConstants.TAG_A2.equals(generalPlace.getTlvTag())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setText(TreeItem item) {
		StringJoiner joiner = new StringJoiner(", ");
		extractPrimitiveStrings(joiner, (TlvDataObject) item.getData());
		String newText = joiner.toString();
		
		ObjectHandler handler = (ObjectHandler) item.getData(HANDLER);
		if (handler != null) {
			newText += getChangedText(item);
		}

		item.setText(newText);
		
	}

	private void extractPrimitiveStrings(StringJoiner joiner, TlvDataObject data) {
		if (data instanceof PrimitiveTlvDataObject) {
			joiner.add(shorten(StringTlvHandler.getStringFromTlv((PrimitiveTlvDataObject) data)));
		} else if (data instanceof ConstructedTlvDataObject) {
			for (TlvDataObject current : ((ConstructedTlvDataObject) data).getTlvDataObjectContainer()
					.getTlvObjects()) {
				extractPrimitiveStrings(joiner, current);
			}
		}
	}

	private String shorten(String string) {
		if (string.length() > 16) {
			return string.substring(0, 15) + "...";
		}
		return string;
	}

	@Override
	protected String getType() {
		return "GeneralPlace";
	}

	@Override
	protected void handleItem(ConstructedTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		item.setData(tlv);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		ConstructedTlvDataObject tlv = (ConstructedTlvDataObject) item.getData();
		
		if (TlvConstants.TAG_SEQUENCE.equals(tlv.getTlvTag())){
			createField(item, false, composite, tlv, TlvConstants.TAG_AA, TlvConstants.TAG_UTF8_STRING,	StandardCharsets.UTF_8, "Street", new UpperCaseTextFieldChecker());
			createField(item, true, composite, tlv, TlvConstants.TAG_AB, TlvConstants.TAG_UTF8_STRING, StandardCharsets.UTF_8, "City", new UpperCaseTextFieldChecker());
			createField(item, false, composite, tlv, TlvConstants.TAG_AC, TlvConstants.TAG_UTF8_STRING,	StandardCharsets.UTF_8, "State or region", new UpperCaseTextFieldChecker());
			createField(item, true, composite, tlv, TlvConstants.TAG_AD, TlvConstants.TAG_PRINTABLE_STRING,	StandardCharsets.US_ASCII, "Country code", new AndChecker(new OrChecker(new LengthChecker(1, 1), new LengthChecker(3, 3), new UpperCaseTextFieldChecker())));
			createField(item, false, composite, tlv, TlvConstants.TAG_AE, TlvConstants.TAG_PRINTABLE_STRING, StandardCharsets.US_ASCII, "Zipcode", new UpperCaseTextFieldChecker());
		} else if (TlvConstants.TAG_A1.equals(tlv.getTlvTag())){
			createSimpleField(item, true, composite, tlv, StandardCharsets.UTF_8, "Freetext Place", new UpperCaseTextFieldChecker());
		} else if (TlvConstants.TAG_A2.equals(tlv.getTlvTag())){
			createSimpleField(item, true, composite, tlv, StandardCharsets.UTF_8, "NoPlaceInfo", new UpperCaseTextFieldChecker());
		}
		

	}

	private void createSimpleField(TreeItem item, boolean mandatory, Composite composite, ConstructedTlvDataObject wrapper, Charset charset, String infoText, TextFieldChecker checker) {

			EditorFieldHelper.createField(item, mandatory, composite, new AbstractObjectModifier() {
				
				@Override
				public void setValue(String value) {
					PrimitiveTlvDataObject ptlv = (PrimitiveTlvDataObject) wrapper.getTlvDataObjectContainer().iterator().next();
					ptlv.setValue(value.getBytes(charset));
					ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
					if (handler != null){
						handler.changed(item);
					}
				}
				
				@Override
				public void remove() {
				}
				
				@Override
				public String getValue() {
					return new String(wrapper.getTlvDataObjectContainer().iterator().next().getValueField(), charset);
				}
			}, checker, infoText);
		

	}

private void createField(TreeItem item, boolean mandatory, Composite composite, ConstructedTlvDataObject generalPlaceSequence,
		TlvTag tlvTag, TlvTag typeTag, Charset charset, String infoText, TextFieldChecker checker) {

		EditorFieldHelper.createField(item, mandatory, composite, new AbstractObjectModifier() {
			
			@Override
			public void setValue(String value) {
				if (!generalPlaceSequence.containsTlvDataObject(tlvTag)){
					generalPlaceSequence.addTlvDataObject(new ConstructedTlvDataObject(tlvTag));
				}
				ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) generalPlaceSequence.getTlvDataObject(tlvTag);
				if (!ctlv.containsTlvDataObject(typeTag)){
					ctlv.addTlvDataObject(new PrimitiveTlvDataObject(typeTag));
				}
				PrimitiveTlvDataObject ptlv = (PrimitiveTlvDataObject) ctlv.getTlvDataObject(typeTag);
				ptlv.setValue(value.getBytes(charset));
				
				generalPlaceSequence.sort(new Comparator<TlvDataObject>() {
					
					@Override
					public int compare(TlvDataObject o1, TlvDataObject o2) {
						return o1.getTagNo() - o2.getTagNo();
					}
				});
				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null){
					handler.changed(item);
				}
			}
			
			@Override
			public void remove() {
				if (generalPlaceSequence.containsTlvDataObject(tlvTag)){
					generalPlaceSequence.removeTlvDataObject(tlvTag);
				}
				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null){
					handler.changed(item);
				}
			}
			
			@Override
			public String getValue() {
				if (generalPlaceSequence.containsTlvDataObject(tlvTag)){
					ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) generalPlaceSequence.getTlvDataObject(tlvTag);
					if (ctlv.containsTlvDataObject(typeTag)){
						return new String(ctlv.getTlvDataObject(typeTag).getValueField(), charset);
					}
				}
				return null;
			}
		}, checker, infoText);
	

}
}
