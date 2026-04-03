package Sb_new_project.demo.util;

public class RegexConstant {
    private RegexConstant(){}

    public static final String USERNAME = "^[A-Za-z][A-Za-z0-9_ ]{2,49}$";

    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&]).{6,}$";

    public static final String PHONE = "^(\\+91)?[6-9][0-9]{9}$";


}
