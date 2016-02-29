package com.clarity.sourcemodel;

import java.util.ArrayList;

/**
 * Represents a reference to an external type in a source file.
 *
 * @author Muntazir Fadhel
 */
public class TypeReference {

	private String type;
	private ArrayList<Integer> lines = new ArrayList<Integer>();

	public TypeReference(String externalTypeName, int lineNum) {

		type = externalTypeName;
		lines.add(lineNum);
	}

	public TypeReference(TypeReference ref) {
		for (final Integer lineNum : ref.getReferenceLineNums()) {
			lines.add(lineNum);
		}
		type = ref.getExternalTypeName();
	}

	/**
	 * @return the externalTypeName
	 */
	public String getExternalTypeName() {
		return type;
	}

	/**
	 * @param externalTypeName
	 *            the externalTypeName to set
	 */
	public void setExternalTypeName(final String externalTypeName) {
		type = externalTypeName;
	}

	public void insertReferenceLineNum(final int lineNum) {
		if (!lines.contains(lineNum)) {
			lines.add(lineNum);
		}
	}

	/**
	 * @return the referenceLineNums
	 */
	public ArrayList<Integer> getReferenceLineNums() {
		return lines;
	}

	/**
	 * @param referenceLineNums
	 *            the referenceLineNum to set
	 */
	public void setReferenceLineNum(ArrayList<Integer> referenceLineNums) {
		lines = referenceLineNums;
	}
}
