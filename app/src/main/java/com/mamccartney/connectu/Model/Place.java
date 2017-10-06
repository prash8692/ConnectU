package com.mamccartney.connectu.Model;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * https://www.census.gov/geo/maps-data/data/gazetteer2016.html
 * Illinois Places
 * https://www2.census.gov/geo/docs/maps-data/data/gazetteer/2016_Gazetteer/2016_gaz_place_17.txt
 * Created by sonic on 5/11/2017.
 */

public class Place {
/*
                   "USPS": "IL",
                  "GEOID": 1703012,
               "ANSICODE": "02394031",
                   "NAME": "Aurora city",
                   "LSAD": 25,
               "FUNCSTAT": "A",
                  "ALAND": 116273603,
                 "AWATER": 2278285,
             "ALAND_SQMI": 44.893,
            "AWATER_SQMI": 0.880,
               "INTPTLAT": 41.763455,
              "INTPTLONG": -88.290099

USPS	    United States Postal Service State Abbreviation
GEOID	    Geographic Identifier - fully concatenated geographic code
                (State FIPS and Place FIPS)
ANSICODE	American National Standards Insititute code
NAME	    Name
LSAD	    Legal/Statistical area descriptor
FUNCSTAT	Functional status of entity
ALAND	    Land Area (square meters) - Created for statistical purposes only
AWATER	    Water Area (square meters) - Created for statistical purposes only
ALAND_SQMI	Land Area (square miles) - Created for statistical purposes only
AWATER_SQMI	Water Area (square miles) - Created for statistical purposes only
INTPTLAT	Latitude (decimal degrees) First character is blank or
                "-" denoting North or South latitude respectively
INTPTLONG	Longitude (decimal degrees) First character is blank or
                "-" denoting East or West longitude respectively
*/

    private String usps;
    private Integer geoid;
    private String ansicode;
    private String name;
    private Integer lsad;
    private String funcstat;
    private Integer aland;
    private Integer awater;
    private Double aland_sqmi;
    private Double awater_sqmi;
    private Double intptlat;
    private Double intptlong;

    public Place() {}

    public Place jsonObject2Place(JSONObject jsonCity) {
        Place place = new Place();
        try {
            place.setUsps(jsonCity.getString("USPS"));
            place.setGeoid(jsonCity.getInt("GEOID"));
            place.setAnsicode(jsonCity.getString("ANSICODE"));
            place.setName(jsonCity.getString("NAME"));
            place.setLsad(jsonCity.getInt("LSAD"));
            place.setFuncstat(jsonCity.getString("FUNCSTAT"));
            place.setAland(jsonCity.getInt("ALAND"));
            place.setAwater(jsonCity.getInt("AWATER"));
            place.setAland_sqmi(jsonCity.getDouble("ALAND_SQMI"));
            place.setAwater_sqmi(jsonCity.getDouble("AWATER_SQMI"));
            place.setIntptlat(jsonCity.getDouble("INTPTLAT"));
            place.setIntptlong(jsonCity.getDouble("INTPTLONG"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }

    public static Place jsonReader2Place(JsonReader reader) {
        Place place = new Place();
        try {
            reader.beginObject();  // begin place json object
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "USPS":
                        place.setUsps(reader.nextString());
                        break;
                    case "GEOID":
                        place.setGeoid(reader.nextInt());
                        break;
                    case "ANSICODE":
                        place.setAnsicode(reader.nextString());
                        break;
                    case "NAME":
                        place.setName(reader.nextString());
                        break;
                    case "LSAD":
                        place.setLsad(reader.nextInt());
                        break;
                    case "FUNCSTAT":
                        place.setFuncstat(reader.nextString());
                        break;
                    case "ALAND":
                        place.setAland(reader.nextInt());
                        break;
                    case "AWATER":
                        place.setAwater(reader.nextInt());
                        break;
                    case "ALAND_SQMI":
                        place.setAland_sqmi(reader.nextDouble());
                        break;
                    case "AWATER_SQMI":
                        place.setAwater_sqmi(reader.nextDouble());
                        break;
                    case "INTPTLAT":
                        place.setIntptlat(reader.nextDouble());
                        break;
                    case "INTPTLONG":
                        place.setIntptlong(reader.nextDouble());
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();  // end place json object
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return place;
    }

    public String getUsps() { return usps; }
    public void setUsps(String usps) { this.usps = usps; }

    public Integer getGeoid() { return geoid; }
    public void setGeoid(Integer geoid) { this.geoid = geoid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAnsicode() { return ansicode; }
    public void setAnsicode(String ansicode) { this.ansicode = ansicode; }

    public Integer getLsad() { return lsad; }
    public void setLsad(Integer lsad) { this.lsad = lsad; }

    public String getFuncstat() { return funcstat; }
    public void setFuncstat(String funcstat) { this.funcstat = funcstat; }

    public Integer getAland() { return aland; }
    public void setAland(Integer aland) { this.aland = aland; }

    public Integer getAwater() { return awater; }
    public void setAwater(Integer awater) { this.awater = awater; }

    public Double getAland_sqmi() { return aland_sqmi; }
    public void setAland_sqmi(Double aland_sqmi) { this.aland_sqmi = aland_sqmi; }

    public Double getAwater_sqmi() { return awater_sqmi; }
    public void setAwater_sqmi(Double awater_sqmi) { this.awater_sqmi = awater_sqmi; }

    public Double getIntptlat() { return intptlat; }
    public void setIntptlat(Double intptlat) { this.intptlat = intptlat; }

    public Double getIntptlong() { return intptlong; }
    public void setIntptlong(Double intptlong) { this.intptlong = intptlong; }

    @Override
    public String toString() {
        return "Place{" +
            "usps='" + usps + '\'' +
            ", geoid=" + geoid +
            ", ansicode='" + ansicode + '\'' +
            ", name='" + name + '\'' +
            ", lsad=" + lsad +
            ", funcstat='" + funcstat + '\'' +
            ", aland=" + aland +
            ", awater=" + awater +
            ", aland_sqmi=" + aland_sqmi +
            ", awater_sqmi=" + awater_sqmi +
            ", intptlat=" + intptlat +
            ", intptlong=" + intptlong +
            '}';
    }
}
