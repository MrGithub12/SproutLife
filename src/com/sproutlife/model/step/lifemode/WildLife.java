/*******************************************************************************
 * Copyright (c) 2016 Alex Shapiro - github.com/shpralex
 * This program and the accompanying materials
 * are made available under the terms of the The MIT License (MIT)
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *******************************************************************************/
package com.sproutlife.model.step.lifemode;

import com.sproutlife.model.GameModel;
import com.sproutlife.model.echosystem.Organism;

public class WildLife extends CompetitiveLife {    
    public WildLife(GameModel gameModel) {
        super(gameModel);
    }

    protected void updateCompetitiveScore(Organism o) {
        Organism p = o.getParent();
        if (p==null) {
            super.updateCompetitiveScore(o);
            return;
        }

        int cs = (int) p.getAttributes().territoryProduct;
        while (p.getChildren().size()==1) {
            p=p.getParent();
            if (p!=null) {
                // include the first ancestor with child count > 1
                cs = (int) Math.max(cs, p.getAttributes().territoryProduct);
            }
            else {
                break;
            }
        }

        o.getAttributes().competitiveScore = cs;
    }
}
