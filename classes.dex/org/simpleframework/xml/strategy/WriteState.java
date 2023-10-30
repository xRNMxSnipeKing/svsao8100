package org.simpleframework.xml.strategy;

import org.simpleframework.xml.util.WeakCache;

class WriteState extends WeakCache<Object, WriteGraph> {
    private Contract contract;

    public WriteState(Contract contract) {
        this.contract = contract;
    }

    public WriteGraph find(Object map) {
        WriteGraph write = (WriteGraph) fetch(map);
        if (write != null) {
            return write;
        }
        write = new WriteGraph(this.contract);
        cache(map, write);
        return write;
    }
}
