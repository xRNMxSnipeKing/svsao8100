package org.codehaus.jackson.sym;

import java.util.Arrays;
import org.codehaus.jackson.util.InternCache;

public final class CharsToNameCanonicalizer {
    protected static final int DEFAULT_TABLE_SIZE = 64;
    static final int MAX_ENTRIES_FOR_REUSE = 12000;
    protected static final int MAX_TABLE_SIZE = 65536;
    static final CharsToNameCanonicalizer sBootstrapSymbolTable = new CharsToNameCanonicalizer();
    protected Bucket[] _buckets;
    protected final boolean _canonicalize;
    protected boolean _dirty;
    protected int _indexMask;
    protected final boolean _intern;
    protected CharsToNameCanonicalizer _parent;
    protected int _size;
    protected int _sizeThreshold;
    protected String[] _symbols;

    static final class Bucket {
        private final String _symbol;
        private final Bucket mNext;

        public Bucket(String symbol, Bucket next) {
            this._symbol = symbol;
            this.mNext = next;
        }

        public String getSymbol() {
            return this._symbol;
        }

        public Bucket getNext() {
            return this.mNext;
        }

        public String find(char[] buf, int start, int len) {
            String sym = this._symbol;
            Bucket b = this.mNext;
            while (true) {
                if (sym.length() == len) {
                    int i = 0;
                    while (sym.charAt(i) == buf[start + i]) {
                        i++;
                        if (i >= len) {
                            break;
                        }
                    }
                    if (i == len) {
                        return sym;
                    }
                }
                if (b == null) {
                    return null;
                }
                sym = b.getSymbol();
                b = b.getNext();
            }
        }
    }

    public static CharsToNameCanonicalizer createRoot() {
        return sBootstrapSymbolTable.makeOrphan();
    }

    private CharsToNameCanonicalizer() {
        this._canonicalize = true;
        this._intern = true;
        this._dirty = true;
        initTables(64);
    }

    private void initTables(int initialSize) {
        this._symbols = new String[initialSize];
        this._buckets = new Bucket[(initialSize >> 1)];
        this._indexMask = initialSize - 1;
        this._size = 0;
        this._sizeThreshold = initialSize - (initialSize >> 2);
    }

    private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, boolean canonicalize, boolean intern, String[] symbols, Bucket[] buckets, int size) {
        this._parent = parent;
        this._canonicalize = canonicalize;
        this._intern = intern;
        this._symbols = symbols;
        this._buckets = buckets;
        this._size = size;
        int arrayLen = symbols.length;
        this._sizeThreshold = arrayLen - (arrayLen >> 2);
        this._indexMask = arrayLen - 1;
        this._dirty = false;
    }

    public synchronized CharsToNameCanonicalizer makeChild(boolean canonicalize, boolean intern) {
        return new CharsToNameCanonicalizer(this, canonicalize, intern, this._symbols, this._buckets, this._size);
    }

    private CharsToNameCanonicalizer makeOrphan() {
        return new CharsToNameCanonicalizer(null, true, true, this._symbols, this._buckets, this._size);
    }

    private synchronized void mergeChild(CharsToNameCanonicalizer child) {
        if (child.size() > MAX_ENTRIES_FOR_REUSE) {
            initTables(64);
        } else if (child.size() > size()) {
            this._symbols = child._symbols;
            this._buckets = child._buckets;
            this._size = child._size;
            this._sizeThreshold = child._sizeThreshold;
            this._indexMask = child._indexMask;
        }
        this._dirty = false;
    }

    public void release() {
        if (maybeDirty() && this._parent != null) {
            this._parent.mergeChild(this);
            this._dirty = false;
        }
    }

    public int size() {
        return this._size;
    }

    public boolean maybeDirty() {
        return this._dirty;
    }

    public String findSymbol(char[] buffer, int start, int len, int hash) {
        if (len < 1) {
            return "";
        }
        if (!this._canonicalize) {
            return new String(buffer, start, len);
        }
        hash &= this._indexMask;
        String sym = this._symbols[hash];
        if (sym != null) {
            if (sym.length() == len) {
                int i = 0;
                while (sym.charAt(i) == buffer[start + i]) {
                    i++;
                    if (i >= len) {
                        break;
                    }
                }
                if (i == len) {
                    return sym;
                }
            }
            Bucket b = this._buckets[hash >> 1];
            if (b != null) {
                sym = b.find(buffer, start, len);
                if (sym != null) {
                    return sym;
                }
            }
        }
        if (!this._dirty) {
            copyArrays();
            this._dirty = true;
        } else if (this._size >= this._sizeThreshold) {
            rehash();
            hash = calcHash(buffer, start, len) & this._indexMask;
        }
        this._size++;
        String newSymbol = new String(buffer, start, len);
        if (this._intern) {
            newSymbol = InternCache.instance.intern(newSymbol);
        }
        if (this._symbols[hash] == null) {
            this._symbols[hash] = newSymbol;
        } else {
            int bix = hash >> 1;
            this._buckets[bix] = new Bucket(newSymbol, this._buckets[bix]);
        }
        return newSymbol;
    }

    public static int calcHash(char[] buffer, int start, int len) {
        int hash = buffer[0];
        for (int i = 1; i < len; i++) {
            hash = (hash * 31) + buffer[i];
        }
        return hash;
    }

    public static int calcHash(String key) {
        int hash = key.charAt(0);
        for (int i = 1; i < key.length(); i++) {
            hash = (hash * 31) + key.charAt(i);
        }
        return hash;
    }

    private void copyArrays() {
        String[] oldSyms = this._symbols;
        int size = oldSyms.length;
        this._symbols = new String[size];
        System.arraycopy(oldSyms, 0, this._symbols, 0, size);
        Bucket[] oldBuckets = this._buckets;
        size = oldBuckets.length;
        this._buckets = new Bucket[size];
        System.arraycopy(oldBuckets, 0, this._buckets, 0, size);
    }

    private void rehash() {
        int size = this._symbols.length;
        int newSize = size + size;
        if (newSize > 65536) {
            this._size = 0;
            Arrays.fill(this._symbols, null);
            Arrays.fill(this._buckets, null);
            this._dirty = true;
            return;
        }
        int i;
        String[] oldSyms = this._symbols;
        Bucket[] oldBuckets = this._buckets;
        this._symbols = new String[newSize];
        this._buckets = new Bucket[(newSize >> 1)];
        this._indexMask = newSize - 1;
        this._sizeThreshold += this._sizeThreshold;
        int count = 0;
        for (i = 0; i < size; i++) {
            int index;
            String symbol = oldSyms[i];
            if (symbol != null) {
                count++;
                index = calcHash(symbol) & this._indexMask;
                if (this._symbols[index] == null) {
                    this._symbols[index] = symbol;
                } else {
                    int bix = index >> 1;
                    this._buckets[bix] = new Bucket(symbol, this._buckets[bix]);
                }
            }
        }
        size >>= 1;
        for (i = 0; i < size; i++) {
            for (Bucket b = oldBuckets[i]; b != null; b = b.getNext()) {
                count++;
                symbol = b.getSymbol();
                index = calcHash(symbol) & this._indexMask;
                if (this._symbols[index] == null) {
                    this._symbols[index] = symbol;
                } else {
                    bix = index >> 1;
                    this._buckets[bix] = new Bucket(symbol, this._buckets[bix]);
                }
            }
        }
        if (count != this._size) {
            throw new Error("Internal error on SymbolTable.rehash(): had " + this._size + " entries; now have " + count + ".");
        }
    }
}
