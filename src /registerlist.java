public class registerlist {
	Node head;
    public void printList()
    {
        Node n = head;
        while (n != null)
        {
            System.out.print(n.data+" ");
            n = n.next;
        }
    }
    public Node searchprenode(String name) {
    		Node n = head;
        while (n.next != null)
        {
            if(name == n.next.data) {
            	return n;
            }
            n = n.next;
        }
        return null;
    }
    public void remove(String name) {
    		Node n = searchprenode(name);
    		if(n.next.next!=null) {
    			Node q = n.next.next;
    			n.next = q;
    		}
    		else {
    			n.next = null;
    		}
    }
}
