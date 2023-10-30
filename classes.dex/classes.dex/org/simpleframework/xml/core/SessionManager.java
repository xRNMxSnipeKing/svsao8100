package org.simpleframework.xml.core;

class SessionManager {
    private ThreadLocal<Reference> local = new ThreadLocal();

    private static class Reference {
        private int count;
        private Session session;

        public Reference(boolean strict) {
            this.session = new Session(strict);
        }

        public Session get() {
            if (this.count >= 0) {
                this.count++;
            }
            return this.session;
        }

        public int clear() {
            int i = this.count - 1;
            this.count = i;
            return i;
        }
    }

    public Session open() throws Exception {
        return open(true);
    }

    public Session open(boolean strict) throws Exception {
        Reference session = (Reference) this.local.get();
        if (session != null) {
            return session.get();
        }
        return create(strict);
    }

    private Session create(boolean strict) throws Exception {
        Reference session = new Reference(strict);
        if (session != null) {
            this.local.set(session);
        }
        return session.get();
    }

    public void close() throws Exception {
        Reference session = (Reference) this.local.get();
        if (session == null) {
            throw new PersistenceException("Session does not exist", new Object[0]);
        } else if (session.clear() == 0) {
            this.local.remove();
        }
    }
}
