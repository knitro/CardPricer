package knitro.betterSearch.database.card;

import java.util.Date;

public class DbPrinting implements Comparable<DbPrinting> {
	
	public enum FrameVersion {
		
		FRAME_1993("1993"), FRAME_1997("1997"), FRAME_2003("2003"), FRAME_2015("2015"), FRAME_future("future"); 
		
		private String value;
		private FrameVersion(String value) {
			this.value = value;
		}
		public static FrameVersion getFrameVersion(String value) {
			if ("1993".equals(value)) {
				return FRAME_1993;
			} else if ("1997".equals(value)) {
				return FRAME_1997;
			} else if ("2003".equals(value)) {
				return FRAME_2003;
			} else if ("2015".equals(value)) {
				return FRAME_2015;
			} else if ("future".equals(value)) {
				return FRAME_future;
			} else {
				return null;
			}
		}
	}
	
	public enum Rarity {
		COMMON, UNCOMMON, RARE, MYTHIC 
	}
	
	private String setCode;
	private String id;
	private Date date;
	private boolean hasFoil;
	private boolean hasNonFoil;
	private FrameVersion frame;
	private Rarity rarity;
	
	public DbPrinting(String setCode, String id, Date date, boolean hasFoil, boolean hasNonFoil, String frameVersion, String rarity) {
		super();
		this.setCode = setCode;
		this.id = id;
		this.date = date;
		this.hasFoil = hasFoil;
		this.hasNonFoil = hasNonFoil;
		this.frame = FrameVersion.getFrameVersion(frameVersion);
		this.rarity = Rarity.valueOf(rarity.toUpperCase());
	}
	
	public boolean isHasFoil() {
		return hasFoil;
	}

	public boolean isHasNonFoil() {
		return hasNonFoil;
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
	
	public FrameVersion getFrameVersion() {
		return frame;
	}
	 
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public int compareTo(DbPrinting o) {
		
		Date date_current = this.getDate();
		Date date_other = o.getDate();
		
		if ((date_current == null) && (date_other == null)) {
			return 0;
		} else if (date_current == null) {
			return -1;
		} else if (date_other == null) {
			return 1;
		}
		
		if (date_current.after(date_other)) {
			return -1;
		} else if (date_other.after(date_current)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
}
