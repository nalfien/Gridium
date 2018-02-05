package gridiumscraper;

public class City {
	private String cityName;
	private String stateName;
	
	public City(String cityStr) {
		int separator = cityStr.indexOf(", ");
		
		cityName = cityStr.substring(0, separator);
		stateName = cityStr.substring(separator + 2);
	}
	
	public String getUrl() {
		return urlify(cityName); 
	}
	
	public String getUrlWithState() {
		return urlify(cityName + " " + stateName);
	}
	
	@Override
	public String toString() {
		return cityName + ", " + stateName;
	}

	private String urlify(String toUrl) {
		return "https://www.tide-forecast.com/locations/" + toUrl.replaceAll("[^A-Za-z ]", "").replaceAll(" ", "-") + "/tides/latest";
	}
}
