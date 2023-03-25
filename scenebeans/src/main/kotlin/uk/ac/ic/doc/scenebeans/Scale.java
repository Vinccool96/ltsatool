/**
 * SceneBeans, a Java API for animated 2D graphics.
 * <p>
 * Copyright (C) 2000 Nat Pryce and Imperial College
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */


package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;


/** The <a href="../../../../../../beans/scale.html">Scale</a> 
 *  SceneBean.
 */
public class Scale extends TransformBase {
    private double _x, _y;

    public Scale() {
        super();
        _x = 1.0;
        _y = 1.0;
    }

    public Scale(double x, double y, SceneGraph g) {
        super(g);
        _x = x;
        _y = y;
    }

    public double getX() {
        return _x;
    }

    public void setX(double x) {
        _x = x;
        setDirty(true);
    }

    public double getY() {
        return _y;
    }

    public void setY(double y) {
        _y = y;
        setDirty(true);
    }

    public AffineTransform getTransform() {
        return AffineTransform.getScaleInstance(_x, _y);
    }

    public class X implements DoubleBehaviourListener, java.io.Serializable {
        public void behaviourUpdated(double v) {
            setX(v);
        }
    }

    public final X newXAdapter() {
        return new X();
    }

    public class Y implements DoubleBehaviourListener, java.io.Serializable {
        public void behaviourUpdated(double v) {
            setY(v);
        }
    }

    public final Y newYAdapter() {
        return new Y();
    }
}
