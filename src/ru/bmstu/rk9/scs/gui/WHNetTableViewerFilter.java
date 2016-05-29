package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;

public class WHNetTableViewerFilter extends ViewerFilter {

	private String searchString;
	private String upperCaseSearchString;

	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
		upperCaseSearchString = ".*" + s.toUpperCase() + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		ResultItem r = (ResultItem) element;
		if (r.getWarehouse().getName().matches(searchString)) {
			return true;
		}
		if (r.getResource().getName().matches(searchString)) {
			return true;
		}
		if (r.getType().toString().matches(upperCaseSearchString)) {
			return true;
		}
		return false;
	}
}
