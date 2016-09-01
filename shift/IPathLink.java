/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author phusisian
 */
public interface IPathLink
{
    Path getBoundPath();
    boolean pathLinkContainsPoint(Point p);
    Point[] getLinkPoints();
    int[] getLinkHeights();
    void setConnectedPath(Path p);
    Path getConnectedPath();
}
