package de.persosim.editor.ui.editor;

import java.util.HashMap;
import java.util.Map;

public class EidDgMapping {

	public static Map<Integer, String> getMapping() {
		Map<Integer, String> dgMapping = new HashMap<>();
		dgMapping.put((Integer) 0x01, "Document Type");
		dgMapping.put((Integer) 0x02, "Issuing Entity");
		dgMapping.put((Integer) 0x03, "Validity Period");
		dgMapping.put((Integer) 0x04, "Given Names");
		dgMapping.put((Integer) 0x05, "Family Names");
		dgMapping.put((Integer) 0x06, "Nom de Plume");
		dgMapping.put((Integer) 0x07, "Academic Title");
		dgMapping.put((Integer) 0x08, "Date of Birth");
		dgMapping.put((Integer) 0x09, "Place of Birth");
		dgMapping.put((Integer) 0x0A, "Nationality");
		dgMapping.put((Integer) 0x0B, "Sex");
		dgMapping.put((Integer) 0x0C, "Optional Data");
		dgMapping.put((Integer) 0x0D, "Birth Name");
		dgMapping.put((Integer) 0x0E, "Written Signature");
		dgMapping.put((Integer) 0x0F, "RFU");
		dgMapping.put((Integer) 0x10, "RFU");
		dgMapping.put((Integer) 0x11, "Place of Residence");
		dgMapping.put((Integer) 0x12, "Municipality ID");
		dgMapping.put((Integer) 0x13, "Residence Permit I");
		dgMapping.put((Integer) 0x14, "Residence Permit II");
		dgMapping.put((Integer) 0x15, "Phone Number");
		dgMapping.put((Integer) 0x16, "Email Address");
		return dgMapping;
	}

	public static Map<String, Integer> getMappingToNumber() {
		Map<String, Integer> dgMapping = new HashMap<>();
		dgMapping.put("Document Type", (Integer) 0x01);
		dgMapping.put("Issuing Entity", (Integer) 0x02);
		dgMapping.put("Validity Period", (Integer) 0x03);
		dgMapping.put("Given Names", (Integer) 0x04);
		dgMapping.put("Family Names", (Integer) 0x05);
		dgMapping.put("Nom de Plume", (Integer) 0x06);
		dgMapping.put("Academic Title", (Integer) 0x07);
		dgMapping.put("Date of Birth", (Integer) 0x08);
		dgMapping.put("Place of Birth", (Integer) 0x09);
		dgMapping.put("Nationality", (Integer) 0x0A);
		dgMapping.put("Sex", (Integer) 0x0B);
		dgMapping.put("Optional Data", (Integer) 0x0C);
		dgMapping.put("Birth Name", (Integer) 0x0D);
		dgMapping.put("Written Signature", (Integer) 0x0E);
		dgMapping.put("RFU", (Integer) 0x0F);
		dgMapping.put("RFU", (Integer) 0x10);
		dgMapping.put("Place of Residence", (Integer) 0x11);
		dgMapping.put("Municipality ID", (Integer) 0x12);
		dgMapping.put("Residence Permit I", (Integer) 0x13);
		dgMapping.put("Residence Permit II", (Integer) 0x14);
		dgMapping.put("Phone Number", (Integer) 0x15);
		dgMapping.put("Email Address", (Integer) 0x16);
		return dgMapping;
	}

}
