package com.gabrielhd.kitpvp.Blocks;

import com.gabrielhd.kitpvp.KitPvP;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class GameBlock {

    @Getter private final Location location;
    @Getter private final Block block;
    @Getter private double ticks;
    @Getter private State state;

    public GameBlock(Block block) {
        this.block = block;
        this.location = block.getLocation();
        this.ticks = 0.0;
        this.state = State.ONE;
        KitPvP.getBlocks().add(this);
    }

    public void update() {
        this.ticks += 0.10;
        if(this.ticks >= 5) {
            if(this.block.getType() == Material.STAINED_GLASS) {
                if(this.state == State.ONE) {
                    this.state = State.TWO;
                    this.block.setData((byte) 4);
                } else if(this.state == State.TWO) {
                    this.state = State.THREE;
                    this.block.setData((byte) 4);
                } else if(this.state == State.THREE) {
                    this.delete();
                }
            } else if(this.block.getType() == Material.WOOL) {
                if(this.state == State.ONE) {
                    this.state = State.TWO;
                    this.block.setData((byte)4);
                } else if(this.state == State.TWO) {
                    this.state = State.THREE;
                    this.block.setData((byte)14);
                } else if(this.state == State.THREE) {
                    this.delete();
                }
            } else {
                if(this.state == State.ONE) {
                    this.state = State.TWO;
                    this.block.setType(Material.COBBLESTONE);
                } else if(this.state == State.TWO) {
                    this.state = State.THREE;
                    this.block.setType(Material.MOSSY_COBBLESTONE);
                } else if(this.state == State.THREE) {
                    this.delete();
                }
            }
            this.ticks = 0.0;
        }
    }

    public void delete() {
        this.block.setType(Material.AIR);
        KitPvP.getBlocks().remove(this);
    }

    public enum State {
        ONE,
        TWO,
        THREE
    }
}
