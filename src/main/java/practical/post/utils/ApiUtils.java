package practical.post.utils;

public class ApiUtils {
    public static String getMethodName(){
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
