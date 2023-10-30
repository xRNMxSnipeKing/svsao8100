package org.codehaus.jackson.sym;

import java.util.Arrays;
import org.codehaus.jackson.util.InternCache;

public final class BytesToNameCanonicalizer {
    protected static final int DEFAULT_TABLE_SIZE = 64;
    static final int INITIAL_COLLISION_LEN = 32;
    static final int LAST_VALID_BUCKET = 254;
    static final int MAX_ENTRIES_FOR_REUSE = 6000;
    protected static final int MAX_TABLE_SIZE = 65536;
    static final int MIN_HASH_SIZE = 16;
    private int _collCount;
    private int _collEnd;
    private Bucket[] _collList;
    private boolean _collListShared;
    private int _count;
    final boolean _intern;
    private int[] _mainHash;
    private int _mainHashMask;
    private boolean _mainHashShared;
    private Name[] _mainNames;
    private boolean _mainNamesShared;
    private transient boolean _needRehash;
    final BytesToNameCanonicalizer _parent;

    static final class Bucket {
        protected final Name _name;
        protected final Bucket _next;

        Bucket(Name name, Bucket next) {
            this._name = name;
            this._next = next;
        }

        public int length() {
            int len = 1;
            for (Bucket curr = this._next; curr != null; curr = curr._next) {
                len++;
            }
            return len;
        }

        public Name find(int hash, int firstQuad, int secondQuad) {
            if (this._name.hashCode() == hash && this._name.equals(firstQuad, secondQuad)) {
                return this._name;
            }
            for (Bucket curr = this._next; curr != null; curr = curr._next) {
                Name currName = curr._name;
                if (currName.hashCode() == hash && currName.equals(firstQuad, secondQuad)) {
                    return currName;
                }
            }
            return null;
        }

        public Name find(int hash, int[] quads, int qlen) {
            if (this._name.hashCode() == hash && this._name.equals(quads, qlen)) {
                return this._name;
            }
            for (Bucket curr = this._next; curr != null; curr = curr._next) {
                Name currName = curr._name;
                if (currName.hashCode() == hash && currName.equals(quads, qlen)) {
                    return currName;
                }
            }
            return null;
        }
    }

    public static BytesToNameCanonicalizer createRoot() {
        return new BytesToNameCanonicalizer(64, true);
    }

    public synchronized BytesToNameCanonicalizer makeChild(boolean canonicalize, boolean intern) {
        return new BytesToNameCanonicalizer(this, intern);
    }

    public void release() {
        if (maybeDirty() && this._parent != null) {
            this._parent.mergeChild(this);
            markAsShared();
        }
    }

    private BytesToNameCanonicalizer(int hashSize, boolean intern) {
        this._parent = null;
        this._intern = intern;
        if (hashSize < 16) {
            hashSize = 16;
        } else if (((hashSize - 1) & hashSize) != 0) {
            int curr = 16;
            while (curr < hashSize) {
                curr += curr;
            }
            hashSize = curr;
        }
        initTables(hashSize);
    }

    private BytesToNameCanonicalizer(BytesToNameCanonicalizer parent, boolean intern) {
        this._parent = parent;
        this._intern = intern;
        this._count = parent._count;
        this._mainHashMask = parent._mainHashMask;
        this._mainHash = parent._mainHash;
        this._mainNames = parent._mainNames;
        this._collList = parent._collList;
        this._collCount = parent._collCount;
        this._collEnd = parent._collEnd;
        this._needRehash = false;
        this._mainHashShared = true;
        this._mainNamesShared = true;
        this._collListShared = true;
    }

    private void initTables(int hashSize) {
        this._count = 0;
        this._mainHash = new int[hashSize];
        this._mainNames = new Name[hashSize];
        this._mainHashShared = false;
        this._mainNamesShared = false;
        this._mainHashMask = hashSize - 1;
        this._collListShared = true;
        this._collList = null;
        this._collEnd = 0;
        this._needRehash = false;
    }

    private synchronized void mergeChild(BytesToNameCanonicalizer child) {
        if (child._count > this._count) {
            if (child.size() > MAX_ENTRIES_FOR_REUSE) {
                initTables(64);
            } else {
                this._count = child._count;
                this._mainHash = child._mainHash;
                this._mainNames = child._mainNames;
                this._mainHashShared = true;
                this._mainNamesShared = true;
                this._mainHashMask = child._mainHashMask;
                this._collList = child._collList;
                this._collCount = child._collCount;
                this._collEnd = child._collEnd;
            }
        }
    }

    private void markAsShared() {
        this._mainHashShared = true;
        this._mainNamesShared = true;
        this._collListShared = true;
    }

    public int size() {
        return this._count;
    }

    public boolean maybeDirty() {
        return !this._mainHashShared;
    }

    public static Name getEmptyName() {
        return Name1.getEmptyName();
    }

    public Name findName(int firstQuad) {
        int hash = calcHash(firstQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        val &= 255;
        if (val > 0) {
            Bucket bucket = this._collList[val - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, 0);
            }
        }
        return null;
    }

    public Name findName(int firstQuad, int secondQuad) {
        int hash = calcHash(firstQuad, secondQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad, secondQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        val &= 255;
        if (val > 0) {
            Bucket bucket = this._collList[val - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, secondQuad);
            }
        }
        return null;
    }

    public Name findName(int[] quads, int qlen) {
        int hash = calcHash(quads, qlen);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null || name.equals(quads, qlen)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        val &= 255;
        if (val > 0) {
            Bucket bucket = this._collList[val - 1];
            if (bucket != null) {
                return bucket.find(hash, quads, qlen);
            }
        }
        return null;
    }

    public Name addName(String symbolStr, int q1, int q2) {
        if (this._intern) {
            symbolStr = InternCache.instance.intern(symbolStr);
        }
        int hash = q2 == 0 ? calcHash(q1) : calcHash(q1, q2);
        Name symbol = constructName(hash, symbolStr, q1, q2);
        _addSymbol(hash, symbol);
        return symbol;
    }

    public Name addName(String symbolStr, int[] quads, int qlen) {
        if (this._intern) {
            symbolStr = InternCache.instance.intern(symbolStr);
        }
        int hash = calcHash(quads, qlen);
        Name symbol = constructName(hash, symbolStr, quads, qlen);
        _addSymbol(hash, symbol);
        return symbol;
    }

    public static final int calcHash(int firstQuad) {
        int hash = firstQuad;
        hash ^= hash >>> 16;
        return hash ^ (hash >>> 8);
    }

    public static final int calcHash(int firstQuad, int secondQuad) {
        int hash = (firstQuad * 31) + secondQuad;
        hash ^= hash >>> 16;
        return hash ^ (hash >>> 8);
    }

    public static final int calcHash(int[] quads, int qlen) {
        int hash = quads[0];
        for (int i = 1; i < qlen; i++) {
            hash = (hash * 31) + quads[i];
        }
        hash ^= hash >>> 16;
        return hash ^ (hash >>> 8);
    }

    private void _addSymbol(int hash, Name symbol) {
        if (this._mainHashShared) {
            unshareMain();
        }
        if (this._needRehash) {
            rehash();
        }
        this._count++;
        int ix = hash & this._mainHashMask;
        if (this._mainNames[ix] == null) {
            this._mainHash[ix] = hash << 8;
            if (this._mainNamesShared) {
                unshareNames();
            }
            this._mainNames[ix] = symbol;
        } else {
            if (this._collListShared) {
                unshareCollision();
            }
            this._collCount++;
            int entryValue = this._mainHash[ix];
            int bucket = entryValue & 255;
            if (bucket == 0) {
                if (this._collEnd <= LAST_VALID_BUCKET) {
                    bucket = this._collEnd;
                    this._collEnd++;
                    if (bucket >= this._collList.length) {
                        expandCollision();
                    }
                } else {
                    bucket = findBestBucket();
                }
                this._mainHash[ix] = (entryValue & -256) | (bucket + 1);
            } else {
                bucket--;
            }
            this._collList[bucket] = new Bucket(symbol, this._collList[bucket]);
        }
        int hashSize = this._mainHash.length;
        if (this._count > (hashSize >> 1)) {
            int hashQuarter = hashSize >> 2;
            if (this._count > hashSize - hashQuarter) {
                this._needRehash = true;
            } else if (this._collCount >= hashQuarter) {
                this._needRehash = true;
            }
        }
    }

    private void rehash() {
        this._needRehash = false;
        this._mainNamesShared = false;
        int len = this._mainHash.length;
        int newLen = len + len;
        if (newLen > 65536) {
            nukeSymbols();
            return;
        }
        int i;
        this._mainHash = new int[newLen];
        this._mainHashMask = newLen - 1;
        Name[] oldNames = this._mainNames;
        this._mainNames = new Name[newLen];
        int symbolsSeen = 0;
        for (i = 0; i < len; i++) {
            Name symbol = oldNames[i];
            if (symbol != null) {
                symbolsSeen++;
                int hash = symbol.hashCode();
                int ix = hash & this._mainHashMask;
                this._mainNames[ix] = symbol;
                this._mainHash[ix] = hash << 8;
            }
        }
        int oldEnd = this._collEnd;
        if (oldEnd != 0) {
            this._collCount = 0;
            this._collEnd = 0;
            this._collListShared = false;
            Bucket[] oldBuckets = this._collList;
            this._collList = new Bucket[oldBuckets.length];
            for (i = 0; i < oldEnd; i++) {
                for (Bucket curr = oldBuckets[i]; curr != null; curr = curr._next) {
                    symbolsSeen++;
                    symbol = curr._name;
                    hash = symbol.hashCode();
                    ix = hash & this._mainHashMask;
                    int val = this._mainHash[ix];
                    if (this._mainNames[ix] == null) {
                        this._mainHash[ix] = hash << 8;
                        this._mainNames[ix] = symbol;
                    } else {
                        this._collCount++;
                        int bucket = val & 255;
                        if (bucket == 0) {
                            if (this._collEnd <= LAST_VALID_BUCKET) {
                                bucket = this._collEnd;
                                this._collEnd++;
                                if (bucket >= this._collList.length) {
                                    expandCollision();
                                }
                            } else {
                                bucket = findBestBucket();
                            }
                            this._mainHash[ix] = (val & -256) | (bucket + 1);
                        } else {
                            bucket--;
                        }
                        this._collList[bucket] = new Bucket(symbol, this._collList[bucket]);
                    }
                }
            }
            if (symbolsSeen != this._count) {
                throw new RuntimeException("Internal error: count after rehash " + symbolsSeen + "; should be " + this._count);
            }
        }
    }

    private void nukeSymbols() {
        this._count = 0;
        Arrays.fill(this._mainHash, 0);
        Arrays.fill(this._mainNames, null);
        Arrays.fill(this._collList, null);
        this._collCount = 0;
        this._collEnd = 0;
    }

    private int findBestBucket() {
        Bucket[] buckets = this._collList;
        int bestCount = Integer.MAX_VALUE;
        int bestIx = -1;
        int len = this._collEnd;
        for (int i = 0; i < len; i++) {
            int count = buckets[i].length();
            if (count < bestCount) {
                if (count == 1) {
                    return i;
                }
                bestCount = count;
                bestIx = i;
            }
        }
        return bestIx;
    }

    private void unshareMain() {
        int[] old = this._mainHash;
        int len = this._mainHash.length;
        this._mainHash = new int[len];
        System.arraycopy(old, 0, this._mainHash, 0, len);
        this._mainHashShared = false;
    }

    private void unshareCollision() {
        Bucket[] old = this._collList;
        if (old == null) {
            this._collList = new Bucket[32];
        } else {
            int len = old.length;
            this._collList = new Bucket[len];
            System.arraycopy(old, 0, this._collList, 0, len);
        }
        this._collListShared = false;
    }

    private void unshareNames() {
        Name[] old = this._mainNames;
        int len = old.length;
        this._mainNames = new Name[len];
        System.arraycopy(old, 0, this._mainNames, 0, len);
        this._mainNamesShared = false;
    }

    private void expandCollision() {
        Bucket[] old = this._collList;
        int len = old.length;
        this._collList = new Bucket[(len + len)];
        System.arraycopy(old, 0, this._collList, 0, len);
    }

    private static Name constructName(int hash, String name, int q1, int q2) {
        if (q2 == 0) {
            return new Name1(name, hash, q1);
        }
        return new Name2(name, hash, q1, q2);
    }

    private static Name constructName(int hash, String name, int[] quads, int qlen) {
        if (qlen < 4) {
            switch (qlen) {
                case 1:
                    return new Name1(name, hash, quads[0]);
                case 2:
                    return new Name2(name, hash, quads[0], quads[1]);
                case 3:
                    return new Name3(name, hash, quads[0], quads[1], quads[2]);
            }
        }
        int[] buf = new int[qlen];
        for (int i = 0; i < qlen; i++) {
            buf[i] = quads[i];
        }
        return new NameN(name, hash, buf, qlen);
    }
}
