package kr.co.ezinfotech.parkingparking.UTIL;

public class LoginManager {
    private static final LoginManager ourInstance = new LoginManager();

    private static boolean isLogin = false;
    private static String email = null;
    private static String name = null;
    private static String phone = null;

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private LoginManager() {
    }

    public static void setEmail(String emailVal) {
        email = emailVal;
    }

    public static void setName(String nameVal) {
        name = nameVal;
    }

    public static void setPhone(String phoneVal) {
        phone = phoneVal;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getPhone() {
        return phone;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static void login() {
        isLogin = true;
    }

    public static void logout() {
        email = "";
        name = "";
        phone = "";
        isLogin = false;
    }
}
