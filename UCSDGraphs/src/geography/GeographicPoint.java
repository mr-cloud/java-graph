package geography;

import java.awt.geom.Point2D.Double;

@SuppressWarnings("serial")
public class GeographicPoint extends Double {
	
	public GeographicPoint(double latitude, double longitude)
	{
		super(latitude, longitude);
	}
	
	/**
	 * Calculates the geographic distance in km between this point and 
	 * the other point. 
	 * @param other
	 * @return The distance between this lat, lon point and the other point
	 */
	public double distance(GeographicPoint other)
	{
		return getDist(this.getX(), this.getY(),
                other.getX(), other.getY());     
	}
	public double myDistance(GeographicPoint other)
	{
		return getMyDist(this.getX(), this.getY(),
                other.getX(), other.getY());     
	}
	
    /**
     * Calculates the geographic distance in km between this point and 
	 * the other point. 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return double ,the distance between two points on the earth surface.
     */
    private double getMyDist(double lat1, double lon1, double lat2, double lon2)
    {
    	int R = 6373; // radius of the earth in kilometres
    	lat1 = Math.toRadians(lat1);
    	lat2 = Math.toRadians(lat2);
    	lon1 = Math.toRadians(lon1);
    	lon2 = Math.toRadians(lon2);
    	
    	double angle = Math.acos(Math.sin(lon1)*Math.sin(lon2)
    			+ Math.cos(lon1)*Math.cos(lon2)*Math.cos(lat1 - lat2));
    	double arc = R * angle;
		return arc;
    }
    private double getDist(double lat1, double lon1, double lat2, double lon2)
    {
    	int R = 6373; // radius of the earth in kilometres
    	double lat1rad = Math.toRadians(lat1);
    	double lat2rad = Math.toRadians(lat2);
    	double deltaLat = Math.toRadians(lat2-lat1);
    	double deltaLon = Math.toRadians(lon2-lon1);

    	double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
    	        Math.cos(lat1rad) * Math.cos(lat2rad) *
    	        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    	double d = R * c;
    	return d;
    }
    
    public String toString()
    {
    	return "Lat: " + getX() + ", Lon: " + getY();
    }
	
	
}
