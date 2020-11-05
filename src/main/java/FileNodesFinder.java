import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNodesFinder {
    Logger log = Logger.getLogger(FileNodesFinder.class.getName());
    List<String> filePaths = new ArrayList<>();

    // find file paths by given XML-filename and regex
    public void find(String fileName, String regex) {
        if (fileName != null) {
            NodeList nodeList = Objects.requireNonNull(getRoot(fileName)).getChildNodes();
            if (nodeList.getLength() > 0) {
                findFilePaths(nodeList);
            }
        }
        if (regex != null) {
            filter(regex);
        }
        print();
    }

    // get root of XML-file
    private Element getRoot(String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.severe("Serious parser configuration error: " + e.getMessage());
        }

        Document document = null;
        try {
            if (builder != null) {
                document = builder.parse(new File(fileName));
            }
        } catch (SAXException | IOException e) {
            log.severe("I/O or parse exception: " + e.getMessage());
        }

        if (document != null) {
            document.getDocumentElement().normalize();
            return document.getDocumentElement();
        }

        return null;
    }

    // find file paths
    private void findFilePaths(NodeList nodeList) {
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node node = nodeList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (Boolean.parseBoolean(eElement.getAttribute("is-file"))) {
                        String prefix = "";
                        prefix = getFullPathFromRoot(node, "");
                        String filePath = (prefix +
                                eElement.getElementsByTagName("name").item(0).getTextContent().trim())
                                .replace("//", "/");
                        filePaths.add(filePath);
                    } else {
                        findFilePaths(node.getChildNodes());
                    }
                }
            }
        }
    }

    // recursive method for getting full path from root to file
    public String getFullPathFromRoot(Node node, String endOfParentName) {
        String parentName = "";
        Node parentNode = node.getParentNode();
        if (parentNode != null && !parentNode.getNodeName().equals("#document")) {
            NodeList parentChildNodes = parentNode.getChildNodes();
            Node parentNameTagChild = null;
            for (int i = 0; i < parentChildNodes.getLength(); i++) {
                if (parentChildNodes.item(i).getNodeName().equals("name")) {
                    parentNameTagChild = parentChildNodes.item(i);
                    break;
                }
            }
            if (parentNameTagChild != null) {
                parentName = parentNameTagChild.getTextContent() + "/";
            }
            parentName = getFullPathFromRoot(parentNode, parentName + endOfParentName);
        } else {
            return endOfParentName;
        }
        return parentName;
    }

    // filter file paths by given regular expression
    private void filter(String regex) {
        Pattern pattern = Pattern.compile(regex);
        Iterator<String> iterator = filePaths.listIterator();
        while (iterator.hasNext()) {
            Matcher matcher = pattern.matcher(iterator.next());
            if (!matcher.find()) {
                iterator.remove();
            }
        }
    }

    // print matched file paths
    private void print() {
        for (String filePath : filePaths) {
            System.out.println(filePath);
        }
    }

}
