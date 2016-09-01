/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

/**
 *
 * @author phusisian
 */
public class Level 
{
    private Tile[] levelTiles;
    private int unitsWidth, unitsHeight;
    public Level(Tile[] levelTilesIn, int unitsWidthIn, int unitsHeightIn)
    {
        spawnLevel();
        levelTiles = levelTilesIn;
        unitsWidth = unitsWidthIn;
        unitsHeight = unitsHeightIn;
    }
    public void spawnLevel()
    {
        MergedBlockTiles.blockTiles.clear();
        MergedPaths.pathList.clear();
        MergedPaths.pathLinks.clear();
        TileDrawer.tileList.clear();
        TileDrawer.waterDroplets.clear();
        TileSorter.holdList.clear();
        TileSorter.tileList.clear();
    }
}
