package shift;

import java.awt.Point;

public interface IPathLink
{
    Path getBoundPath();
    boolean pathLinkContainsPoint(Point p);
    Point[] getLinkPoints();
    int[] getLinkHeights();
    void setConnectedPath(Path p);
    Path getConnectedPath();
}
