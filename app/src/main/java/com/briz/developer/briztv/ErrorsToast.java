package com.briz.developer.briztv;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Класс показа кастомных тостов сообщений об ошибках
 * @author Vladislav Deltsov
 * @version 27.05.15.
 */
public class ErrorsToast{


    /**
     * Статический метод вывода тоста сообщения об ошибках
     * @param ctx - контекст пиложения
     * @param act - активная активность
     * @param text - текст сообщения об ошибке
     */
    public static void showToast(Context ctx, Activity act, String text) {

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View layout = inflater.inflate(R.layout.errors_toast,
                (ViewGroup) act.findViewById(R.id.err_toast_root));

        TextView tvErrors = (TextView) layout.findViewById(R.id.lblErrorDescription);
        tvErrors.setText(text);

        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

}
