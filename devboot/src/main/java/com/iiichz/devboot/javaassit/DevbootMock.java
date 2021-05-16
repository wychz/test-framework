package com.iiichz.devboot.javaassit;

import javassist.CtClass;
import javassist.Modifier;

public class DevbootMock {
    public static void removeFinal(CtClass clazz) {

        int modifiers = clazz.getModifiers();

        if (Modifier.isFinal(modifiers)) {
            System.out.println("Removing final modifier: " + clazz.getName());
            int notFinalModifier = Modifier.clear(modifiers, Modifier.FINAL);
            clazz.setModifiers(notFinalModifier);
        }
    }

    public static String getWriteFilePath() {
        return DevbootMock.class.getResource("/").getPath();
    }
}