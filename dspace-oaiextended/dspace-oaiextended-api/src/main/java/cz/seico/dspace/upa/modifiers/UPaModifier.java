package cz.seico.dspace.upa.modifiers;

import java.sql.SQLException;

import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

@SuppressWarnings("deprecation")
public class UPaModifier extends Modifier {

	private static final String DC_SCHEMA = "dc";

	/**
	 * Provede transformaci nazvu elementu a qualifieru. Nezmeni language ani
	 * value.
	 * 
	 * @param item
	 *            Zaznam
	 * @param element1
	 *            Puvodni element
	 * @param qualifier1
	 *            Puvodni qualifier
	 * @param element2
	 *            Novy element
	 * @param qualifier2
	 *            Novy qualifier
	 * @return Zmeneny zaznam
	 */
	private Item transformElementAndQualifier(Item item, String element1,
			String qualifier1, String element2, String qualifier2) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, element1, qualifier1,
				Item.ANY);
		item.clearMetadata(DC_SCHEMA, element1, qualifier1, Item.ANY);
		for (DCValue dcValue : metadata) {
			item.addMetadata(DC_SCHEMA, element2, qualifier2, dcValue.language,
					dcValue.value);
		}
		return item;
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			Item item = oaiItem.getInformation().item;
			transformItem(item);
		}
	}

	/**
	 * Provede kompletni transformaci zaznamu z UPa formatu do DRIVER formatu. K
	 * tomu pouzije ostatni transformacni metody. Je vhodne volat pouze tuto
	 * metodu z metody modifyItem.
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item transformItem(Item item) {
		// item = transformContributorAuthor2Creator(item); //NEE

		// tohle se musi volat uplne na zacatku!
		// item = deleteIdentifiersWithoutHandle(item);

		item = trimGarbage(item);

		item = transformRights(item);

		item = transformDateIssued2Date(item);
		item = transformIdentifierIssn2Source(item);
		item = transformIdentifierUri2Identifier(item);
		item = transformDescriptionAbstract2Description(item);
		item = transformLanguageIso2Language(item);
		item = transformRelationIspartof2Source(item);
		item = transformContributorAdvisor2Contributor(item);
		// item = assignFormat(item); //NEE
		item = deleteFormatsWithoutMimetype(item);
		item = transformValuesDcType(item);

		item = normalizeNames(item);

		item = transformProjectId(item);

		// musi se volat na konci vsech transformaci!
		// item = clearUnusedMetadata(item); //NEE

		item = removeEmptyMetadata(item);

		return item;
	}

	private Item transformProjectId(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "project", "ID",
				Item.ANY);
		item.clearMetadata(DC_SCHEMA, "project", "ID", Item.ANY);
		for (DCValue dcValue : metadata) {
			item.addMetadata(dcValue.schema, "relation", null,
					dcValue.language, "info:eu-repo/grantAgreement/EC/FP7/"
							+ dcValue.value);
		}
		return item;
	}

	private Item transformRights(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "rights", Item.ANY,
				Item.ANY);
		item.clearMetadata(DC_SCHEMA, "rights", Item.ANY, Item.ANY);
		for (DCValue dcValue : metadata) {
			if (dcValue.value != null) {
				if (dcValue.value.equalsIgnoreCase("bez omezení")) {
					dcValue.value = "info:eu-repo/semantics/openAccess";
				} else if (dcValue.value
						.equalsIgnoreCase("pouze v rámci univerzity")) {
					dcValue.value = "info:eu-repo/semantics/restrictedAccess";
				} else if (dcValue.value.equalsIgnoreCase("nepřístupná")) {
					dcValue.value = "info:eu-repo/semantics/closedAccess";
				} else if (dcValue.value.equalsIgnoreCase("open access")) {
					dcValue.value = "info:eu-repo/semantics/openAccess";
				}
				// TODO DODELAT EMBARGO!
			}
			item.addMetadata(dcValue.schema, dcValue.element,
					dcValue.qualifier, dcValue.language, dcValue.value);
		}
		if (metadata.length == 0) {
			// nema zadna prava
			try {
				Bundle[] bundles = item.getBundles("ORIGINAL");
				if (bundles.length > 0) {
					// ma soubory => je nutne nastavit prava
					item.addMetadata(DC_SCHEMA, "rights", null, Item.ANY,
							"info:eu-repo/semantics/openAccess");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	private Item trimGarbage(Item item) {
		item.clearMetadata(DC_SCHEMA, "title", "alternative", Item.ANY);
		item.clearMetadata(DC_SCHEMA, "identifier", "signature", Item.ANY);
		item.clearMetadata(DC_SCHEMA, "identifier", "stag", Item.ANY);
		return item;
	}

	private String normalizeNameZkratkaNaKonci(String[] partName) {
		if (partName.length == 3 && partName[2].length() == 2
				&& partName[2].endsWith(".")) {
			// jmeno je ve formatu: Kaylor, Michael M.
			// udelam z toho format: Kaylor, M. Michael
			return partName[0].trim() + " " + partName[2].trim() + " "
					+ partName[1].trim();
		}
		return null;
	}

	Item normalizeNames(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "contributor",
				Item.ANY, Item.ANY);
		item.clearMetadata(DC_SCHEMA, "contributor", Item.ANY, Item.ANY);
		for (DCValue dcValue : metadata) {
			String name = dcValue.value.trim();
			if (name != null) {
				String[] partName = name.split(" ");
				String newName = normalizeNameZkratkaNaKonci(partName);
				if (newName != null) {
					// item.clearMetadata(dcValue.schema, dcValue.element,
					// dcValue.qualifier, dcValue.language);
					dcValue.value = newName;
				}
			}
			item.addMetadata(dcValue.schema, dcValue.element,
					dcValue.qualifier, dcValue.language, dcValue.value);
		}
		return item;
	}

	/**
	 * Smaze tagy s prazdnymi metadaty
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item removeEmptyMetadata(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, Item.ANY, Item.ANY,
				Item.ANY);
		for (DCValue dcValue : metadata) {
			if (dcValue.value == null || dcValue.value.isEmpty()) {
				item.clearMetadata(dcValue.schema, dcValue.element,
						dcValue.qualifier, dcValue.language);
			}
		}
		return item;
	}

	/**
	 * Smaze prebytecna metadata ze zaznamu. DRIVERu se totiz bude posilat pouze
	 * jednoznacna mnozina zaznamu. Tato metoda by se mela volat uplne na konci
	 * vsech transformaci, protoze oreze pouze prebytecna metadata, ktera se
	 * nebudou do DRIVERu posilat.
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item clearUnusedMetadata(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, Item.ANY, Item.ANY,
				Item.ANY);
		item.clearMetadata(DC_SCHEMA, Item.ANY, Item.ANY, Item.ANY);
		for (DCValue dcValue : metadata) {
			boolean pridat = false;
			if (dcValue.qualifier != null) {
				continue;
			}
			String element = dcValue.element;
			if ("creator".equals(element)) {
				pridat = true;
			} else if ("date".equals(element)) {
				pridat = true;
			} else if ("source".equals(element)) {
				pridat = true;
			} else if ("identifier".equals(element)) {
				pridat = true;
			} else if ("description".equals(element)) {
				pridat = true;
			} else if ("language".equals(element)) {
				pridat = true;
			} else if ("publisher".equals(element)) {
				pridat = true;
			} else if ("source".equals(element)) {
				pridat = true;
			} else if ("subject".equals(element)) {
				pridat = true;
			} else if ("title".equals(element)) {
				pridat = true;
			} else if ("type".equals(element)) {
				pridat = true;
			} else if ("format".equals(element)) {
				pridat = true;
			} else if ("contributor".equals(element)) {
				pridat = true;
			}
			if (pridat) {
				item.addMetadata(DC_SCHEMA, dcValue.element, dcValue.qualifier,
						dcValue.language, dcValue.value);
			}
		}
		return item;
	}

	/**
	 * Transformuje dc.type. Jedna se nejprve o transformaci dc.type a pote
	 * transformaci dc.publicationstatus na dc.type.
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item transformValuesDcType(Item item) {
		// musi se volat jako prvni!
		item = transformValuesDcType2DcType(item);
		// musi se volat jako druha!
		item = transformValuesDcPublicationstatus2DcType(item);
		return item;
	}

	/**
	 * Provede transformaci obsahu pole dc.publicationstatus z formatu DSpace
	 * UPa na format DRIVERu. Navic pretransformuje umisteni pole na dc.type.
	 * Napriklad text "published" zmeni na "publishedVersion". Tyto transformace
	 * provede pouze u jednoznacne definovanych typu. U ostatnich typu ponecha
	 * hodnotu pole dc.publicationstatus nezmenenou. Tato metoda se musi volat
	 * az po zavolani metody transformValuesDcType2DcType()!
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item transformValuesDcPublicationstatus2DcType(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "publicationstatus",
				null, Item.ANY);
		item.clearMetadata(DC_SCHEMA, "publicationstatus", null, Item.ANY);
		for (DCValue dcValue : metadata) {
			String typeNew = null;
			String typeOld = dcValue.value;
			if (typeOld != null) {
				typeOld = typeOld.trim();
			}
			if ("published".equalsIgnoreCase(typeOld)) {
				typeNew = "publishedVersion";
			} else if ("postprint".equalsIgnoreCase(typeOld)) {
				typeNew = "acceptedVersion";
			} else if ("preprint".equalsIgnoreCase(typeOld)) {
				typeNew = "submittedVersion";
			} else {
				typeNew = typeOld;
			}
			if (typeNew != null) {
				item.addMetadata(DC_SCHEMA, "type", null, Item.ANY, typeNew);
			}
		}
		return item;
	}

	/**
	 * Provede transformaci obsahu pole dc.type z formatu DSpace UPa na format
	 * DRIVERu. Napriklad text "bakalarska prace" zmeni na "bachelorThesis".
	 * Tyto transformace provede pouze u jednoznacne definovanych typu. U
	 * ostatnich typu ponecha hodnotu pole dc.type nezmenenou. Tato metoda se
	 * musi volat pred zavolanim metody
	 * transformValuesDcPublicationstatus2DcType()!
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item transformValuesDcType2DcType(Item item) {
		DCValue[] metadata = item
				.getMetadata(DC_SCHEMA, "type", null, Item.ANY);
		item.clearMetadata(DC_SCHEMA, "type", null, Item.ANY);
		for (DCValue dcValue : metadata) {
			String typeNew = null;
			String typeOld = dcValue.value;
			if (typeOld != null) {
				typeOld = typeOld.trim();
			}
			if (typeOld == null) {
				typeNew = typeOld;
			} else if (typeOld.toLowerCase().startsWith("bakalářská práce")) {
				typeNew = "bachelorThesis";
			} else if (typeOld.toLowerCase().startsWith("diplomová práce")) {
				typeNew = "masterThesis";
			} else if (typeOld.toLowerCase().startsWith("disertační práce")) {
				typeNew = "doctoralThesis";
			} else if (typeOld.toLowerCase().startsWith("závěrečná práce")) {
				typeNew = "other";
			} else {
				typeNew = typeOld;
			}
			if (typeNew != null) {
				if (typeNew.startsWith("info:eu-repo/semantics/")) {
					typeNew = typeNew.replace("info:eu-repo/semantics/", "");
				}
				// nastavi prvni pismeno typeNew na lowercase
				if (typeNew.length() > 0) {
					String firstCharacter = "" + typeNew.charAt(0);
					typeNew = firstCharacter.toLowerCase()
							+ typeNew.substring(1);
				}
				item.addMetadata(DC_SCHEMA, "type", null, Item.ANY,
						"info:eu-repo/semantics/" + typeNew);
			}
		}
		return item;
	}

	/**
	 * Pokusi se nalezt pole dc.format.mimetype v zaznamu. Takove pole byva u
	 * VSKP.
	 * 
	 * @param item
	 *            Zaznam
	 * @return Nalezeny dc.format.mimetype. Pokud toto pole neexistuje, pak
	 *         vraci null. V pripade, ze je takovych poli u jednoho zaznamu
	 *         vice, tak se vyhodi runtime exception.
	 */
	String findFormatMimetypeFromMetadata(Item item) {
		DCValue[] dcValues = item.getMetadata(DC_SCHEMA, "format", "mimetype",
				Item.ANY);
		if (dcValues.length > 1) {
			throw new RuntimeException(
					"Zaznam nemuze mit vice nez jeden dc.format.mimetype! Handle zaznamu: "
							+ item.getHandle());
		} else if (dcValues.length == 0) {
			return null;
		} else {
			return dcValues[0].value;
		}
	}

	/**
	 * Pokusi se ziskat mimetype z primarniho bitstreamu zaznamu. Pokud takovy
	 * bitstream neexistuje, pak vrati null.
	 * 
	 * @param item
	 *            Zaznam
	 * @return Mimetype primarniho bitstreamu nebo null v pripade, ze zaznam
	 *         nema primarni bitstream.
	 */
	String findFormatMimetypeFromPrimaryBitstream(Item item) {
		Bundle[] bundles = null;
		try {
			bundles = item.getBundles("ORIGINAL");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		if (bundles.length > 1) {
			throw new RuntimeException(
					"Zaznam nemuze mit vice nez jeden bundle s nazvem ORIGINAL! Handle zaznamu: "
							+ item.getHandle());
		} else if (bundles.length == 0) {
			return null;
		} else {
			Bundle bundleOriginal = bundles[0];
			int primaryBitstreamID = bundleOriginal.getPrimaryBitstreamID();
			if (primaryBitstreamID == -1) {
				return null;
			} else {
				Bitstream[] bitstreams = bundleOriginal.getBitstreams();
				for (Bitstream bitstream : bitstreams) {
					if (bitstream.getID() == primaryBitstreamID) {
						return bitstream.getFormat().getMIMEType();
					}
				}
				return null;
			}
		}
	}

	/**
	 * Pokusi se ziskat mimetype z prvniho bitstreamu zaznamu. Pokud zaznam nema
	 * zadne soubory, pak vrati null.
	 * 
	 * @param item
	 *            Zaznam
	 * @return Mimetype prvniho bitstream zaznamu nebo null v pripade, ze zaznam
	 *         nema zadny bitstream.
	 */
	String findFormatMimetypeFromFirstBitstream(Item item) {
		Bundle[] bundles = null;
		try {
			bundles = item.getBundles("ORIGINAL");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		if (bundles.length > 1) {
			throw new RuntimeException(
					"Zaznam nemuze mit vice nez jeden bundle s nazvem ORIGINAL! Handle zaznamu: "
							+ item.getHandle());
		} else if (bundles.length == 0) {
			return null;
		} else {
			Bundle bundleOriginal = bundles[0];
			Bitstream[] bitstreams = bundleOriginal.getBitstreams();
			if (bitstreams.length > 0) {
				return bitstreams[0].getFormat().getMIMEType();
			} else {
				return null;
			}
		}
	}

	/**
	 * Smaze vsechny hodnoty pole dc.format. Mimetype se nastavi automaticky na
	 * zaklade prilozenych souboru. Ostatni formaty jsou zbytecne.
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item deleteFormatsWithoutMimetype(Item item) {
		// smaze jakykoli dc.format, ktery by mohl byt nastaveny
		// krome mimetype
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "format", Item.ANY,
				Item.ANY);
		for (DCValue dcValue : metadata) {
			if (!"*".equals(dcValue.qualifier)) {
				item.clearMetadata(dcValue.schema, dcValue.element,
						dcValue.qualifier, dcValue.language);
			}
		}
		return item;
	}

	/**
	 * Smaze vsechny hodnoty pole dc.identifier krome tech, co obsahuji handle
	 * (cili retezec "hdl.handle.net")
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item deleteIdentifiersWithoutHandle(Item item) {
		DCValue[] metadata = item.getMetadata(DC_SCHEMA, "identifier",
				Item.ANY, Item.ANY);
		for (DCValue dcValue : metadata) {
			if (dcValue.value != null
					&& dcValue.value.indexOf("hdl.handle.net") == -1) {
				item.clearMetadata(dcValue.schema, dcValue.element,
						dcValue.qualifier, dcValue.language);
			}
		}
		return item;
	}

	/**
	 * Priradi k zaznamu mimetype. Pokusi se hledat mimetype v metadatech,
	 * primarnim bitstreamu a kdyz to selze, tak vezme mimetype prvniho souboru
	 * u zaznamu. Zjisteny mimetype ulozi do pole dc.format.
	 * 
	 * @param item
	 *            Vstupni zaznam
	 * @return Vystupni zaznam
	 */
	Item assignFormat(Item item) {
		// smaze jakykoli dc.format, ktery by mohl byt nastaveny
		item.clearMetadata(DC_SCHEMA, "format", null, Item.ANY);
		// ziska mimetype z pole dc.format.mimetype
		String type = findFormatMimetypeFromMetadata(item);
		if (type == null) {
			// ziska mimetype z primarniho bitstreamu
			type = findFormatMimetypeFromPrimaryBitstream(item);
			if (type == null) {
				// ziska mimetype z prvniho souboru v ORIGINAL bundle
				type = findFormatMimetypeFromFirstBitstream(item);
			}
		}
		if (type != null) {
			item.addMetadata(DC_SCHEMA, "format", null, Item.ANY, type);
		}
		return item;
	}

	Item transformContributorAuthor2Creator(Item item) {
		return transformElementAndQualifier(item, "contributor", "author",
				"creator", null);
	}

	Item transformDateIssued2Date(Item item) {
		return transformElementAndQualifier(item, "date", "issued", "date",
				null);
	}

	Item transformIdentifierIssn2Source(Item item) {
		return transformElementAndQualifier(item, "identifier", "issn",
				"source", null);
	}

	Item transformIdentifierUri2Identifier(Item item) {
		// nejprve musim smazat identifier.null
		// je tam vetsinou Univerzitní knihovna (studovna)
		item.clearMetadata(DC_SCHEMA, "identifier", null, Item.ANY);
		// nasledne na jeho misto dam handle
		return transformElementAndQualifier(item, "identifier", "uri",
				"identifier", null);
	}

	Item transformDescriptionAbstract2Description(Item item) {
		return transformElementAndQualifier(item, "description", "abstract",
				"description", null);
	}

	Item transformLanguageIso2Language(Item item) {
		return transformElementAndQualifier(item, "language", "iso",
				"language", null);
	}

	Item transformRelationIspartof2Source(Item item) {
		return transformElementAndQualifier(item, "relation", "ispartof",
				"source", null);
	}

	Item transformContributorAdvisor2Contributor(Item item) {
		return transformElementAndQualifier(item, "contributor", "advisor",
				"contributor", null);
	}

}

