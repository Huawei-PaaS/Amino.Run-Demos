package org.openalpr;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * App specific configuration.
 *
 * @author arun
 */
public class Constants
{
    public static final String SERVER_DIRECTORY = "./";
    public static final int MAX_NUM_OF_PLATES = 50;
    public static final String RUNTIME_ASSET_DIR_LINUX = "/usr/share/openalpr/runtime_data";
    public static final String OPEN_ALPR_CONF_FILE_LINUX = "/etc/openalpr/openalpr.conf";

    // Max size of pixels to process either at width or height (original picture should be compressed to this max size).
    public static int maxSizeOfPictureToProcess = 1600;
}
