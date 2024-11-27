package io.ballerina.xsd.cmd;

import io.ballerina.xsdtorecordconverter.XSDToRecord;
import picocli.CommandLine;
import io.ballerina.cli.BLauncherCmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class to implement "xsd" command for ballerina.
 */
@CommandLine.Command(
        name = "xsd",
        description = "Generates Ballerina records for XSD specification"
)
public class XsdCmd implements BLauncherCmd {
    private static final String CMD_NAME = "xsd";
    private final PrintStream outStream;
    private final boolean exitWhenFinish;
    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"-i", "--input"}, description = "Input the XSD file")
    private String inputPath;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Generating the types from the XSD file")
    private String outputPath = "types.bal";

    public XsdCmd() {
        this.outStream = System.err;
        this.exitWhenFinish = true;
    }

    @Override
    public void execute() {
        if (this.helpFlag) {
            StringBuilder stringBuilder = new StringBuilder();
            printLongDesc(stringBuilder);
            System.out.println(stringBuilder);
            return;
        }
        if (inputPath == null || inputPath.isBlank()) {
            outStream.println("error");
            exitOnError();
            return;
        }
        try {
            String xmlFileContent = Files.readString(Path.of(inputPath));
            String result = XSDToRecord.convert(xmlFileContent);
            Path destinationFile = Paths.get(outputPath);
            Files.writeString(destinationFile, result);
            System.out.println("Processing completed. Output written to " + outputPath);
        } catch (Exception e) {
            outStream.println("Error: " + e.getLocalizedMessage());
            exitOnError();
        }
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder outStream) {
        Class<?> clazz = XsdCmd.class;
        ClassLoader classLoader = clazz.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("cli-docs/xsd-help.help");
        if (inputStream != null) {
            try (InputStreamReader inputStreamREader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(inputStreamREader)) {
                String content = br.readLine();
                outStream.append(content);
                while ((content = br.readLine()) != null) {
                    outStream.append('\n').append(content);
                }
            } catch (IOException e) {
                outStream.append("Helper text is not available.");
            }
        }
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }

    private void exitOnError() {
        if (exitWhenFinish) {
            Runtime.getRuntime().exit(1);
        }
    }
}
