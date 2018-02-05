package gridiumscraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GridiumScraper {
	/*
	 * Read in a carriage-return delimited list of cities.
	 * Pass those cities to the getPage() function.
	 * If that function can't find the city, it is probably saved with the state, try again with it.
	 * If it still fails, report the failure and move on.
	 * Having compiled all Tides, dump them to the console.
	 */
	public static void main(String[] args) throws IOException, ParseException {
		String holder;
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "cityList.txt"));
		} catch(FileNotFoundException ex) {
			System.out.println("To use: create a file called \"citylist.txt\" in the same directory as this jar, and put the name of each city on a new line in it.");
			return;
		}
		
		List<City> cityList = new ArrayList<City>();
		
		while((holder = br.readLine()) != null) {
			cityList.add(new City(holder));
		}
		
		br.close();
		
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		
		for(City city : cityList) {
			System.out.println(city.toString() + ":");
			
			List<Tide> tideList;
			
			tideList = getPage(city.getUrl());
			
			if(tideList == null)
			{
				tideList = getPage(city.getUrlWithState());
			}
			
			if(tideList == null)
			{
				System.out.println("City was not found, please check syntax.");
			}
			
			for(Tide tide : tideList) {
				System.out.println(outputFormat.format(tide.getDate()) + " - " + tide.getHeight());
			}
			
			System.out.println("");
		}
	}
	
	/*
	 * Get a connection to the given URL.
	 * If the URL can't be found, return null.
	 * Otherwise, generate some helper variables.
	 * Now, step through the page until you find the start of the relevant data.
	 * Continue stepping through, setting flags and variables as needed.
	 * If a low tide is found during the day, save it to the output list.
	 * Return that list.
	 */
	private static List<Tide> getPage(String pageUrl) throws IOException, ParseException {
		URL url;
		URLConnection con;
		BufferedReader br;
		
		url = new URL(pageUrl);

        con = url.openConnection();
		
        try {
        	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch(FileNotFoundException ex) {
        	return null;
        }
		
		String holder = "";
		boolean isDay = false;
		
		List<Tide> output = new ArrayList<Tide>(); 
		
		String currentYear = (new SimpleDateFormat("YYYY")).format(new Date());
		
		SimpleDateFormat currentFormat = new SimpleDateFormat("EEEE dd MMM yyyy HH:mm a");
		String currentDayStr = null;
		float tideHeightTemp = 0;
		Date currentDate = null;
		
        
		
		while (((holder = br.readLine()) != null) && !holder.contains("Tide table:</h2>"));
		
		while(((holder = br.readLine()) != null) && !holder.contains("</table>")) {
			if(holder.contains("class=\"date\"")) {
				currentDayStr = GetRowValue(holder) + " " + currentYear;
			}
			
			if(holder.contains("Sunrise")) {
				isDay = true;
			}
			
			if(holder.contains("Sunset")) {
				isDay = false;
			}
			
			if(isDay && holder.contains("class=\"time tide\"")) {
				String timeTemp = GetRowValue(holder);
				if(timeTemp.charAt(0) != ' ') {
					timeTemp = " " + timeTemp;
				}
				
				currentDate = currentFormat.parse(currentDayStr + timeTemp);
			}
			
			if(isDay && holder.contains("class=\"level\"") && (holder.contains("feet") || holder.contains("ft"))) {
				String tideHeightTempStr = GetRowValue(GetRowValue(holder));
				tideHeightTemp = Float.parseFloat(tideHeightTempStr.substring(0, tideHeightTempStr.indexOf(" ")));
			}
			
			if(isDay && holder.contains("class=\"tide\"") && holder.contains("Low Tide")) {
				output.add(new Tide(currentDate, tideHeightTemp));
			}
		}
		
		return output;
	}
	
	/*
	 * Helper function that gets the contents of a properly formatted HTML tag, i.e.:
	 * <tag>value</tag>
	 * Would return "value" 
	 */
	private static String GetRowValue(String row) {
		return row.substring(row.indexOf(">") + 1, row.lastIndexOf("<"));
	}
}
