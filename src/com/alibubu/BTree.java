package com.alibubu;

public class BTree {
    public BTreeNode root;
    public int min_degree;

    public BTree (int min_degree) {
        this.min_degree = min_degree;
        this.root = null;
    }

    public void traverse(){
        if (root != null)
            root.traverse();
    }

    public BTreeNode search(int k){
        return (root == null)? null : root.search(k);
    }

    public void insert(int k){
        if (root == null) {
            root = new BTreeNode(min_degree, true);
            root.keys[0] = k; // Insert key
            root.numKeys = 1;
        }
        else {
            // If root is full, then tree grows in height
            if (root.numKeys == 2*min_degree - 1){
                BTreeNode newRoot = new BTreeNode(min_degree, false);
                newRoot.children.add(0, root);  // Make old root as child of new root
                newRoot.splitChild(0, root);   // Split the old root and move 1 key to the new root

                // New root has two children after splitting.  Decide which of the two children is going to have new key
                int i = (newRoot.keys[0] < k) ? 1 : 0;
                newRoot.children.get(i).insertWhenNotFull(k);

                //change root
                this.root = newRoot;
            }
            else {
                root.insertWhenNotFull(k);
            }
        }
    }

}
