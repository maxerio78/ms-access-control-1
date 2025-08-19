package sunseries.travel.access.control.constant;

import com.google.gson.Gson;

public class Constant {

    public static final String PREFIX_ERROR_CODE = "10-";
    public static final String GENERAL_ERROR_CODE = "999999";
    public static final String NOT_FOUND_ERROR_CODE = "000001";
    public static final String DUPLICATED_ERROR_CODE = "000002";

    public static final Gson GSON = new Gson();

    public static final String PERMISSION_PREFIX = "permission::";
}
