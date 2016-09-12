package cn.ittiger;

import android.content.Context;
import android.widget.Toast;

/**
 * @author: laohu on 2016/7/25
 * @site: http://ittiger.cn
 */
public class UIUtil {

    public static void showToast(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
