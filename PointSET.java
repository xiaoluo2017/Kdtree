import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
    private TreeSet<Point2D> tree;
	
    public PointSET() {
        tree = new TreeSet<>();
        // construct an empty set of points 
    }
	
	
	
    public boolean isEmpty() {
        return tree.isEmpty();
        // decide if the set is empty 
    }
	
    public int size() {
        return tree.size();
        // number of points in the set 
    }
	
    public void insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        tree.add(p);
        // add the point to the set (if it is not already in the set)
    }
	
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        return tree.contains(p);
        // decide if the set contains point p 
    }
	
    public void draw() {
        if (tree == null) {
            return;
        }
        for (Point2D p : tree) {
            p.draw();
        }
        // draw all points to standard draw 
    }
	
    public Iterable<Point2D> range(RectHV rect) {
        List<Point2D> list = new ArrayList<>();
        if (tree == null) {
            return list;
        }
        for (Point2D p : tree) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }
        return list;
        // all points that are inside the rectangle 
    }
	
    public Point2D nearest(Point2D p) {
        if (tree == null) {
            return null;
        }
        double len = Double.MAX_VALUE;
        Point2D res = null;
        for (Point2D point : tree) {
            double currLen = p.distanceSquaredTo(point);
            if (len > currLen) {
                len = currLen;
                res = point;
            }
        }
        return res;
        // a nearest neighbor in the set to point p; null if the set is empty 
    }

    public static void main(String[] args) {
    
        // unit testing of the methods
    }
}
