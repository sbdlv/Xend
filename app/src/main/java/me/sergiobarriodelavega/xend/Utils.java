package me.sergiobarriodelavega.xend;

import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;

public class Utils {
    public static Context context;

    public static boolean isEmpty(TextInputEditText txt, String errorMsg){
        String s = txt.getText().toString();

        if(s == null || s.isEmpty()){
            txt.setError(errorMsg);
            return true;
        }

        return false;
    }

    public static boolean isEmpty(TextInputEditText txt){
        return isEmpty(txt, context.getString(R.string.obligatory_field));
    }

    public static boolean hasSpaces(TextInputEditText txt, String errorMsg){
        String s = txt.getText().toString();

        if(s.contains(" ")){
            txt.setError(errorMsg);
            return true;
        }

        return false;
    }

    public static boolean hasSpaces(TextInputEditText txt){
        return hasSpaces(txt, context.getString(R.string.field_with_spaces_error_msg));
    }
}
