package com.alibubu;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode {
    public int[] keys;     //minimum min_degree - 1 keys, max min_degree - 1 keys. root can have min 1 key
    public int min_degree;
    public List<BTreeNode> children;  //no of children = no of keys + 1
    public int numKeys;
    public boolean isLeaf;

    public BTreeNode(int min_degree, boolean isLeaf) {
        this.min_degree = min_degree;
        this.isLeaf = isLeaf;
        keys = new int[2*min_degree-1];
        children = new ArrayList<BTreeNode>(2 * min_degree);
    }

    public void traverse(){
        // There are n keys and n+1 children, travers through n keys
        // and first n children

        int i;
        for (i = 0; i < numKeys; i++){
            // If this is not leaf, then before printing key[i],
            // traverse the subtree rooted with child C[i].
            if (!isLeaf){
                children.get(i).traverse();
                System.out.println(String.valueOf(keys[i]));
            }
        }
    }

    public BTreeNode search (int k){
        int i = 0;
        while (i < numKeys && k > keys[i])
            i++;
        if (keys[i] == k)
            return this;
        if (isLeaf)
            return null;
        return children.get(i).search(k);
    }
}
