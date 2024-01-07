package material.utils.structures;

import java.util.Iterator;

public class LanguageCompatibleString {

    public static class Node {
        private final String curr;
        public Node next;
        private final boolean isSupportedByFont;

        public Node(String curr, boolean isSupportedByFont) {
            this.curr = curr;
            this.isSupportedByFont = isSupportedByFont;
        }

        public String getCurr() {
            return curr;
        }

        public boolean isSupportedByFont() {
            return isSupportedByFont;
        }

        @Override
        public String toString() {
            return "Node{" +
                    " curr='" + curr + '\'' +
                    " | next=" + next +
                    " | isSupportedByFont=" + isSupportedByFont +
                    "}\n";
        }
    }

    String originalString;
    private boolean isCompletelyCompatible = true;
    private Node root;
    private Node currNode;

    public LanguageCompatibleString(String originalString) {
        this.originalString = originalString;
    }

    public void addString(String str, boolean isSupported) {
        if (root == null) {
            root = new Node(str, isSupported);
            currNode = root;
        }else {
            currNode.next = new Node(str, isSupported);
            currNode = currNode.next;
        }
        if(!isSupported && isCompletelyCompatible)
            isCompletelyCompatible = false;
    }

    public Iterator<Node> getIterator() {
        return new StringCompatibleIterator();
    }

    public String getOriginalString() {
        return originalString;
    }

    public boolean isCompletelyCompatible() {
        return isCompletelyCompatible;
    }

    @Override
    public String toString() {
        return """
               Original string: %s1
               Nodes:
               %s2
               """.formatted(originalString,root.toString());
    }

    private class StringCompatibleIterator implements Iterator<Node> {
        Node curr;

        public StringCompatibleIterator() {
            curr = root;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public Node next() {
            Node temp = curr;
            curr = curr.next;
            return temp;
        }
    }
}
