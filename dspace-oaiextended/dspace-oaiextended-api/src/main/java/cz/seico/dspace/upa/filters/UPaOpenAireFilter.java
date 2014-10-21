package cz.seico.dspace.upa.filters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dspace.content.Bundle;
import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

@SuppressWarnings("deprecation")
public class UPaOpenAireFilter extends Filter {

	public static final String APPLICABLE_SETS_SPECS = "ec_fundedresources";
	private static final String DC_SCHEMA = "dc";
	private List<String> applicableSetSpecs;

	public UPaOpenAireFilter() {
		String[] array = super.getProperty("applicableSetsSpecs",
				APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
	}

	@Override
	public boolean isItemFiltered(AgentItem item) {
		// zjisti, jestli ma zaznam vyplnene pole dc.project.ID
		// pokud nema, tak nema vyznam pokracovat
		DCValue[] metadata = item.getInformation().item.getMetadata(DC_SCHEMA,
				"project", "ID", Item.ANY);
		if (metadata == null || metadata.length == 0) {
			return true;
		}
		// zjisti, jestli ma zaznam soubory
		// pokud zaznam nema soubory, nema vyznam pokracovat
		try {
			Bundle[] bundles = item.getInformation().item
					.getBundles("ORIGINAL");
			if (bundles.length == 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// zjisti, jestli je zaznam namapovan v kolekci publikacni cinnost
		@SuppressWarnings("rawtypes")
		List handles = item.getInformation().collectionHandles;
		for (Object object : handles) {
			String handle = object.toString();

			// ziska handle kolekce s publikacni cinnosti
			String handlePublikacniCinnost;
			if (handle.startsWith("123456789")) {
				handlePublikacniCinnost = "123456789/3";
			} else {
				handlePublikacniCinnost = "10195/34541";
			}

			if (handle.equals(handlePublikacniCinnost)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isFilterApplicable() {
		if (!super.getContext().isVirtualSet())
			return false;
		if (this.applicableSetSpecs.contains(super.getContext().getHarvestSet()
				.toLowerCase()))
			return true;
		else
			return false;
	}

	@Override
	protected void updateContext() {
		super.getContext().setHarvestSet(null);
	}
}

