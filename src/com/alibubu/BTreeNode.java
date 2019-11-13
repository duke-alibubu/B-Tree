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
            // traverse the subtree rooted with children[i].
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

    public void insertWhenNotFull(int k){

        // Initialize index as index of rightmost element
        int i = numKeys - 1;

        if (isLeaf) {
            // The following loop does two things
            // a) Finds the location of new key to be inserted
            // b) Moves all greater keys to one place ahead

            while (i >= 0 && keys[i] > k){
                keys[i+1] = keys[i];
                i--;
            }

            // Insert the new key at found location
            keys[i+1] = k;
            numKeys++;
        }
        else {
            // Find the child which is going to have the new key
            while (i >= 0 && keys[i] > k)
                i--;

            // See if the found child is full
            if (children.get(i+1).numKeys == 2*min_degree - 1){

                // If the child is full, then split it
                splitChild(i+1, children.get(i+1));

                // After split, the middle key of C[i] goes up and
                // C[i] is splitted into two.  See which of the two
                // is going to have the new key
                if (keys[i+1] < k)
                    i++;
            }
            children.get(i+1).insertWhenNotFull(k);
        }

    }

    public void splitChild(int index, BTreeNode child){
        // Create a new node which is going to store (minDegree - 1) keys
        // of child
        BTreeNode secondHalf = new BTreeNode(child.min_degree, child.isLeaf);
        secondHalf.numKeys = min_degree - 1;

        // Copy the last (minDegree-1) keys of child to secondHalf
        for (int i=0; i < min_degree - 1; i++)
            secondHalf.keys[i] = child.keys[i + min_degree];

        // Copy the last (minDegree) children of child to secondHalf, if it's not a leaf
        if (!child.isLeaf){
            for (int i = 0; i < min_degree; i++){
                secondHalf.children.add(i, child.children.get(i + min_degree));
            }
        }

        // Reduce the number of keys in child
        child.numKeys = min_degree - 1;

        // Since this node is going to have a new child,
        // create space of new child
        for (int i = numKeys; i >= index + 1; i--){
            children.remove(i+1);
            children.add(i+1, children.get(i));
        }
        // Link the new child to this node
        children.remove(index+1);
        children.add(index+1, secondHalf);

        // A key of child will move to this node. Find the location of
        // new key and move all greater keys one space ahead
        for (int j = numKeys - 1; j >= index; j--)
            keys[j+1] = keys[j];

        // Copy the middle key of child to this node
        keys[index] = child.keys[min_degree - 1];

        numKeys++;
    }
}
