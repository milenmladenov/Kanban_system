package com.tusofia.diplomna.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NameValidator {

    public static boolean check(String namVar) {
        String namRegExpVar = "^[A-Z][a-z]{2,}(?: [A-Z][a-z]*)*$";
        Pattern pVar = Pattern.compile(namRegExpVar);
        Matcher mVar = pVar.matcher(namVar);
        return mVar.matches();
    }
}
