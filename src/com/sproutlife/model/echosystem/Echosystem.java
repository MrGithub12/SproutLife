/*******************************************************************************
 * Copyright (c) 2016 Alex Shapiro - github.com/shpralex
 * This program and the accompanying materials
 * are made available under the terms of the The MIT License (MIT)
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *******************************************************************************/
package com.sproutlife.model.echosystem;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

import com.sproutlife.model.GameClock;
import com.sproutlife.model.seed.Seed;

/**
 * The Echosystem keeps track of all our organisms, living and recently retired.
 * It also manages the Board.
 * 
 * @author Alex Shapiro
 */

public class Echosystem {
    List<Organism> organisms;
    Deque<Organism> retiredOrganisms;

    Board board;

    int defaultOrgLifespan;
    int retirementTimeSpan;

    GameClock clock;

    public int sequentialOrganismId = 0;

    public Echosystem(GameClock clock) {
        this.organisms = new ArrayList<Organism>();
        this.retiredOrganisms = new ArrayDeque<Organism>();
        this.board = new Board();
        this.clock = clock;

        this.defaultOrgLifespan = 15;
        this.retirementTimeSpan = 1000;
    }

    public Collection<Organism> getOrganisms() {
        return organisms;
    }

    public Deque<Organism> getRetiredOrganisms() {
        return retiredOrganisms;
    }

    public Board getBoard() {
        return board;
    }

    public GameClock getClock() {
        return clock;
    }

    public int getTime() {
        return clock.getTime();
    }

    /*
     * Convenience method. Maybe slow, and shouldn't be used often. Should be
     * replaced by custom iterator.
     * 
     * @Return all the cells
     */
    public ArrayList<Cell> getCells() {
        ArrayList<Cell> cellList = new ArrayList<Cell>();
        for (Organism o : getOrganisms()) {
            cellList.addAll(o.getCells());
        }

        return cellList;
    }

    public void resetCells() {
        ArrayList<Cell> cells = new ArrayList<Cell>(getCells());
        for (Cell c : cells) {
            removeCell(c);
        }

        this.setDefaultOrgLifespan(15);
    }

    public boolean removeCell(Cell c) {
        return removeCell(c, true);
    }

    public boolean removeCell(Cell c, boolean updateBoard) {
        if (c == null) {
            return false;
        }

        boolean result = c.getOrganism().removeCell(c);

        if (updateBoard) {
            // We don't want to update the board if we just reset it.
            getBoard().removeCell(c);
        }

        return result;
    }

    public Cell addCell(int x, int y, Organism org) {
        Cell c = org.addCell(x, y);
        if (c != null) {
            getBoard().setCell(c);
        }
        return c;
    }

    /*
     * Add a cell that's been created but not yet added
     */
    public void addCell(Cell c) {
        c.getOrganism().addCell(c);
        getBoard().setCell(c);
    }

    /*
     * Create a cell but don't add it
     */
    public Cell createCell(int x, int y, Organism o) {
        return o.createCell(x, y);
    }

    public void removeCell(int x, int y) {
        Cell c = getBoard().getCell(x, y);
        if (c != null) {
            removeCell(c);
        }
    }

    public void setDefaultOrgLifespan(int orgLifespan) {
        this.defaultOrgLifespan = orgLifespan;
    }

    public int getDefaultOrgLifespan() {
        return defaultOrgLifespan;
    }

    public int getRetirementTimeSpan() {
        return retirementTimeSpan;
    }

    public void setRetirementTimeSpan(int retirementTimeSpan) {
        this.retirementTimeSpan = retirementTimeSpan;
    }

    public Organism createOrganism(int x, int y, Organism parent, Seed seed) {
        sequentialOrganismId++;

        Organism newOrg = new Organism(sequentialOrganismId, getClock(), x, y, parent, seed);
        if (parent == null) {
            newOrg.setLifespan(getDefaultOrgLifespan());
        }

        organisms.add(newOrg);
        return newOrg;

    }

    public void removeOrganism(Organism o) {
        for (Cell c : o.getCells()) {
            getBoard().removeCell(c);
        }
        organisms.remove(o);
    }

    /*
     * A "retired" organism is actually dead, maybe the function should be renamed.
     * Retired organisms hang around in a separate list for statistics purposes.
     */
    public void retireOrganism(Organism o) {
        for (Cell c : o.getCells()) {
            getBoard().removeCell(c);
        }
        o.setAlive(false);
        o.setTimeOfDeath(getTime());

        organisms.remove(o);
        retiredOrganisms.add(o);
    }

    public void removeRetired(Organism o) {
        retiredOrganisms.remove(o);
    }

    public void clearRetiredOrgs() {
        getRetiredOrganisms().clear();
    }
}
