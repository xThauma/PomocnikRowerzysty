package kamil.packahe;

/**
 * Created by Kamil on 06.06.2018.
 */

public class HistoryPlace {
    private String fromLat, toLat, fromLang, toLang, fromName, toName, totalKm;

    public HistoryPlace(String fromLat, String toLat, String fromLang, String toLang, String fromName, String toName, String totalKm) {
        this.fromLat = fromLat;
        this.toLat = toLat;
        this.fromLang = fromLang;
        this.toLang = toLang;
        this.fromName = fromName;
        this.toName = toName;
        this.totalKm = totalKm;
    }

    public String getFromLat() {
        return fromLat;
    }

    public void setFromLat(String fromLat) {
        this.fromLat = fromLat;
    }

    public String getToLat() {
        return toLat;
    }

    public void setToLat(String toLat) {
        this.toLat = toLat;
    }

    public String getFromLang() {
        return fromLang;
    }

    public void setFromLang(String fromLang) {
        this.fromLang = fromLang;
    }

    public String getToLang() {
        return toLang;
    }

    public void setToLang(String toLang) {
        this.toLang = toLang;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(String totalKm) {
        this.totalKm = totalKm;
    }

    @Override
    public String toString() {
        return  fromName + " - " + toName+" ("+totalKm+")";
    }
}
