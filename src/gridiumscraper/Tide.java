package gridiumscraper;

import java.util.Date;

public class Tide {
	private Date date;
	private float height;
	
	public Tide(Date date, float height) {
		this.date = date;
		this.height = height;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
