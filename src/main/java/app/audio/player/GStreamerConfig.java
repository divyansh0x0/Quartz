package app.audio.player;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import material.utils.Log;

import java.io.File;
import java.util.Arrays;

/**
 * Utility methods for use in examples.
 */
class GStreamerConfig {

    private GStreamerConfig() {
    }

    /**
     * Configures paths to the GStreamer libraries. On Windows queries various
     * GStreamer environment variables, and then sets up the PATH environment
     * variable. On macOS, adds the location to jna.library.path (macOS binaries
     * link to each other). On both, the gstreamer.path system property can be
     * used to override. On Linux, assumes GStreamer is in the path already.
     */
    protected static void configurePaths() {

            if (Platform.isWindows()) {
                String gstPath =  findWindowsLocation();
                Log.warn("GST PATH: " + gstPath);
                if (!gstPath.isEmpty()) {
                    String systemPath = System.getenv("PATH");
                    if (systemPath == null || systemPath.trim().isEmpty()) {
                        Kernel32.INSTANCE.SetEnvironmentVariable("PATH", gstPath);
                    } else {
                        Kernel32.INSTANCE.SetEnvironmentVariable("PATH", gstPath
                                + File.pathSeparator + systemPath);
                    }
                }
            } else if (Platform.isMac()) {
                String gstPath = System.getProperty("gstreamer.path",
                        "/Library/Frameworks/GStreamer.framework/Libraries/");
                if (!gstPath.isEmpty()) {
                    String jnaPath = System.getProperty("jna.library.path", "").trim();
                    if (jnaPath.isEmpty()) {
                        System.setProperty("jna.library.path", gstPath);
                    } else {
                        System.setProperty("jna.library.path", jnaPath + File.pathSeparator + gstPath);
                    }
                }

            }
        }

//    private static String originalEnv;
//    private static String TempGstPath;
//    private static boolean foundGstreamerFromResources() {
//        try {
//            URL resource = GStreamerConfig.class.getClassLoader().getResource("gstreamer/bin/bz2.dll");
////            File gstFile = new File(Paths.get(resource.toURI()).toFile().getAbsolutePath() + "/gstreamer/bin/");
////            if( gstFile.exists()){
//            TempGstPath = new File(resource.toURI()).getParent();
//            if (TempGstPath != null) {
//                Log.info("gst path found: " + TempGstPath);
//                originalEnv = System.getenv("PATH").replace(TempGstPath + File.pathSeparator,"");
//
//                String newEnv = TempGstPath + File.pathSeparator + originalEnv;
//                Kernel32.INSTANCE.SetEnvironmentVariable("PATH", newEnv);
//                Log.success("Found gstreamer " + TempGstPath);
//                Log.success("environment variable set to : " + newEnv);
//                //Unset environment variable
//                Runtime.getRuntime().addShutdownHook(new Thread(()->{
//                    Kernel32.INSTANCE.SetEnvironmentVariable("PATH", originalEnv);
//                    Log.success("Changed environment variable back to: " + originalEnv);
//                }));
//                return true;
//            } else {
//                Log.error("Couldn't find gstreamer!" + resource);
//                return false;
//            }
////            }
////            else {
//
////            }
//
//        } catch (Exception e) {
//            Log.error("Error occurred while loading gstreamer from resources: " + e);
//            return false;
//        }
//    }

    /**
     * Query over a stream of possible environment variables for GStreamer
     * location, filtering on the first non-null result, and adding \bin\ to the
     * value.
     *
     * @return location or empty string
     */
    static String findWindowsLocation() {
        if (Platform.is64Bit()) {
            for (String s : Arrays.asList("GSTREAMER_1_0_ROOT_MSVC_X86_64",
                    "GSTREAMER_1_0_ROOT_MINGW_X86_64",
                    "GSTREAMER_1_0_ROOT_MSVC_X86_64",
                    "GSTREAMER_1_0_ROOT_X86_64")) {
                String p = System.getenv(s);
                if (p != null) {
                    return p.endsWith("\\") ? p + "bin\\" : p + "\\bin\\";
                }
            }
            return "";
        } else {
            return "";
        }
    }

}