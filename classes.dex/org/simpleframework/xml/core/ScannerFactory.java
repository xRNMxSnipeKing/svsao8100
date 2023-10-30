package org.simpleframework.xml.core;

class ScannerFactory {
    private final ScannerCache cache = new ScannerCache();

    public Scanner getInstance(Class type) throws Exception {
        Scanner schema = (Scanner) this.cache.get(type);
        if (schema != null) {
            return schema;
        }
        schema = new Scanner(type);
        this.cache.put(type, schema);
        return schema;
    }
}
