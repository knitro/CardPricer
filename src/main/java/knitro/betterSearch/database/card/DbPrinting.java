package knitro.betterSearch.database.card;

import java.util.Date;

public class DbPrinting implements Comparable<DbPrinting> {
	
	private String setCode;
	private String id;
	private Date date;
	
	public DbPrinting(String setCode, String id, Date date) {
		super();
		this.setCode = setCode;
		this.id = id;
		this.date = date;
	}
	
	public String getSetCode() {
		return setCode;
	}

	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int compareTo(DbPrinting o) {
		
		Date date_current = this.getDate();
		Date date_other = o.getDate();
		
		if (date_current.after(date_other)) {
			return -1;
		} else {
			return 1;
		}
	}
	
	
}
