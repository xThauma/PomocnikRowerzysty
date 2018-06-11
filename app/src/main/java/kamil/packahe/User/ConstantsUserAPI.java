package kamil.packahe.User;

/**
 * Created by Kamil Lenartowicz on 2018-01-09.
 */

public interface ConstantsUserAPI {
    String URL_ROOT = "http://192.168.209.1/zz/v1/";

    String URL_LOGIN = URL_ROOT + "userLogin.php?pesel=";

    String URL_REGISTER = URL_ROOT + "registerUser.php";

    String URL_POST_DATA = URL_ROOT + "postData.php";

    String URL_GET_DATA = URL_ROOT + "getData.php";

    String URL_GET_HISTORY = URL_ROOT + "getHistory.php";

    String URL_POST_HISTORY = URL_ROOT + "postHistory.php";

    String URL_GET_TOTALKMS = URL_ROOT + "getKm.php";

}
