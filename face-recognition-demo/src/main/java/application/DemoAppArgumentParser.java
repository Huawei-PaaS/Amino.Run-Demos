package application;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.Converters.BooleanConverter;

import amino.run.common.ArgumentParser.KernelServerArgumentParser;

/**
 * Class maintain command line arguments for face recognition app.
 */
public class DemoAppArgumentParser extends KernelServerArgumentParser {
    @Option(
            name = "inference-Type",
            help = "App can be start for detection or tracking",
            defaultValue = "tracking",
            category = "startup")
    public String inferenceType;

    @Option(
            name = "source-Type",
            help = "Video frame source. Currently video or camera are supported",
            defaultValue = "video",
            category = "startup")
    public String sourceType;

    @Option(
            name = "target-Type",
            help = "Processed frame target location. file or display",
            defaultValue = "display",
            category = "startup")
    public String targetType;

    @Option(
            name = "start-kernelserver",
            help = "Start App with kernel server. true and false are valid values",
            defaultValue = "false",
            category = "startup",
            converter = BooleanConverter.class)
    public Boolean startKernelServer;
}
