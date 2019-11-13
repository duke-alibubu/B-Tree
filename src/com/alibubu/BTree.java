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


}
