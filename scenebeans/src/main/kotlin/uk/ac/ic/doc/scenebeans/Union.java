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

import uk.ac.ic.doc.scenebeans.cag.CAGProcessor;
import uk.ac.ic.doc.scenebeans.cag.UnionProcessor;

import java.awt.*;


/** The <a href="../../../../../../beans/union.html">Union</a> 
 *  SceneBean.
 */
public class Union extends CAGComposite {
    protected CAGProcessor newCAGProcessor(Graphics2D g) {
        return new UnionProcessor(g);
    }
}
