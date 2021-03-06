/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.step.filter;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class CoinStep<S> extends FilterStep<S> {

    private static final Random RANDOM = new Random();
    private final double probability;

    public CoinStep(final Traversal.Admin traversal, final double probability) {
        super(traversal);
        this.probability = probability;
    }

    @Override
    protected boolean filter(final Traverser.Admin<S> traverser) {
        long newBulk = 0l;
        if (traverser.bulk() < 100) {
            for (int i = 0; i < traverser.bulk(); i++) {
                if (this.probability >= RANDOM.nextDouble())
                    newBulk++;
            }
        /*} else if (traverser.bulk() < 1000000) {
            final double cumulative = RANDOM.nextDouble();
            final long current = Double.valueOf(traverser.bulk() / 2.0d).longValue();
            final double next = choose(traverser.bulk(), current) * Math.pow(this.probability,current) * Math.pow(1.0d - this.probability,traverser.bulk() - current);
            if()
            */
        } else {
            newBulk = Math.round(this.probability * traverser.bulk());
        }
        if (0 == newBulk) return false;
        traverser.setBulk(newBulk);
        return true;
    }

    @Override
    public String toString() {
        return StringFactory.stepString(this, this.probability);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Double.hashCode(this.probability);
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return Collections.singleton(TraverserRequirement.BULK);
    }

    //////

    private static double choose(long x, long y) {
        if (y < 0 || y > x) return 0;
        if (y > x / 2) {
            // choose(n,k) == choose(n,n-k),
            // so this could save a little effort
            y = x - y;
        }

        double denominator = 1.0, numerator = 1.0;
        for (long i = 1; i <= y; i++) {
            denominator *= i;
            numerator *= (x + 1 - i);
        }
        return numerator / denominator;
    }
}
