import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Tree root;
    private int size;
	
    public KdTree() {
        root = null;
        size = 0;
        // construct an empty set of points 
    }
	
    private static class Tree {
        Tree left;
        Tree right;
        Point2D point;
        RectHV area;
        boolean isRed;
		
        public Tree(Point2D point, RectHV area, boolean isRed) {
            this.point = point;
            this.area = area;
            this.isRed = isRed;
        }
    }
	
    public boolean isEmpty() {
        return root == null;
        // decide if the set is empty 
    }
	
    public int size() {
        return size;
        // number of points in the set 
    }
	
    private Tree insertHelper(Tree root, Point2D p, RectHV rect, boolean isRed) {
        if (root == null) {
            size++;
            return new Tree(p, rect, isRed);
        } else if (isRed) { 
            if (root.point.equals(p)) {
                return root;
            } else if (root.point.x() > p.x()) {
                root.left = insertHelper(root.left, p, new RectHV(rect.xmin(), rect.ymin(), root.point.x(), rect.ymax()), !isRed);
            } else {
                root.right = insertHelper(root.right, p, new RectHV(root.point.x(), rect.ymin(), rect.xmax(), rect.ymax()), !isRed);
            }
        } else {
            if (root.point.equals(p)) {
                return root;
            } else if (root.point.y() > p.y()) {
                root.left = insertHelper(root.left, p, new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), root.point.y()), !isRed);
            } else {
                root.right = insertHelper(root.right, p, new RectHV(rect.xmin(), root.point.y(), rect.xmax(), rect.ymax()), !isRed);
            }
        }
        return root;
    }
	
    public void insert(Point2D p) {
        root = insertHelper(root, p, new RectHV(0, 0, 1, 1), true);
        // add the point to the set (if it is not already in the set)
    }
	
    private boolean contains(Point2D p, Tree root) {
        if (root == null) {
            return false;
        }
        if (root.point.equals(p)) {
            return true;
        }
        if (root.isRed) {
            if (root.point.x() > p.x()) {
                return contains(p, root.left);
            } else {
                return contains(p, root.right);
            }
        } else {
            if (root.point.y() > p.y()) {
                return contains(p, root.left);
            } else {
                return contains(p, root.right);
            }
        }
    }
	
    public boolean contains(Point2D p) {
        return contains(p, root);
        // decide if the set contains point p 
    }
	
    public void draw() {
        draw(root);
        // draw all points to standard draw 
    }
	
    private void draw(Tree root) {
        if (root == null) {
            return;
        }
        Point2D point = root.point;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        point.draw();
        if (root.isRed) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(point.x(), root.area.ymin(), point.x(), root.area.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(root.area.xmin(), point.y(), root.area.xmax(), point.y());
        }
        draw(root.left);
        draw(root.right);
    }
	
    public Iterable<Point2D> range(RectHV rect) {
        List<Point2D> list = new ArrayList<>();
        search(list, root, rect);
        return list;
        // all points that are inside the rectangle 
    }
	
    private void search(List<Point2D> list, Tree root, RectHV rect) {
        if (root == null) {
            return;
        }
        Point2D p = root.point;
        if (rect.contains(p)) {
            list.add(p);
            search(list, root.left, rect);
            search(list, root.right, rect);
        } else {
            if (root.isRed) {
                if (rect.xmax() < p.x()) {
                    search(list, root.left, rect);
                } else if (rect.xmin() > p.x()) {
                    search(list, root.right, rect);
                } else {
                    search(list, root.left, rect);
                    search(list, root.right, rect);
                }
            } else {
                if (rect.ymax() < p.y()) {
                    search(list, root.left, rect);
                } else if (rect.ymin() > p.y()) {
                    search(list, root.right, rect);
                } else {
                    search(list, root.left, rect);
                    search(list, root.right, rect);
                }
            }
        }
    }

    private Point2D nearest(Point2D p, Point2D point, double len, Tree root) {
        if (root == null) {
            return point;
        }
        if (p.distanceSquaredTo(root.point) < len) {
            len = p.distanceSquaredTo(root.point);
            point = root.point;
        }
        RectHV rect = root.area;
        RectHV left, right;  
        double xmin = rect.xmin(), ymin = rect.ymin(), xmax = rect.xmax(), ymax = rect.ymax();
        if (root.isRed) {
            left = new RectHV(xmin, ymin, root.point.x(), ymax);
            right = new RectHV(root.point.x(), ymin, xmax, ymax);
        } else {
            left = new RectHV(xmin, ymin, xmax, root.point.y());
            right = new RectHV(xmin, root.point.y(), xmax, ymax);
        }
        double le = left.distanceSquaredTo(p), ri = right.distanceSquaredTo(p);
        if (le < ri) {
            Point2D tmp = nearest(p, point, len, root.left);
            if (tmp.distanceSquaredTo(p) < point.distanceSquaredTo(p)) {
                point = tmp;
            }
        } else {
            Point2D tmp = nearest(p, point, len, root.right);
            if (tmp.distanceSquaredTo(p) < point.distanceSquaredTo(p)) {
                point = tmp;
            }
        }
        if (le <= ri && ri < len) {
            Point2D tmp = nearest(p, point, len, root.right);
            if (tmp.distanceSquaredTo(p) < point.distanceSquaredTo(p)) {
                point = tmp;
            }
        }
        if (le >= ri && le < len) {
            Point2D tmp = nearest(p, point, len, root.left);
            if (tmp.distanceSquaredTo(p) < point.distanceSquaredTo(p)) {
                point = tmp;
            }
        }
        return point;
    }
	
    public Point2D nearest(Point2D p) {
        return nearest(p, null, Double.MAX_VALUE, root);
        // a nearest neighbor in the set to point p; null if the set is empty 
    }
	

	
    public static void main(String[] args) {
        String filename = "D:/Study/Graduate/coursera/Algorithms, Part I Princeton University/Kd-Trees/kdtree/circle10.txt";
        In in = new In(filename);

        StdDraw.enableDoubleBuffering();

        // initialize the two data structures with point from standard input
        PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }

        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw in red the nearest neighbor (using brute-force algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            brute.nearest(query).draw();
            StdDraw.setPenRadius(0.02);

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenColor(StdDraw.BLUE);
            kdtree.nearest(query).draw();
            StdDraw.show();
            StdDraw.pause(40);
        }
	}
}
