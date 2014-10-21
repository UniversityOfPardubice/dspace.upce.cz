package cz.seico.dspace.upa.filters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dspace.content.Bundle;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

public class UPaDriverFilter extends Filter {

	public static final String APPLICABLE_SETS_SPECS = "driver";

	private List<String> applicableSetSpecs;

	public UPaDriverFilter() {
		String[] array = super.getProperty("applicableSetsSpecs",
				APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
	}

	@Override
	public boolean isItemFiltered(AgentItem item) {
		try {
			Bundle[] bundles = item.getInformation().item
					.getBundles("ORIGINAL");
			if (bundles.length == 0) {
				// vrati true, pokud ma zaznam soubory
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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

