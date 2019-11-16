# BTree

- Simple implementaion of BTree using Java.
- Reference : `https://www.geeksforgeeks.org/introduction-of-b-tree-2/`
- Some general notes for BTree deleting operation:
  + During the delete track-down, if spot 2 adjacent siblings with numKey to be both t-1 --> attempt to merge.
- The main idea of using B-Trees is to reduce the number of disk accesses. Disk access takes very long, and is accessed in form of pages.
- The height of B-Trees is kept low by putting maximum possible keys in a B-Tree node
- Compare to normal binary tree, BTree does not take much time doing the update of the tree (moving the data part) which is usually very tedious. (Since a node in BTree contains many node...)
- In a B-tree the left and right side of each node is roughly kept to the same size (number of subnodes). For normal binary tree, search time for an unbalanced tree might be worse in some cases.
- Each node can be considered a chunk or page of data that has to be read in its entirety.
- Due to the tree being relatively flat, the number of pages that has to be read to lookup a specific data point is low.

## Properties of B-Tree
1. All leaves at same level
2. A B-Tree is defined by the term minimum degree ‘t’. The value of t depends upon disk block size.
3. Every node except root must contain at least t-1 keys. Root may contain minimum 1 key.
4. All nodes (including root) may contain at most 2t – 1 keys.
5. Number of children of a node is equal to the number of keys in it plus 1.
6. All keys of a node are sorted in increasing order. The child between two keys k1 and k2 contains all keys in the range from k1 and k2.
7. B-Tree grows and shrinks from the root which is unlike Binary Search Tree. Binary Search Trees grow downward and also shrink from downward.
8. Like other balanced Binary Search Trees, time complexity to search, insert and delete is O(Logn).

## Indexing in SQL databases: When to use and when not to ?
#### When to avoid
- Table size is small.
- Table columns that have frequent, large batch updates or insert operations.
- Indexes should not be used on columns that contain a high number of NULL values.
- Use index on columns that do not have many different values.

#### When to use
- Table columns that are frequently SEARCH and WHERE.
- Use index on columns that have many different values.

