package io.ballerina.xsdtorecordconverter;

import org.w3c.dom.Node;

public class Element implements IComponent {
    Node node;
    boolean isSubType;

    public Element(Node node) {
        this.node = node;
        this.isSubType = false;
    }

    public Node getNode() {
        return node;
    }

    public String accept(IXSDVisitor xsdVisitor) {
        if (isSubType) {
            return xsdVisitor.visit(this, true);
        }
        return xsdVisitor.visit(this);
    }

    @Override
    public void setSubType(boolean subType) {
        this.isSubType = subType;
    }

    @Override
    public boolean isSubType() {
        return this.isSubType;
    }
}
