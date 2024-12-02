/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.xsd.cmd;

import io.ballerina.xsdtorecordconverter.XSDToRecord;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import picocli.CommandLine;
import io.ballerina.cli.BLauncherCmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    @CommandLine.Parameters(description = "Input file path of the XSD schema")
    private final List<String> argList = new ArrayList<>();

    @CommandLine.Option(names = {"-o", "--output"}, description = "Destination file path of the generated types from " +
            "the XSD file")
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
        if (argList.isEmpty()) {
            outStream.println("An XSD file path is required to generate the types");
            outStream.println("e.g: bal xsd <xsd source file path>");
            exitOnError();
            return;
        }
        try {
            if (!Files.exists(Path.of(argList.get(0)))) {
                outStream.println(argList.get(0) + " file does not exist.");
                return;
            }
            String xmlFileContent = Files.readString(Path.of(argList.get(0)));
            Document document = parseXSD(xmlFileContent);
            String result = XSDToRecord.convert(document);
            Path destinationFile = Files.exists(Paths.get(outputPath))
                    ? handleFileOverwrite(Paths.get(outputPath), outStream) : Paths.get(outputPath);
            Path parentDirectory = destinationFile.getParent();
            if (parentDirectory != null && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }
            Files.writeString(destinationFile, result);
            System.out.println("Output is written to " + destinationFile);
        } catch (Exception e) {
            outStream.println("Error occurred: " + e.getLocalizedMessage());
            exitOnError();
        }
    }

    public static Path handleFileOverwrite(Path destinationFile, PrintStream outStream) {
        if (!Files.exists(destinationFile)) {
            return destinationFile;
        }
        String filePath = destinationFile.toString();
        outStream.print("File already exists at " + filePath + ". Overwrite? (y/N): ");
        String response = new Scanner(System.in).nextLine().trim().toLowerCase();
        if (response.equals("y")) {
            return destinationFile;
        }
        int counter = 1;
        String fileName = new File(filePath).getName();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
        String extension = dotIndex == -1 ? "" : fileName.substring(dotIndex);
        String parentPath = new File(filePath).getParent() != null ? new File(filePath).getParent() : "";
        while (Files.exists(destinationFile)) {
            String newFileName = baseName + "." + counter + extension;
            destinationFile = Path.of(parentPath, newFileName);
            counter++;
        }
        return destinationFile;
    }

    private static Document parseXSD(String xsdData) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xsdData.getBytes(StandardCharsets.UTF_8));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        docBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) {
                throw new RuntimeException(exception);
            }

            @Override
            public void error(SAXParseException exception) {
                throw new RuntimeException(exception);
            }

            @Override
            public void fatalError(SAXParseException exception) {
                throw new RuntimeException(exception);
            }
        });
        return docBuilder.parse(inputStream);
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
