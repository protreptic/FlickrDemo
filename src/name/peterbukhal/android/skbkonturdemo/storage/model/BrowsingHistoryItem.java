package name.peterbukhal.android.skbkonturdemo.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class BrowsingHistoryItem {

	@DatabaseField(id = true)
	private Integer id;
	
}
