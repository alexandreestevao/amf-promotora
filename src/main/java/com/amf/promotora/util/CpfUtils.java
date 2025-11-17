package com.amf.promotora.util;

public class CpfUtils {
    public static boolean isValid(String cpf) {
        if (cpf == null) return false;
        String onlyDigits = cpf.replaceAll("\\D", "");
        return onlyDigits.length() == 11;
    }
}
