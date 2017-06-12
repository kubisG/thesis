package cz.osu.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Project: thesis
 * Created by Jakub on 9. 6. 2017.
 */
public class ClassFinderUtils {

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final char NESTED_CLASS_SEPARATOR = '$';

    private static final String CLASS_SUFFIX = ".class";

    private static final String CLASS_NAME_REGEX = "^([a-zA-Z_$][a-zA-Z\\d_$]*)\\.class$";

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile(CLASS_NAME_REGEX);

    private ClassFinderUtils() {
    }

    public static Map<String, String> getAvailableClassNames(String jarName, List<String> packageNames) {
        Map<String, String> availableClassNames = new HashMap<>();
        List<String> pNames = changePackageNameSeparators(packageNames);

        try {
            JarFile jarFile = new JarFile(new File(jarName));

            jarFile.stream()
                    .map(jarEntry -> jarEntry.getName())
                    .filter(jarEntryName -> isPartOfAnyPackage(jarEntryName, pNames))
                    .map(jarEntryName -> adjustJarEntryName(jarEntryName))
                    .forEach(jarEntryName -> availableClassNames.put(getClassNameKey(jarEntryName),
                            getClassNameValue(jarEntryName)));
        }
        catch(Exception e){
            throw new IllegalStateException("Seeking class is not listed in JAR");
        }
        return availableClassNames;
    }

    private static List<String> changePackageNameSeparators(List<String> packageNames) {
        return packageNames.stream()
                .map(packageName -> packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR) + DIR_SEPARATOR)
                .collect(Collectors.toList());
    }

    private static String adjustJarEntryName(String jarEntryName) {
        return jarEntryName.replace(NESTED_CLASS_SEPARATOR, PKG_SEPARATOR).replace(CLASS_SUFFIX, "");
    }

    private static String getClassNameKey(String jarEntryName) {
        int index = jarEntryName.lastIndexOf(DIR_SEPARATOR) + 1;
        return jarEntryName.substring(index);
    }

    private static String getClassNameValue(String jarEntryName) {
        return jarEntryName.replace(DIR_SEPARATOR, PKG_SEPARATOR);
    }

    private static boolean isPartOfAnyPackage(String jarEntryName, List<String> packageNames) {
        return packageNames.stream()
                .anyMatch(packageName -> isPartOfPackage(jarEntryName, packageName));
    }

    private static boolean isPartOfPackage(String jarEntryName, String packageName) {
        if (!jarEntryName.contains(packageName)) {
            return false;
        }
        String jarEntryClassName = jarEntryName.replaceFirst(packageName, "");
        Matcher matcher = CLASS_NAME_PATTERN.matcher(jarEntryClassName);

        return matcher.matches();
    }
}
