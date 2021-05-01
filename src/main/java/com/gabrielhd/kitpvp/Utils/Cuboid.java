package com.gabrielhd.kitpvp.Utils;

import lombok.Getter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cuboid implements Iterable<Block> {

    @Getter private final Location l1;
    @Getter private final Location l2;
    protected final String worldName;
    protected final int x1, y1, z1;
    protected final int x2, y2, z2;

    public Cuboid(Location l1, Location l2) {
        if (!l1.getWorld().equals(l2.getWorld())) throw new IllegalArgumentException("Locations must be on the same world");
        this.l1 = l1;
        this.l2 = l2;
        this.worldName = l1.getWorld().getName();
        this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }

    public List<Block> getBlocks() {
        Iterator<Block> blockI = this.iterator();
        List<Block> copy = new ArrayList<>();
        while (blockI.hasNext())
            copy.add(blockI.next());
        return copy;
    }

    public World getWorld() {
        World world = Bukkit.getWorld(this.worldName);
        if (world == null) throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
        return world;
    }

    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public boolean inCuboid(Player player){
        return new IntRange(this.x1, this.x2).containsDouble(player.getLocation().getX())
                && new IntRange(this.y1, this.y2).containsDouble(player.getLocation().getY())
                &&  new IntRange(this.z1, this.z2).containsDouble(player.getLocation().getZ());
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2;
    }

    public class CuboidIterator implements Iterator<Block> {
        private final World w;
        private final int baseX;
        private final int baseY;
        private final int baseZ;
        private int x, y, z;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;

        public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            this.baseX = x1;
            this.baseY = y1;
            this.baseZ = z1;
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            this.x = this.y = this.z = 0;
        }

        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        public Block next() {
            Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
            if (++x >= this.sizeX) {
                this.x = 0;
                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }
            return b;
        }

        public void remove() {
        }
    }
}