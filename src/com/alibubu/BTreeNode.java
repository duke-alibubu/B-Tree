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
            }
            System.out.println(String.valueOf(keys[i]));
        }

        // Print the subtree rooted with last child
        if (!isLeaf)
            children.get(i).traverse();
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


    // A utility function to insert a new key in the subtree rooted with
    // this node. The assumption is, the node must be non-full when this
    // function is called
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



    // A utility function to split the child y of this node. index is index
    // of child in child array list.  The Child  must be full when this
    // function is called
    public void splitChild(int index, BTreeNode childToSplit){
        // Create a new node which is going to store (minDegree - 1) keys
        // of child
        BTreeNode secondHalf = new BTreeNode(childToSplit.min_degree, childToSplit.isLeaf);
        secondHalf.numKeys = min_degree - 1;

        // Copy the last (minDegree-1) keys of child to secondHalf
        for (int i=0; i < min_degree - 1; i++)
            secondHalf.keys[i] = childToSplit.keys[i + min_degree];

        // Copy the last (minDegree) children of child to secondHalf, if it's not a leaf
        if (!childToSplit.isLeaf){
            for (int i = 0; i < min_degree; i++){
                secondHalf.children.add(i, childToSplit.children.get(i + min_degree));
            }
        }

        // Reduce the number of keys in child
        childToSplit.numKeys = min_degree - 1;

        // Since this node is going to have a new child,
        // create space of new child
        for (int i = numKeys; i >= index + 1; i--){
            children.remove(i+1);
            children.add(i+1, children.get(i));
        }
        // Link the new child to this node
        children.add(index+1, secondHalf);

        // A key of child will move to this node. Find the location of
        // new key and move all greater keys one space ahead
        for (int j = numKeys - 1; j >= index; j--)
            keys[j+1] = keys[j];

        // Copy the middle key of child to this node
        keys[index] = childToSplit.keys[min_degree - 1];

        numKeys++;
    }

    // A wrapper function to remove the key k in subtree rooted with
    // this node.
    public void remove(int k){
        // the index of the first key that is greater than or equal to k
        int index = 0;
        while (index < numKeys && keys[index] < k)
            index++;

        // The key to be removed is present in this node
        if (index < numKeys && keys[index] == k){
            if (isLeaf)
                removeFromLeaf(index);
            else
                removeFromNonLeaf(index);
        }
        else {
            // If this node is a leaf node, then the key is not present in tree
            if (isLeaf){
                System.out.println(k + " is not present in the tree");
                return;
            }
            // The key to be removed is present in the sub-tree rooted with this node
            // The flag indicates whether the key is present in the sub-tree rooted
            // with the last child of this node
            boolean flag = (index == numKeys);

            // If the child where the key is supposed to exist has less that t keys,
            // we fill that child
            if (children.get(index).numKeys < min_degree)
                fill(index);

            // If the last child has been merged, it must have merged with the previous
            // child and so we recurse on the (idx-1)th child. Else, we recurse on the
            // (idx)th child which now has atleast t keys
            if (flag && index > numKeys)
                children.get(index - 1).remove(k);
            else
                children.get(index).remove(k);
        }
    }

    // A function to remove the key present in idx-th position in
    // this node which is a leaf
    public void removeFromLeaf(int index){
        for (int i = index + 1; i < numKeys; i++){
            keys[i-1] = keys[i];
        }

        // Reduce the count of keys
        numKeys--;
    }

    // A function to remove the key present in idx-th position in
    // this node which is a non-leaf node
    public void removeFromNonLeaf(int index){
        int k = keys[index];

        // If the child that precedes k (C[idx]) has atleast t keys,
        // find the predecessor 'pred' of k in the subtree rooted at
        // C[idx]. Replace k by pred. Recursively delete pred
        // in C[idx]
        if (children.get(index).numKeys >= min_degree){
            int pred = getPredecessor(index);
            keys[index] = pred;
            children.get(index).remove(pred);
        }
        else if (children.get(index + 1).numKeys >= min_degree){
            // If the child C[idx] has less that t keys, examine C[idx+1].
            // If C[idx+1] has at least t keys, find the successor 'succ' of k in
            // the subtree rooted at C[idx+1]
            // Replace k by 'succ'
            // Recursively delete 'succ' in C[idx+1]
            int succ = getSuccessor(index);
            keys[index] = succ;
            children.get(index + 1).remove(succ);
        }
        else {
            // If both C[idx] and C[idx+1] has less that t keys,merge k and all of C[idx+1]
            // into C[idx]
            // Now C[idx] contains 2t-1 keys
            // Free C[idx+1] and recursively delete k from C[idx]
            merge(index);
            children.get(index).remove(k);
        }
    }

    // A function to get the predecessor of the key- where the key
    // is present in the idx-th position in the node
    public int getPredecessor(int index){
        // Keep moving to the right most node until we reach a leaf
        BTreeNode cur = children.get(index);
        while (!cur.isLeaf)
            cur = cur.children.get(cur.numKeys);
        return cur.keys[cur.numKeys - 1];
    }

    // A function to get the successor of the key- where the key
    // is present in the idx-th position in the node
    public int getSuccessor(int index){
        BTreeNode cur = children.get(index + 1);
        while (!cur.isLeaf)
            cur = cur.children.get(0);
        return cur.keys[0];
    }

    // A function to fill up the child node present in the idx-th
    // position in the children array if that child has less than t-1 keys
    public void fill(int index){
        // If the previous child(C[idx-1]) has more than t-1 keys, borrow a key
        // from that child
        if (index != 0 && children.get(index-1).numKeys >= min_degree)
            borrowFromPrev(index);
        else if (index != numKeys && children.get(index + 1).numKeys >= min_degree)
            // If the next child(C[idx+1]) has more than t-1 keys, borrow a key
            // from that child
            borrowFromNext(index);
        else {
            // Merge C[idx] with its sibling
            // If C[idx] is the last child, merge it with with its previous sibling
            // Otherwise merge it with its next sibling
            if (index != numKeys)
                merge(index);
            else
                merge(index-1);
        }
    }

    // A function to borrow a key from the C[idx-1]-th node and place
    // it in C[idx]th node
    public void borrowFromPrev(int index){
        BTreeNode child = children.get(index);
        BTreeNode sibling = children.get(index - 1);

        // The last key from C[idx-1] goes up to the parent and key[idx-1]
        // from parent is inserted as the first key in C[idx]. Thus, this loses
        // sibling one key and child gains one key

        // Moving all key in C[idx] one step ahead
        for (int i = child.numKeys - 1; i >= 0; i--)
            child.keys[i+1] = child.keys[i];

        // If C[idx] is not a leaf, move all its child pointers one step ahead
        if (!child.isLeaf){
            for (int i = child.numKeys; i >= 0; i--){
                child.children.remove(i+1);
                child.children.add(i+1, child.children.get(i));
            }
        }

        // Setting child's first key equal to keys[idx-1] from the current node
        child.keys[0] = keys[index - 1];

        // Moving sibling's last child as C[idx]'s first child
        if(!child.isLeaf){
            child.children.remove(0);
            child.children.add(0, sibling.children.get(sibling.numKeys));
        }

        // Moving the key from the sibling to the parent
        // This reduces the number of keys in the sibling
        keys[index-1] = sibling.keys[sibling.numKeys-1];

        child.numKeys++;
        sibling.numKeys--;
    }

    // A function to borrow a key from the C[idx+1]-th node and place it
    // in C[idx]th node
    public void borrowFromNext(int index){
        BTreeNode child = children.get(index);
        BTreeNode sibling = children.get(index + 1);


        // keys[idx] is inserted as the last key in C[idx]
        child.keys[child.numKeys] = keys[index];

        // Sibling's first child is inserted as the last child
        // into C[idx]
        if (!child.isLeaf)
            child.children.add(child.numKeys + 1, sibling.children.get(0));

        //The first key from sibling is inserted into keys[idx]
        keys[index] = sibling.keys[0];

        // Moving all keys in sibling one step behind
        for (int i=1; i<sibling.numKeys; i++)
            sibling.keys[i-1] = sibling.keys[i];

        // Moving the child pointers one step behind
        if (!sibling.isLeaf)
        {
            for(int i=1; i<=sibling.numKeys; ++i){
                sibling.children.remove(i-1);
                sibling.children.add(i-1,sibling.children.get(i-1));
            }
        }
        child.numKeys++;
        sibling.numKeys--;
    }

    // A function to merge idx-th child of the node with (idx+1)th child of
    // the node
    public void merge(int index){
        BTreeNode child = children.get(index);
        BTreeNode sibling = children.get(index + 1);

        // Pulling a key from the current node and inserting it into (min_degree-1)th <this case child has min_degree - 1 keys>
        // position of C[idx]
        child.keys[min_degree-1] = keys[index];

        //Copying the keys from sibling C[idx+1] to child C[idx] at the end
        for (int i =0; i < sibling.numKeys; i++)
            child.keys[i + min_degree] = sibling.keys[i];

        // Copying the child pointers from C[idx+1] to C[idx]
        if (!child.isLeaf){
            for (int i = 0; i <= sibling.numKeys; i++){
                child.children.add(i + min_degree, sibling.children.get(i));
            }
        }

        // Moving all keys after idx in the current node one step before -
        // to fill the gap created by moving keys[idx] to C[idx]
        for (int i = index + 1; i <numKeys; i++){
            keys[i - 1] = keys[i];
        }

        // Moving the child pointers after (idx+1) in the current node one
        // step before
        for (int i = index + 2; i <= numKeys; i++){
            children.remove(i - 1);
            children.add(i-1, children.get(i-1));
        }

        child.numKeys += (sibling.numKeys + 1);
        numKeys--;

        sibling = null;
    }
}
