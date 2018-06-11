package kamil.packahe;

/**
 * Created by Kamil on 29.05.2018.
 */

public class Widok {
    String komenatrz, typ, lon, lat;

    public Widok(String komenatrz, String typ, String lon, String lat) {
        this.komenatrz = komenatrz;
        this.typ = typ;
        this.lon = lon;
        this.lat = lat;
    }

    public String getKomenatrz() {
        return komenatrz;
    }

    public void setKomenatrz(String komenatrz) {
        this.komenatrz = komenatrz;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
