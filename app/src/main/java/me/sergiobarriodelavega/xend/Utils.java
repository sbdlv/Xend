package me.sergiobarriodelavega.xend;

import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;

public class Utils {

    public static boolean isEmpty(TextInputEditText txt, String errorMsg){
        String s = txt.getText().toString();

        if(s == null || s.isEmpty()){
            txt.setError(errorMsg);
            return true;
        }

        return false;
    }

    public static boolean isEmpty(TextInputEditText txt){
        return isEmpty(txt, App.getContext().getString(R.string.obligatory_field));
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
        return hasSpaces(txt, App.getContext().getString(R.string.field_with_spaces_error_msg));
    }
}
