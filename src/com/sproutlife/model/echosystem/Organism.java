/*******************************************************************************
 * Copyright (c) 2016 Alex Shapiro - github.com/shpralex
 * This program and the accompanying materials
 * are made available under the terms of the The MIT License (MIT)
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *******************************************************************************/
package com.sproutlife.model.echosystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.sproutlife.model.GameClock;
import com.sproutlife.model.seed.Seed;

/**
 * Organisms are a collection of Cells. They keep track of the location where
 * they were born, the seed from which they sprouted, their parents and
 * children. They have a genome which stores their mutations.
 * 
 * Organisms have a lot going on, and some experimental attributes are placed in
 * OrgAttributes to keep the code a bit cleaner.
 * 
 * @author Alex Shapiro
 */

public class Organism {
    private int id;
    private ArrayList<Cell> cells;
    private Organism parent;
    private ArrayList<Organism> children;
    private Genome genome;
    private Seed seed;

    //The clock lets us know the organism's age, so we can just track when it was born 
    public GameClock clock;

    // Lifespan isn's age, but a self destruct value above
    // which the organism is removed
    public int lifespan; 
    private int born; //Game time when the organism was born.
    private boolean alive = true;
    private int timeOfDeath;         
                        
    private Point location;
    public int x; //duplicate location for shorter code
    public int y; //duplicate location for shorter code
    
    //Keep track of random and experimental attributes in separate class    
    private OrgAttributes attributes;
            
    public Organism(int id, GameClock clock, int x, int y, Organism parent, Seed seed) {
        this.id = id;
        this.clock = clock;
        this.born = clock.getTime();
        this.parent = parent;
        this.children = new ArrayList<Organism>();
        this.setLocation(x, y);
        this.seed = seed;
        this.genome = new Genome();
        this.cells = new ArrayList<Cell>();
        this.timeOfDeath = -1;  
        this.attributes = new OrgAttributes(this);
   
        if (parent!=null) {
            parent.addChild(this);
            this.genome = parent.getGenome().clone();
            this.lifespan = parent.lifespan;
        }
    }
    
    public int getId() {
        return id;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
        this.location = new Point(x,y);
    }

    public Point getLocation() {
        return location;
    }   
    
    public Organism getParent() {
        return parent;
    }
    
    public void setParent(Organism parent) {
        this.parent = parent;
    }
    
    public ArrayList<Organism> getChildren() {
        return children;
    }
    
    public void addChild(Organism childOrg) {
        children.add(childOrg);
    }
    
    public void setSeed(Seed seed) {
        this.seed = seed;
    }
    
    public Seed getSeed() {
        return seed;
    }
    
    public GameClock getClock() {
        return clock;
    }
    
    public int getLifespan() {
        return lifespan;
    }
    
    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }
    
    public int getBorn() {
        return born;
    }
   
    /* 
     * @return return the time since the organism was born, even if it's dead
     */
    public int getTimeSinceBorn() {
        return getClock().getTime() - this.born;
    }
    
    /* 
     * @return if the organism is alive, return the time since it was born, 
     * otherwise, return the age at which the organism died.
     */
    public int getAge() {
        if (isAlive()) {
            return getTimeSinceBorn();
        }
        else {
            return this.timeOfDeath - this.born;
        }
    }

    public OrgAttributes getAttributes() {
        return attributes;
    }    
           
    public Genome getGenome() {
        return genome;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public Cell addCell(int x, int y) {
        Cell c = new Cell(x, y, this);
        addCell(c);
        return c;
    }
    
    /* 
     * Add a cell that's been created
     * 
     * @param c - cell to add
     */
    public void addCell(Cell c) {
        cells.add(c);
    }
    
    /*
     * Create cell but don't add it;
     * @param x - the x coordinate of the cell
     * @param y - the y coordinate of the cell
     * @return Create but don't add the cell
     */    
    public Cell createCell(int x, int y) {
        Cell c = new Cell(x, y, this);
        return c;
    }

    public Cell getCell(int x, int y) {
        for (Cell c: cells) {
            if (c.x ==x && c.y==y) {
                return c;
            }
        }
        return null;
    }
    
   
    public boolean removeCell(Cell c) {        
        return cells.remove(c);            
    }

    /* 
     * @return the number of cells an organism has
     */
    public int size() {
        return cells.size();
    }
        
    public boolean removeFromTerritory(Cell c) {
        boolean result = getAttributes().territory.remove(c);
        
        return result;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public boolean isAlive() {
        return alive && !(getTimeOfDeath()>0);
    }
    
    public int getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(int timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }
    
    public boolean equals(Organism o) {
        return this.id == o.id;
    }
}